package com.cards.games;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.cards.server.GameManager;
import com.cards.server.User;

public abstract class Game {
	private static final String game_id = UUID.randomUUID().toString();
	private static Map<String,User> players = new HashMap<String,User>();
	private static int REQUEST_TIMEOUT = 30*1000;
	
	// Must implement Play (Move from player)
	public abstract void Play(JSONObject response);
	
	// Must implement addPlayer(String player_id)
	public abstract void addPlayer(String id);
	
	// Must implement removePlayer(String player_id)
	public abstract void removePlayer(String id);
	
	// Must implement isGameFull()
	public abstract Boolean isGameFull();
	
	// Must implement getGameType()
	public abstract String getGameType();
	
	// Must implement isCurrentTurn(String player_id)
	public abstract Boolean isCurrentTurn(String id);
	
	// Broadcast updated game
	public void update(String id, String gameobject) {
		User user = players.get(id);
		user.sendMessage(gameobject);
	}
		
	// Request Move from player
	public void sendRequest(String id,String request) {
		User user = players.get(id);
		user.scheduleTimeoutEvent(REQUEST_TIMEOUT);
		user.sendMessage(request);
	}
	
	// Notify Game is over
	public void gameOver() {
		GameManager.getInstance().removeGame(Game.game_id);
	}
	
	// Get Game ID
	public String getGameId() {
		return Game.game_id;
	}
	
	// Add User
	public void addUser(User user) {
		players.put(user.getSession_id(), user);
		addPlayer(user.getSession_id());
	}
	
	// Remove User
	public void removeUser(User user) {
		String key = getKeyFromUser(user);
		players.remove(key);
		removePlayer(key);
	}
	
	// Set Request Timeout
	public void setRequestTimeout(int timeout) {
		Game.REQUEST_TIMEOUT = timeout;
	}
	
	private String getKeyFromUser(User user) {
		String key = null;
		for (Map.Entry<String,User> player : players.entrySet()) {
			if(player.getValue().equals(user)) {
				key = player.getKey();
				break;
			}
		}
		return key;
	}
}
