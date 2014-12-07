package com.cards.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import com.cards.utils.MongoDbManager;

import naga.ConnectionAcceptor;
import naga.NIOServerSocket;
import naga.NIOService;
import naga.NIOSocket;
import naga.ServerSocketObserver;
import naga.eventmachine.EventMachine;

public class Server implements ServerSocketObserver {

	private static EventMachine eventmachine;
	private static SSLEngine engine;
	private static NIOServerSocket serversocket;
	private static int port = 5217;
	private static String ip = "localhost";
	private static int middlewarePort = 3000;
	private static String middlewareip = ip;
	private static InetSocketAddress address = new InetSocketAddress(ip,port);
	private static WS_Server ws_server;
	
	public Server() {
		try{
			//Event Machine for delayed tasks
		eventmachine = new EventMachine();
			//SSLEngine for encrypted communications
		engine = SSLContext.getDefault().createSSLEngine();
			//Start Server on specified InetSocketAddress
		//serversocket = eventmachine.getNIOService().openServerSocket(address,port);
		serversocket = eventmachine.getNIOService().openServerSocket(port);
			//Start listening for sockets
		serversocket.listen(this);
		serversocket.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
		eventmachine.start();
		System.out.println("***** SERVER STARTED *****");
		System.out.println("connected on ip " + serversocket.getIp());
		System.out.println("listening on port " + serversocket.getPort());
		System.out.println("--------------------------");
		
			//Initialize WebSocket Middleware
		//ws_server = new WS_Server(middlewarePort);
		InetSocketAddress address = new InetSocketAddress(middlewareip, middlewarePort);
		ws_server = new WS_Server(address,eventmachine.getNIOService());
		ws_server.start();

		System.out.println("--------------------------");
		
			//Initialize UserManager
		UserManager.getInstance().init(eventmachine);
			//Initialize MongoDbManager
		MongoDbManager.getInstance().init();
			//Initialize GameManager
		GameManager.getInstance().init();
			
		System.out.println("--------------------------");
		System.out.println();
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
	
	public NIOService getService() {
		return eventmachine.getNIOService();
	}
}
