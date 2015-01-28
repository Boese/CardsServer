package com.cards.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import naga.NIOService;
import naga.NIOSocket;
import naga.SocketObserver;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

// Middleware: WebSocket -> TCP Socket
// 			   WebSocket <- TCP Socket

// (server side WebSocket -> TCP Socket)
public class WS_Server extends WebSocketServer {
	
	private Map<WebSocket,Socket> sockets = new HashMap<WebSocket,Socket>();
	private NIOService service;
	
	public WS_Server(int port, NIOService service) throws UnknownHostException {
		super(new InetSocketAddress( port ));
		this.service = service;
		loaded();
	}
	
	public WS_Server(InetSocketAddress address, NIOService service) throws UnknownHostException {
		super(address);
		this.service = service;
		loaded();
	}

	@Override
	public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
		getSocket(arg0).socket.closeAfterWrite();
		removeSocket(arg0);
	}

	@Override
	public void onError(WebSocket arg0, Exception arg1) {
		for (Socket soc : sockets.values()) {
			soc.socket.closeAfterWrite();
		}
	}

	@Override
	public void onMessage(WebSocket arg0, String arg1) {
		getSocket(arg0).sendMessage(arg1);
	}

	@Override
	public void onOpen(WebSocket arg0, ClientHandshake arg1) {
		sockets.put(arg0, new Socket(arg0));
		System.out.println("new web socket connection, # of websockets : " + sockets.size());
	}
	
	public Socket getSocket(WebSocket ws) {
		return sockets.get(ws);
	}
	
	public void removeSocket(WebSocket ws) {
		sockets.remove(ws);
		System.out.println("web socket disconnected, # of websockets : " + sockets.size());
	}
	
	public void loaded() {
		System.out.println("***** WEBSOCKET PROXY STARTED *****");
		System.out.println("connected on ip " + this.getAddress());
		System.out.println("listening on port " + this.getAddress().getPort());
	}
	
	// (client side WebSocket <- TCP Socket)
	private class Socket implements SocketObserver{

		private WebSocket ws;
		private NIOSocket socket;
		
		Socket(WebSocket ws) {
			this.ws = ws;
			try {
				this.socket = service.openSocket("localhost", 5217);
				this.socket.setPacketReader(new AsciiLinePacketReader());
		        this.socket.setPacketWriter(new AsciiLinePacketWriter());
		        this.socket.listen(this);
		        System.out.println("listening to web socket");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void connectionBroken(NIOSocket arg0, Exception arg1) {
			System.out.println("connection broken");
			socket.closeAfterWrite();
			removeSocket(this.ws);
		}
		@Override
		public void connectionOpened(NIOSocket arg0) {}
		@Override
		public void packetSent(NIOSocket arg0, Object arg1) {}
		@Override
		public void packetReceived(NIOSocket arg0, byte[] arg1) {
			//System.out.println("packet : websocket <- tcp_socket");
			ws.send(new String(arg1));
		}
		
		public void sendMessage(String msg) {
			//System.out.println("packet : websocket -> tcp_socket");
			this.socket.write(msg.getBytes());
		}
	}
}
