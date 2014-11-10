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
		socket.closeAfterWrite();
		System.out.println("socket disconnected on : " + socket.getPort());
	}

	@Override
	public void connectionOpened(NIOSocket socket) {
		// We start by scheduling a disconnect event for the login.
//        disconnectEvent = server.getEventMachine().executeLater(new Runnable()
//        {
//            public void run()
//            {
//                socket.write("Disconnecting due to inactivity".getBytes());
//                socket.closeAfterWrite();
//            }
//        }, LOGIN_TIMEOUT);
//
//        // Send the request to log in.
//        socket.write("Please enter your name:".getBytes());
		System.out.println("socket connected on : " + socket.getPort());
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
        disconnectEvent = UserManager.getInstance().eventmachine.executeLater(new Runnable()
        {
            public void run()
            {
                socket.write("Disconnected due to inactivity.".getBytes());
                socket.closeAfterWrite();
            }
        }, INACTIVITY_TIMEOUT);
    }
}
