package de.szut.dqi12.cheftrainer.server.UserCommunication;

import java.util.ArrayList;

import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ClientHandler;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.Server;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ServerProperties;

/**
 * This Class uses the ConnectorLib to create a new server.
 * 
 * @author Alexander Brennecke
 *
 */
public class ServerController {

	private Server server;
	private ArrayList<ClientHandler> clientList = new ArrayList<>();

	/**
	 * This method creates a new server and starts it.
	 * @throws Exception 
	 */
	public ServerController(ServerProperties serverProps) throws Exception{
		try {
			server = new Server(serverProps);
		} catch (Exception e) {
			throw e;
		}
		server.run();
	}

	/**
	 * This method sends the given String to the first ClientHandler in the list
	 * Code must be updated so that it is possible to send to every
	 * ClientHandler
	 */
	public void sendMessage(Message message) {
		if (server != null) {
			ClientHandler receiver = clientList.get(0);
			if (receiver != null) {
				receiver.sendMessage(message);
			}
		}
	}

}
