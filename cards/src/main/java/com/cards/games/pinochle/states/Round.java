package com.cards.games.pinochle.states;

import org.json.JSONObject;

import com.cards.games.pinochle.Pinochle;


public class Round implements iPinochleState {
	Pinochle mP;
	public Round(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("Playing Round...");
		mP.update();
		mP.setCurrentState(Pinochle.getGameover());
		mP.Play(null);
	}
}
