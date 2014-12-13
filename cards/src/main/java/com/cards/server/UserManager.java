package com.cards.server;

import java.util.ArrayList;
import java.util.List;

import naga.NIOSocket;
import naga.eventmachine.EventMachine;

public class UserManager {
	private UserManager() {}
	
	private static final UserManager INSTANCE = new UserManager();
	private List<User> users;
	private List<User> authenticatedUsers;
	
	// Common
	public EventMachine eventmachine;
	
	public static UserManager getInstance() {
		return INSTANCE;
	}
	
	// **Initialize**
	public void init(EventMachine eventmachine) {
		this.eventmachine = eventmachine;
		this.users = new ArrayList<User>();
		this.authenticatedUsers = new ArrayList<User>();
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
	
	public void addAuthenticatedUser(User user) {
		authenticatedUsers.add(user);
		users.remove(user);
	}
	
	public User isLoggedIn(String username) {
		for (User user : authenticatedUsers) {
			if(user.getUser_name().equals(username))
				return user;
		}
		return null;
	}
	
	public void removeUser(User user) {
		try {
			GameManager.getInstance().removeUserFromGame(user);
			users.remove(user);
			if(user.getUser_name() != null)
				System.out.println("User " + user.getUser_name() + " disconnected");
			else
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
