package com.cards.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageTransformer {
	private ObjectMapper mapper;
	
	public MessageTransformer() {
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
	}
	
	public Object getMessage(String json, Class<?> clazz) {
		Object object = null;
		try {
			object = mapper.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
	
	public String writeMessage(Object json) {
		String message = null;
		try {
			message = mapper.writeValueAsString(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
}
