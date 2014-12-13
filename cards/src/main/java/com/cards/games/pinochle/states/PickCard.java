package com.cards.games.pinochle.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Card;
import com.cards.games.pinochle.enums.CardComparator;
import com.cards.games.pinochle.enums.CardComparatorFace;
import com.cards.games.pinochle.enums.Request;
import com.cards.message.PlayerResponse;

public class PickCard implements iPinochleState {

	Pinochle mP;
	List<Card> cards;
	Map<Card,String> playerCard = new HashMap<Card,String>();
	public PickCard(Pinochle p){
		this.mP = p;
		this.cards = new ArrayList<Card>();
	}
	
	@Override
	public void Play(PlayerResponse response) {
		mP.setCurrentMessage("Pick your best card " + mP.getCurrentUser().getUser_name());
		mP.update();
		
		try {
			Card card = response.getCard();
			mP.getPlayer(mP.getCurrentTurn()).removeCard(card);
			cards.add(card);
			playerCard.put(card, mP.getCurrentUser().getUser_name());
			
			if(cards.size() < 4) {
			mP.setCurrentTurn(mP.getCurrentTurn().getNext(1));
			mP.setCurrentMessage("Pick your best card " + mP.getCurrentUser().getUser_name());
			mP.update();
			} 
			else {
				cards.sort(new CardComparatorFace());
				mP.setCurrentMessage("Player " + playerCard.get(cards.get(cards.size()-1)) + " picked the highest card! " + cards.get(cards.size()-1).toString());
				mP.update();
				TimerTask t = new TimerTask() {
					@Override
					public void run() {
						mP.setState(mP.getGameover());
						mP.Play(null);
					}
				};
				new Timer().schedule(t, 5*1000);
			}
		}catch(Exception e) {}
	}

}
