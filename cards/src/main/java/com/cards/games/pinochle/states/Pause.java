package com.cards.games.pinochle.states;

import org.json.JSONObject;

import com.cards.games.pinochle.Pinochle;


public class Pause implements iPinochleState {
	static boolean gameHasStarted;
	
	Pinochle mP;
	public Pause(Pinochle p){
		this.mP = p;
		gameHasStarted = false;
	}
	@Override
	public void Play(JSONObject response) {
		if(mP.isGameFull()) {
			gameHasStarted = true;
			mP.setCurrentMessage("Game is about to start...");
			mP.update();
			mP.setState(Pinochle.getStart());
			mP.Play(null);
		}
		else {
			int playersNeeded = 4 - mP.getPlayers().size();
			if(gameHasStarted)
				mP.setCurrentMessage("**PAUSED - RESTARTING ROUND** Waiting for " + playersNeeded + " more player(s)");
			else
				mP.setCurrentMessage("Waiting for " + playersNeeded + " more player(s) to start game");
			mP.update();
		}
	}
}
