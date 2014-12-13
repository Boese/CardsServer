package com.cards.games;

import java.util.UUID;

import com.cards.message.PlayerResponse;
import com.cards.server.GameManager;
import com.cards.server.User;

public abstract class Game {
	private final String game_id = UUID.randomUUID().toString();
	
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
	public abstract void addPlayer(User user);
	
	// Must implement player removed()
	public abstract void removePlayer(User user);
	
	// Must implement number of players
	public abstract int numPlayers();
	
	// Notify Game is over
	public void gameOver() {
		GameManager.getInstance().removeGame(game_id);
	}
	
	public void removeGame() {
		GameManager.getInstance().removeGame(game_id);
	}
	
	// Get Game ID
	public String getGameId() {
		return game_id;
	}
}
