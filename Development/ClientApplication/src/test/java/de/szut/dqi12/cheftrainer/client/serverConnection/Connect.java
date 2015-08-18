package de.szut.dqi12.cheftrainer.client.serverConnection;

import javax.crypto.SecretKey;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.ConnectorLib.CipherFactory;
import de.szut.dqi12.cheftrainer.ConnectorLib.KeyGenerator;
import de.szut.dqi12.cheftrainer.client.serverCommunication.ServerHandler;

public class Connect{
	
	@Test
	public void testConnection(){
		ServerHandler serverHandler = new ServerHandler();
		serverHandler.run();
		try {
			Thread.sleep(20000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void cipherTest(){
		String text = "hallo ich bin ein text";
		try {
			SecretKey key = KeyGenerator.getRandomAESKey();
			CipherFactory c = new CipherFactory(key, "AES");
			String geheim = c.encrypt(text);
			System.out.println(geheim);
			String neu = c.decrypt(geheim);
			System.out.println(neu);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
