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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
