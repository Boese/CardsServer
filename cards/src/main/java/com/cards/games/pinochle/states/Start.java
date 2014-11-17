package com.cards.games.pinochle.states;

import org.json.JSONObject;

import com.cards.games.pinochle.Pinochle;


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
		mP.update();
		mP.setState(Pinochle.getDeal());
		mP.Play(null);
	}
}
