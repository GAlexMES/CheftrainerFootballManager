package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;


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
		retval.put(MIDs.AUTHENTIFICATE, false);
		if ((!retval.get(MIDs.USER_EXISTS)) && (!retval.get(MIDs.EMAIL_EXISTS))) {
			addNewUserToDatabase(newUser);
			retval.put(MIDs.AUTHENTIFICATE, true);
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
		retval.put(MIDs.USER_EXISTS, false);
		retval.put(MIDs.EMAIL_EXISTS, false);
		String sqlQuery = "SELECT * FROM Nutzer;";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				if (rs.getString(4) != null
						&& rs.getString(4).equals(newUser.getUserName())) {
					retval.put(MIDs.USER_EXISTS, true);
				}
				if (rs.getString(5) != null
						&& rs.getString(5).equals(newUser.geteMail())) {
					retval.put(MIDs.EMAIL_EXISTS, true);
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
		retval.put(MIDs.USER_EXISTS, false);
		retval.put(MIDs.PASSWORD, false);
		String sqlQuery = "select Passwort FROM Nutzer where Nutzername = '"
				+ user.getUserName() + "'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		int counter = 0;
		try {
			while (rs.next()) {
				counter ++;
				if (rs.getString(1).equals(user.getPassword())) {
					retval.put(MIDs.PASSWORD, true);
				}
			}
		} catch (SQLException e) {

		}
		if(counter == 1){
			retval.put(MIDs.USER_EXISTS, true);
		}
		return retval;
	}
	
	/**
	 * This mehtod maps all information of the given user to a new User object and returns it.
	 * @param userName is used to find the information in the database and to map it to the User Object.
	 * @return a new User object with the mapped parameters.
	 */
	public User getUserValues(String userName){
		String sqlQuery = "select * FROM Nutzer where Nutzername = '"
				+ userName + "'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		User retval = new User();
		try {
			while (rs.next()) {
				retval.seteMail(rs.getString("EMail"));
				retval.setFirstName(rs.getString("Vorname"));
				retval.setLastName(rs.getString("Nachname"));
				retval.setUserName(rs.getString("Nutzername"));
				retval.setUserId(rs.getInt("ID"));
				retval.setPassword(rs.getString("Passwort"));
			}
		} catch (SQLException e) {

		}
		return retval;
	}
	
	/**
	 * Searches in the database for the {@link User}, that owns the {@link Manager} with the given ID
	 * @param managerID the ID of one of the managers, the wanted user owns.
	 * @return the name of the {@link User}, that owns a {@link Manager} with the given ID.
	 */
	public String getUserName(int managerID){
		String sqlQuery = "select Nutzername from Nutzer inner join Manager where Nutzer.ID = Manager.Nutzer_ID and Manager.ID="+managerID;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				return rs.getString("Nutzername");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

}
