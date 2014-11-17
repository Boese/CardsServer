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
    
    User(NIOSocket socket)
    {
        this.socket = socket;
        this.state = UserState.PreLogin;
        this.router = new UserRouter();
        this.socket.setPacketReader(new AsciiLinePacketReader());
        this.socket.setPacketWriter(new AsciiLinePacketWriter());
        this.socket.listen(this);
    }
    
	@Override
	public void connectionBroken(NIOSocket socket, Exception e) {
		System.out.println("socket disconnected on : " + socket.getPort());
		socket.closeAfterWrite();
		GameManager.getInstance().removeUserFromGame(this);
		UserManager.getInstance().removeUser(this);
	}

	@Override
	public void connectionOpened(NIOSocket socket) {
		System.out.println("socket connected on : " + socket.getPort());
		router.routeUser(this, null);
	}

	@Override
	public void packetReceived(NIOSocket socket, byte[] packet) {
		router.routeUser(this, new String(packet));
	}

	@Override
	public void packetSent(NIOSocket socket, Object packet) {
	}
	
	public void sendMessage(String message) {
		socket.write(message.getBytes());
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
}
