package de.szut.dqi12.cheftrainer.server.test.additionaltests;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.databasecommunication.SchedulePointManagement;
import de.szut.dqi12.cheftrainer.server.parsing.ScheduleParser;

public class TimeTaskTest {
	
	private int matchday = 19;
	private int season = 2015;
	
	private Controller con;
	
	@After
	public void closeDatabase(){
		con.getSQLConnection().close();
	}
	
	@Test
	public void test() {
		con = Controller.getInstance();
		try {
			con.creatDatabaseCommunication(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		User newUser = new User();
		newUser.seteMail("test@abc.de");
		newUser.setFirstName("Alex");
		newUser.setLastName("B.");
		newUser.setPassword("abc");
		newUser.setUserName("Alexander");
		DatabaseRequests.registerNewUser(newUser);
		
		User secondUser = new User();
		newUser.seteMail("abc@abc.de");
		newUser.setFirstName("Robin");
		newUser.setLastName("B.");
		newUser.setPassword("abc");
		newUser.setUserName("Robin");
		DatabaseRequests.registerNewUser(secondUser);
		
		DatabaseRequests.createNewCommunity("Testcommunity", "test", 0);
		
		DatabaseRequests.createNewManager("Testcommunity", 0);
		DatabaseRequests.createNewManager("Testcommunity", 1);
		
		DatabaseRequests.copyManagerTeams();
		
		readPoints();
		
	}
	
	/**
	 * This functions uses a List of {@link Match} to call the {@link SchedulePointManagement}, to get the newest points for each player of each match in the list. 
	 */
	private void readPoints() {
		List<Match> matches = updateSchedule();
		SchedulePointManagement spm = DatabaseRequests.getSchedulePointManagement();

		List<HashMap<String, HashMap<String, Player>>> parsedPoints = spm.readPointsForMatches(matches);
		for(int match = 0; match<parsedPoints.size();match++){
			HashMap<String, HashMap<String, Player>> playerPoints = parsedPoints.get(match);
			for (String s : playerPoints.keySet()) {
				DatabaseRequests.writePointsToDatabase(playerPoints.get(s));
				DatabaseRequests.addPointsToPlayingPlayers(playerPoints.get(s));
			}
		}
		DatabaseRequests.addTempPointsToManager(matchday);
		DatabaseRequests.updatedPlacement();
	}

	/**
	 * This function is called, to parse the current matchday and the next one again. This is used to updated dates.
	 * @return returns a List of {@link Match}es for the current matchday.
	 */
	private List<Match> updateSchedule() {
		ScheduleParser sp = new ScheduleParser();
		List<Match> currentMatches = new ArrayList<>();
		List<Match> nextMatches = new ArrayList<>();
		try {
			nextMatches = sp.createSchedule(matchday + 1, season);
			currentMatches = sp.createSchedule(matchday, season);
			DatabaseRequests.updateSchedule(nextMatches, matchday + 1, season);
			DatabaseRequests.updateSchedule(currentMatches, matchday, season);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return currentMatches;
	}
}