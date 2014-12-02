package com.cards.games.pinochle.states;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Request;
import com.cards.games.pinochle.enums.Suit;
import com.cards.message.PlayerResponse;


public class Trump implements iPinochleState {
	Pinochle mP;
	public Trump(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(PlayerResponse response) {
		try {
			Suit move = response.getTrump();
			
			mP.setCurrentTrump(move);
			mP.setCurrentMessage(mP.getCurrentTurn() + " selected " + mP.getCurrentTrump() + " as trump!");
			mP.update();
			mP.setState(Pinochle.getPass());
			mP.Play(null);
		} catch (Exception e) {
			mP.update(Request.Trump);
		}
	}
}
