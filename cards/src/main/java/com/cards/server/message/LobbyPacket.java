package com.cards.server.message;

/*
 * Join Game {
 * 	request: 'join_game',
 * 	session_id: 'session_id',
 * 	game_type: 'game_type',
 * 	game_id: 'game_id'
 * 	move: 'move'
 * }
 */
public class LobbyPacket {
	private String request;
	private String session_id;
	private String game_type;
	private Integer game_id;
	private Object move;
	
	LobbyPacket() {}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getGame_type() {
		return game_type;
	}

	public void setGame_type(String game_type) {
		this.game_type = game_type;
	}

	public Integer getGame_id() {
		return game_id;
	}

	public void setGame_id(Integer game_id) {
		this.game_id = game_id;
	}

	public Object getMove() {
		return move;
	}

	public void setMove(Object move) {
		this.move = move;
	}
}
