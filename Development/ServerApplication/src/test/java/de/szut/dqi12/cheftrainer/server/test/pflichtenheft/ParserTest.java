package de.szut.dqi12.cheftrainer.server.test.pflichtenheft;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Position;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.RealTeam;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;
import de.szut.dqi12.cheftrainer.server.parsing.PointsParser;
import de.szut.dqi12.cheftrainer.server.parsing.ScheduleParser;
import de.szut.dqi12.cheftrainer.server.test.utils.TestUtils;

public class ParserTest {
	
	private static SQLConnection sqlCon;

	@BeforeClass
	public static void prepareDatabase() throws IOException{
		Controller controller = Controller.getInstance();
		controller.creatDatabaseCommunication(false);
		sqlCon = controller.getSQLConnection();
		TestUtils.cleareDatabase(sqlCon);
		sqlCon.close();
		
		controller.creatDatabaseCommunication(true);
		sqlCon = controller.getSQLConnection();
	}
	
	/**
	 * Tests if four {@link Player}s exists, have the correct {@link Position} and are in the correct {@link RealTeam}.
	 * @throws IOException 
	 * @see /T2013/
	 */
	@Test
	public void testPlayerTable() throws IOException{
		List<Player> playerList = createTestPlayers();
		for(Player p : playerList){
			checkPlayer(p);
		}
	}
	
	/**
	 * Tests if the schedule for a specific matchday will be parsed correct.
	 * @throws MalformedURLException
	 * @see /T2011/
	 */
	@Test
	public void testSchedule() throws MalformedURLException{
		ScheduleParser sp = new ScheduleParser();
		List<Match> parsedMatches = sp.createSchedule(2, 2015);
		assertEquals(9, parsedMatches.size());
		
		for(Match m : parsedMatches){
			checkMatch(m);
		}
	}
	
	@Test
	public void testPointsParsing() throws Exception{
		ScheduleParser sp = new ScheduleParser();
		PointsParser pp = new PointsParser();
		Map<String,Map<String,Player>> playerList = new HashMap<>();
		try {
			List<Match> matches = sp.createSchedule(7, 2015);
			for (Match m : matches) {
				m.setSportalMatchID(ScheduleParser.getSportalID(m.getDetailURL()));
				@SuppressWarnings("static-access")
				HashMap<String,HashMap<String,Player>> currentPlayerList = pp.getPlayerPoints(2015, 7, m.getSportalMatchID());
				playerList.putAll(currentPlayerList);
			}
		} catch (MalformedURLException e) {
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
	
	
	
	private void checkMatch(Match m){
		String homeTeam = m.getHome();
		switch(homeTeam){
		case "Hertha BSC": 
			compareMatch(m,1,1,"Werder Bremen","21.08.2015","20:30");
			break;
		case "1. FC Köln": 
			compareMatch(m,1,1,"VfL Wolfsburg","22.08.2015","15:30");
			break;
		case "Eintracht Frankfurt": 
			compareMatch(m,1,1,"FC Augsburg","22.08.2015","15:30");
			break;
		case "1899 Hoffenheim": 
			compareMatch(m,1,2,"Bayern München","22.08.2015","15:30");
			break;
		case "FC Schalke 04": 
			compareMatch(m,1,1,"Darmstadt 98","22.08.2015","15:30");
			break;
		case "Hannover 96": 
			compareMatch(m,0,1,"Bayer Leverkusen","22.08.2015","15:30");
			break;
		case "Hamburger SV": 
			compareMatch(m,3,2,"VfB Stuttgart","22.08.2015","18:30");
			break;
		case "FC Ingolstadt": 
			compareMatch(m,0,4,"Borussia Dortmund","23.08.2015","15:30");
			break;
		case "Mönchengladbach": 
			compareMatch(m,1,2,"FSV Mainz 05","23.08.2015","17:30");
			break;
		default:
			assertTrue("Invalide home team:"+homeTeam,false);
		}
	}
	
	private void compareMatch(Match m, int goalsHome, int goalsGuest, String guestTeam, String date, String time){
		assertEquals(goalsHome, m.getGoalsHome());
		assertEquals(goalsGuest, m.getGoalsGuest());
		assertEquals(guestTeam, m.getGuest());
		assertEquals(date, m.getDate());
		assertEquals(time,m.getTime());
	}
	
	private void checkPlayer(Player assertPlayer) throws IOException{
		int playerID = DatabaseRequests.getUniqueInt("ID", "Spieler", "Name='"+assertPlayer.getName()+"'");
		Player p = DatabaseRequests.getPlayer(playerID);
		assertEquals(assertPlayer.getName(),p.getName());
		assertEquals(assertPlayer.getPosition(),p.getPosition());
		assertEquals(assertPlayer.getTeamName(),p.getTeamName());
	}
	
	private List<Player> createTestPlayers(){
		List<Player> retval = new ArrayList<>();
		String[] playerNames = {"Ralf Fährmann","Philipp Lahm","Hakan Calhanoglu","Raul Bobadilla"};
		String[] positions = {Position.KEEPER,Position.DEFENCE,Position.MIDDLE,Position.OFFENCE};
		String[] team ={"FC Schalke 04","Bayern München","Bayer Leverkusen","FC Augsburg"};
		for(int i = 0;i<playerNames.length;i++){
			Player p = new Player();
			p.setName(playerNames[i]);
			p.setPosition(positions[i]);
			p.setTeamName(team[i]);
			retval.add(p);
		}
		return retval;
	}
}
