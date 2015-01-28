package com.cards.games.pinochle.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Formatter.BigDecimalLayoutForm;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Card;
import com.cards.games.pinochle.enums.CardComparator;
import com.cards.games.pinochle.enums.CardComparatorFace;
import com.cards.games.pinochle.enums.Face;
import com.cards.games.pinochle.enums.Position;
import com.cards.games.pinochle.player.PinochlePlayer;
import com.cards.games.pinochle.utils.PinochleMessage;
import com.cards.message.PlayerResponse;


public class Round implements iPinochleState {
	Pinochle mP;
	List<Card> roundCards = new ArrayList<Card>(4);
	List<Position> currentOrder;
	int numRounds = 12;
	int currentRound = 1;
	public Round(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(PlayerResponse response) {
		try{
			Card card = response.getCards().get(0);
			
			System.out.println("currentTurn : " + mP.getCurrentTurn());
			System.out.println(card);
			
			List<Card> cards = new ArrayList<Card>(mP.getPlayer(mP.getCurrentTurn()).getCurrentCards());
			if(!cards.contains(card))
				throw new Exception("not an available card");
			cards.remove(card);
			mP.getPlayer(mP.getCurrentTurn()).setCards(cards);
			
			mP.setLastMove(response);
			mP.setCurrentTurn(mP.getCurrentTurn().getNext(1));
			roundCards.add(card);
			
			if(roundCards.size() == 4) {
				System.out.println("Cards in Round " + currentRound + " : " + roundCards);
				Card highestCard = new Card();
				int numTricks = 0;
				roundCards.sort(new CardComparator());
				highestCard = roundCards.get(roundCards.size()-1);
				roundCards.sort(new CardComparatorFace());
				for (Card roundCard : roundCards) {
					if(roundCard.suit == mP.getCurrentTrump()) {
						highestCard = roundCard;
					}
					if(roundCard.face == Face.King || roundCard.face == Face.Ten || roundCard.face == Face.Ace)
						numTricks++;
				}
				System.out.println("highest Card : " + highestCard);
				int next = roundCards.indexOf(highestCard);
				int team = mP.getPlayer(currentOrder.get(next)).getTeam();
				if(currentRound == 12)
					numTricks++;
				if(team == 1) {
					mP.setTeam2Score(mP.getTeam2Score() + numTricks);
				} else {
					mP.setTeam1Score(mP.getTeam1Score() + numTricks);
				}
				roundCards = new ArrayList<Card>(4);
				mP.setCurrentTurn(currentOrder.get(next));
			}
			if(currentRound == 12) {
				mP.setState(mP.getGameover());
				mP.Play(null);
			}
			
			for (PinochlePlayer player : mP.getPlayers()) {
				PinochleMessage message = new PinochleMessage();
				if(mP.getPlayerPositionOffset(player) == 0 || mP.getPlayerPositionOffset(player) == 2) {
					message.setTeam1Score(mP.getTeam1Score());
					message.setTeam2Score(mP.getTeam2Score());
				} else {
					message.setTeam1Score(mP.getTeam2Score());
					message.setTeam2Score(mP.getTeam1Score());
				}
				message.setCards(player.getCurrentCards());
				player.setMessage(message);
			}
			mP.updateAll();
		} catch(Exception e) {
			for (PinochlePlayer player : mP.getPlayers()) {
				PinochleMessage message = new PinochleMessage();
				if(mP.getPlayerPositionOffset(player) == 0 || mP.getPlayerPositionOffset(player) == 2) {
					message.setTeam1Score(mP.getTeam1Score());
					message.setTeam2Score(mP.getTeam2Score());
				} else {
					message.setTeam1Score(mP.getTeam2Score());
					message.setTeam2Score(mP.getTeam1Score());
				}
				message.setCards(player.getCurrentCards());
				player.setMessage(message);
			}
			mP.updateAll();
		}
	}
	
	public void startRound() {
		currentRound = 1;
		setOrder();
	}
	
	private void setOrder() {
		System.out.println("Starting round with " + mP.getCurrentTurn());
		currentOrder = new ArrayList<Position>(4);
		currentOrder.add(mP.getCurrentTurn());
		currentOrder.add(mP.getCurrentTurn().getNext(1));
		currentOrder.add(mP.getCurrentTurn().getNext(2));
		currentOrder.add(mP.getCurrentTurn().getNext(3));
	}
}
