package com.cards.games.pinochle.utils;

import java.util.List;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Card;
import com.cards.games.pinochle.enums.Position;
import com.cards.games.pinochle.enums.Request;
import com.cards.games.pinochle.player.Player;
import com.cards.games.pinochle.states.iPinochleState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PinochleMessage {
	int team1Score;
	int team2Score;
	Position currentTurn;
	Request currentRequest;
	iPinochleState currentState;
	String currentMessage;
	List<Card> cards;
	Position myPosition;
	Boolean myTurn;
	
	public PinochleMessage() {}
	
	public PinochleMessage(Pinochle pin) {
		team1Score = pin.getTeam1Score();
		team2Score = pin.getTeam2Score();
		currentTurn = pin.getCurrentTurn();
		currentRequest = pin.getCurrentRequest();
		currentState = pin.getCurrentState();
		currentMessage = pin.getCurrentMessage();
	}
	
	public String update(Player pl) {
		cards = pl.getCurrentCards();
		myPosition = pl.getPosition();
		if(myPosition == currentTurn)
			myTurn = true;
		else
			myTurn = false;
		
		ObjectMapper mapper = new ObjectMapper();
		String result = "";
		
		try {
			result = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

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
		this.cards = cards;
	}

	public Position getMyPosition() {
		return myPosition;
	}

	public void setMyPosition(Position myPosition) {
		this.myPosition = myPosition;
	}

	public Boolean getMyTurn() {
		return myTurn;
	}

	public void setMyTurn(Boolean myTurn) {
		this.myTurn = myTurn;
	}
}
