package de.szut.dqi12.cheftrainer.server.UserCommunication;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.szut.dqi12.cheftrainer.server.databaseCommunication.SQLConnection;

public class UserRegistration {
	
	SQLConnection sqlCon = null;
	public UserRegistration(SQLConnection sqlCon){
		this.sqlCon = sqlCon;
	}
	
	public boolean register(User newUser){
		if(!existUserName(newUser.getUserName())){
			addNewUserToDatabase(newUser);
		}
		
		return false;
	}
	
	private boolean existUserName(String userName){
		String sqlQuery = "SELECT * FROM Nutzer;";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				if(rs.getString(4).equals(userName)){
					return true;
				}
			}
		} catch (SQLException e) {
		}
		return false;
	}
	
	private void addNewUserToDatabase(User newUser){
		String values = newUser.getAllForSQL();
		String sqlQuery = "INSERT INTO Nutzer (Vorname,Nachname,Nutzername,EMail,Passwort) VALUES ( " + values + ");";
		sqlCon.sendQuery(sqlQuery);
	}
	

}



