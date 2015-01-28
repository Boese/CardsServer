package com.cards.games.pinochle.player;


import java.util.ArrayList;
import java.util.List;

import com.cards.games.pinochle.enums.Card;
import com.cards.games.pinochle.enums.CardComparator;
import com.cards.games.pinochle.enums.Face;
import com.cards.games.pinochle.enums.Position;
import com.cards.games.pinochle.utils.CalculateMeld;
import com.cards.games.pinochle.utils.PinochleMessage;
import com.cards.server.User;

public class PinochlePlayer {
	private PinochleMessage message;
	private Position position; // enum position
	private int team; //team 1 or team 2
	private List<Card> currentCards; //current cards List<enum cards>
	private Position teamMate;
	private User user;
	
	public PinochlePlayer(Position position, int team, User user) {
		this.position=position;
		this.teamMate = position.getNext(2);
		this.team = team;
		this.currentCards = new ArrayList<Card>();
		this.setUser(user);
	}
	
	public PinochleMessage getMessage() {
		return message;
	}

	public void setMessage(PinochleMessage message) {
		this.message = message;
	}
	
	public Position getTeamMate() {
		return teamMate;
	}
	
	public void setCards(List<Card> newCards) {
		this.currentCards = newCards;
		this.currentCards.sort(new CardComparator());
	}
	
	public List<Card> getCurrentCards() {
		return this.currentCards;
	}

	public Position getPosition() {
		return this.position;
	}
	
	public int getTeam() {
		return this.team;
	}
	
	public boolean containsFiveNinesNoMeld() {
		int numberOfNines = 0;
		for (Card card : currentCards) {
			if(card.face.equals(Face.Nine))
				numberOfNines++;
		}
		
		if(numberOfNines >= 5 && new CalculateMeld(null, currentCards).calculate() == 0)
			return true;
		return false;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
