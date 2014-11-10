package com.cards.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import naga.ConnectionAcceptor;
import naga.NIOServerSocket;
import naga.NIOSocket;
import naga.ServerSocketObserver;
import naga.SocketObserver;
import naga.eventmachine.DelayedEvent;
import naga.eventmachine.EventMachine;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;

public class Server implements ServerSocketObserver {

	private static EventMachine eventmachine;
	private static NIOServerSocket serversocket;
	private static List<User> users;
	private static int port = 5217;
	
	private final static long LOGIN_TIMEOUT = 30 * 1000;
	
	public Server() {
		try{
		eventmachine = new EventMachine();
		serversocket = eventmachine.getNIOService().openServerSocket(port);
		serversocket.listen(this);
		serversocket.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
		eventmachine.start();
		users = new ArrayList<User>();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void acceptFailed(IOException e) {
		e.printStackTrace();
	}

	@Override
	public void newConnection(NIOSocket socket) {
		System.out.println("socket connected on : " + socket.getPort());
		users.add(new User(this,socket));
	}

	@Override
	public void serverSocketDied(Exception e) {
		e.printStackTrace();
	}
	
	public void removeUser(User user) {
		users.remove(user);
		System.out.println("socket disconnected on : " + user.socket.getPort());
	}
	
	public EventMachine getEventMachine()
    {
        return eventmachine;
    }
	
	private static class User implements SocketObserver {

        private final Server server;
        private final NIOSocket socket;
        private DelayedEvent disconnectEvent;
        
        private User(Server server, NIOSocket socket)
        {
            this.server = server;
            this.socket = socket;
            this.socket.setPacketReader(new AsciiLinePacketReader());
            this.socket.setPacketWriter(new AsciiLinePacketWriter());
            this.socket.listen(this);
        }
        
		@Override
		public void connectionBroken(NIOSocket socket, Exception e) {
			socket.closeAfterWrite();
			server.removeUser(this);
		}

		@Override
		public void connectionOpened(NIOSocket socket) {
			// We start by scheduling a disconnect event for the login.
//            disconnectEvent = server.getEventMachine().executeLater(new Runnable()
//            {
//                public void run()
//                {
//                    socket.write("Disconnecting due to inactivity".getBytes());
//                    socket.closeAfterWrite();
//                }
//            }, LOGIN_TIMEOUT);
//
//            // Send the request to log in.
//            socket.write("Please enter your name:".getBytes());
			socket.write(new String("connected").getBytes());
		}

		@Override
		public void packetReceived(NIOSocket socket, byte[] packet) {
			// Reset inactivity timer.
            //scheduleInactivityEvent(5000);
			socket.write(packet);
		}

		@Override
		public void packetSent(NIOSocket socket, Object packet) {
			
		}
		
		private void scheduleInactivityEvent(int INACTIVITY_TIMEOUT)
        {
            // Cancel the last disconnect event, schedule another.
            if (disconnectEvent != null) disconnectEvent.cancel();
            disconnectEvent = server.getEventMachine().executeLater(new Runnable()
            {
                public void run()
                {
                    socket.write("Disconnected due to inactivity.".getBytes());
                    socket.closeAfterWrite();
                }
            }, INACTIVITY_TIMEOUT);
        }
		
	}


}
