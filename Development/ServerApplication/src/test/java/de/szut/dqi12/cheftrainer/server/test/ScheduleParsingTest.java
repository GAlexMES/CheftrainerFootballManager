package de.szut.dqi12.cheftrainer.server.test;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.parsing.PointsParser;
import de.szut.dqi12.cheftrainer.server.parsing.ScheduleParser;

/**
 *	This class testes the Schedule and Point parser. It loads all matches for a given gameday and checks the points for a few players.
 * @author Alexander Brennecke
 *
 */
public class ScheduleParsingTest {

	@Test
	public void testGrades() {
		ScheduleParser sp = new ScheduleParser();
		PointsParser pp = new PointsParser();
		Map<String,Map<String,Player>> playerList = new HashMap<>();
		try {
			List<Match> matches = sp.createSchedule(7, 2015);
			for (Match m : matches) {
				m.setSportalMatchID(ScheduleParser.getSportalID(m.getDetailURL()));
				Map<String,Map<String,Player>> currentPlayerList = pp.getPlayerPoints(2015, 7, m.getSportalMatchID());
				playerList.putAll(currentPlayerList);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String player = "Geis";
		int points = playerList.get("FC Schalke 04").get(player).getPoints();
		assertTrue("Assert that points are 8 but were "+points +" for player "+player, points == 8);
		
		player = "Lewandowski";
		int goals = playerList.get("Bayern München").get(player).getGoals();
		assertTrue("Assert that goals are 2 but were "+goals +" for player "+player, goals == 2);
		
		player = "Sané";
		Player p = playerList.get("FC Schalke 04").get(player);
		goals = p.getGoals();
		points = p.getPoints();
		assertTrue("Assert that points are 8 but were "+points +" for player "+player, points == 8);
		assertTrue("Assert that goals are 1 but were "+goals +" for player "+player, goals == 1);

	}

}
