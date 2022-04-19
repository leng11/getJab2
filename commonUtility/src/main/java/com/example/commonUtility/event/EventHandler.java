package com.example.commonUtility.event;

import java.util.Map;

public interface EventHandler {
	public void onEvent(final Map<String, Object> headers, final String payload);
}
