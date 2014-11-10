package com.cards.games.pinochle.enums;

public enum Suit {
	Hearts,
	Spades,
	Diamonds,
	Clubs;
	public Suit getNext(int i) {
		return values()[(ordinal()+i) % values().length];
	}
}
