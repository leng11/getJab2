package integration.com.example.centerService;

import static com.example.centerService.controller.Controller.*;
import static com.example.centerService.service.CenterIncomingEvent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import com.example.centerService.model.Center;
import com.example.centerService.model.Inventory;
import com.example.centerService.model.Vaccine;
import com.example.centerService.model.clientFacing.Shipment;
import com.example.commonUtility.event.KafkaUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, classes=com.example.centerService.CenterServiceApplication.class)
@EmbeddedKafka(topics={"${spring.application.event.incoming.topic.shotAdministrated}",
								"${spring.application.event.outgoing.topic.reminder}",
										"${spring.application.event.outgoing.topic.restock}"})
@TestPropertySource(properties= {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
										"spring.kafka.admin.bootstrap-servers=${spring.embedded.kafka.brokers}"})
class CenterServiceApplicationTests {
	
	@Autowired
	private EmbeddedKafkaBroker embeddedBroker;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Value("${spring.application.event.outgoing.topic.restock}")
	private String restockTopic;
	
	private Consumer<Long, String> consumer;
	
	private static final String VACCINE_NAME = "vaccineTypeOne";
	private static final String CENTER_NAME = "center-1";
	
	@BeforeEach
	void setup() {
		Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("integrationGroup1", "true", embeddedBroker));
		consumer = new DefaultKafkaConsumerFactory<>(configs, new LongDeserializer(), new StringDeserializer()).createConsumer();
		embeddedBroker.consumeFromAllEmbeddedTopics(consumer);
	}
	
	@AfterEach
	void tearDown() {
		consumer.close();
	}
	
	@Test
	void createDuplicateCenter() {
		Center center = createCenterObject(CENTER_NAME);
		

		HttpEntity<Center> request = new HttpEntity<>(center, createHttpHeaders());
		ResponseEntity<Center> responseEntity = restTemplate.exchange(ADD_CENTER_URL, HttpMethod.POST,
																					request, Center.class);
		responseEntity = restTemplate.exchange(ADD_CENTER_URL, HttpMethod.POST, request, Center.class);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode(), "failed detected duplicate center name/");					
	}

	@Test
	void centerApi() {
		Center center = createCenterObject(CENTER_NAME);
		
		HttpEntity<Center> request = new HttpEntity<>(center, createHttpHeaders());
		ResponseEntity<Center> responseEntity = restTemplate.exchange(ADD_CENTER_URL, HttpMethod.POST,
																					request, Center.class);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "failed add center");

		long assignedCenterId = responseEntity.getBody().getId();
		assertTrue(responseEntity.getBody().getId() > 0, "invalid center id");

		assertEquals(CENTER_NAME, responseEntity.getBody().getName(), "center object content changed");

		ResponseEntity<Center[]> responseEntity2 = restTemplate.getForEntity(LIST_CENTER_URL, Center[].class);
		assertTrue(responseEntity2.getBody().length == 1, "expected only one center");
		assertEquals(assignedCenterId, responseEntity2.getBody()[0].getId(), "mismatch center id");
	}
	
	
	 
	@Test
	void vaccineApi() {
		Vaccine vaccine = createVaccineObject(VACCINE_NAME);

		HttpEntity<Vaccine> request = new HttpEntity<>(vaccine, createHttpHeaders());
		ResponseEntity<Vaccine> responseEntity = restTemplate.exchange(ADD_VACCINE_URL, HttpMethod.POST, 
																					request, Vaccine.class);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "failed add vaccine");
		
		long assignedVaccineId = responseEntity.getBody().getId();
		assertTrue(responseEntity.getBody().getId() > 0, "invalid vaccine id");

		assertEquals(VACCINE_NAME, responseEntity.getBody().getName(), "vaccine object content changed");

		ResponseEntity<Vaccine[]> responseEntity2 = restTemplate.getForEntity(LIST_VACCINE_URL, Vaccine[].class);
		assertTrue(responseEntity2.getBody().length == 1, "expected only one vaccine");
		assertEquals(assignedVaccineId, responseEntity2.getBody()[0].getId(), "mismatch center id");
	}

	@Test
	@Timeout(10)
	void addInventory() throws ParseException, JsonProcessingException {
		addVaccineAndCenterIfAbsent();

		ObjectMapper objectMapper = new ObjectMapper();
		
		Shipment shipment = createShipmentObject();

		HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(shipment), createHttpHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange(RESTOCK_URL, HttpMethod.POST,
																					request, String.class);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "failed to restock vaccine");
		Inventory inventory = objectMapper.readValue(responseEntity.getBody(), Inventory.class);

		assertTrue(inventory.getId() != 0, "failed to take shipment");

		// verify restock Kafka message to listener.
		ConsumerRecord<Long, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, restockTopic);
		assertTrue(inventory.getId() == consumerRecord.key(), "mismatched message key");
		
		Map<String, Object> msgMap = KafkaUtil.payloadToMap(consumerRecord.value());
		assertTrue(shipment.getVaccineId() == (msgMap.get(VACCINE_ID_ATTR) == null ? -1
														: Long.parseLong(msgMap.get(VACCINE_ID_ATTR).toString())), "mismatch vaccine id");
		assertTrue(shipment.getLot().equals(msgMap.get(LOT_ATTR)), "mismatch lot");
		assertTrue(shipment.getLotSize() == (msgMap.get(LOT_SIZE_ATTR) == null ? -1 : (int) msgMap.get(LOT_SIZE_ATTR)),
																										"mismatch lot size");
		assertTrue((msgMap.get(CENTER_ID_ATTR) == null ? -1 : (int) msgMap.get(CENTER_ID_ATTR)) > 0,
																							"missing center id");
	}

	@Test
	@Timeout(10)
	void addInventoryWithInvalidVaccineId() throws ParseException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		Shipment shipment = createShipmentObject();
		
		HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(shipment), createHttpHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange(RESTOCK_URL, HttpMethod.POST,
																						request, String.class);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode(), "failed to restock vaccine");

		// verify no Kafka message being sent.
		Exception exception = org.junit.jupiter.api.Assertions.assertThrows(java.lang.IllegalStateException.class,
																				() -> {
																					KafkaTestUtils.getSingleRecord(consumer, restockTopic, 3000);
																				});
		org.junit.jupiter.api.Assertions.assertEquals("No records found for topic", exception.getMessage());
	}

	
