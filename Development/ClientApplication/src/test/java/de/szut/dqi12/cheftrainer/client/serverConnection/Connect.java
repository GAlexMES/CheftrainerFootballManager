package de.szut.dqi12.cheftrainer.client.serverConnection;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.crypto.SecretKey;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.client.serverCommunication.ClientController;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;

public class Connect{
	
	@Test
	public void testConnection(){
		ClientController  clController = new ClientController();
		ClientProperties clientProps = new ClientProperties();
		URL path = null;
		try {
			path = de.szut.dqi12.cheftrainer.client.callables.Test.class.getResource(".").toURI().toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	clientProps.setPathToCallableDir(path);
    	String pathPackage = de.szut.dqi12.cheftrainer.client.callables.Test.class.getName();
    	clientProps.setPackagePathToCallableDir(pathPackage);
		clientProps.setPort(5000);
		clientProps.setServer_ip("127.0.0.1");
		clController.createClient(clientProps);
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clController.sendMessage("Hallo Server!");
		try {
			Thread.sleep(200000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
