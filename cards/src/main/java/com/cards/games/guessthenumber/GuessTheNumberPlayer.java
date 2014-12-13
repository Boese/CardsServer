package com.cards.games.guessthenumber;

import com.cards.games.pinochle.enums.Position;
import com.cards.server.User;

public class GuessTheNumberPlayer {
	private Position position; // enum position
	private int number; //current cards List<enum cards>
	private User user;
	
	public GuessTheNumberPlayer(Position p, User user) {
		this.position = p;
		this.user = user;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
