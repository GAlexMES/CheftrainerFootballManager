package de.szut.dqi12.cheftrainer.server.UserCommunication;

import java.util.ArrayList;

import de.szut.dqi12.cheftrainer.ConnectorLib.ServerSide.ClientHandler;
import de.szut.dqi12.cheftrainer.ConnectorLib.ServerSide.Server;
import de.szut.dqi12.cheftrainer.ConnectorLib.ServerSide.ServerInterface;

public class ServerController implements ServerInterface {

	private Server server;

	private ArrayList<ClientHandler> clientList = new ArrayList<>();

	@Override
	public void receiveMessage(String message) {
		System.out.println(message);
		sendMessage("Hallo Client!");
	}

	@Override
	public void sendMessage(String message) {
		if (server != null) {
			ClientHandler receiver = clientList.get(0);
			if (receiver != null) {
				receiver.sendMessage(message);
			}
		}
	}

	@Override
	public void createServer() {
		server = new Server(this);
		server.run();
	}

	@Override
	public void updateClientHandlerList(ArrayList<ClientHandler> clientList) {
		this.clientList = clientList;
	}

}
