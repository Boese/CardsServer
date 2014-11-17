package com.cards.message;

public class ResponsePacket {
	private String response;
	private String message;
	
	public ResponsePacket(String response,  String message) {
		setResponse(response);
		setMessage(message);
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
