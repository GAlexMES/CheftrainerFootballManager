package de.szut.dqi12.cheftrainer.client.serverCommunication;

import de.szut.dqi12.cheftrainer.ConnectorLib.ClientSide.Client;
import de.szut.dqi12.cheftrainer.ConnectorLib.ClientSide.ClientInterface;

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
	public void createClient(){
		client = new Client(this);
		client.run();
	}

}
