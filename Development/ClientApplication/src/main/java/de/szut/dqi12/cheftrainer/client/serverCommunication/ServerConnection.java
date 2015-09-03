package de.szut.dqi12.cheftrainer.client.serverCommunication;

import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;


public class ServerConnection {
	
	private Client client;

	public ServerConnection(ClientProperties clientProps ){
		client = new Client(clientProps);
	}
	
	public void sendMessage(Message message){
		client.sendMesage(message);
	}

}
