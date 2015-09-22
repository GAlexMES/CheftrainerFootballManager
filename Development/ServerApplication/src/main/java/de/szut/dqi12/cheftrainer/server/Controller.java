package de.szut.dqi12.cheftrainer.server;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ServerProperties;
import de.szut.dqi12.cheftrainer.server.databasecommunication.SQLConnection;
import de.szut.dqi12.cheftrainer.server.usercommunication.SocketController;

public class Controller {
	
	private static Controller instance;
	private SocketController socketController;
	private SQLConnection sqlConnection;
	
	
	public static Controller getInstance(){
		if(instance == null){
			instance = new Controller();
		}
		return instance;
	}

	public void startServerSocket(String packagePath){
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
		IDClass_Path_Mapper idMapper = new IDClass_Path_Mapper(cts, path, packagePath);
		serverProps.addClassPathMapper(idMapper);
    	serverProps.setPort(5000);
    	try {
    		socketController = new SocketController(serverProps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void creatDatabaseCommunication(String sqlName, String sqlPath) {
		sqlConnection = new SQLConnection(sqlName,sqlPath);
	}
	
	public SQLConnection getSQLConnection(){
		return sqlConnection;
	}
	
	public SocketController getSocketController(){
		return socketController;
	}
}
