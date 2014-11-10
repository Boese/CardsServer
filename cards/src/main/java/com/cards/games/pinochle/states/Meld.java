package com.cards.games.pinochle.states;

import org.json.JSONObject;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.utils.iPinochleState;


public class Meld implements iPinochleState {
	Pinochle mP;
	public Meld(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("Melding Cards...");
		mP.notifyObservers();
		mP.setState(mP.getRoundState());
		mP.Play(null);
	}
	
}
