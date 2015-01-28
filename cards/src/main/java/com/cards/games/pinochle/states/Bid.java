package com.cards.games.pinochle.states;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Position;
import com.cards.message.PlayerResponse;


public class Bid implements iPinochleState{
	List<Position> bidders;
	Iterator<Position> bidTurn;
	Position lastBidder;
	int currentBid;
	
	Pinochle mP;
	public Bid(Pinochle p){
		this.mP = p;
		this.lastBidder = mP.getCurrentTurn();
	}
	@Override
	public void Play(PlayerResponse response) {
		try {
			// Try to get bid from JSONObject
			int bid = response.getBid();
			System.out.println("bid : " + response.getBid());
			System.out.println("bid from : " + mP.getCurrentTurn());
			
			
			
			// Bidder passed, remove from remaining
			if(bid == 0) {
				bidTurn.remove();
				incTurn();
				if(currentBid == 0)
					mP.setLastMove(response);
			}
		
			// Bidder bid
			if(bid > currentBid) {
				currentBid = bid;
				mP.setLastMove(response);
				incTurn();
			}
			
			System.out.println("next bidder : " + mP.getCurrentTurn());
			System.out.println("bidders remaining : " + bidders);
			
			// Everyone passed, Re-Deal
			if(bidders.size() == 0) {
				lastBidder = lastBidder.getNext(1);
				mP.setState(mP.getDeal());
				mP.Play(null);
			}
			
			// Winner, go to state Trump
			if(bidders.size() == 1 && currentBid > 0) {
				mP.setState(mP.getTrump());
				mP.setCurrentTurn(bidders.get(0));
				lastBidder = lastBidder.getNext(1);
				mP.Play(null);
			}
			
			mP.updateAll();
			
		// Invalid response from player
		} catch (Exception e) {
			mP.update();
		} 
	}
	
	private void incTurn() {
		//Check if iterator is at end of bidders
		if(!bidTurn.hasNext())
			bidTurn = bidders.listIterator();
		
		// Make sure bidders is not empty
		if(bidders.size() > 0)
			mP.setCurrentTurn(bidTurn.next());
	}
	
	public void startBid() {
		// Set current turn to last bidder. Initialize currentBid to 0.
		mP.setCurrentTurn(lastBidder);
		currentBid = 0;
		
		// Initialize bidders list with the correct order of players starting with last bidder. Set bidTurn to bidders.listIterator
		bidders = new ArrayList<Position>();
		for(int i=0;i<4;i++)
			bidders.add(lastBidder.getNext(i));
		bidTurn = bidders.listIterator();
		bidTurn.next();
		
		// Notify Bid Round
		mP.updateAll();
	}
}
