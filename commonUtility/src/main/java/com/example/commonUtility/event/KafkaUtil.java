package com.example.commonUtility.event;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaUtil {
	private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER_POOL = new ThreadLocal<ObjectMapper>() {
	    @Override
	    protected ObjectMapper initialValue() {
	        ObjectMapper objectMapper = new ObjectMapper();
	        return objectMapper;
	    }
	};
	
	public static <K,V> Map<K,V> payloadToMap(final String payload) {
		Map<K, V> map = new HashMap<K, V>();
		ObjectMapper mapper = OBJECT_MAPPER_POOL.get();
		try {
			map = mapper.readValue(payload, new TypeReference<Map<K, V>>() {});
		} catch (JsonGenerationException e) {
			e.printStackTrace();
			throw new RuntimeException("ObjectMapper failed to read JSON from consumerRecord.");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new RuntimeException("ObjectMapper failed to construct map from JSON.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("ObjectMapper failed to read JSON.");
		}
		
		return map;
	}
	
	public static <K, V> String mapToPayload(final Map<K, V> msgMap) {
		ObjectMapper mapper = OBJECT_MAPPER_POOL.get();
		String payload = null;
		try {
			// Convert Map to JSON
			payload = mapper.writeValueAsString(msgMap);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
			throw new RuntimeException("ObjectMapper failed to convert to JSON.");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new RuntimeException("ObjectMapper failed to convert map to JSON.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("ObjectMapper failed to generate JSON string.");
		}
		return payload;
	}
}
