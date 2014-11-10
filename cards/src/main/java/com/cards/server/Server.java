package com.cards.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import naga.ConnectionAcceptor;
import naga.NIOServerSocket;
import naga.NIOSocket;
import naga.ServerSocketObserver;
import naga.eventmachine.EventMachine;

public class Server implements ServerSocketObserver {

	private static EventMachine eventmachine;
	private static SSLEngine engine;
	private static NIOServerSocket serversocket;
	private static int port = 5217;
	private static InetSocketAddress address = new InetSocketAddress("localhost",port);
	
	public Server() {
		try{
			//Event Machine for delayed tasks
		eventmachine = new EventMachine();
			//SSLEngine for encrypted communications
		engine = SSLContext.getDefault().createSSLEngine();
			//Start Server on specified InetSocketAddress
		serversocket = eventmachine.getNIOService().openServerSocket(address,0);
		System.out.println("Server started on " + serversocket.getAddress());
			//Start listening for sockets
		serversocket.listen(this);
		serversocket.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
		eventmachine.start();
			//Initialize UserManager
		UserManager.getInstance().init(eventmachine);
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
		UserManager.getInstance().addUser(socket);
	}

	@Override
	public void serverSocketDied(Exception e) {
		e.printStackTrace();
	}
	
	public SSLEngine getEngine() {
		return engine;
	}
}
