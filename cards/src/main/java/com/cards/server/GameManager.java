package com.cards.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.cards.games.Game;
import com.cards.games.guessthenumber.GuessTheNumber;
import com.cards.games.pinochle.Pinochle;
import com.cards.message.GameInfo;
import com.cards.message.PlayerResponse;
import com.cards.message.ResponsePacket;
import com.cards.utils.MessageTransformer;

public class GameManager {
	private GameManager() {}
	
	private static final GameManager INSTANCE = new GameManager();
	private static Map<String,Game> games;
	private static List<String> game_types;
	private MessageTransformer msgTransformer;
	
	// **Initialize**
	public void init() {
		games = new HashMap<String, Game>();
		game_types = new ArrayList<String>();
		game_types.add("pinochle");
		game_types.add("GuessTheNumber");
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
			sendLobbyInfo();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// **Create/Join Games**
	public void createGame(User user, String gametype) {
		removeUserFromGame(user);
		Game game = null;
		switch(gametype) {
		case "pinochle": game = new Pinochle(); break;
		case "GuessTheNumber" : game = new GuessTheNumber(); break;
		}
		
		if(game != null) {
			games.put(game.getGameId(), game);
			game.addPlayer(user);
			user.setGame_id(game.getGameId());
			System.out.println("User " + user.getUser_name() + " joined new game of " + game.getGameType());
			System.out.println(game.getGameType() + " game added. Number of games : " + games.size());
			sendLobbyInfo();
		}
	}
	
	public void joinRandomGame(User user, String gametype){
		removeUserFromGame(user);
		for (Game game : games.values()) {
			if(game.getGameType().equalsIgnoreCase(gametype)) {
				if(!game.isGameFull()) {
					game.addPlayer(user);
					user.setGame_id(game.getGameId());
					System.out.println("User " + user.getUser_name() + " joined random game of " + game.getGameType());
					sendLobbyInfo();
					return;
				}
			}
		}
		
		// No games available, create new one
		createGame(user, gametype);
	}
	
	public void joinSelectedGame(User user, String game_id) {
		removeUserFromGame(user);
		Game game = games.get(game_id);
		if(!game.isGameFull()) {
			game.addPlayer(user);
			user.setGame_id(game_id);
			System.out.println("User " + user.getUser_name() + " joined current game of " + game.getGameType());
			sendLobbyInfo();
		}
		else
			user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("game is full")));
	}
	
	public void removeUserFromGame(User user) {
		try {
			games.get(user.getGame_id()).removePlayer(user);
			System.out.println("User " + user.getPort() + " quit game of " + games.get(user.getGame_id()).getGameType());
			sendLobbyInfo();
		} catch(Exception e) {}
	}
	
	public void play(User user, PlayerResponse response) {
		try {
			if(games.get(user.getGame_id()).isCurrentTurn(user.getSession_id())) {
				games.get(user.getGame_id()).Play(response);
			}
		} catch(Exception e) {}
	}
	
	public void sendLobbyInfo() {
		List<GameInfo> gameinfos = new ArrayList<GameInfo>();
		for (Game g : games.values()) {
			gameinfos.add(new GameInfo(g.numPlayers(),g.getGameId(),g.getGameType()));
		}
		UserManager.getInstance().broadcast(msgTransformer.writeMessage(new ResponsePacket()
			.setResponse("lobby").setGame_types(game_types).setGames(gameinfos)));
	}
}
