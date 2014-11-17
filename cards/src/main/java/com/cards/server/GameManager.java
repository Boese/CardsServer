package com.cards.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.cards.games.Game;
import com.cards.games.pinochle.Pinochle;
import com.cards.message.ResponsePacket;
import com.cards.utils.MessageTransformer;

public class GameManager {
	private static final GameManager INSTANCE = new GameManager();
	private static Map<String,Game> games;
	private MessageTransformer msgTransformer;
	
	private GameManager() {
		games = new HashMap<String, Game>();
		msgTransformer = new MessageTransformer();
	}
	
	public static GameManager getInstance() {
		return INSTANCE;
	}
	
	public void removeGame(String key) {
		games.remove(key);
	}
	
	// **Create/Join Games**
	public void createGame(User user, String gametype) {
		Game game = null;
		switch(gametype) {
		case "pinochle": game = new Pinochle();
		case "hearts" : 
		case "cribbage":
			// ..etc..
		}
		
		if(game != null) {
			String key = UUID.randomUUID().toString();
			games.put(key, game);
			game.addUser(user);
			user.setGame_id(key);
		}
	}
	
	public void joinRandomGame(User user, String gametype){
		for (Game game : games.values()) {
			if(game.getGameType().equalsIgnoreCase(gametype)) {
				if(!game.isGameFull()) {
					game.addUser(user);
					user.setGame_id(game.getGameId());
					return;
				}
			}
		}
		
		// No games available, create new one
		createGame(user, gametype);
	}
	
	public void joinSelectedGame(User user, String game_id) {
		Game game = games.get(game_id);
		if(!game.isGameFull()) {
			game.addUser(user);
			user.setGame_id(game_id);
		}
		else
			user.sendMessage(msgTransformer.writeMessage(new ResponsePacket("response", "Game is full")));
	}
	
	public void removeUserFromGame(User user) {
		try {
			games.get(user.getGame_id()).removeUser(user);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play(User user, JSONObject response) {
		try {
			if(games.get(user.getGame_id()).isCurrentTurn(user.getSession_id())) {
				games.get(user.getGame_id()).Play(response);
			}
			else {
				this.removeUserFromGame(user);
				UserManager.getInstance().removeUser(user);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
