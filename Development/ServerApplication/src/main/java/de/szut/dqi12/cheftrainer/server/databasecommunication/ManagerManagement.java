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

//	public List<Community> getCommunitiesForUser(int userID) {
//		List<Community> retval = new ArrayList<Community>();
//		String sqlQuery = "SELECT Spielrunde.ID, Spielrunde.Name, Spielrunde.Liga_ID, Spielrunde.Administrator_ID, Spielrunde.Passwort FROM Spielrunde INNER JOIN Manager where Manager.Nutzer_ID = '"
//				+ userID + "'";
//		ResultSet rs = sqlCon.sendQuery(sqlQuery);
//		try {
//			while (rs.next()) {
//				Community community = new Community();
//				community.setName(rs.getString("Name"));
//				community.setCommunityID(rs.getInt("ID"));
//				community.addManagers(getManagers(community.getCommunityID()));
//				retval.add(community);
//			}
//		} catch (SQLException e) {
//
//		}
//		return retval;
//	}
	public List<Community> getCummunities(int userID) {
		List<Community> retval = new ArrayList<>();
		String sqlQuery = "SELECT Spielrunde.ID FROM Spielrunde INNER JOIN Manager where Manager.Nutzer_ID ="+ userID+" And Manager.Spielrunde_ID=Spielrunde.ID";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		List<Integer> idList = new ArrayList<>();
		try {
			while (rs.next()) {
				int id = rs.getInt("ID");
				idList.add(id);
			}
		} catch (SQLException e) {

		}
		for(Integer i : idList){
			retval.add(getCommunity(i));
		}
		return retval;
	}
	
	private Community getCommunity(int communityID){
		Community retval = new Community();
		String sqlQuery = "Select * FROM Spielrunde where ID = "+communityID;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				retval.setCommunityID(communityID);
				retval.setName(rs.getString("Name"));
			}
		} catch (SQLException e) {
		}
		retval.addManagers(getManagers(communityID));
		return retval;
	}
	
	private List<Manager> getManagers(int communityID){
		List<Manager> retval = new ArrayList<>();
		String sqlQuery = "SELECT Manager.ID, Nutzer.Nutzername, Manager.Budget, Manager.Punkte "
				+ "FROM  Manager INNER JOIN  Nutzer where Spielrunde_ID="+communityID
				+ " AND Manager.Nutzer_ID=Nutzer.ID";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				String managerName = rs.getString("Nutzername");
				double money = rs.getDouble("Budget");
				int points = rs.getInt("Punkte");
				Manager manager = new Manager(managerName,money,points);
				manager.setID(rs.getInt("ID"));
				retval.add(manager);
			}
		} catch (SQLException e) {
		}
		return retval;
	}
}
