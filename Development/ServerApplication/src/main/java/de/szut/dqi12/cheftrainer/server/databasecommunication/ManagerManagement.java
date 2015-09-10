package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.util.ArrayList;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;

public class ManagerManagement {

	private SQLConnection sqlCon;
	
	public ManagerManagement(SQLConnection sqlCon){
		this.sqlCon=sqlCon;
	}
	
	public List<Community> getCommunitiesForUser( int userID){
		List<Community> retval = new ArrayList<Community>();
		return retval;
	}
	
	public List<String> getCummunitiyNamesForUser(int userID){
		List<String> retval = new ArrayList<>();
		String sqlQuery = "SELECT NAME FROM Spielrunde INNER JOIN Manager where Manager.Nutzer_ID = '"+userID+"'";
		return retval;
	}
}
