package de.szut.dqi12.cheftrainer.client.servercommunication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;


public class ServerConnection {
	
	
	private final static String PACKAGE_PATH = "de.szut.dqi12.cheftrainer.client.callables.CLASS";
	
	public static Client createServerConnection(ClientProperties clientProps ) throws IOException{
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
		return new Client(clientProps);
	}
	
}
