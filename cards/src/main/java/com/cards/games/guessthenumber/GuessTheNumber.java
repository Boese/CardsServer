package com.cards.games.guessthenumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.cards.games.Game;
import com.cards.games.GameMessage;
import com.cards.games.pinochle.Pinochle;
import com.cards.games.pinochle.enums.Position;
import com.cards.games.pinochle.enums.Request;
import com.cards.games.pinochle.player.PinochlePlayer;
import com.cards.message.PlayerResponse;
import com.cards.message.ResponsePacket;
import com.cards.server.User;
import com.cards.utils.MessageTransformer;

public class GuessTheNumber extends Game{
	private static final String GAME_TYPE = "guessTheNumber";
	List<GuessTheNumberPlayer> players;
	String currentMessage = "";
	PlayerResponse lastMove = null;
	Position currentTurn = Position.North;
	Request currentRequest = Request.Null;
	
	int num1 = -1;
	int num2 = -1;
	int RandomNumber;
	
	MessageTransformer msgTransformer = new MessageTransformer();
	
	public GuessTheNumber() {
		players = new ArrayList<GuessTheNumberPlayer>();
		Random rn = new Random();
		RandomNumber = rn.nextInt(100) + 1;
	}
	
	@Override
	public void Play(PlayerResponse response) {
		if(response != null) {
			int playerNumber = response.getBid();
			lastMove = response;
			if(num1 == -1) {
				num1 = playerNumber;
				currentTurn = Position.South;
				currentMessage = "Guess a number between 1 & 100 " + getPlayer(currentTurn).getUser().getUser_name();
				update();
			}
			else {
				num2 = playerNumber;
				int deltaNum1 = RandomNumber - num1;
				int deltaNum2 = RandomNumber - num2;
				
				if(deltaNum1 < 0)
					deltaNum1 *= -1;
				if(deltaNum2 < 0)
					deltaNum2 *= -1;
				
				if(deltaNum1 < deltaNum2) {
					currentMessage = "The number was " + RandomNumber + " " + getPlayer(Position.North).getUser().getUser_name() + " chose the closest number";
				}
				else if(deltaNum1 == deltaNum2) {
					currentMessage = "The number was " + RandomNumber + " ,you both chose " + num1 + " and tied!";
				}
				else {
					currentMessage = "The number was " + RandomNumber + " " + getPlayer(Position.South).getUser().getUser_name() + " chose the closest number";
				}
				
				update();
				TimerTask t = new TimerTask() {
					@Override
					public void run() {
						gameover();
					}
				};
				new Timer().schedule(t, 5*1000);
			}
		} else {
		currentMessage = "Guess a number between 1 & 100 " + getPlayer(currentTurn).getUser().getUser_name();
		update();
		}
	}
	
	// Notify Game is over call super
	public void gameover() {
		for (GuessTheNumberPlayer p : players) {
			p.getUser().sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("gameover")));
		}
		gameOver();
	}
	
	// Broadcast updated game call super
	public void update() {
		for (GuessTheNumberPlayer player : players) {
			player.getUser().sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("game").setGame_message(getMessage(player))));
		}
		currentRequest = Request.Null;
	}
	
	public GameMessage getMessage(GuessTheNumberPlayer p) {
		GuessTheNumberMessage g = new GuessTheNumberMessage();
		g.setCurrentMessage(currentMessage);
		g.setCurrentRequest(currentRequest);
		g.setCurrentTurn(currentTurn.getNext(getPlayerPositionOffset(p)));
		g.setPlayers(getUserNamesAndPositions(p));
		g.setLastMove(lastMove);
		return g;
	}
	
	public Map<Position,String> getUserNamesAndPositions(GuessTheNumberPlayer p) {
		int offset = getPlayerPositionOffset(p);
		
		Map<Position,String> userPositions = new HashMap<Position,String>();
		for(GuessTheNumberPlayer player : players) {
			userPositions.put(player.getPosition().getNext(offset),player.getUser().getUser_name());
		}
		return userPositions;
	}
	
	public int getPlayerPositionOffset(GuessTheNumberPlayer p) {
		int offset = 0;
		Position position = p.getPosition();
		switch(position) {
		case North: offset = 2; break;
		case South: offset = 0; break;
		}
		return offset;
	}

	@Override
	public Boolean isGameFull() {
		if(players.size() == 2)
			return true;
		return false;
	}

	@Override
	public String getGameType() {
		return GAME_TYPE;
	}

	@Override
	public Boolean isCurrentTurn(String id) {
		Boolean turn = false;
		GuessTheNumberPlayer p = getPlayer(currentTurn);
		if(p.getUser().getSession_id().equals(id))
			turn = true;
		return turn;
	}
	
	public GuessTheNumberPlayer getPlayer(Position p) {
		GuessTheNumberPlayer g = null;
		for (GuessTheNumberPlayer guessTheNumberPlayer : players) {
			if(guessTheNumberPlayer.getPosition().equals(p)) {
				g = guessTheNumberPlayer;
				break;
			}
		}
		return g;
	}

	@Override
	public void addPlayer(User user) {
		Position position;
		if(numPlayers() == 0)
			position = Position.North;
		else
			position = Position.South;
		players.add(new GuessTheNumberPlayer(position,user));
		if(!isGameFull()) {
			currentMessage ="**Welcome to Guess the number**, " + user.getUser_name() + " just joined, waiting for " + (2-players.size()) + " more players";
			update();
		}
		if(isGameFull())
			Play(null);
	}

	@Override
	public void removePlayer(User user) {
		GuessTheNumberPlayer u = null;
		for (GuessTheNumberPlayer guessTheNumberPlayer : players) {
			if(guessTheNumberPlayer.getUser().equals(user)) {
				u = guessTheNumberPlayer;
				break;
			}
		}
		players.remove(u);
		if(players.size() == 0)
			super.removeGame();
	}

	@Override
	public int numPlayers() {
		return players.size();
	}

}
