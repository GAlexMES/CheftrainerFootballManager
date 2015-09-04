package de.szut.dqi12.cheftrainer.client.servercommunication;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;


public class ServerConnection {
	
	private Client client;
	
	private final static String PACKAGE_PATH = "de.szut.dqi12.cheftrainer.client.callables.CLASS";
	
	private boolean allowSending = false;
	
	public ServerConnection(ClientProperties clientProps ){
		
		URL path = null;
		try {
			String pathAsString = MainApp.class.getResource(".").toURI().toString();
			URI uriPath = new URI(pathAsString+"callables/");
			path = uriPath.toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		ServerToClient_MessageIDs stc = new ServerToClient_MessageIDs();
		IDClass_Path_Mapper idMapper = new IDClass_Path_Mapper(stc,path, PACKAGE_PATH);
		clientProps.addClassPathMapper(idMapper);
		client = new Client(clientProps);
	}
	
	public void sendMessage(Message message){
		client.sendMesage(message);
	}

}
