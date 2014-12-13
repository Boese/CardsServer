package com.cards.games.pinochle.states;

import java.util.Timer;
import java.util.TimerTask;

import com.cards.games.pinochle.Pinochle;
import com.cards.message.PlayerResponse;


/*
 * Start State
 */
public class Start implements iPinochleState {
	Pinochle mP;
	public Start(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(PlayerResponse response) {
		mP.setCurrentMessage("Starting Game!");
		mP.update();
		TimerTask t = new TimerTask() {
			@Override
			public void run() {
				mP.setState(mP.getDeal());
				mP.Play(null);
			}
		};
		new Timer().schedule(t, 5*1000);
	}
}
