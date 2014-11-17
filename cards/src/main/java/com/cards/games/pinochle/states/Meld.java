package com.cards.games.pinochle.states;

import org.json.JSONObject;

import com.cards.games.pinochle.Pinochle;


public class Meld implements iPinochleState {
	Pinochle mP;
	public Meld(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("Melding Cards...");
		mP.update();
		mP.setState(Pinochle.getRound());
		mP.Play(null);
	}
	
}
