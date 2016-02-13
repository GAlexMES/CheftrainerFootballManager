package de.szut.dqi12.cheftrainer.server.test.utils;

import de.szut.dqi12.cheftrainer.server.database.SQLConnection;

public class TestUtils {

	public static void prepareDatabase(SQLConnection sqlCon) {
		clearTable(sqlCon,"NUTZER");
		clearTable(sqlCon,"Spielrunde");
		clearTable(sqlCon,"Manager");
		clearTable(sqlCon,"Mannschaft");
		clearTable(sqlCon,"Transfermarkt");
		clearTable(sqlCon,"Gebote");
	}
	
	public  static void clearTable(SQLConnection sqlCon, String tableName) {
		String query = "DELETE FROM " + tableName;
		sqlCon.sendQuery(query);
	}
}
