package de.szut.dqi12.cheftrainer.server.test.additionaltests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;
import de.szut.dqi12.cheftrainer.server.databasecommunication.ServerPropertiesManagement;
import de.szut.dqi12.cheftrainer.server.test.utils.TestUtils;

public class DatabaseTest {

	private static SQLConnection sqlCon;

	@BeforeClass
	public static void prepareDatabase() throws IOException {
		Controller controller = Controller.getInstance();
		controller.creatDatabaseCommunication(false);
		sqlCon = controller.getSQLConnection();
		TestUtils.cleareDatabase(sqlCon);
		TestUtils.preparePlayerTable(sqlCon);
	}
	
	@AfterClass
	public static void closeDatabase(){
		sqlCon.close();
	}

	@Test
	public void testProperties() {
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

		sqlCon.sendQuery(" DELETE FROM " + ServerPropertiesManagement.SERVER_PROPS_TABLE);
	}
}
