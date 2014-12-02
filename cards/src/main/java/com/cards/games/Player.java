package com.cards.games;

public abstract class Player {
	private String id;
	private GameMessage game_message;
	
	public Player(String id) {
		this.id = id;
	}
	
	public void setGame_message(GameMessage game_message) {
		this.game_message = game_message;
	}

	public GameMessage getGame_message() {
		return game_message;
	}

	public String getId() {
		return id;
	}
}
