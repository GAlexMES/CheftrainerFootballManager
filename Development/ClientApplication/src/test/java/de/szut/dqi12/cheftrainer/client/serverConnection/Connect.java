package de.szut.dqi12.cheftrainer.client.serverConnection;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.client.serverCommunication.ServerHandler;

public class Connect{
	
	@Test
	public void testConnection(){
		ServerHandler serverHandler = new ServerHandler();
		serverHandler.run();
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
