package de.szut.dqi12.cheftrainer.server.test;


import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.server.database.SQLConnection;
import de.szut.dqi12.cheftrainer.server.databasecommunication.ServerPropertiesManagement;

public class Database {
	
	@Test
	public void test() {
		try {
			SQLConnection sqlCon = new SQLConnection(false);
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
