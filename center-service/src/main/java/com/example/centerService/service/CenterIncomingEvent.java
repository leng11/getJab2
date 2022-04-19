package com.example.centerService.service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.centerService.model.Inventory;
import com.example.centerService.repository.InventoryRepo;
import com.example.commonUtility.event.EventHandler;
import com.example.commonUtility.event.KafkaConsumerEventService;
import com.example.commonUtility.event.KafkaUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CenterIncomingEvent implements EventHandler {
	// shotAdministrated message content keys.
	public static final String INVENTORY_ID_ATTR = "inventoryId";
	public static final String VACCINE_ID_ATTR = "vaccineId";
	public static final String LOT_ATTR = "lot";
	public static final String USER_ID_ATTR = "userId";
	
	// vaccineInventory message content key.
	public static final String CENTER_ID_ATTR = "centerId";
	public static final String LOT_SIZE_ATTR = "lotSize";
	
	// publishReminder message content keys.
	public static final String REMINDER_DATE_ATTR = "reminderDate";
	public static final String AVAILABLE_SHOT_ATTR = "availableShot";
	
	@Autowired
	private InventoryRepo inventoryRepo;
	
	
	@Override
	public void onEvent(final Map<String, Object> headers, final String payload) {
		log.info("started processing msg from topic: {}", headers.get(KafkaConsumerEventService.TOPIC_ATTR));
		
		long inventoryId = -1;
		try {
			Map<String, Object> msgMap = KafkaUtil.payloadToMap(payload);
			Object value = msgMap.get(INVENTORY_ID_ATTR);
			if (null == value) {
				log.warn("incoming msg does not have {}, msgContent: {}. Skip processing...", INVENTORY_ID_ATTR,
						msgMap.entrySet().stream().map(entry -> entry.getKey() + '=' + entry.getValue())
								.collect(Collectors.joining(",\\n")));
				return;
			}

			if (!(value instanceof Integer)) {
				log.warn("incoming msg contains incorrect type for {}, passed in type: {}. Skip processing...",
						INVENTORY_ID_ATTR, value.getClass());
				return;
			}

			inventoryId = (Integer) value;
			Inventory anInventory = inventoryRepo.findById(inventoryId).orElseThrow();
			anInventory.setAvailable(anInventory.getAvailable() - 1);
			inventoryRepo.save(anInventory);
		} catch (NoSuchElementException nse) {
			log.error("invalid inventoryId: {}", inventoryId);
		} catch (RuntimeException re) {
			log.error("failed to convert msg content", re);
		}
		
		log.info("compeleted processing msg from topic: {}", headers.get(KafkaConsumerEventService.TOPIC_ATTR));
	}
}
