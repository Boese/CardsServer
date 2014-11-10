package com.cards.games.pinochle.enums;

public enum Position {
	North,
	East,
	South,
	West;
	public Position getNext(int i) {
		return values()[(ordinal()+i) % values().length];
	}
}
