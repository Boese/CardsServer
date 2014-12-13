package com.cards.games.pinochle.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cards.games.GameMessage;
import com.cards.games.pinochle.enums.Card;
import com.cards.games.pinochle.enums.Position;
import com.cards.games.pinochle.enums.Request;
import com.cards.message.PlayerResponse;


public class PinochleMessage implements GameMessage {
	int team1Score;
	int team2Score;
	Position currentTurn;
	Request currentRequest;
	String currentState;
	String currentMessage;
	List<Card> cards;
	Map<Position,String> players;
	Boolean myTurn;
	PlayerResponse lastMove;
	
	public PinochleMessage() {}

	public int getTeam1Score() {
		return team1Score;
	}

	public void setTeam1Score(int team1Score) {
		this.team1Score = team1Score;
	}

	public int getTeam2Score() {
		return team2Score;
	}

	public void setTeam2Score(int team2Score) {
		this.team2Score = team2Score;
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

	public String getCurrentState() {
		return currentState;
	}

	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}

	public String getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		List<Card> temp = new ArrayList<Card>(cards);
		this.cards = temp;
	}

	public Map<Position, String> getPlayers() {
		return players;
	}

	public void setPlayers(Map<Position, String> players) {
		this.players = players;
	}

	public Boolean getMyTurn() {
		return myTurn;
	}

	public void setMyTurn(Boolean myTurn) {
		this.myTurn = myTurn;
	}

	public PlayerResponse getLastMove() {
		return lastMove;
	}

	public void setLastMove(PlayerResponse lastMove) {
		this.lastMove = lastMove;
	}
}
