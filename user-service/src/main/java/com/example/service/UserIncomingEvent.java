package com.example.service;

import com.example.commonUtility.event.EventHandler;
import com.example.commonUtility.event.KafkaConsumerEventService;
import com.example.commonUtility.event.KafkaUtil;
import com.example.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserIncomingEvent implements EventHandler {
    // shotAdministrated message content keys.
    public static final String INVENTORY_ID_ATTR = "inventoryId";
    public static final String VACCINE_ID_ATTR = "vaccineId";
    public static final String LOT_ATTR = "lot";
    public static final String USER_ID_ATTR = "userId";

    @Autowired
    private UserDao dao;


    @Override
    public void onEvent(final Map<String, Object> headers, final String payload) {
        log.info("started processing msg from topic: {}", headers.get(KafkaConsumerEventService.TOPIC_ATTR));


    }
}


