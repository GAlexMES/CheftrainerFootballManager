package de.szut.dqi12.cheftrainer.server.test.pflichtenheft;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Position;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;
import de.szut.dqi12.cheftrainer.server.databasecommunication.CommunityManagement;
import de.szut.dqi12.cheftrainer.server.databasecommunication.PointManagement;
import de.szut.dqi12.cheftrainer.server.test.utils.TestUtils;

public class SystemFunctionTest {
	private static SQLConnection sqlCon;

	private final static String COMMUNITY_NAME = "Testrunde";
	private final static String COMMUNITY_PASSWORD = "654321";

	@BeforeClass
	public static void prepareDatabase() throws IOException {
		Controller controller = Controller.getInstance();
		controller.creatDatabaseCommunication(false);
		sqlCon = controller.getSQLConnection();
		TestUtils.cleareDatabase(sqlCon);
		TestUtils.preparePlayerTable(sqlCon);
	}
	
	@Before
	public void clearDatabase(){
		TestUtils.cleareUserDatabase(sqlCon);
	}

	@AfterClass
	public static void closeDatabase() {
		sqlCon.close();
	}

	/**
	 * Checks, if the update of the worth of a {@link Player} works.
	 * 
	 * @see /T2000/
	 * @throws IOException
	 */
	@Test
	public void testWorthCalculation() throws IOException {
		PointManagement pm = new PointManagement(sqlCon);

		Player p = new Player();
		p.setSportalID(100);

		int[] assertedWorth = { 1862910, 1862910, 1990733, 2246380, 2657652, 3206015, 3926610, 4791324, 5830877, 7030877 };
		for (int i = 0; i < 10; i++) {
			p.setPoints(i * 2);
			updateAndCheckPlayer(p, pm, assertedWorth[i]);
		}
	}

	/**
	 * Tests, if the points of the {@link Manager}es are calculated correctly after a match day.,
	 * @see /T2030/
	 */
	@Test
	public void testManagerPoints() {
		CommunityManagement cm = new CommunityManagement(sqlCon);

		addUser(1, sqlCon);
		cm.createNewCommunity(COMMUNITY_NAME, COMMUNITY_PASSWORD, 1);
		cm.createNewManager(COMMUNITY_NAME, 1);

		Community con = DatabaseRequests.getCummunitiesForUser(1).get(0);
		DatabaseRequests.copyManagerTeams();
		Manager m = con.getManagers().get(0);
		List<Player> managerPlayer = m.getPlayers();
		HashMap<String, HashMap<String, Player>> playerlist = generatePlayerList(addRandomPlayer(managerPlayer, 10));

		for (String s : playerlist.keySet()) {
			DatabaseRequests.writePointsToDatabase(playerlist.get(s));
			DatabaseRequests.addPointsToPlayingPlayers(playerlist.get(s));
		}
		DatabaseRequests.addTempPointsToManager(1);
		DatabaseRequests.updatedPlacement();

		Community dCon = DatabaseRequests.getCummunitiesForUser(1).get(0);
		Manager dM = dCon.getManagers().get(0);

		int managerPoints = getManagerPoints(dM.getLineUp(true));
		assertEquals(managerPoints, dM.getPoints());
	}

	/**
	 * Tests, if the place of a {@link Manager} in his
	 * {@link Community} is calculated correctly.
	 * 
	 * @see /T2030/
	 * @throws IOException
	 * @throws SQLException
	 */
	@Test
	public void testPlacement() throws IOException, SQLException {
		CommunityManagement cm = new CommunityManagement(sqlCon);

		addUser(1, sqlCon);
		cm.createNewCommunity(COMMUNITY_NAME, COMMUNITY_PASSWORD, 1);
		cm.createNewManager(COMMUNITY_NAME, 1);

		for (int i = 2; i <= 5; i++) {
			addUser(i, sqlCon);
			cm.createNewManager(COMMUNITY_NAME, i);
		}

		PointManagement pm = new PointManagement(sqlCon);
		pm.addManagerPoints(1, 1, 15);
		pm.addManagerPoints(1, 2, 20);
		pm.addManagerPoints(1, 3, 15);
		pm.addManagerPoints(1, 4, 25);
		pm.addManagerPoints(1, 5, 20);

		cm.updatePlacement();

		checkDatabase(1, 4, sqlCon);
		checkDatabase(2, 2, sqlCon);
		checkDatabase(3, 4, sqlCon);
		checkDatabase(4, 1, sqlCon);
		checkDatabase(5, 2, sqlCon);
	}

