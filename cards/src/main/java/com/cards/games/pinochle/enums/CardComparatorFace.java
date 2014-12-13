package com.cards.games.pinochle.enums;

import java.util.Comparator;

public class CardComparatorFace implements Comparator<Card>{
	public int compare(Card card1, Card card2) {
		int result = card1.face.ordinal() - card2.face.ordinal();
		if(result == 0) {
			return card1.suit.ordinal() - card2.suit.ordinal();
		}
		else
			return result;
	}
}
