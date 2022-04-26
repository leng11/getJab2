package unit;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import com.example.centerService.controller.Controller;
import com.example.centerService.model.Center;
import com.example.centerService.model.Inventory;
import com.example.centerService.model.Vaccine;
import com.example.centerService.model.clientFacing.Shipment;
import com.example.centerService.repository.CenterRepo;
import com.example.centerService.repository.InventoryRepo;
import com.example.centerService.repository.VaccineRepo;
import com.example.commonUtility.event.KafkaProducerEventService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, classes=com.example.centerService.CenterServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles({ "test" })
@Slf4j
public class CenterServiceApplicationUnitTest {
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	private KafkaProducerEventService<Long> eventProducer;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private static final String CENTER_NAME = "Ceneter-1";
	private static final String VACCINE_NAME = "Vaccine-1";
//
//  The following is MySql specific error message.
//	private static final String DUPLICATE_SQL_EXCEPTION_TEXT = String.join(" ", "Request processing failed; nested exception is org.springframework.dao.DataIntegrityViolationException:",
//																					"could not execute statement; SQL [n/a];",
//																					"constraint [center.UK_9ntt4q0n3w4lywq1k9xveiyo8];",
//																					"nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement");
	
	@Autowired
	private CenterRepo  centerRepo;
	
	@Autowired
	private VaccineRepo vaccineRepo;
	
	@Autowired
	private InventoryRepo inventoryRepo;
	
	@BeforeEach
	void setup() {
		inventoryRepo.deleteAll();
		centerRepo.deleteAll();
		vaccineRepo.deleteAll();	
	}
	
