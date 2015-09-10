package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;

public class ManagerManagement {

	private SQLConnection sqlCon;

	public ManagerManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
	}

	public List<Community> getCommunitiesForUser(int userID) {
		List<Community> retval = new ArrayList<Community>();
		String sqlQuery = "SELECT Spielrunde.ID, Spielrunde.Name, Spielrunde.Liga_ID, Spielrunde.Administrator_ID, Spielrunde.Passwort FROM Spielrunde INNER JOIN Manager where Manager.Nutzer_ID = '"
				+ userID + "'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				Community community = new Community();
				community.setName(rs.getString("Name"));
				community.setCommunityID(rs.getInt("ID"));
				community.addManagers(getManagerForCommunity(community.getCommunityID()));
				retval.add(community);
			}
		} catch (SQLException e) {

		}
		return retval;
	}
	
	private List<Manager> getManagerForCommunity(int communityID){
		List<Manager> retval = new ArrayList<>();
		String sqlQuery = "SELECT * FROM  Manager where Spielrunde_ID= '"+	+ communityID + "'AND Nutzer.ID=Manager.Nutzer_ID" ;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				String managerName = rs.getString("Nutzername");
				double money = rs.getDouble("Budget");
				int points = rs.getInt("Punkte");
				Manager manager = new Manager(managerName,money,points);
				retval.add(manager);
			}
		} catch (SQLException e) {

		}
		return retval;
	}

	public List<String> getCummunitiyNamesForUser(int userID) {
		List<String> retval = new ArrayList<>();
		String sqlQuery = "SELECT * FROM Spielrunde INNER JOIN Manager where Manager.Nutzer_ID = '"
				+ userID + "'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {

			while (rs.next()) {
				retval.add(rs.getString("Name"));
			}
		} catch (SQLException e) {

		}
		return retval;
	}
}
