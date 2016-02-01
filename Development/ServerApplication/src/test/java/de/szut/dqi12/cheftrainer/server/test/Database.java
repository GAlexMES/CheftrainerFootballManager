package de.szut.dqi12.cheftrainer.server.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;
import de.szut.dqi12.cheftrainer.server.databasecommunication.CommunityManagement;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseUtils;
import de.szut.dqi12.cheftrainer.server.databasecommunication.PointManagement;
import de.szut.dqi12.cheftrainer.server.databasecommunication.ServerPropertiesManagement;

public class Database {
	
	@Test
	public void testProperties() {
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

			sqlCon.sendQuery(" DELETE FROM " + ServerPropertiesManagement.SERVER_PROPS_TABLE);
			sqlCon.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPlacement() throws IOException, SQLException {
		Controller con = Controller.getInstance();
		try {
			con.creatDatabaseCommunication();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SQLConnection sqlCon = con.getSQLConnection();
		CommunityManagement cm = new CommunityManagement(sqlCon);
		int id = getHeighestUserID(sqlCon);
		int spielrundeID = getHeighestCommunityID(sqlCon) + 1;
		String communityName = "Testspielrunde-" + spielrundeID;
		Integer[] userIDs = { id + 1, id + 2, id + 3, id + 4, id + 5 };
		
		for(Integer i : userIDs){
			addUser(i,sqlCon);
		}
		
		
		cm.createNewCommunity(communityName, "test", userIDs[0]);
		
		cm.createNewManager(communityName, userIDs[0]);
		cm.createNewManager(communityName, userIDs[1]);
		cm.createNewManager(communityName, userIDs[2]);
		cm.createNewManager(communityName, userIDs[3]);
		cm.createNewManager(communityName, userIDs[4]);

		List<Integer> managerIDs = getManagerIDs(userIDs, sqlCon);

		PointManagement pm = new PointManagement(sqlCon);
		pm.addManagerPoints(1, managerIDs.get(0), 15);
		pm.addManagerPoints(1, managerIDs.get(1), 20);
		pm.addManagerPoints(1, managerIDs.get(2), 15);
		pm.addManagerPoints(1, managerIDs.get(3), 25);
		pm.addManagerPoints(1, managerIDs.get(4), 20);

		cm.updatePlacement();

		checkDatabase(managerIDs.get(0), 4, sqlCon);
		checkDatabase(managerIDs.get(1), 2, sqlCon);
		checkDatabase(managerIDs.get(2), 4, sqlCon);
		checkDatabase(managerIDs.get(3), 1, sqlCon);
		checkDatabase(managerIDs.get(4), 2, sqlCon);
		
		sqlCon.close();
	}
	
	@Test
	public void testWorthCalculation() throws IOException{
		Controller con = Controller.getInstance();
		try {
			con.creatDatabaseCommunication();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SQLConnection sqlCon = con.getSQLConnection();
		
		PointManagement pm = new PointManagement(sqlCon);
		
		Player p = new Player();
		p.setSportalID(28206);
		p.setPoints(10);
		String SQLQuery  = "SportalID = "+p.getSportalID();
		long currentWorth = DatabaseRequests.getUniqueLong("Marktwert", "Spieler", SQLQuery);
		
		Map<String,Player> playerList = new HashMap<>();
		playerList.put("Joel Matip", p);
		pm.updatePointsOfPlayers(playerList);
		
		long newWorth = DatabaseRequests.getUniqueLong("Marktwert", "Spieler", SQLQuery);
		
		assertTrue(currentWorth-newWorth == 5.83*100000);
		
	}

	private int getHeighestCommunityID(SQLConnection sqlCon) throws SQLException {
		String sqlQuery = "Select ID FROM Spielrunde ORDER BY ID DESC";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			rs.next();
			return rs.getInt("ID");
		} catch (SQLException sqe) {
			return 0;
		}
	}

	private int getHeighestUserID(SQLConnection sqlCon) throws SQLException {
		String sqlQuery = "Select ID FROM Nutzer ORDER BY ID DESC";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			rs.next();
			return rs.getInt("ID");
		} catch (SQLException sqe) {
			return 0;
		}
	}

	private List<Integer> getManagerIDs(Integer[] userIDs, SQLConnection sqlCon) throws SQLException {
		List<Integer> retval = new ArrayList<>();
		for (Integer i : userIDs) {
			String sqlQuery = "Select ID FROM Manager WHERE Nutzer_ID=" + i;
			ResultSet rs = sqlCon.sendQuery(sqlQuery);
			rs.next();
			retval.add(rs.getInt("ID"));
		}

		return retval;
	}

	private void checkDatabase(int manager, int place, SQLConnection sqlCon) throws SQLException {
		String sqlQuery = "Select Platz from Manager where ID = " + manager;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		rs.next();
		assertEquals(rs.getInt("Platz"), place);
	}
	
	private void addUser(int id, SQLConnection sqlCon){
		String sqlQuery =  "INSERT INTO Nutzer (ID) VALUES ("+id+")";
		sqlCon.sendQuery(sqlQuery);
	}

}
