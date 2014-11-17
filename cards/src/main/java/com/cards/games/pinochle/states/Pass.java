package com.cards.games.pinochle.states;

import java.util.List;

import org.json.JSONObject;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Card;
import com.cards.games.pinochle.player.Player;


public class Pass implements iPinochleState {
	Pinochle mP;
	public Pass(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(JSONObject response) {
		mP.setCurrentMessage("*** PASSING CARDS ***");
		mP.update();
		mP.setState(Pinochle.getMeld());
		mP.Play(null);
	}
	
	@SuppressWarnings("unused")
	private boolean passCards(Player from, Player to, List<Card> cards) {
		boolean result = false;
		try {
			if(cards.size() != 4)
				throw new Exception("incorrect number of cards");
			to.addCardsToCurrent(cards);
			from.removeCardsFromCurrent(cards);
			result = true;
		}
		catch(Exception e) {}
		return result;
	}
	
}
