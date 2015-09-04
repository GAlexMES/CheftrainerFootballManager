package de.szut.dqi12.cheftrainer.client.serverConnection;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.servercommunication.ServerConnection;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class Connect {

	private final static String PACKAGE_PATH = "de.szut.dqi12.cheftrainer.client.callables.test";
	
	@Test
	public void testConnection() {
		ServerConnection serverCon = null;
		ClientProperties clientProps = new ClientProperties();
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
		clientProps.setPort(5000);
		clientProps.setServerIP("127.0.0.1");
		serverCon = new ServerConnection(clientProps);
	}
}
