package com.cards.server;

import naga.NIOSocket;
import naga.SocketObserver;
import naga.eventmachine.DelayedEvent;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;

public class User implements SocketObserver {
	private final NIOSocket socket;
    private DelayedEvent disconnectEvent;
    
    User(NIOSocket socket)
    {
        this.socket = socket;
        this.socket.setPacketReader(new AsciiLinePacketReader());
        this.socket.setPacketWriter(new AsciiLinePacketWriter());
        this.socket.listen(this);
    }
    
	@Override
	public void connectionBroken(NIOSocket socket, Exception e) {
		System.out.println("socket disconnected on : " + socket.getPort());
		socket.closeAfterWrite();
	}

	@Override
	public void connectionOpened(NIOSocket socket) {
		System.out.println("socket connected on : " + socket.getPort());
	}

	@Override
	public void packetReceived(NIOSocket socket, byte[] packet) {
		//Send to MessageHandler
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
}
