package de.szut.dqi12.cheftrainer.server;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import de.szut.dqi12.cheftrainer.connectorlib.serverside.ServerProperties;
import de.szut.dqi12.cheftrainer.server.UserCommunication.ServerController;
import de.szut.dqi12.cheftrainer.server.callables.Test;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	ServerController conServer = new ServerController();
    	ServerProperties serverProps = new ServerProperties();
    	URL path = null;
		try {
			path = Test.class.getResource(".").toURI().toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	serverProps.setPathToCallableDir(path);
    	String pathPackage = Test.class.getName();
    	serverProps.setPackagePathToCallableDir(pathPackage);
    	serverProps.setPort(5000);
    	conServer.createServer(serverProps);
    }
}
