package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;

public class CommunityManagement {

	private SQLConnection sqlCon;

	public CommunityManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
	}

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
	
	public boolean createNewCommunity(String name, String password, int adminID){
		String sqlQuery = "Select Name From Spielrunde where Name='"+name+"'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			int counter = 0;
			while (rs.next()) {
				counter ++;
			}
			if(counter == 0){
				return createCommunity(name,password, adminID);
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean createCommunity(String name, String password, int adminID){
		String sqlQuery = "INSERT INTO Spielrunde (Name, Administrator_ID, Passwort) VALUES ( '"
							+ name +"', '"+adminID +"', '"+password+"')";
		sqlCon.sendQuery(sqlQuery);
		return true;
	}
}
