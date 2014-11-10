package com.cards.server;

import java.util.ArrayList;
import java.util.List;

import naga.NIOSocket;
import naga.eventmachine.EventMachine;

public class UserManager {
	private static final UserManager INSTANCE = new UserManager();
	
	public EventMachine eventmachine;
	private List<User> users;
	
	private UserManager() {}
	
	public static UserManager getInstance() {
		return INSTANCE;
	}
	
	public void init(EventMachine eventmachine) {
		this.eventmachine = eventmachine;
		this.users = new ArrayList<User>();
	}
	
	public void addUser(NIOSocket socket) {
		users.add(new User(socket));
	}
	
	public void removeUser(User user) {
		users.remove(user);
	}
}
