package com.cards.message;

import java.util.List;

import com.cards.games.GameMessage;

public class ResponsePacket {
	// REQUIRED
	private String response;
	
	// OPTIONAL
	private String message;
	private List<String> game_types;
	private List<GameInfo> games;
	private GameMessage game_message;
	
	public ResponsePacket() {}

	public String getResponse() {
		return response;
	}

	public ResponsePacket setResponse(String response) {
		this.response = response;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public ResponsePacket setMessage(String message) {
		this.message = message;
		return this;
	}

	public List<String> getGame_types() {
		return game_types;
	}

	public ResponsePacket setGame_types(List<String> game_types) {
		this.game_types = game_types;
		return this;
	}

	public List<GameInfo> getGames() {
		return games;
	}

	public ResponsePacket setGames(List<GameInfo> games) {
		this.games = games;
		return this;
	}

	public GameMessage getGame_message() {
		return game_message;
	}

	public ResponsePacket setGame_message(GameMessage game_message) {
		this.game_message = game_message;
		return this;
	}
}
