package com.cards.server;

import com.cards.message.RequestPacket;
import com.cards.utils.MessageTransformer;
import com.cards.utils.MongoDbManager;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class UserRouter {
	private MessageTransformer msgTransformer;
	UserRouter() {
		msgTransformer = new MessageTransformer();
	}
	
	public void routeUser(User user, String message) {
		try {
			// Get Login packet
			RequestPacket reqPacket = (RequestPacket) msgTransformer.getMessage(message, RequestPacket.class);
			
			// Get DB cardsDB
			MongoDbManager.getInstance();
			DB db = MongoDbManager.mongoclient.getDB( "cardsDB" );
			
			// Get DBCollection users
			DBCollection coll = db.getCollection("users");
			
			// Redirect to request
			switch(reqPacket.getRequest()) {
				// Available methods when not authenticated
				case "create_account": LoginManager.getInstance().CreateAccount(reqPacket, coll, user); break;
				case "forgot_password": LoginManager.getInstance().ForgotPassword(reqPacket, coll, user); break;
				case "login": LoginManager.getInstance().Login(reqPacket, coll, user); break;
				case "send_salt" : LoginManager.getInstance().RetrieveSalt(reqPacket, coll, user); break;
				
				// Available methods when authenticated
				default : {
					if(!reqPacket.getSession_id().equalsIgnoreCase(user.getSession_id())) {
						throw new Exception("session_id invalid!");
					}
					switch(reqPacket.getRequest()) {
						case "join_game": GameManager.getInstance().joinSelectedGame(user, reqPacket.getGame_id()); break;
						case "random_game": GameManager.getInstance().joinRandomGame(user, reqPacket.getGame_type()); break;
						case "new_game": GameManager.getInstance().createGame(user, reqPacket.getGame_type()); break;
						case "quit_game": GameManager.getInstance().removeUserFromGame(user); break;
						case "play": GameManager.getInstance().play(user, reqPacket.getMove()); break;
						default : throw new Exception("invalid request!");
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			UserManager.getInstance().removeUser(user);
		}
	}
}
