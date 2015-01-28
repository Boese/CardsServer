package com.cards.games.pinochle.states;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Card;
import com.cards.games.pinochle.enums.Face;
import com.cards.games.pinochle.enums.Suit;
import com.cards.games.pinochle.player.PinochlePlayer;
import com.cards.games.pinochle.utils.PinochleMessage;
import com.cards.message.PlayerResponse;


public class Deal implements iPinochleState {
	Pinochle mP;
	public Deal(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(PlayerResponse response) {
		deal();
		
		// Send all Users their dealt hand
		for (PinochlePlayer player : mP.getPlayers()) {
			PinochleMessage message = new PinochleMessage();
			message.setCards(player.getCurrentCards());
			player.setMessage(message);
		}
		mP.updateAll();
		
		// Check if a user got 5 nines & no meld - redeal if true
		if(!checkForNines()) {
			mP.setState(mP.getBid());
			((Bid)mP.getBid()).startBid();
		}
		
		mP.Play(null);
	}
	
	private void deal() {
		final List<Suit> suits = asList(Suit.Hearts,Suit.Diamonds,Suit.Spades,Suit.Clubs);
		final List<Face> faces = asList(Face.Nine,Face.Jack,Face.Queen,Face.King,Face.Ten,Face.Ace);
		
		List<Card> deck = new ArrayList<Card>(48);
		
		// Fill new Pinochle deck
		for (int i = 0; i < 2; i++) {	// 2 of each card *
			for (Suit suit : suits) {	// 4 of each suit *
				for (Face face : faces) {	// 6 of each face = 48 cards
					deck.add(new Card(suit,face));
				}
			}
		}
		// Shuffle deck
		Collections.shuffle(deck);
		
		// Deal out 12 cards to each player
		int from = 0;
		int to = 12;
		for (PinochlePlayer player : mP.getPlayers()) {
			player.setCards(deck.subList(from, to));
			from += 12;
			to += 12;
		}
	}
	
	private boolean checkForNines() {
		boolean result = false;
		for (PinochlePlayer p : mP.getPlayers()) {
			if(p.containsFiveNinesNoMeld())
				result = true;
		}
		return result;
	}
}
