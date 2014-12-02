package com.cards.message;

import java.util.ArrayList;
import java.util.List;

import com.cards.games.pinochle.enums.Card;
import com.cards.games.pinochle.enums.Suit;


public class PlayerResponse {
	int bid;
	Suit trump;
	List<Card> cards;
	Card card;
	
	public PlayerResponse() {
		cards = new ArrayList<Card>();
	}

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	public Suit getTrump() {
		return trump;
	}

	public void setTrump(Suit trump) {
		this.trump = trump;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}
}
