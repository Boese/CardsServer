package com.cards.games.pinochle.states;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Suit;
import com.cards.games.pinochle.player.PinochlePlayer;
import com.cards.games.pinochle.utils.PinochleMessage;
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
			System.out.println("trump is " + move);
			mP.setLastMove(response);
			mP.setCurrentTrump(move);
			
			// Send trump to players
			PinochleMessage message = new PinochleMessage();
			message.setCurrentTrump(move.toString());
			for (PinochlePlayer player : mP.getPlayers()) {
				player.setMessage(message);
			}
			mP.updateAll();
			
			mP.setState(mP.getPass());
			((Pass)mP.getPass()).startPass();
			mP.Play(null);
		} catch (Exception e) {
			// Request trump from winning bidder
			mP.updateAll();
		}
	}
}
