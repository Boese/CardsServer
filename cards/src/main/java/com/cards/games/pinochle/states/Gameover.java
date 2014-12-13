package com.cards.games.pinochle.states;

import com.cards.games.pinochle.Pinochle;
import com.cards.message.PlayerResponse;


public class Gameover implements iPinochleState {
	Pinochle mP;
	public Gameover(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(PlayerResponse response) {
		mP.setCurrentMessage("Game Over!");
		mP.update();
		mP.gameover();
	}
}
