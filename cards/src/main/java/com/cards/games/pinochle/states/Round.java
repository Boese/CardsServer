package com.cards.games.pinochle.states;

import org.json.JSONObject;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.utils.iPinochleState;


public class Round implements iPinochleState {
	Pinochle mP;
	public Round(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("Playing Round...");
		mP.notifyObservers();
		mP.setCurrentState(mP.getGameoverState());
		mP.Play(null);
	}
}
