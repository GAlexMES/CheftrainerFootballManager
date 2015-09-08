package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import de.szut.dqi12.cheftrainer.server.usercommunication.User;


/**
 * This class handles the User registration and login. I creates database entries for new users.
 * @author Alexander Brennecke
 *
 */
public class UserManagement {

	//Given SGLConnection to database
	private static SQLConnection sqlCon = null;

	/**
	 * Constructor
	 * @param sqlCon active SQL Connection
	 */
	public UserManagement(SQLConnection sqlCon) {
		UserManagement.sqlCon = sqlCon;
	}
	
	/**
	 * This method registers a new user to the database. But only if the username and the eMail doens't exist in the database.
	 * @param newUser a User object of the new user
	 * @return a HashMap with registry information.
	 */

	public HashMap<String, Boolean> register(User newUser) {
		HashMap<String, Boolean> retval = existUser(newUser);
		retval.put("authentificate", false);
		if ((!retval.get("existUser")) && (!retval.get("existEMail"))) {
			addNewUserToDatabase(newUser);
			retval.put("authentificate", true);
		}
		return retval;
	}

	/**
	 * This method checks if a User with the given username and eMail already exists in the database.
	 * @param newUser a User object of the user, who should be checked.
	 * @return a HashMap with information about the existence of the username and the eMail.
	 */
	private HashMap<String, Boolean> existUser(User newUser) {
		HashMap<String, Boolean> retval = new HashMap<String, Boolean>();
		retval.put("existUser", false);
		retval.put("existEMail", false);
		String sqlQuery = "SELECT * FROM Nutzer;";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				if (rs.getString(4) != null
						&& rs.getString(4).equals(newUser.getUserName())) {
					retval.put("existUser", true);
				}
				if (rs.getString(5) != null
						&& rs.getString(5).equals(newUser.geteMail())) {
					retval.put("existEMail", true);
				}
			}
		} catch (SQLException e) {
		}
		return retval;
	}

	/**
	 * This methid adds a new user to the database.
	 * @param newUser
	 */
	private void addNewUserToDatabase(User newUser) {
		String values = newUser.getAllForSQL();
		String sqlQuery = "INSERT INTO Nutzer (Vorname,Nachname,Nutzername,EMail,Passwort) VALUES ( "
				+ values + ");";
		sqlCon.sendQuery(sqlQuery);
	}

	/**
	 * This method is called to log a user in. It compares the user name and the password.
	 * @param user a User Object for the user, who wants to login
	 * @return a HashMap with information about the existence of the username and the correct password.
	 */
	public HashMap<String, Boolean> login(User user) {
		HashMap<String,Boolean> retval = new HashMap<String,Boolean>();
		retval.put("userExist", false);
		retval.put("password", false);
		String sqlQuery = "select Passwort FROM Nutzer where Nutzername = '"
				+ user.getUserName() + "'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		int counter = 0;
		try {
			
			while (rs.next()) {
				counter ++;
				if (rs.getString(1).equals(user.getPassword())) {
					retval.put("password", true);
				}
			}
		} catch (SQLException e) {

		}
		if(counter == 1){
			retval.put("userExist", true);
		}
		return retval;
	}

}
