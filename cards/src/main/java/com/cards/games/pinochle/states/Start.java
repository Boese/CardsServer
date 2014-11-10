package com.cards.games.pinochle.states;

import org.json.JSONObject;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.utils.iPinochleState;


/*
 * Start State
 */
public class Start implements iPinochleState {
	Pinochle mP;
	public Start(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("Starting Game!");
		mP.notifyObservers();
		mP.setState(mP.getDealState());
		mP.Play(null);
	}
}
