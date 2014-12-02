package com.cards.message;



/*
	Login {
	 * request: 'login',
	 * user_name: 'user_name',
	 * email: 'email'
	 * hash_password: 'hash_password'
	 * }
*/
public class RequestPacket {
	// REQUIRED
	private String request;
	
	// OPTIONAL
	private String user_name;
	private String email;
	private String hash_password;
	private String session_id;
	private String game_type;
	private String game_id;
	private PlayerResponse move;
	
	RequestPacket(){}

	public String getRequest() {
		return request;
	}

	public RequestPacket setRequest(String request) {
		this.request = request;
		return this;
	}

	public String getUser_name() {
		return user_name;
	}

	public RequestPacket setUser_name(String user_name) {
		this.user_name = user_name;
		return this;
	}
	
	public String getEmail() {
		return email;
	}

	public RequestPacket setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getHash_password() {
		return hash_password;
	}

	public RequestPacket setHash_password(String hash_password) {
		this.hash_password = hash_password;
		return this;
	}

	public String getSession_id() {
		return session_id;
	}

	public RequestPacket setSession_id(String session_id) {
		this.session_id = session_id;
		return this;
	}

	public String getGame_type() {
		return game_type;
	}

	public RequestPacket setGame_type(String game_type) {
		this.game_type = game_type;
		return this;
	}

	public String getGame_id() {
		return game_id;
	}

	public RequestPacket setGame_id(String game_id) {
		this.game_id = game_id;
		return this;
	}

	public PlayerResponse getMove() {
		return move;
	}

	public void setMove(PlayerResponse move) {
		this.move = move;
	}
}
