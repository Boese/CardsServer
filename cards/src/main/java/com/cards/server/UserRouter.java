package com.cards.server;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.cards.utils.MongoDbManager;
import com.cards.utils.UserState;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class UserRouter {
	private static final int LOGIN_TIMEOUT = 10*1000;
	private String salt;
	
	UserRouter() {}
	
	public String routeUser(User user, String message) {
		switch(user.getUserState()) {
		case PreLogin: SendSalt(user);
			break;
		case Login: HandleLogin(user, message);
			break;
		case Lobby:
			/*
			 * Join Game {
			 * 	request: 'join_game',
			 * 	session_id: 'session_id',
			 * 	game_type: 'game_type',
			 * 	game_id: 'game_id'
			 * }
			 */
			/*
			 * Create Game {
			 * 	request: 'new_game',
			 * 	session_id: 'session_id',
			 * 	game_type: 'game_type'
			 * }
			 */
			break;
		case Game:
			/*
			 * Quit Game {
			 * 	request: 'quit_game',
			 *  session_id: 'session_id'
			 * }
			 * user.state = Lobby
			 */
			/*
			 * Play {
			 * 	request: 'play',
			 * 	session_id: 'session_id',
			 * 	move: 'move'
			 * }
			 */
			break;
		default: 
			break;
		}
		return null;
	}
	
	private void SendSalt(User user) {
		salt = BCrypt.gensalt(12);
		user.sendMessage("salt : " + salt);
		user.setUserState(UserState.Login);
	}
	
	// Create Account, Forgot Password, Login
	private void HandleLogin(User user, String message) {
		try {
			JSONObject object = new JSONObject(message);
			String request = object.optString("request");
			
			// Get DB cardsDB
			MongoDbManager.getInstance();
			DB db = MongoDbManager.mongoclient.getDB( "cardsDB" );
			
			// Get DBCollection users
			DBCollection coll = db.getCollection("users");
			
			if(request.equals("create_account"))
				CreateAccount(user, object, coll);
			else if(request.equals("forgot_password"))
				ForgotPassword(user, object, coll);
			else if(request.equals("login"))
				Login(user, object, coll);
		}catch(Exception e) {
			e.printStackTrace();
			UserManager.getInstance().removeUser(user);
		}
	}
	
	private void CreateAccount(User user, JSONObject object, DBCollection coll) {
		try {
			System.out.println(object.toString(2));
			String user_name = object.optString("user_name");
			
			// Create Query with user_name
			BasicDBObject queryUserName = new BasicDBObject("user_name", user_name);
			
			// Use cursor to find query
			DBCursor cursor = coll.find(queryUserName);
			
			/*
			 * Create Account {
			 * 	request: 'create_account'
			 * 	user_name: 'user_name',
			 * 	email: 'email',
			 * 	hash_password: 'hash_password'
			 * }
			 * 
			 * .append('salt',salt)
			 */
			String email = object.optString("email");
			String hash_password = object.optString("hash_password");
			
			// Create Query with email
			BasicDBObject queryEmail = new BasicDBObject("email",email);
			DBCursor cursorEmail = coll.find(queryEmail);
			
			// Check if username or email is in db
			if(cursor != null) {
				if(cursorEmail != null) {
				// Save user to db
				BasicDBObject doc = new BasicDBObject("user_name", user_name)
		        	.append("email", email)
		        	.append("salt", salt)
		        	.append("hash_password", hash_password);
				coll.insert(doc);
				
				user.sendMessage("Successfully created account");
				} else {
					user.sendMessage("email already in use");
				}
			} else {
				user.sendMessage("username already taken");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			UserManager.getInstance().removeUser(user);
		}
	}
	
	private void ForgotPassword(User user, JSONObject object, DBCollection coll) {
		try {
			String username = object.optString("username");
			
			// Create Query with user_name
			BasicDBObject queryUserName = new BasicDBObject("username", username);
			
			// Use cursor to find query
			DBCursor cursor = coll.find(queryUserName);
			
			/*
			 * Forgot Password {
			 * 	request: 'forgot_password',
			 * 	user_name: 'user_name',
			 * 	email: 'email'
			 * }
			 */
			
			// check if username and email is in db
			// Send password reset link to email
		} catch (Exception e) {
			e.printStackTrace();
			UserManager.getInstance().removeUser(user);
		}
	}
	
	private void Login(User user, JSONObject object, DBCollection coll) {
		try {
			String username = object.optString("username");
			
			// Create Query with user_name
			BasicDBObject queryUserName = new BasicDBObject("username", username);
			
			// Use cursor to find query
			DBCursor cursor = coll.find(queryUserName);
			
			/*
			 * Login {
			 * 	request: 'login',
			 * 	user_name: 'user_name',
			 * 	hash_token: 'hash_token'
			 * }
			 */
			//hash_token
			String hash_token = object.optString("hash_token");
			
			String salted_hash_token = BCrypt.hashpw(hash_token, salt);
			
			// Salt users password with token
			String salted_hash_password = BCrypt.hashpw(cursor.toString(), salt);

			// Check that an unencrypted password matches one that has
			// previously been hashed
			if (salted_hash_token.equals(salted_hash_password))
			    user.sendMessage("Login Success");
			user.sendMessage("Login Failed");
		} catch (Exception e) {
			e.printStackTrace();
			UserManager.getInstance().removeUser(user);
		}
	}
}
