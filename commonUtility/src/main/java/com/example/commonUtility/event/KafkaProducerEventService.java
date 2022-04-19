package com.example.commonUtility.event;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaProducerEventService<K> {
	
	@Autowired
	KafkaTemplate<K, String> kafkaTemplate;
	
	public boolean sendEvent(final String topic, final K key, final String payload) {
		ProducerRecord<K, String> producerRecord = new ProducerRecord<K, String>(topic, null, key, payload, null);
		
		ListenableFuture<SendResult<K, String>> future = kafkaTemplate.send(producerRecord);
		
		future.addCallback(new ListenableFutureCallback<SendResult<K, String>>() {
			public void onFailure(Throwable ex) {			
				handleFailure(key, payload, ex);
			}
			
			public void onSuccess(SendResult<K, String> result) {
				handleSuccess(key, payload, result);
			}
		});
		return true;
	}
	
	public void handleFailure(final K key, final String payload, final Throwable ex) {
		log.error("Error sending message.  Exception: {}", ex.getMessage());
		try {
			throw ex;
		} catch(Throwable t) {
			log.error("Error on passing exception up in onFailure: {}", t.getMessage());
		}
	}
	
	public void handleSuccess(final K key, final String payload, final SendResult<K, String> result) {
		log.info("Message sent successfully for key: {}, topic: {}, partition: {}, payload: {}",
					key, result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), payload);
	}
}
