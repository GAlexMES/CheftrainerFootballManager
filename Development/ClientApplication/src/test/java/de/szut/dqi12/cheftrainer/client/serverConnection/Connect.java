package de.szut.dqi12.cheftrainer.client.serverConnection;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.serverCommunication.ServerConnection;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;

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
		clientProps.setPathToCallableDir(path);
		clientProps.setPackagePathToCallableDir(PACKAGE_PATH);
		clientProps.setPort(5000);
		clientProps.setServer_ip("127.0.0.1");
		serverCon = new ServerConnection(clientProps);
	}
}
