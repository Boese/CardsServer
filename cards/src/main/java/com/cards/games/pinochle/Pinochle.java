package com.cards.games.pinochle;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.cards.games.Game;
import com.cards.games.GameMessage;
import com.cards.games.Player;
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
import com.cards.games.pinochle.states.Start;
import com.cards.games.pinochle.states.Trump;
import com.cards.games.pinochle.states.iPinochleState;
import com.cards.games.pinochle.utils.GameStateObserver;
import com.cards.games.pinochle.utils.PinochleMessage;
import com.cards.message.PlayerResponse;
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
	String currentMessage;
	
	//** iPinochleStates
	static iPinochleState Start;
	static iPinochleState Deal;
	static iPinochleState Bid;
	static iPinochleState Trump;
	static iPinochleState Pass;
	static iPinochleState Meld;
	static iPinochleState Pause;
	static iPinochleState Gameover;
	static iPinochleState Round;
	
	//** Observers
	List<GameStateObserver> pinochleGameObservers;
	PinochleMessage pinochleMessage;
	
	//** Current iPinochleState
	iPinochleState currentState = Pause;
	
	//** JSON Mapper
	ObjectMapper mapper;
	
	public Pinochle() {
		players = new ArrayList<PinochlePlayer>(4);
		currentTurn = Position.North;
		currentTrump = null;
		currentRequest = Request.Null;
		currentMessage = "";
		
		Start = new Start(this);
		Deal = new Deal(this);
		Bid = new Bid(this);
		Trump = new Trump(this);
		Pass = new Pass(this);
		Meld = new Meld(this);
		Pause = new Pause(this);
		Gameover = new Gameover(this);
		Round = new Round(this);
		
		pinochleGameObservers = new ArrayList<GameStateObserver>();
		pinochleMessage = new PinochleMessage();
		
		mapper = new ObjectMapper();
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
		if(p.getId().equals(id))
			turn = true;
		return turn;
	}
	
	@Override
	public GameMessage getGameMessage(String id) {
		for (PinochlePlayer	player : players) {
			if(id.equals(player.getId()))
				return player.getGame_message();
		}
		return null;
	}
		
	@Override
	public void addPlayer(String id) {
		Position position = findNextAvailablePosition();
		int teamNum = 1;
		if(position.equals(Position.East) || position.equals(Position.West))
			teamNum = 2;
		PinochlePlayer p = new PinochlePlayer(position,teamNum,id);
		if(players.size() <= 3) {
			players.add(p);
			currentMessage = "**WELCOME TO PINOCHLE**";
			update();
		}
	}
		
	@Override
	public void removePlayer(String id) {
		for (PinochlePlayer player : players) {
			if(player.getId().equalsIgnoreCase(id)) {
				players.remove(player);
				currentRequest = Request.Null;
				currentMessage = "Player " + player.getPosition() + " just quit...";
				update();
				break;
			}
		}
	}
	
	// Set current state
	public void setState(final iPinochleState state) {
		this.currentState = state;
	}
	
	// Broadcast updated game call super
	public void update(Request request) {
		setCurrentRequest(request);
		PinochleMessage pinMessage = new PinochleMessage(this);
		for (PinochlePlayer player : players) {
			pinMessage.update(player);
		}
		super.update();
		currentRequest = Request.Null;
	}
	
	// Notify Game is over call super
	public void gameover() {
		super.gameOver();
	}
	
	// Helper methods
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
			if(player.getId().equalsIgnoreCase(id))
				tempPlayer = player;
		}
		return tempPlayer;
	}
		
	public Position getPosition(String id) {
		Position tempPosition = null;
		for (PinochlePlayer player : players) {
			if(player.getId().equalsIgnoreCase(id))
				tempPosition = player.getPosition();
		}
		return tempPosition;
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

	public String getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}

	public List<GameStateObserver> getPinochleGameObservers() {
		return pinochleGameObservers;
	}

	public void setPinochleGameObservers(
			List<GameStateObserver> pinochleGameObservers) {
		this.pinochleGameObservers = pinochleGameObservers;
	}

	public PinochleMessage getPinochleMessage() {
		return pinochleMessage;
	}

	public void setPinochleMessage(PinochleMessage pinochleMessage) {
		this.pinochleMessage = pinochleMessage;
	}

	public iPinochleState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(iPinochleState currentState) {
		this.currentState = currentState;
	}

	public static iPinochleState getStart() {
		return Start;
	}

	public static void setStart(iPinochleState start) {
		Start = start;
	}

	public static iPinochleState getDeal() {
		return Deal;
	}

	public static void setDeal(iPinochleState deal) {
		Deal = deal;
	}

	public static iPinochleState getBid() {
		return Bid;
	}

	public static void setBid(iPinochleState bid) {
		Bid = bid;
	}

	public static iPinochleState getTrump() {
		return Trump;
	}

	public static void setTrump(iPinochleState trump) {
		Trump = trump;
	}

	public static iPinochleState getPass() {
		return Pass;
	}

	public static void setPass(iPinochleState pass) {
		Pass = pass;
	}

	public static iPinochleState getMeld() {
		return Meld;
	}

	public static void setMeld(iPinochleState meld) {
		Meld = meld;
	}

	public static iPinochleState getPause() {
		return Pause;
	}

	public static void setPause(iPinochleState pause) {
		Pause = pause;
	}

	public static iPinochleState getGameover() {
		return Gameover;
	}

	public static void setGameover(iPinochleState gameover) {
		Gameover = gameover;
	}

	public static iPinochleState getRound() {
		return Round;
	}

	public static void setRound(iPinochleState round) {
		Round = round;
	}
}
