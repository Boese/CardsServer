package com.cards.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import naga.NIOService;
import naga.NIOSocket;
import naga.SocketObserver;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;

public class TestClient {
	private static String session_id;
	private static String salt = null;
	TestClient() {}
	
	public static JSONObject login() {
		//Start new thread to capture input. Write to NIOSocket
		JSONObject object = new JSONObject();
		
		new Runnable() {

			@Override
			public void run() {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				try {
					/*
					 * Create Account {
					 * 	request: 'create_account'
					 * 	user_name: 'user_name',
					 * 	email: 'email',
					 * 	hash_password: 'hash_password'
					 * }
					 * 
					 * Login {
					 * request: 'login',
					 * user_name: 'user_name',
					 * hash_password: 'hash_password'
					 * }
					 * 
					 * Forgot password {
					 * request: 'forgot_password',
					 * user_name: 'user_name',
					 * email: 'email'
					 * }
					 */
					object.put("request", "login");
					System.out.println("Enter Username: ");
					object.put("user_name", reader.readLine());
					System.out.println("Enter password: ");
					String password = reader.readLine();
					object.put("hash_password", BCrypt.hashpw(password, salt));
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}.run();
		
        return object;
	}
	
	public static JSONObject lobby() {
		//Start new thread to capture input. Write to NIOSocket
		JSONObject object = new JSONObject();
		
		new Runnable() {

			@Override
			public void run() {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				try {
					/*
					 * Join Game {
					 * 	request: 'join_game',
					 * 	session_id: 'session_id',
					 * 	game_type: 'game_type',
					 * 	game_id: 'game_id'
					 * 	move: 'move'
					 * }
					 */
					object.put("request", "new_game");
					object.put("session_id", session_id);
					object.put("game_type", "pinochle");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}.run();
		
        return object;
	}
	
	public static void main(String... args)
    {
            try
            {
                    // Start up the service.
                    NIOService service = new NIOService();

                    // Open our socket.
                    NIOSocket socket = service.openSocket("localhost", 5217);

                    // Use regular 1 byte header reader/writer
                    socket.setPacketReader(new AsciiLinePacketReader());
                    socket.setPacketWriter(new AsciiLinePacketWriter());
                    

                    // Start listening to the socket.
                    socket.listen(new SocketObserver()
                    {
                    	@Override
                        public void connectionOpened(NIOSocket nioSocket)
                        {
                                System.out.println("connection opened");
                        }
                    	@Override
			            public void packetSent(NIOSocket socket, Object tag)
			            {
			                System.out.println("Packet sent");
			            }
                    	@Override
		            	public void packetReceived(NIOSocket socket, byte[] packet)
                        {
                                try
                                {
                                    JSONObject object = new JSONObject(new String(packet));
                                    if(object.optString("response").equalsIgnoreCase("salt") ||
                                    		object.optString("response").equalsIgnoreCase("login")) {
                                    	salt = object.optString("message");
                                    	System.out.println("Salt : " + salt);
	                                    String login = login().toString();
	                                    System.out.println(login);
	                                    socket.write(login.getBytes());
                                    }
                                    if(object.optString("response").equalsIgnoreCase("session_id")) {
                                    	session_id = object.optString("message");
                                    	System.out.println("Session_id : " + session_id);
                                    	String lobby = lobby().toString();
                                    	System.out.println(lobby);
                                    	socket.write(lobby.getBytes());
                                    }
                                    else {
                                    	System.out.println(new String(packet));
                                    }
                                }
                                catch (Exception e)
                                {
                                        e.printStackTrace();
                                }
                        }
                    	@Override
                        public void connectionBroken(NIOSocket nioSocket, Exception exception)
                        {
                                System.out.println("Connection failed.");
                                // Exit the program.
                                System.exit(-1);
                        }
                });
                    // Read IO until process exits.
                    while (true)
                    {
                            service.selectNonBlocking();
                    }
            }
            catch (Exception e)
            {
                    e.printStackTrace();
            }
    }

}