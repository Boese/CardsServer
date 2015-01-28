package com.cards.games.pinochle.states;

import java.util.ArrayList;
import java.util.List;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Card;
import com.cards.games.pinochle.player.PinochlePlayer;
import com.cards.games.pinochle.utils.PinochleMessage;
import com.cards.message.PlayerResponse;


public class Pass implements iPinochleState {
	Pinochle mP;
	PinochlePlayer bidWinner;
	PinochlePlayer bidPartner;
	
	public Pass(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(PlayerResponse response) {
		try {
			List<Card> cards = response.getCards();
			if(cards.size() < 4)
				throw new Exception("not enough cards");
			
			if(mP.getCurrentTurn() == bidPartner.getPosition()) {
				passCards(bidPartner, bidWinner, cards);
				mP.setCurrentTurn(bidWinner.getPosition());
				for (PinochlePlayer player : mP.getPlayers()) {
					PinochleMessage message = new PinochleMessage();
					message.setCards(player.getCurrentCards());
					player.setMessage(message);
				}
				mP.updateAll();
			}
			else {
				passCards(bidWinner, bidPartner, cards);
				for (PinochlePlayer player : mP.getPlayers()) {
					PinochleMessage message = new PinochleMessage();
					message.setCards(player.getCurrentCards());
					player.setMessage(message);
				}
				mP.updateAll();
				
				mP.setState(mP.getMeld());
				mP.setCurrentTurn(bidWinner.getPosition());
				mP.Play(null);
			}
			
		} catch(Exception e) {
			for (PinochlePlayer player : mP.getPlayers()) {
				PinochleMessage message = new PinochleMessage();
				message.setCards(player.getCurrentCards());
				player.setMessage(message);
			}
			mP.updateAll();
		}
	}
	
	private boolean passCards(PinochlePlayer from, PinochlePlayer to, List<Card> cards) {
		boolean result = false;
		try {
			List<Card> fromCards = new ArrayList<Card>(from.getCurrentCards());
			List<Card> toCards = new ArrayList<Card>(to.getCurrentCards());
			
			System.out.println("from Old : "  + fromCards);
			System.out.println("to Old : " + toCards);
			
			for (Card card : cards) {
				toCards.add(card);
				fromCards.remove(card);
			}
			from.setCards(fromCards);
			to.setCards(toCards);
			System.out.println("from New : "  + fromCards);
			System.out.println("to New : " + toCards);
			result = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void startPass() {
		bidWinner = mP.getPlayer(mP.getCurrentTurn());
		bidPartner = mP.getPlayer(bidWinner.getTeamMate());
		mP.setCurrentTurn(bidPartner.getPosition());
	}
	
}
