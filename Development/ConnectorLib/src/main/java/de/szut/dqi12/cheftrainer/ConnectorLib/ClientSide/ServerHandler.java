package de.szut.dqi12.cheftrainer.connectorlib.clientside;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messages.MessageController;


/**
 * This class is the direct connection class to a java server. It receives and
 * sends messages to the server.
 * 
 * @author Alexander Brennecke
 *
 */
public class ServerHandler implements Runnable {
	private BufferedReader reader;
	private PrintWriter writer;
	private MessageController messageController;

	/**
	 * Constructor
	 * 
	 * @param socket
	 */
	public ServerHandler(Socket socket, ClientProperties clientProps)
			throws Exception {
		ServerToClient_MessageIDs stc_messageIDs = new ServerToClient_MessageIDs();
		messageController = new MessageController(stc_messageIDs.getIDs(),clientProps.getPathToCallableDir(), clientProps.getPackagePathToCallableDir());
		InputStreamReader streamReader = new InputStreamReader(
				socket.getInputStream());

		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(socket.getOutputStream());
		messageController.setWriter(writer);
	}

	
	/**
	 * This method runs the client thread. It also receives every message, which will be sent to the client from the server.
	 */
	public void run() {
		String message;
		try {
			while ((message = reader.readLine()) != null) {
				messageController.receiveMessage(message);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	/**
	 * This method will sent the given message, if the handshake was already completed.
	 * @param message the message, that should be sent to the server
	 */
	public void sendMessage(Message message) {
		messageController.sendMessage(message);
	}
}