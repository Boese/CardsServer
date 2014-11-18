package com.cards.server;

import com.cards.utils.UserState;

import naga.NIOSocket;
import naga.SocketObserver;
import naga.eventmachine.DelayedEvent;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;

public class User implements SocketObserver {
	private final NIOSocket socket;
    private DelayedEvent disconnectEvent;
    private UserRouter router;
    private UserState state;
    private String session_id;
    private String game_id;
    private int port;
    
    User(NIOSocket socket)
    {
        this.socket = socket;
        this.setPort(socket.getPort());
        this.state = UserState.PreLogin;
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
		router.routeUser(this, null);
	}

	@Override
	public void packetReceived(NIOSocket socket, byte[] packet) {
		System.out.println("message received from user " + this.getPort() + " : " + new String(packet));
		router.routeUser(this, new String(packet));
	}

	@Override
	public void packetSent(NIOSocket socket, Object packet) {
	}
	
	public void sendMessage(String message) {
		socket.write(message.getBytes());
		System.out.println("message sent to user " + this.port + " : " + message);
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
	
	public void setUserState(UserState state) {
		this.state = state;
	}
	
	public UserState getUserState() {
		return state;
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
}
