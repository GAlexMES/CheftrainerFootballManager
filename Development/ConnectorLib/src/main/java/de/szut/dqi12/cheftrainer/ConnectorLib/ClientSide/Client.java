package de.szut.dqi12.cheftrainer.ConnectorLib.ClientSide;

import java.io.IOException;
import java.net.Socket;

/**
 * Handles the Server Connection
 * @author Alexander Brennecke
 *
 */
public class Client {

	private Socket socket;
	private ClientInterface conInterface;
	private ServerHandler servHandler;
	
	/**
	 * Constructor
	 * @param conInterface
	 */
	public Client(ClientInterface conInterface){
		this.conInterface = conInterface;
	}

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
			servHandler = new ServerHandler(socket,conInterface);
			Thread readerThread = new Thread(servHandler);
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
	
	
	/**
	 * Is used to forward a message to the serverHandler
	 * @param message the decrypted message that should be send.
	 */
	public void sendMesage(String message){
		if(servHandler!=null){
			servHandler.sendMessage(message);
		}
	}
}
