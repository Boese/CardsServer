package com.cards.server;

import java.util.List;

public class GameManager {
	private GameManager() {}
	
	private static final GameManager INSTANCE = new GameManager();
	
	public static GameManager getInstance() {
		return INSTANCE;
	}
	
	// **Create/Join Games**
	public void createGame(User user, String gametype) {
		/*
		 * Start a new game instance of game type with user
		 * gamenum = x
		 * Broadcast
		 */
	}
	
	public void joinGame(User user, String gametype){
		/*
		 * Put user in a random game of game type
		 * Broadcast
		 */
	}
	
	public void joinGame(User user, int gamenum) {
		/*
		 * Put user in a selected game
		 * Broadcast
		 */
	}
	
	public void quitGame(User user) {
		/*
		 * Quit user from game
		 * Broadcast
		 */
	}
	
	public void play(User user) {
		/*
		 * Play move from player
		 * Broadcast
		 */
	}
}
