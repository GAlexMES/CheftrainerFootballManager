package de.szut.dqi12.cheftrainer.server.test;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.server.App;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.logic.TeamGenerator;

public class TeamGeneratorTest {

	private final static String DB_NAME = "Database";
	private final static String DB_PATH = App.class.getResource(
			"../../../../../Database").toString();

	@Test
	public void test() {
		Controller con = Controller.getInstance();
		con.creatDatabaseCommunication(DB_NAME, DB_PATH);
		TeamGenerator tg = new TeamGenerator();
		tg.generateTeamForUser(1, 1);
	}

}