	@Test
	void addCenter() throws Exception {
		Center center = createCeneterObject(CENTER_NAME);
		String json = objectMapper.writeValueAsString(center);
		
		mockMvc.perform(post(Controller.ADD_CENTER_URL)
							.content(json)
							.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	void addDuplicateCenter() throws Exception {
		Center center = createCeneterObject(CENTER_NAME);
		String json = objectMapper.writeValueAsString(center);
		mockMvc.perform(post(Controller.ADD_CENTER_URL)
							.content(json)
							.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().is2xxSuccessful());
	
		Exception exception = org.junit.jupiter.api.Assertions.assertThrows(NestedServletException.class,
				() -> {
					mockMvc.perform(post(Controller.ADD_CENTER_URL)
							.content(json)
							.contentType(MediaType.APPLICATION_JSON));
				});
//		org.junit.jupiter.api.Assertions.assertEquals(DUPLICATE_SQL_EXCEPTION_TEXT, exception.getMessage());
	}
	
	@Test
	void addNullNameCenter() throws Exception {
		Center center = createCeneterObject(null);
		String json = objectMapper.writeValueAsString(center);
		mockMvc.perform(post(Controller.ADD_CENTER_URL)
									.content(json)
									.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is4xxClientError());
	}
	
	@Test
	void deleteCenterByName() throws Exception {
		String encodedDeleteUrl = Controller.DELETE_CENTER_BY_NAME_URL.replace("{name}", CENTER_NAME);
		
		// delete non-existed entity.
		mockMvc.perform(delete(encodedDeleteUrl))
				.andExpect(status().is2xxSuccessful());
		
		addACenter(CENTER_NAME);
		
		// delete existed entity.
		mockMvc.perform(delete(encodedDeleteUrl))
				.andExpect(status().is2xxSuccessful());
	}
	
	
	@Test
	void listCenter() throws Exception {
		// List with empty table.
		mockMvc.perform(get(Controller.LIST_CENTER_URL))
				.andExpect(status().is2xxSuccessful());
		
		Set<String> names = new HashSet<>();
		Center center = addACenter(CENTER_NAME);
		names.add(center.getName());
		center = addACenter(CENTER_NAME+"2");
		names.add(center.getName());
		
		// List of 2 entity return.
		Center[] centers = objectMapper.readValue(mockMvc.perform(get(Controller.LIST_CENTER_URL))
											.andReturn().getResponse().getContentAsString(), Center[].class);
		assertTrue(centers.length == 2, "incorrect number of center returned");
		
		Arrays.stream(centers).forEach(c->{ assertTrue(names.contains(c.getName()), "unknown ceneter - " +c.getName()); });
	}
	
	
	@Test
	void restock() throws Exception {
		Center addedCenter = addACenter(CENTER_NAME);
	
		Vaccine addedVaccine = addAVaccine(VACCINE_NAME);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

		Shipment shipment = createAShipment(addedVaccine.getId(), sdf.parse("3030-12-30"));
										
		String json = objectMapper.writeValueAsString(shipment);
		
		Mockito.when(eventProducer.sendEvent(isA(String.class), isA(Long.class), isA(String.class))).thenReturn(true);
		Inventory inventory = objectMapper.readValue(mockMvc.perform(post(Controller.RESTOCK_URL)
																.content(json)
																.contentType(MediaType.APPLICATION_JSON))
										.andExpect(status().is2xxSuccessful())
										.andReturn().getResponse().getContentAsString(), Inventory.class);
		log.info("Inventory: {}", objectMapper.writeValueAsString(inventory));
		assertTrue(addedCenter.getId()==inventory.getCenter().getId(), "invalid center id");
	}
	
	
	@Test
	void publishReminder() throws Exception {
		// no reminder due to no available vaccine.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		String status = mockMvc.perform(put(Controller.PUBLISH_REMINDER_URL)
									.queryParam("date", sdf.format(new Date())))
									.andExpectAll(status().is2xxSuccessful())
									.andReturn().getResponse().getContentAsString();
		assertTrue(status.equalsIgnoreCase("false"), "invalid publish reminder event");
				
		// restock vaacine.
		addACenter(CENTER_NAME);
		
		Vaccine addedVaccine = addAVaccine(VACCINE_NAME);

		Shipment shipment = createAShipment(addedVaccine.getId(), sdf.parse("3030-12-30"));
										
		String json = objectMapper.writeValueAsString(shipment);
		
		Mockito.when(eventProducer.sendEvent(isA(String.class), isA(Long.class), isA(String.class))).thenReturn(true);
		mockMvc.perform(post(Controller.RESTOCK_URL)
										.content(json)
										.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful());
		
		status = mockMvc.perform(put(Controller.PUBLISH_REMINDER_URL)
										.queryParam("date", sdf.format(new Date())))
						.andExpectAll(status().is2xxSuccessful())
						.andReturn().getResponse().getContentAsString();
		assertTrue(status.equalsIgnoreCase("true"), "missing publish reminder event");
	}
	
	private static Center createCeneterObject(final String name) {
		return Center.builder().name(name)
								.manager("manager-1")
								.address("1 First Street")
								.phone("123-456-7890")
								.build();
	}
	
	private static Vaccine createVaccineObject(final String vaccineName) {
		return Vaccine.builder().name(vaccineName)
								.provider("vaccine manfacturer 1")
								.contact("234-567-8901")
								.build();
	}
	
	
	private Center addACenter(final String name) throws Exception {
		Center center = createCeneterObject(name);
		String json = objectMapper.writeValueAsString(center);
		
		return objectMapper.readValue(mockMvc.perform(post(Controller.ADD_CENTER_URL)
														.content(json)
														.contentType(MediaType.APPLICATION_JSON))
											.andReturn().getResponse().getContentAsString(), Center.class);
	}

	private Vaccine addAVaccine(final String name) throws Exception {
		Vaccine vaccine = createVaccineObject(name);
		String json = objectMapper.writeValueAsString(vaccine);
		return objectMapper.readValue(mockMvc.perform(post(Controller.ADD_VACCINE_URL)
														.content(json)
														.contentType(MediaType.APPLICATION_JSON))
											.andReturn().getResponse().getContentAsString(), Vaccine.class);
	}
	
	private Shipment createAShipment(long vaccineId, final Date expiration) {
		return Shipment.builder()
				.lot("lot info")
				.lotSize(1000)
				.vaccineId(vaccineId)
				.expiration(expiration)
				.build();
	}

}
