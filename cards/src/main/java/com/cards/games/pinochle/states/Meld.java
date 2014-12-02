package com.cards.games.pinochle.states;

import com.cards.games.pinochle.Pinochle;
import com.cards.message.PlayerResponse;


public class Meld implements iPinochleState {
	Pinochle mP;
	public Meld(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(PlayerResponse response) {
		mP.setCurrentMessage("Melding Cards...");
		mP.update();
		mP.setState(Pinochle.getRound());
		mP.Play(null);
	}
	
}
