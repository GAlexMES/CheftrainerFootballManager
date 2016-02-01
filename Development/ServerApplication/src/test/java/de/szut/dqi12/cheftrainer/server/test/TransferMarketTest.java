package de.szut.dqi12.cheftrainer.server.test;


import org.junit.Test;

import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;

public class TransferMarketTest {


	@Test
	public void transfer() {
		Controller con = Controller.getInstance();
		try {
			con.creatDatabaseCommunication();
			DatabaseRequests.doTransactions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
