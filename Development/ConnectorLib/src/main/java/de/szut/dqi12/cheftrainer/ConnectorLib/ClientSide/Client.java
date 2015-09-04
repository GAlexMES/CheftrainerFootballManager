package de.szut.dqi12.cheftrainer.connectorlib.clientside;

import java.io.IOException;
import java.net.Socket;

import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * Handles the Server Connection
 * @author Alexander Brennecke
 *
 */
public class Client {

	private Socket socket;
	private ClientProperties clientProps;
	private ServerHandler servHandler;
	
	/**
	 * Constructor
	 * @param conInterface
	 */
	public Client(ClientProperties clientProps){
		this.clientProps = clientProps;
		startConnection(clientProps.getServerIP(),clientProps.getPort());
	}

	/**
	 * Builds a new Connection to a java server socket
	 */
	private void startConnection(String serverIP, int serverPort) {
		try {
			socket = new Socket(serverIP,serverPort);
			servHandler = new ServerHandler(socket,clientProps);
			Thread readerThread = new Thread(servHandler);
			readerThread.start();
			System.out.println("Verbindung Aufgebaut");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Keine Verbindung Aufgebaut");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Is used to forward a message to the serverHandler
	 * @param message the decrypted message that should be send.
	 */
	public void sendMesage(Message message){
		if(servHandler!=null){
			servHandler.sendMessage(message);
		}
	}
}
