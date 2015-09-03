package de.szut.dqi12.cheftrainer.server;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ServerProperties;
import de.szut.dqi12.cheftrainer.server.UserCommunication.ServerController;


/**
 * Hello world!
 *
 */
public class App {
	
	private final static String PACKAGE_PATH = "de.szut.dqi12.cheftrainer.server.callables.test";
    public static void main( String[] args )
    {
    	ServerController conServer;
    	ServerProperties serverProps = new ServerProperties();
    	URL path = null;
		try {
			String pathAsString = App.class.getResource(".").toURI().toString();
			URI uriPath = new URI(pathAsString+"callables/");
			path = uriPath.toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		ClientToServer_MessageIDs cts = new ClientToServer_MessageIDs();
		IDClass_Path_Mapper idMapper = new IDClass_Path_Mapper(cts, path, PACKAGE_PATH);
		serverProps.addClassPathMapper(idMapper);
    	serverProps.setPort(5000);
    	try {
			conServer = new ServerController(serverProps);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