	/**
	 * Tests the generation of multiple teams in the same league.
	 * @see /T2020/
	 */
	@Test
	public void testTeamGeneration() {
		TestUtils.clearTable(sqlCon, "Transfermarkt");
		TestUtils.clearTable(sqlCon, "Mannschaft");
		CommunityManagement cm = new CommunityManagement(sqlCon);

		addUser(1, sqlCon);
		cm.createNewCommunity(COMMUNITY_NAME, COMMUNITY_PASSWORD, 1);
		cm.createNewManager(COMMUNITY_NAME, 1);

		for (int i = 2; i <= 5; i++) {
			addUser(i, sqlCon);
			cm.createNewManager(COMMUNITY_NAME, i);
		}

		Community com = DatabaseRequests.getCummunitiesForUser(1).get(0);
		for (Manager m : com.getManagers()) {
			checkManager(m);
		}
	}

	private void checkManager(Manager m){
		assertEquals(15,m.getPlayers().size());
		
		int[] positions = countPosition(m.getPlayers());	
		int[] assertValues = {2,5,5,3};
		compareIntArray(assertValues,positions);
		
		List<Player> playingPlayers = m.getLineUp(true);
		assertEquals(11, playingPlayers.size());

		int[] playingPositions = countPosition(playingPlayers);
		int[] playingAssertValues = {1,4,4,2};
		compareIntArray(playingAssertValues, playingPositions);
	}
	
	private void compareIntArray(int[] assertValue, int[] value){
		assertEquals(assertValue.length, value.length);
		for(int i = 0; i<assertValue.length;i++){
			assertEquals(assertValue[i],value[i]);
		}
	}

	private int[] countPosition(List<Player> players) {
		int[] positions = { 0, 0, 0, 0 };
		for (Player p : players) {
			int i = -1;
			switch (p.getPosition()) {
			case Position.KEEPER:
				i = 0;
				break;
			case Position.DEFENCE:
				i = 1;
				break;
			case Position.MIDDLE:
				i = 2;
				break;
			case Position.OFFENCE:
				i = 3;
				break;
			default:
				assertTrue("Wrong position " + p.getPosition(), false);
			}
			positions[i] = positions[i]+1;
		}
		return positions;
	}

	private void updateAndCheckPlayer(Player p, PointManagement pm, int assertedWorth) throws IOException {
		String SQLQuery = "SportalID = " + p.getSportalID();

		Map<String, Player> playerList = new HashMap<>();
		playerList.put("Testplayer", p);
		pm.updatePointsOfPlayers(playerList);

		long newWorth = DatabaseRequests.getUniqueLong("Marktwert", "Spieler", SQLQuery);
		assertTrue(newWorth == assertedWorth);
	}

	private void checkDatabase(int manager, int place, SQLConnection sqlCon) throws SQLException {
		String sqlQuery = "Select Platz from Manager where ID = " + manager;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		rs.next();
		assertEquals(place, rs.getInt("Platz"));
	}

	private void addUser(int id, SQLConnection sqlCon) {
		String userName = "user" + id;
		User u = new User();
		u.setFirstName(userName);
		u.setLastName(userName);
		u.setUserName(userName);
		u.seteMail(userName + "@test.de");

		DatabaseRequests.registerNewUser(u);
	}

	private List<Player> addRandomPlayer(List<Player> players, int number) {
		List<Integer> idList = new ArrayList<>();
		List<Player> retval = new ArrayList<>();
		for (Player p : players) {
			idList.add(p.getSportalID());
			retval.add(p);
		}

		for (int i = 0; i < number; i++) {
			if (!idList.contains(i)) {
				Player p = DatabaseRequests.getPlayer(i);
				retval.add(p);
				idList.add(i);
			}
		}

		return retval;
	}

	private HashMap<String, HashMap<String, Player>> generatePlayerList(List<Player> players) {
		HashMap<String, HashMap<String, Player>> retval = new HashMap<String, HashMap<String, Player>>();
		HashMap<String, Player> team = new HashMap<>();
		for (Player p : players) {
			p.setPoints(p.getSportalID() % 10);
			team.put(p.getName(), p);
		}
		retval.put("testteam", team);
		return retval;
	}

	private int getManagerPoints(List<Player> players) {
		int retval = 0;
		for (Player p : players) {
			retval = retval + (p.getSportalID() % 10);
		}
		return retval;
	}

}
