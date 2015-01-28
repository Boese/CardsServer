package com.cards.games.pinochle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cards.games.Game;
import com.cards.games.pinochle.enums.Position;
import com.cards.games.pinochle.enums.Request;
import com.cards.games.pinochle.enums.Suit;
import com.cards.games.pinochle.player.PinochlePlayer;
import com.cards.games.pinochle.states.Bid;
import com.cards.games.pinochle.states.Deal;
import com.cards.games.pinochle.states.Gameover;
import com.cards.games.pinochle.states.Meld;
import com.cards.games.pinochle.states.Pass;
import com.cards.games.pinochle.states.Pause;
import com.cards.games.pinochle.states.Round;
import com.cards.games.pinochle.states.Trump;
import com.cards.games.pinochle.states.iPinochleState;
import com.cards.games.pinochle.utils.GameStateObserver;
import com.cards.games.pinochle.utils.PinochleMessage;
import com.cards.message.PlayerResponse;
import com.cards.message.ResponsePacket;
import com.cards.server.User;
import com.cards.utils.MessageTransformer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Pinochle extends Game{
	private static final String GAME_TYPE = "Pinochle";
	
	//** Class Variables
	List<PinochlePlayer> players;
	int team1Score = 0;
	int team2Score = 0;
	Position currentTurn;
	Suit currentTrump;
	Request currentRequest;
	PlayerResponse lastMove;
	
	//** iPinochleStates
	 iPinochleState Start;
	 iPinochleState Deal;
	 iPinochleState Bid;
	 iPinochleState Trump;
	 iPinochleState Pass;
	 iPinochleState Meld;
	 iPinochleState Pause;
	 iPinochleState Gameover;
	 iPinochleState Round;
	 iPinochleState PickCard;
	
	//** Observers
	List<GameStateObserver> pinochleGameObservers;
	MessageTransformer msgTransformer;
	
	//** Current iPinochleState
	iPinochleState currentState;
	
	//** JSON Mapper
	ObjectMapper mapper;
	
	public Pinochle() {
		players = new ArrayList<PinochlePlayer>(4);
		currentTurn = Position.North;
		currentTrump = null;
		currentRequest = Request.Null;
		
		Deal = new Deal(this);
		Bid = new Bid(this);
		Trump = new Trump(this);
		Pass = new Pass(this);
		Meld = new Meld(this);
		Pause = new Pause(this);
		Gameover = new Gameover(this);
		Round = new Round(this);
		
		pinochleGameObservers = new ArrayList<GameStateObserver>();
		msgTransformer = new MessageTransformer();
		setCurrentState(Pause);
		
		mapper = new ObjectMapper();
	}
	
	public User getCurrentUser() {
		for(PinochlePlayer player : players) {
			if(player.getPosition() == currentTurn) {
				return player.getUser();
			}
		}
		return null;
	}
	
	//** Play function will call current state Play()
	@Override
	public void Play(PlayerResponse response) {
		if(!isGameFull())
			setState(Pause);
		currentState.Play(response);
	}
	
	@Override
	public Boolean isGameFull() {
		Boolean full = false;
		if(players.size() == 4) {
			full = true;
		}
		return full;
	}

	@Override
	public String getGameType() {
		return GAME_TYPE;
	}

	@Override
	public Boolean isCurrentTurn(String id) {
		Boolean turn = false;
		PinochlePlayer p = getPlayer(currentTurn);
		if(p.getUser().getSession_id().equals(id))
			turn = true;
		return turn;
	}
		
	@Override
	public void addPlayer(User user) {
		Position position = findNextAvailablePosition();
		int teamNum = 1;
		if(position.equals(Position.East) || position.equals(Position.West))
			teamNum = 2;
		PinochlePlayer p = new PinochlePlayer(position,teamNum,user);
		if(players.size() <= 3) {
			players.add(p);
		}
		
		Play(null);
	}
		
	@Override
	public void removePlayer(User user) {
		for (PinochlePlayer player : players) {
			if(player.getUser().equals(user)) {
				players.remove(player);
				currentRequest = Request.Null;
				break;
			}
		}
		if(players.size() == 0)
			super.removeGame();
		Play(null);
	}
	
	// Set current state
	public void setState(final iPinochleState state) {
		this.currentState = state;
	}
	
	// Send user game update/request
	public void update() {
		PinochlePlayer player = getPlayer(currentTurn);
		PinochleMessage message = player.getMessage();
		message.setCurrentState(currentState.getClass().getSimpleName());
		message.setLastMove(lastMove);
		message.setCurrentTurn(currentTurn.getNext(getPlayerPositionOffset(player)));
		player.getUser().sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("game").setGame_message(message)));
		player.setMessage(new PinochleMessage());
	}
	
	// Send all users game update
	public void updateAll() {
		for (PinochlePlayer player : players) {
			PinochleMessage message = player.getMessage();
			message.setCurrentState(currentState.getClass().getSimpleName());
			message.setLastMove(lastMove);
			message.setCurrentTurn(currentTurn.getNext(getPlayerPositionOffset(player)));
			player.getUser().sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("game").setGame_message(message)));
			player.setMessage(new PinochleMessage());
		}
	}
	
	// Notify Game is over call super
	public void gameover() {
		setState(getGameover());
		for (PinochlePlayer p : players) {
			p.getUser().sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("gameover")));
		}
		Pinochle.this.gameOver();
	}
	
	// Helper Methods
	private Position findNextAvailablePosition() {
		List<Position> availPositions = new ArrayList<Position>();
		availPositions.add(Position.North);
		availPositions.add(Position.East);
		availPositions.add(Position.South);
		availPositions.add(Position.West);
		for (PinochlePlayer player : players) {
			availPositions.remove(player.getPosition());
		}
		return availPositions.get(0);
	}
		
	public PinochlePlayer getPlayer(Position position) {
		PinochlePlayer tempPlayer = null;
		for (PinochlePlayer player : players) {
			if(player.getPosition() == position)
				tempPlayer = player;
		}
		return tempPlayer;
	}
	
	public PinochlePlayer getPlayer(String id) {
		PinochlePlayer tempPlayer = null;
		for (PinochlePlayer player : players) {
			if(player.getUser().getSession_id().equalsIgnoreCase(id))
				tempPlayer = player;
		}
		return tempPlayer;
	}
		
	public Position getPosition(String id) {
		Position tempPosition = null;
		for (PinochlePlayer player : players) {
			if(player.getUser().getSession_id().equalsIgnoreCase(id))
				tempPosition = player.getPosition();
		}
		return tempPosition;
	}
	
	// Create map of player positions according to their relative location
	public Map<Position,String> getUserNamesAndPositions(PinochlePlayer p) {
		int offset = getPlayerPositionOffset(p);
		
		Map<Position,String> userPositions = new HashMap<Position,String>();
		for(PinochlePlayer player : players) {
			userPositions.put(player.getPosition().getNext(offset),player.getUser().getUser_name());
		}
		return userPositions;
	}
	
	public int getPlayerPositionOffset(PinochlePlayer p) {
		int offset = 0;
		Position position = p.getPosition();
		switch(position) {
		case North: offset = 2; break;
		case East: offset = 1; break;
		case South: offset = 0; break;
		case West: offset = 3; break;
		}
		return offset;
	}
	
	public List<PinochlePlayer> getPlayers() {
		return this.players;
	}

	public int getTeam1Score() {
		return team1Score;
	}

	public void setTeam1Score(int team1Score) {
		this.team1Score = team1Score;
	}

	public int getTeam2Score() {
		return team2Score;
	}

	public void setTeam2Score(int team2Score) {
		this.team2Score = team2Score;
	}

	public Position getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(Position currentTurn) {
		this.currentTurn = currentTurn;
	}

	public Suit getCurrentTrump() {
		return currentTrump;
	}

	public void setCurrentTrump(Suit currentTrump) {
		this.currentTrump = currentTrump;
	}

	public Request getCurrentRequest() {
		return currentRequest;
	}

	public void setCurrentRequest(Request currentRequest) {
		this.currentRequest = currentRequest;
	}
	
	public ObjectMapper getMapper() {
		return mapper;
	}

	public List<GameStateObserver> getPinochleGameObservers() {
		return pinochleGameObservers;
	}

	public void setPinochleGameObservers(
			List<GameStateObserver> pinochleGameObservers) {
		this.pinochleGameObservers = pinochleGameObservers;
	}

	public iPinochleState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(iPinochleState currentState) {
		this.currentState = currentState;
	}

	public iPinochleState getStart() {
		return Start;
	}

	public  void setStart(iPinochleState start) {
		Start = start;
	}

	public iPinochleState getDeal() {
		return Deal;
	}

	public  void setDeal(iPinochleState deal) {
		Deal = deal;
	}

	public   iPinochleState getBid() {
		return Bid;
	}

	public  void setBid(iPinochleState bid) {
		Bid = bid;
	}

	public  iPinochleState getTrump() {
		return Trump;
	}

	public  void setTrump(iPinochleState trump) {
		Trump = trump;
	}

	public  iPinochleState getPass() {
		return Pass;
	}

	public  void setPass(iPinochleState pass) {
		Pass = pass;
	}

	public  iPinochleState getMeld() {
		return Meld;
	}

	public  void setMeld(iPinochleState meld) {
		Meld = meld;
	}

	public  iPinochleState getPause() {
		return Pause;
	}

	public  void setPause(iPinochleState pause) {
		Pause = pause;
	}

	public  iPinochleState getGameover() {
		return Gameover;
	}

	public  void setGameover(iPinochleState gameover) {
		Gameover = gameover;
	}

	public  iPinochleState getPickCard() {
		return PickCard;
	}

	public  void setPickCard(iPinochleState pickCard) {
		PickCard = pickCard;
	}

	public  iPinochleState getRound() {
		return Round;
	}

	public  void setRound(iPinochleState round) {
		Round = round;
	}

	@Override
	public int numPlayers() {
		return players.size();
	}

	public PlayerResponse getLastMove() {
		return lastMove;
	}

	public void setLastMove(PlayerResponse lastMove) {
		this.lastMove = lastMove;
	}
}
