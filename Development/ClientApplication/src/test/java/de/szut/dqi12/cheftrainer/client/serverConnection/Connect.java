package de.szut.dqi12.cheftrainer.client.serverConnection;

import javax.crypto.SecretKey;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.client.serverCommunication.ClientController;

public class Connect{
	
	@Test
	public void testConnection(){
		ClientController  clController = new ClientController();
		clController.createClient();
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
