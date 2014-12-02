package com.cards.games.pinochle.states;

import com.cards.games.pinochle.Pinochle;
import com.cards.message.PlayerResponse;


public class Round implements iPinochleState {
	Pinochle mP;
	public Round(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(PlayerResponse response) {
		mP.setCurrentMessage("Playing Round...");
		mP.update();
		mP.setCurrentState(Pinochle.getGameover());
		mP.Play(null);
	}
}
