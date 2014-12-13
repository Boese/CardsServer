package com.cards.server;


import naga.NIOSocket;
import naga.SocketObserver;
import naga.eventmachine.DelayedEvent;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;

public class User implements SocketObserver {
	private final NIOSocket socket;
	private final int TIMEOUT = 1000*60*5; 
    private DelayedEvent disconnectEvent;
    private UserRouter router;
    private String user_name;
    private String session_id;
    private String game_id;
    private int port;
    
    User(NIOSocket socket)
    {
        this.socket = socket;
        this.setPort(socket.getPort());
        this.router = new UserRouter();
        this.socket.setPacketReader(new AsciiLinePacketReader());
        this.socket.setPacketWriter(new AsciiLinePacketWriter());
        this.socket.listen(this);
    }
    
	@Override
	public void connectionBroken(NIOSocket socket, Exception e) {
		socket.closeAfterWrite();
		UserManager.getInstance().removeUser(this);
	}

	@Override
	public void connectionOpened(NIOSocket socket) {
		scheduleTimeoutEvent(TIMEOUT);
	}

	@Override
	public void packetReceived(NIOSocket socket, byte[] packet) {
		if(user_name == null)
			System.out.println("message received from user " + port + " : " + new String(packet));
		else
			System.out.println("message received from user " + user_name + " : " + new String(packet));
		router.routeUser(this, new String(packet));
	}

	@Override
	public void packetSent(NIOSocket socket, Object packet) {
	}
	
	public void sendMessage(String message) {
		socket.write(message.getBytes());
		if(user_name == null)
			System.out.println("message sent to user " + port + " : " + message);
		else
			System.out.println("message sent to user " + user_name + " : " + message);
	}
	
	public void closeSocket() {
		socket.closeAfterWrite();
	}
	
	public void scheduleTimeoutEvent(int TIMEOUT)
    {
        // Cancel the last disconnect event, schedule another.
        if (disconnectEvent != null) disconnectEvent.cancel();
        disconnectEvent = UserManager.getInstance().eventmachine.executeLater(new Runnable()
        {
            public void run()
            {
                socket.closeAfterWrite();
            }
        }, TIMEOUT);
    }
	
	public void cancelTimeoutEvent() {
		if(disconnectEvent != null) disconnectEvent.cancel();
	}

	public String getGame_id() {
		return game_id;
	}

	public void setGame_id(String game_id) {
		this.game_id = game_id;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
}
