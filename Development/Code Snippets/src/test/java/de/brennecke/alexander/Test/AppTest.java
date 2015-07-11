package de.brennecke.alexander.Test;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import Parsers.PlayerParser;
import Parsers.TeamParser;

public class AppTest {
	
	private int NUMBER_OF_KOELN_PLAYER = 23;

	private static String rootURL = "http://www.ran.de/datenbank/fussball";
	private List<Team> teamList;

	@Test
	public void generalTest() {
		assertTrue("No correct number of teams",teamList.size()==18);
		Team team = teamList.get(0);
		assertTrue("No correct number of players at Koeln, must be updated maybe", team.getPlayerList().size()== NUMBER_OF_KOELN_PLAYER);

	}

	@Before
	public void generateTeams() {
		TeamParser tp = new TeamParser();
		teamList = tp.getTeamlist(rootURL);
		PlayerParser pp = new PlayerParser();
		for (Team t : teamList) {
			URL teamURL;
			try {
				teamURL = new URL(rootURL + t.getTeamUrl());
				t.setPlayerList(pp.getPlayers(teamURL));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
}
