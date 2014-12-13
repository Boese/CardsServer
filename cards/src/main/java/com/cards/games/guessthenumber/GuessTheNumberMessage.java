package com.cards.games.guessthenumber;

import java.util.Map;

import com.cards.games.GameMessage;
import com.cards.games.pinochle.enums.Position;
import com.cards.games.pinochle.enums.Request;
import com.cards.message.PlayerResponse;

public class GuessTheNumberMessage implements GameMessage {
	String currentMessage;
	PlayerResponse lastMove;
	Position currentTurn;
	Request currentRequest;
	Map<Position,String> players;
	
	public GuessTheNumberMessage() {}

	public String getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}

	public PlayerResponse getLastMove() {
		return lastMove;
	}

	public void setLastMove(PlayerResponse lastMove) {
		this.lastMove = lastMove;
	}

	public Position getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(Position currentTurn) {
		this.currentTurn = currentTurn;
	}

	public Request getCurrentRequest() {
		return currentRequest;
	}

	public void setCurrentRequest(Request currentRequest) {
		this.currentRequest = currentRequest;
	}

	public Map<Position, String> getPlayers() {
		return players;
	}

	public void setPlayers(Map<Position, String> players) {
		this.players = players;
	}
}
