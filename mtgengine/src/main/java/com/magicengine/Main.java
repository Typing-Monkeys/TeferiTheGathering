package com.magicengine;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
	
	private ServerSocket serverSocket;
	private Socket socket = null;
	//Server settings
	private final static int PORT_NUMBER = 2013;
	private InetAddress ADDRESS=null;
	private final int NUM_CONN;
	
	public Main(){
		try {
			//this.ADDRESS = InetAddress.getByName("141.250.25.139"); 
			//@Galt 2021
			this.ADDRESS = InetAddress.getByName("127.0.0.1"); //localhost
		} catch (UnknownHostException e) {
			e.printStackTrace();
			//something goes wrong, exiting....
			System.exit(1);
		}
		NUM_CONN = 0;
	}
	
	void createNewGame(String portNumber) {
		try {
			serverSocket = new ServerSocket(Integer.parseInt(portNumber)/*,NUM_CONN,ADDRESS*/);
			while (true) {
				System.out.println("Waiting for new game...");
				socket = serverSocket.accept();
				new GameEngine(socket).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Main server = new Main();
		server.createNewGame(String.valueOf(PORT_NUMBER));
	}
}