//     @Test
//     void shotAdministrated() {
//
//     }
//
//     @Test
//     void publishReminder() {
//     }
//     

	private void addVaccineAndCenterIfAbsent() {
		ResponseEntity<Vaccine[]> responseEntity = restTemplate.getForEntity(LIST_VACCINE_URL, Vaccine[].class);
		if (responseEntity.getBody().length == 0) {
			Vaccine vaccine = createVaccineObject(VACCINE_NAME);

			HttpEntity<Vaccine> request = new HttpEntity<>(vaccine, createHttpHeaders());
			ResponseEntity<Vaccine> vaccineResponseEntity = restTemplate.exchange(ADD_VACCINE_URL, HttpMethod.POST,
					request, Vaccine.class);
			assertEquals(HttpStatus.OK, vaccineResponseEntity.getStatusCode(), "failed add vaccine");
		}

		ResponseEntity<Center[]> responseEntity2 = restTemplate.getForEntity(LIST_CENTER_URL, Center[].class);
		if (responseEntity2.getBody().length == 0) {
			Center center = createCenterObject(CENTER_NAME);

			HttpEntity<Center> request = new HttpEntity<>(center, createHttpHeaders());
			ResponseEntity<Center> centerResponseEntity = restTemplate.exchange(ADD_CENTER_URL, HttpMethod.POST,
					request, Center.class);
			assertEquals(HttpStatus.OK, centerResponseEntity.getStatusCode(), "failed add center");
		}
	}

	private static HttpHeaders createHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	private static Vaccine createVaccineObject(final String vaccineName) {
		return Vaccine.builder().name(vaccineName)
								.provider("vaccine manfacturer 1")
								.contact("234-567-8901")
								.build();
	}

	private static Center createCenterObject(final String centerName) {
		return Center.builder().name(centerName)
								.phone("123-456-7890")
								.manager("center manager1")
								.address("101 First St")
								.build();
	}
	
	private static Shipment createShipmentObject() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		return Shipment.builder().expiration(sdf.parse("2030-12-30"))
									.lot("lot 1")
									.lotSize(1000)
									.vaccineId(1)
									.build();
	}
}
