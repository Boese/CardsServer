package com.cards.games.pinochle.player;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONObject;

import com.cards.games.pinochle.enums.Card;
import com.cards.games.pinochle.enums.CardComparator;
import com.cards.games.pinochle.enums.Face;
import com.cards.games.pinochle.enums.Position;
import com.cards.games.pinochle.enums.Suit;
import com.cards.games.pinochle.utils.CalculateMeld;

public class Player {
	private Position position; // enum position
	private int team; //team 1 or team 2
	private List<Card> currentCards; //current cards List<enum cards>
	private Position teamMate;
	private JSONObject currentJSON;
	private String id;
	
	public Player(Position position, int team, String id) {
		this.position=position;
		this.teamMate = position.getNext(2);
		this.team = team;
		this.currentCards = new ArrayList<Card>();
		this.currentJSON = new JSONObject();
		this.setId(id);
	}
	
	public String toString() {
		String player = "";
		player += "Position : " + position + "\n";
		player += "Team : " + team + "\n";
		player += "Cards : ";
		for (Card card : currentCards) {
			player += card + " , ";
		}
		player += "\n";
		return player;
	}
	
	public String toCardString() {
		currentCards.sort(new CardComparator());
		String hearts = "Hearts : \n";
		String diamonds = "Diamonds : \n";
		String clubs = "Clubs : \n";
		String spades = "Spades : \n";
		int i = 1;
		for (Card card : currentCards) {
			Suit s = card.suit;
			switch(s) {
			case Clubs: clubs += "\t" + i + " - " + card + "\n";
				break;
			case Diamonds: diamonds += "\t" + i + " - " + card + "\n";
				break;
			case Hearts: hearts += "\t" + i + " - " + card + "\n";
				break;
			case Spades: spades += "\t" + i + " - " + card + "\n";
				break;
			} i++;
		}
		String returnCards = hearts + spades + diamonds + clubs;
		return returnCards;
	}
	
	public void setJSON(JSONObject ob) {
		this.currentJSON = ob;
	}
	
	public JSONObject getJSON() {
		return this.currentJSON;
	}
	
	public List<Card> addCardsToCurrent(List<Card> cards) {
		List<Card> temp = new ArrayList<Card>(currentCards);
		temp.addAll(cards);
		return temp;
	}
	
	public List<Card> removeCardsFromCurrent(List<Card> cards) {
		List<Card> mutableCards = new ArrayList<Card>(cards);
		List<Card> copyOfCards = new ArrayList<Card>(cards);
		ListIterator<Card> cardsIterator = copyOfCards.listIterator();
		while(cardsIterator.nextIndex() < copyOfCards.size() && mutableCards.size() >= 0) {
			int potentialmatch = cardsIterator.nextIndex();
			if(mutableCards.contains(mutableCards.get(potentialmatch))) {
				mutableCards.remove(potentialmatch);
				copyOfCards.remove(potentialmatch);
				cardsIterator = copyOfCards.listIterator();
			}
			else
				cardsIterator.next();
		}
		return mutableCards;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
