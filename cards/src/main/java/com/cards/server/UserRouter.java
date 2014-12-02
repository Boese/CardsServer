package com.cards.server;

import java.util.UUID;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.cards.message.RequestPacket;
import com.cards.message.ResponsePacket;
import com.cards.utils.MessageTransformer;
import com.cards.utils.MongoDbManager;
import com.cards.utils.ParamValidator;
import com.cards.utils.UserState;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class UserRouter {
	private static final int LOGIN_TIMEOUT = 5*60*1000;
	private String salt;
	private MessageTransformer msgTransformer;
	private ParamValidator paramValidator;
	
	UserRouter() {
		msgTransformer = new MessageTransformer();
		paramValidator = new ParamValidator();
	}
	
	public String routeUser(User user, String message) {
		user.cancelTimeoutEvent();
		switch(user.getUserState()) {
			case PreLogin: SendSalt(user);
				break;
			case Login: HandleLogin(user, message);
				break;
			case Lobby: HandleLobby(user, message);
				break;
		}
		return null;
	}
	
	// Create Account, Forgot Password, Login
	private void HandleLogin(User user, String message) {
		try {
			// Get Login packet
			RequestPacket loginPacket = (RequestPacket) msgTransformer.getMessage(message, RequestPacket.class);
			
			// Get DB cardsDB
			MongoDbManager.getInstance();
			DB db = MongoDbManager.mongoclient.getDB( "cardsDB" );
			
			// Get DBCollection users
			DBCollection coll = db.getCollection("users");
			
			// Redirect to request
			switch(loginPacket.getRequest()) {
				case "create_account": CreateAccount(loginPacket, coll, user); break;
				case "forgot_password": ForgotPassword(loginPacket, coll, user); break;
				case "login": Login(loginPacket, coll, user); break;
				case "send_salt" : RetrieveSalt(loginPacket, coll, user); break;
			}
		}catch(Exception e) {
			e.printStackTrace();
			UserManager.getInstance().removeUser(user);
		}
	}
	
	private void HandleLobby(User user, String message) {
		try {
			RequestPacket reqPacket = (RequestPacket) msgTransformer.getMessage(message, RequestPacket.class);
			
			if(!reqPacket.getSession_id().equalsIgnoreCase(user.getSession_id())) {
				throw new Exception("session_id invalid");
			}
			
			switch(reqPacket.getRequest()) {
				case "join_game": GameManager.getInstance().joinSelectedGame(user, reqPacket.getGame_id()); break;
				case "random_game": GameManager.getInstance().joinRandomGame(user, reqPacket.getGame_type()); break;
				case "new_game": GameManager.getInstance().createGame(user, reqPacket.getGame_type()); break;
				case "quit_game": GameManager.getInstance().removeUserFromGame(user); break;
				case "play": GameManager.getInstance().play(user, reqPacket.getMove()); break;
			}
		} catch(Exception e) {
			e.printStackTrace();
			UserManager.getInstance().removeUser(user);
		}
	}
	
	// Send random salt to user on connect
	private void SendSalt(User user) {
		salt = BCrypt.gensalt();
		user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("salt").setMessage(salt)));
		user.setUserState(UserState.Login);
		user.scheduleTimeoutEvent(LOGIN_TIMEOUT);
	}
	
	private void sendUUID(User user) {
		String session_id = UUID.randomUUID().toString();
		user.setSession_id(session_id);
		user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("session_id").setMessage(session_id)));
	}
	
	private void CreateAccount(RequestPacket request, DBCollection coll, User user) {
		try {
			if(!(paramValidator.validate(request.getUser_name(), "username") && 
					paramValidator.validate(request.getEmail(), "email"))) {
				user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("Invalid username or email")));
				return;
			}
			// Check if username or email is already taken
			if(!coll.find(new BasicDBObject("user_name", request.getUser_name())).hasNext()) {
				if(!coll.find(new BasicDBObject("email", request.getEmail())).hasNext()) {
				// Save user to db
				BasicDBObject doc = new BasicDBObject()
					.append("user_name", request.getUser_name())
		        	.append("email", request.getEmail())
		        	.append("hash_password", request.getHash_password())
					.append("salt", salt);
				coll.insert(doc);
				
				user.setUserState(UserState.Lobby);
				user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("create_success").setMessage("account created")));
				sendUUID(user);
				System.out.println("User " + user.getPort() + " created account & authenticated");
				} else {
					user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("email already taken")));
				}
			} else {
				user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("username already taken")));
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void ForgotPassword(RequestPacket request, DBCollection coll, User user) {
		try {
			// Check that given username and email match a record in DB
			DBCursor cursor = coll.find(new BasicDBObject("user_name", request.getUser_name()));
			if(cursor.hasNext()) {
				if(request.getEmail().equalsIgnoreCase(((BasicDBObject) cursor.next()).getString("email")))
					user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("success")));
				else
					user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("Invalid email")));
			} else
				user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("Invalid username")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void Login(RequestPacket request, DBCollection coll, User user) {
		try {
			// Use cursor to find query -> password
			DBCursor cursor = coll.find(new BasicDBObject("user_name", request.getUser_name()));
			
			if(cursor.hasNext()) {
				String salted_hash_password = BCrypt.hashpw(((BasicDBObject) cursor.next()).getString("hash_password"), salt);
				
				// Check that an unencrypted password matches one that has
				// previously been hashed
				if (request.getHash_password().equals(salted_hash_password)) {
					user.setUserState(UserState.Lobby);
					sendUUID(user);
					System.out.println("User " + user.getPort() + " authenticated");
				}
				else {
					user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("Invalid username or password")));
				}
			} else
				user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("Invalid username or password")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void RetrieveSalt(RequestPacket request, DBCollection coll, User user) {
		try {
			// Use cursor to find query -> password
			DBCursor cursor = coll.find(new BasicDBObject("user_name", request.getUser_name()));
			
			if(cursor.hasNext()) {
				String stored_salt = cursor.next().get("salt").toString();
				user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("stored_salt").setMessage(stored_salt)));
			}
			else
				user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("stored_salt_failed").setMessage("Invalid username or password")));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
