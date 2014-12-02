package com.cards.games.pinochle.states;

import java.util.List;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Card;
import com.cards.games.pinochle.player.PinochlePlayer;
import com.cards.message.PlayerResponse;


public class Pass implements iPinochleState {
	Pinochle mP;
	public Pass(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(PlayerResponse response) {
		mP.setCurrentMessage("*** PASSING CARDS ***");
		mP.update();
		mP.setState(Pinochle.getMeld());
		mP.Play(null);
	}
	
	@SuppressWarnings("unused")
	private boolean passCards(PinochlePlayer from, PinochlePlayer to, List<Card> cards) {
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
