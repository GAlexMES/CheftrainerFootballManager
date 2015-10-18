package de.szut.dqi12.cheftrainer.server.test;


import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.server.ServerApplication;
import de.szut.dqi12.cheftrainer.server.databasecommunication.SQLConnection;
import de.szut.dqi12.cheftrainer.server.databasecommunication.ServerPropertiesManagement;

public class Database {
	
	private final static String DB_NAME = "Database";
	private final static String DB_PATH = ServerApplication.class.getResource(
			"../../../../../Database").toString();

	@Test
	public void test() {
		try {
			SQLConnection sqlCon = new SQLConnection(DB_NAME, DB_PATH,false);
			ServerPropertiesManagement spm = new ServerPropertiesManagement(sqlCon);
			spm.setProperty("TestBoolean", true);
			spm.setProperty("TestInt", 9564);
			spm.setProperty("TestString", "Test String");
			
			Boolean testBoolean = spm.getPropAsBoolean("TestBoolean");
			Integer testInt = spm.getPropAsInt("TestInt");
			String testString = spm.getPropAsString("TestString");
			
			assertTrue(testBoolean);
			assertTrue(testInt == 9564);
			assertTrue(testString.equals("Test String"));
			
			spm.setProperty("TestBoolean", false);
			testBoolean = spm.getPropAsBoolean("TestBoolean");
			assertFalse(testBoolean);
			
			sqlCon.sendQuery(" DELETE FROM "+ServerPropertiesManagement.SERVER_PROPS_TABLE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
