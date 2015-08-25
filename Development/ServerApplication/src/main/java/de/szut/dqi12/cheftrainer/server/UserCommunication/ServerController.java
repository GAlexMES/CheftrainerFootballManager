package de.szut.dqi12.cheftrainer.server.UserCommunication;

import java.util.ArrayList;

import de.szut.dqi12.cheftrainer.connectorlib.serverside.ClientHandler;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.Server;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ServerInterface;


/**
 * This Class uses the ConnectorLib to create a new server.
 * @author Alexander Brennecke
 *
 */
public class ServerController implements ServerInterface {

	private Server server;

	private ArrayList<ClientHandler> clientList = new ArrayList<>();

	/**
	 * Is called, when a new message was send to the server by any client.
	 */
	@Override
	public void receiveMessage(String message) {
		System.out.println(message);
		sendMessage("Hallo Client!");
	}

	/**
	 * This method sends the given String to the first ClientHandler in the list
	 * Code must be updated so that it is possible to send to every ClientHandler
	 */
	@Override
	public void sendMessage(String message) {
		if (server != null) {
			ClientHandler receiver = clientList.get(0);
			if (receiver != null) {
				receiver.sendMessage(message);
			}
		}
	}

	
	/**
	 * This methos creates a new server and starts it.
	 */
	@Override
	public void createServer() {
		server = new Server(this);
		server.run();
	}

	
	/**
	 * This method is called by the server, when a new Client registers himself to the server.
	 */
	@Override
	public void updateClientHandlerList(ArrayList<ClientHandler> clientList) {
		this.clientList = clientList;
	}

}
