package com.cards.server;

import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import com.cards.message.RequestPacket;
import com.cards.message.ResponsePacket;
import com.cards.utils.MessageTransformer;
import com.cards.utils.ParamValidator;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class LoginManager {
	private LoginManager() {
		msgTransformer = new MessageTransformer();
		paramValidator = new ParamValidator();
	}
	
	private static LoginManager INSTANCE = new LoginManager();
	private final int TIMEOUT = 1000*60*5;
	
	private MessageTransformer msgTransformer;
	private ParamValidator paramValidator;
	private String salt;
	
	public static LoginManager getInstance() {
		return INSTANCE;
	}
	
	public void sendUUID(User user) {
		String session_id = UUID.randomUUID().toString();
		user.setSession_id(session_id);
		user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("session_id").setMessage(session_id)));
	}
	
	public void CreateAccount(RequestPacket request, DBCollection coll, User user) {
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
				
				user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("create_success").setMessage("account created")));
				sendUUID(user);
				String user_name = request.getUser_name();
				user.setUser_name(user_name);
				UserManager.getInstance().addAuthenticatedUser(user);
				System.out.println("User " + user_name + " created account & authenticated");
				GameManager.getInstance().sendLobbyInfo();
				} else {
					user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("email already taken")));
				}
			} else {
				user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("username already taken")));
			}
			
		}catch(Exception e) {
			user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("Invalid username or email")));
		}
	}
	
	public void ForgotPassword(RequestPacket request, DBCollection coll, User user) {
		try {
			// Check that given username and email match a record in DB
			DBCursor cursor = coll.find(new BasicDBObject("user_name", request.getUser_name()));
			if(cursor.hasNext()) {
				if(request.getEmail().equalsIgnoreCase(((BasicDBObject) cursor.next()).getString("email"))) {
					user.scheduleTimeoutEvent(TIMEOUT);
					user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("success")));
				}
				else
					user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("Invalid email")));
			} else
				user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("Invalid username")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void Login(RequestPacket request, DBCollection coll, User user) {
		try {
			// Use cursor to find query -> password
			DBCursor cursor = coll.find(new BasicDBObject("user_name", request.getUser_name()));
			
			if(cursor.hasNext()) {
				BasicDBObject ob = (BasicDBObject) cursor.next();
				// Check that user_password matches stored_password
				if (request.getHash_password().equals(ob.getString("hash_password"))) {
					user.cancelTimeoutEvent();
					sendUUID(user);
					String user_name = ob.getString("user_name");
					user.setUser_name(user_name);
					UserManager.getInstance().addAuthenticatedUser(user);
					System.out.println("User " + user_name + " authenticated");
					GameManager.getInstance().sendLobbyInfo();
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
	
	public void RetrieveSalt(RequestPacket request, DBCollection coll, User user) {
		try {
			// Use cursor to find query -> password
			DBCursor cursor = coll.find(new BasicDBObject("user_name", request.getUser_name()));
			
			if(cursor.hasNext()) {
				String stored_salt = cursor.next().get("salt").toString();
				user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("salt").setMessage(stored_salt)));
			}
			else {
				salt = BCrypt.gensalt(12);
				user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("salt").setMessage(salt)));
			}
				
		} catch(Exception e) {
			user.sendMessage(msgTransformer.writeMessage(new ResponsePacket().setResponse("response").setMessage("Invalid username or password")));
		}
	}
}
