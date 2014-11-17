package com.cards.server.message;

/*
	Login {
	 * request: 'login',
	 * user_name: 'user_name',
	 * email: 'email'
	 * hash_password: 'hash_password'
	 * }
*/
public class LoginPacket {
	private String request;
	private String user_name;
	private String email;
	private String hash_password;
	
	LoginPacket(){}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHash_password() {
		return hash_password;
	}

	public void setHash_password(String hash_password) {
		this.hash_password = hash_password;
	}
}
