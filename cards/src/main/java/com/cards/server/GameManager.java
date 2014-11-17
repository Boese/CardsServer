package com.cards.server;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

import com.cards.games.Game;
import com.cards.games.pinochle.Pinochle;
import com.cards.message.ResponsePacket;
import com.cards.utils.MessageTransformer;

public class GameManager {
	private GameManager() {}
	
	private static final GameManager INSTANCE = new GameManager();
	private static Map<String,Game> games;
	private MessageTransformer msgTransformer;
	
	// **Initialize**
	public void init() {
		games = new HashMap<String, Game>();
		msgTransformer = new MessageTransformer();
		System.out.println("**Game Manager started**");
	}
	
	public static GameManager getInstance() {
		return INSTANCE;
	}
	
	public void removeGame(String key) {
		try {
			Game game = games.get(key);
			games.remove(key);
			System.out.println(game.getGameType() + " game removed. Number of games : " + games.size());
		} catch(Exception e) {
			e.printStackTrace();
		}
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
			games.put(game.getGameId(), game);
			game.addUser(user);
			user.setGame_id(game.getGameId());
			System.out.println("User " + user.getPort() + " joined new game of " + game.getGameType());
			System.out.println(game.getGameType() + " game added. Number of games : " + games.size());
		}
		else {
			UserManager.getInstance().removeUser(user);
		}
	}
	
	public void joinRandomGame(User user, String gametype){
		for (Game game : games.values()) {
			if(game.getGameType().equalsIgnoreCase(gametype)) {
				if(!game.isGameFull()) {
					game.addUser(user);
					user.setGame_id(game.getGameId());
					System.out.println("User " + user.getPort() + " joined random game of " + game.getGameType());
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
			System.out.println("User " + user.getPort() + " joined current game of " + game.getGameType());
		}
		else
			user.sendMessage(msgTransformer.writeMessage(new ResponsePacket("response", "Game is full")));
	}
	
	public void removeUserFromGame(User user) {
		try {
			System.out.println("User " + user.getPort() + " quit game of " + games.get(user.getGame_id()).getGameType());
			games.get(user.getGame_id()).removeUser(user);
		} catch(Exception e) {}
	}
	
	public void play(User user, JSONObject response) {
		try {
			if(games.get(user.getGame_id()).isCurrentTurn(user.getSession_id())) {
				games.get(user.getGame_id()).Play(response);
			}
			else {
				UserManager.getInstance().removeUser(user);
			}
		} catch(Exception e) {}
	}
}
