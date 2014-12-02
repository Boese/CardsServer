package com.cards.games.pinochle.states;

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
		mP.setState(Pinochle.getDeal());
		mP.Play(null);
	}
}
