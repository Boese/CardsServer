package com.cards.games;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;

import com.cards.message.PlayerResponse;
import com.cards.server.GameManager;
import com.cards.server.User;
import com.cards.utils.MessageTransformer;

public abstract class Game {
	private final String game_id = UUID.randomUUID().toString();
	private final MessageTransformer msgTransformer = new MessageTransformer();
	private List<User> users = new ArrayList<User>();
	private int REQUEST_TIMEOUT = 30*1000;
	
	//** Must Implement **//
	
	// Must implement Play (Move from player)
	public abstract void Play(PlayerResponse response);
	
	// Must implement isGameFull()
	public abstract Boolean isGameFull();
	
	// Must implement getGameType()
	public abstract String getGameType();
	
	// Must implement isCurrentTurn(String player_id)
	public abstract Boolean isCurrentTurn(String id);
	
	// Must implement player added()
	public abstract void addPlayer(String id);
	
	// Must implement player removed()
	public abstract void removePlayer(String id);
	
	// Must implement getGameMessage(String id)
	public abstract GameMessage getGameMessage(String id);
	
	//** Implemented **//
	
	// Broadcast updated game
	public void update() {
		for(User user : users) {
			user.sendMessage(msgTransformer.writeMessage(getGameMessage(user.getSession_id())));
		}
	}
	
	// Notify Game is over
	public void gameOver() {
		GameManager.getInstance().removeGame(game_id);
	}
	
	// Get Game ID
	public String getGameId() {
		return game_id;
	}
	
	// Add User
	public void addUser(User user) {
		users.add(user);
		addPlayer(user.getSession_id());
	}
	
	// Remove User
	public void removeUser(User user) {
		users.remove(user);
		removePlayer(user.getSession_id());
		if(users.size() == 0)
			gameOver();
	}
	
	// Get User from Player id
	public User getUser(String id) {
		for (User user : users) {
			if(user.getSession_id().equals(id))
				return user;
		}
		return null;
	}
	
	public int getNumPlayers() {
		return users.size();
	}
}
