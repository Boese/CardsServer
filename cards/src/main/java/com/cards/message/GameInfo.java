package com.cards.message;

public class GameInfo {
	private int num_players;
	private String game_id;
	private String game_type;
	
	public GameInfo() {}
	public GameInfo(int num_players, String game_id, String game_type) {
		this.num_players = num_players;
		this.game_id = game_id;
		this.game_type = game_type;
	}
	public int getNum_players() {
		return num_players;
	}
	public void setNum_players(int num_players) {
		this.num_players = num_players;
	}
	public String getGame_id() {
		return game_id;
	}
	public void setGame_id(String game_id) {
		this.game_id = game_id;
	}
	public String getGame_type() {
		return game_type;
	}
	public void setGame_type(String game_type) {
		this.game_type = game_type;
	}
}
