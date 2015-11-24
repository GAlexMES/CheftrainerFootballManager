package de.szut.dqi12.cheftrainer.server.test;

import java.io.IOException;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.server.ServerApplication;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.logic.TeamGenerator;

/**
 * Test for team generation. Is not used at the moment.
 * @author Alexander Brennecke
 *
 */
public class TeamGeneratorTest {

	private final static String DB_NAME = "Database";
	private final static String DB_PATH = ServerApplication.class.getResource(
			"../../../../../Database").toString();

	@Test
	public void test() {
		Controller con = Controller.getInstance();
		try {
			con.creatDatabaseCommunication(DB_NAME, DB_PATH);
			TeamGenerator tg = new TeamGenerator();
			tg.generateTeamForUser(1, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
