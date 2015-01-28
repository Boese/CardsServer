package com.cards.games.pinochle.states;

import java.util.HashMap;
import java.util.Map;

import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Position;
import com.cards.games.pinochle.player.PinochlePlayer;
import com.cards.games.pinochle.utils.CalculateMeld;
import com.cards.games.pinochle.utils.PinochleMessage;
import com.cards.message.PlayerResponse;


public class Meld implements iPinochleState {
	Pinochle mP;
	public Meld(Pinochle p){
		this.mP = p;
	}
	@Override
	public void Play(PlayerResponse response) {
		// Send trump to players
		Map<Position,Integer> playersMeldServer = getPlayersMeld(Position.South);
		mP.setTeam1Score(playersMeldServer.get(Position.North) + playersMeldServer.get(Position.South));
		mP.setTeam2Score(playersMeldServer.get(Position.East) + playersMeldServer.get(Position.West));
		System.out.println("team 1 score : " + mP.getTeam1Score());
		System.out.println("team 2 score : " + mP.getTeam2Score());
		for (PinochlePlayer player : mP.getPlayers()) {
			PinochleMessage message = new PinochleMessage();
			if(mP.getPlayerPositionOffset(player) == 0 || mP.getPlayerPositionOffset(player) == 2) {
				message.setTeam1Score(mP.getTeam1Score());
				message.setTeam2Score(mP.getTeam2Score());
			} else {
				message.setTeam1Score(mP.getTeam2Score());
				message.setTeam2Score(mP.getTeam1Score());
			}
			message.setPlayersMeld(getPlayersMeld(player.getPosition()));
			player.setMessage(message);
		}
		mP.updateAll();
		
		((Round)mP.getRound()).startRound();
		mP.setState(mP.getRound());
		mP.Play(null);
	}
	
	public Map<Position,Integer> getPlayersMeld(Position position) {
		Map<Position,Integer> playersMeld = new HashMap<Position,Integer>();
		for (PinochlePlayer player : mP.getPlayers()) {
			int offset = mP.getPlayerPositionOffset(mP.getPlayer(position));
			playersMeld.put(player.getPosition().getNext(offset), new CalculateMeld(mP.getCurrentTrump(), player.getCurrentCards()).calculate());
		}
		System.out.println("players meld for position " + position + " : " + playersMeld);
		return playersMeld;
	}
}
