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
	public Client(ClientProperties clientProps) throws IOException{
		this.clientProps = clientProps;
		startConnection(clientProps.getServerIP(),clientProps.getPort(),clientProps.getConnectionDiedListener());
	}
	
	

	/**
	 * Builds a new Connection to a java server socket
	 */
	private void startConnection(String serverIP, int serverPort, ConnectionDiedListener cdl) throws IOException {
		try {
			socket = new Socket(serverIP,serverPort);
			servHandler = new ServerHandler(socket,clientProps,cdl);
			Thread readerThread = new Thread(servHandler);
			readerThread.start();
		} catch (IOException ex) {
			throw ex;
		} catch (Exception e) {
		}
	}
	
	
	/**
	 * Is used to forward a message to the serverHandler
	 * @param message the decrypted message that should be send.
	 */
	public void sendMessage(Message message){
		if(servHandler!=null){
			servHandler.sendMessage(message);
		}
	}
	
	public String getServerIP(){
		return socket.getInetAddress().toString();
	}
	
	public int getServerPort(){
		return socket.getPort();
	}
	
	public ServerHandler getServerHandler(){
		return servHandler;
	}
	
	
}
