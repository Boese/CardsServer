package com.cards.games.pinochle.states;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.player.PinochlePlayer;
import com.cards.games.pinochle.utils.PinochleMessage;
import com.cards.message.PlayerResponse;


public class Pause implements iPinochleState {
	Pinochle mP;
	public Pause(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(PlayerResponse response) {
		// Send all Users the players and their positions relative to User
		for (PinochlePlayer player : mP.getPlayers()) {
			PinochleMessage message = new PinochleMessage();
			message.setPlayers(mP.getUserNamesAndPositions(player));
			player.setMessage(message);
		}
		mP.updateAll();
		
		if(mP.isGameFull()) {
			mP.setState(mP.getDeal());
			mP.Play(null);
		}
	}
}
