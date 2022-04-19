package com.example.commonUtility.event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(
		  value="spring.kafka.consumer.topic.name", 
		  matchIfMissing = false)
@Slf4j
public class KafkaConsumerEventService {
	public static final String TOPIC_ATTR = "topic";
	
	// topic -> EventHandler
	private static final Map<String, EventHandler> MSG_HANDLER_REGISTRY = new ConcurrentHashMap<>();
	
	
	public static EventHandler addHandler(final String topic, final EventHandler handler) {
		log.info("registered {} for topic: {}", handler, topic);
		return MSG_HANDLER_REGISTRY.put(topic, handler);
	}
	
	public static EventHandler removeHandler(final String topic) {
		log.info("un-registered {} for topic: {}", topic);
		return MSG_HANDLER_REGISTRY.remove(topic);
	}
	
	@KafkaListener(topics="#{'${spring.kafka.consumer.topic.name}'.split(',')}")
	public void onMsg(ConsumerRecord<Integer, String> consumerRecord) {
		Map<String, Object> headers = new HashMap<>();
		headers.put(TOPIC_ATTR, consumerRecord.topic());
		
		EventHandler handler = MSG_HANDLER_REGISTRY.get(consumerRecord.topic());
		if(null != handler) {
			log.info("dispatch msg from topic: {} key: {}, partition: {} to {}",
										consumerRecord.topic(), consumerRecord.key(),
											consumerRecord.partition(), handler.getClass());
			
			handler.onEvent(headers, consumerRecord.value());
		} else {
			log.warn("topic: {} does not have event handler.", consumerRecord.topic());
		}
	}
}
