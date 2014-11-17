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
	TestClient() {}
	
	public static JSONObject input() {
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
					object.put("request", "forgot_password");
					System.out.println("Enter Username: ");
					object.put("user_name", reader.readLine());
					System.out.println("Enter email: ");
					object.put("email", reader.readLine());
					
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
                                    System.out.println(new String(packet));
                                    String login = input().toString();
                                    System.out.println(login);
                                    socket.write(login.getBytes());
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