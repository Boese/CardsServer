package com.cards.server;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClient;

import naga.NIOSocket;
import naga.eventmachine.EventMachine;

public class UserManager {
	private static final UserManager INSTANCE = new UserManager();
	
	public EventMachine eventmachine;
	public UserRouter userrouter;
	public MongoClient mongoclient;
	private List<User> users;
	// Games
	
	private UserManager() {}
	
	public static UserManager getInstance() {
		return INSTANCE;
	}
	
	// **Initialize**
	public void init(EventMachine eventmachine) {
		this.eventmachine = eventmachine;
		this.userrouter = new UserRouter();
		this.users = new ArrayList<User>();
	}
	
	// **Add/Remove Users**
	public void addUser(NIOSocket socket) {
		User user = new User(socket);
		users.add(user);
	}
	
	public void removeUser(User user) {
		users.remove(user);
	}
	
	// **Create/Join Games**
	public void startNewGame(User user, String gametype) {
		/*
		 * Start a new game instance of game type with user
		 * Broadcast
		 */
	}
	
	public void joinRandomGame(User user, String gametype){
		/*
		 * Put user in a random game of game type
		 * Broadcast
		 */
	}
	
	public void joinGame(User user, int gamenum) {
		/*
		 * Put user in a selected game
		 * Broadcast
		 */
	}
}
