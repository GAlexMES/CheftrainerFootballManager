package de.szut.dqi12.cheftrainer.client.serverCommunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles the Server Connection
 * @author Alexander Brennecke
 *
 */
public class ServerHandler {

	Socket socket;

	/**
	 * Initializes a new Server Connection 
	 */
	public void run() {
		startConnection("127.0.0.1",5000);
	}

	/**
	 * Builds a new Connection to a java server socket
	 */
	private void startConnection(String serverIP, int serverPort) {
		try {
			socket = new Socket(serverIP,serverPort);
			Thread readerThread = new Thread(new ServerConnection(socket));
			readerThread.start();
			System.out.println("Verbindung Aufgebaut");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Keine Verbindung Aufgebaut");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
