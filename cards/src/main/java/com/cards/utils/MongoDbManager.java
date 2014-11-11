package com.cards.utils;

import com.mongodb.MongoClient;

public class MongoDbManager {
	private MongoDbManager() {}
	
	private static final MongoDbManager INSTANCE = new MongoDbManager();
	private static final String DbHost = "localhost";
	
	public static MongoClient mongoclient;
	
	public static MongoDbManager getInstance() {
		return INSTANCE;
	}
	
	public void init() {
		try {
			mongoclient = new MongoClient(DbHost);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
