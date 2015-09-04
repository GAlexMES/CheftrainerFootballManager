package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import de.szut.dqi12.cheftrainer.server.usercommunication.User;

public class UserManagement {
	
	private static SQLConnection sqlCon = null;
	
	public UserManagement(SQLConnection sqlCon){
		UserManagement.sqlCon = sqlCon;
	}
	
	public static SQLConnection getSQLConnection(){
		return sqlCon;
	}
	
	public HashMap<String,Boolean> register(User newUser){
		HashMap<String,Boolean> retval = existUserName(newUser);
		retval.put("authentificate", false);
		if((!retval.get("existUser")) && (!retval.get("existEMail"))){
			addNewUserToDatabase(newUser);
			retval.put("authentificate", true);
		}
		return retval;
	}
	
	private HashMap<String,Boolean> existUserName(User newUser){
		HashMap<String, Boolean> retval = new HashMap<String,Boolean>();
		retval.put("existUser", false);
		retval.put("existEMail", false);
		String sqlQuery = "SELECT * FROM Nutzer;";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				if(rs.getString(4)!=null && rs.getString(4).equals(newUser.getUserName())){
					retval.put("existUser", true);
				}
				if(rs.getString(5)!=null && rs.getString(5).equals(newUser.geteMail())){
					retval.put("existEMail", true);
				}
			}
		} catch (SQLException e) {
		}
		return retval;
	}
	
	private void addNewUserToDatabase(User newUser){
		String values = newUser.getAllForSQL();
		String sqlQuery = "INSERT INTO Nutzer (Vorname,Nachname,Nutzername,EMail,Passwort) VALUES ( " + values + ");";
		sqlCon.sendQuery(sqlQuery);
	}
	

}



