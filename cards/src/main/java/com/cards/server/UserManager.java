package com.cards.server;

import java.util.ArrayList;
import java.util.List;

import naga.NIOSocket;
import naga.eventmachine.EventMachine;

public class UserManager {
	private UserManager() {}
	
	private static final UserManager INSTANCE = new UserManager();
	private List<User> users;
	
	// Common
	public EventMachine eventmachine;
	
	public static UserManager getInstance() {
		return INSTANCE;
	}
	
	// **Initialize**
	public void init(EventMachine eventmachine) {
		this.eventmachine = eventmachine;
		this.users = new ArrayList<User>();
		System.out.println("**User Manager started**");
	}
	
	// **Add/Remove Users**
	public void addUser(NIOSocket socket) {
		try {
			User user = new User(socket);
			users.add(user);
			System.out.println("User " + user.getPort() + " connected");
			System.out.println("Number of users connected: " + users.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removeUser(User user) {
		try {
			GameManager.getInstance().removeUserFromGame(user);
			users.remove(user);
			System.out.println("User " + user.getPort() + " disconnected");
			System.out.println("Number of users connected: " + users.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void broadcast(String message) {
		for (User user : users) {
			user.sendMessage(message);
		}
	}
}
