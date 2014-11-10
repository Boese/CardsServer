package com.cards.server;

import java.util.Random;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.cards.utils.UserState;

public class UserRouter {
	private static final int LOGIN_TIMEOUT = 10*1000;
	private Random random;
	private int randomNumber;
	
	UserRouter() {
		randomNumber = random.nextInt();
	}
	
	public String routeUser(User user, String message) {
		switch(user.getUserState()) {
		case Login: 
			/*
			 * Create Account {
			 * 	request: 'create_account'
			 * 	user_name: 'user_name',
			 * 	email: 'email',
			 * 	password: 'password'
			 * }
			 */
			/*
			 * Forgot Password {
			 * 	request: 'forgot_password',
			 * 	user_name: 'user_name',
			 * 	email: 'email'
			 * }
			 */
			/*
			 * Login {
			 * 	request: 'login'
			 * 	user_name: 'user_name'
			 * 	password: 'password'
			 * }
			 */
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
	
	// Authenticate user against hashed number
	private Boolean Authenticate(JSONObject login) {
		/*
		 * Login {
		 * 	request: 'login'
		 * 	user_name: 'user_name'
		 * 	password: 'password'
		 * }
		 */
		
		String user_name = login.optString("user_name");
		String password = login.optString("password");
		
		//Look up password in MongoDb
		String hashed = "";

		// Check that an unencrypted password matches one that has
		// previously been hashed
		if (BCrypt.checkpw(password, hashed))
		    return true;
		return false;
	}
}
