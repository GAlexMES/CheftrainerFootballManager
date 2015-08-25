package de.szut.dqi12.cheftrainer.client.serverCommunication;

import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientInterface;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;


public class ClientController implements ClientInterface {
	
	private Client client;

	@Override
	public void receiveMessage(String message) {
		System.out.println(message);
	}

	@Override
	public void sendMessage(String message) {
		if(client!=null){
			client.sendMesage(message);
		}
	}
	
	@Override
	public void createClient(ClientProperties clientProps ){
		clientProps.setClientInterface(this);
		client = new Client(clientProps);
		client.run();
	}

}
