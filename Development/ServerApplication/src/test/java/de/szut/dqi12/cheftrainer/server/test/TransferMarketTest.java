package de.szut.dqi12.cheftrainer.server.test;


import org.junit.Test;

import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.ServerApplication;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;

public class TransferMarketTest {

	private final static String DB_NAME = "Database";
	private final static String DB_PATH = ServerApplication.class.getResource("../../../../../Database").toString();

	@Test
	public void transfer() {
		Controller con = Controller.getInstance();
		try {
			con.creatDatabaseCommunication(DB_NAME, DB_PATH);
			DatabaseRequests.doTransactions();
//			String sqlQuery = "SELECT * FROM Spieler";
//			User u = Mockito.mock(User.class);
//			Mockito.when(u.geteMail()).thenReturn("alex@gmx.de");
//			Mockito.when(u.getFirstName()).thenReturn("Alexander");
//			Mockito.when(u.getLastName()).thenReturn("B");
//			Mockito.when(u.getPassword()).thenReturn("123456");
//			Mockito.when(u.getUserName()).thenReturn("GAlexMES");
//			DatabaseRequests.registerNewUser(u);
//			DatabaseRequests.createNewCommunity(u.getUserName(), u.getPassword(), 1);
//			DatabaseRequests.createNewManager("Testrunde",34);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
