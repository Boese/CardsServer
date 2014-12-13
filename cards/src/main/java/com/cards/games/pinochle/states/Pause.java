package com.cards.games.pinochle.states;

import java.util.Timer;
import java.util.TimerTask;

import com.cards.games.pinochle.Pinochle;
import com.cards.message.PlayerResponse;


public class Pause implements iPinochleState {
	static boolean gameHasStarted;
	
	Pinochle mP;
	public Pause(Pinochle p){
		this.mP = p;
		gameHasStarted = false;
	}
	@Override
	public void Play(PlayerResponse response) {
		if(mP.isGameFull()) {
			gameHasStarted = true;
			mP.setCurrentMessage("Game is about to start...");
			mP.update();

			TimerTask t = new TimerTask() {
				@Override
				public void run() {
					mP.setState(mP.getStart());
					mP.Play(null);
				}
			};
			new Timer().schedule(t, 3*1000);
			
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
