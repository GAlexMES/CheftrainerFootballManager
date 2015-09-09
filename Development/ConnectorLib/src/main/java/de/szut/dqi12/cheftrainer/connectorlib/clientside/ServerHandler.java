package de.szut.dqi12.cheftrainer.connectorlib.clientside;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.messages.HandshakeMapperCreator;
import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;
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

		List<IDClass_Path_Mapper> idMappers = new ArrayList<IDClass_Path_Mapper>();
		idMappers.addAll(clientProps.getIDMappers());
		idMappers.add(HandshakeMapperCreator.getIDClassPathMapperForHandshake());

		messageController = new MessageController(idMappers);
		InputStreamReader streamReader = new InputStreamReader(
				socket.getInputStream());

		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(socket.getOutputStream());
		messageController.setWriter(writer);
	}
	
	/**
	 * This method runs the client thread. It also receives every message, which
	 * will be sent to the client from the server.
	 */
	public void run() {
		String message;
		try {
			while ((message = reader.readLine()) != null) {
				messageController.receiveMessage(message);
				System.out.println("receive:   "+message);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * This method will sent the given message, if the handshake was already
	 * completed.
	 * 
	 * @param message
	 *            the message, that should be sent to the server
	 */
	public void sendMessage(Message message) {
		messageController.sendMessage(message);
	}
}
