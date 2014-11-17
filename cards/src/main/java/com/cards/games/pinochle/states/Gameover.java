package com.cards.games.pinochle.states;

import org.json.JSONObject;

import com.cards.games.pinochle.Pinochle;


public class Gameover implements iPinochleState {
	Pinochle mP;
	public Gameover(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("Game Over!");
		mP.update();
		mP.gameover();
	}
}
