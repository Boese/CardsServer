package com.cards.games.pinochle.states;

import org.json.JSONObject;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Request;
import com.cards.games.pinochle.enums.Suit;
import com.cards.games.pinochle.player.PlayerResponse;
import com.cards.games.pinochle.utils.iPinochleState;


public class Trump implements iPinochleState {
	Pinochle mP;
	public Trump(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		try {
			PlayerResponse playerresponse = new PlayerResponse();
			playerresponse = mP.getMapper().readValue(response.toString(), PlayerResponse.class);
			Suit move = playerresponse.getTrump();
			
			mP.setCurrentTrump(move);
			mP.setCurrentMessage(mP.getCurrentTurn() + " selected " + mP.getCurrentTrump() + " as trump!");
			mP.notifyObservers();
			mP.setState(mP.getPassState());
			mP.Play(null);
		} catch (Exception e) {
			mP.notifyObservers(Request.Trump);
		}
	}
}
