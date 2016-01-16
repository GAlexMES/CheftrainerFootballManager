package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;

/**
 * This class provides a few simple method, to communicate with the database or to check ResultSets.
 * @author Alexander Brennecke
 *
 */
public class DatabaseUtils {
	
	private SQLConnection sqlCon;
	
	public DatabaseUtils(SQLConnection sqlCon){
		this.sqlCon = sqlCon;
	}

	/**
	 * This method checks, if the given ResultSet is empty|has zero rows
	 * 
	 * @param rs
	 *            the ResultSet, that should be checked.
	 * @return true = the ResultSet is empty.
	 */
	public boolean isResultSetEmpty(ResultSet rs) {
		try {
			if (!rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/**
	 * This function will create a query, that will delete all information, stored in the given database table.
	 * @param tableName the name of the table, that will be cleared.
	 */
	public void clearTable(String tableName){
		String sqlQuery = "DELETE FROM "+tableName;
		sqlCon.sendQuery(sqlQuery);
		
	}
	
	/**
	 * This function creates a query and sends it to the database. The ResultSet will show the 
	 * {@link Manager} ID for the given {@link User} in the given {@link Community};
	 * @param userID the ID of the {@link User}, that owns the {@link Manager}
	 * @param communityID the ID of the {@link Community}, in which the {@link Manager} plays
	 * @return the ID of the searched {@link Manager} or -1, if no {@link Manager} was found.
	 */
	public int getManagerID(int userID,
			int communityID) {
		try {
			String condition = "Manager.Nutzer_ID=" + userID
					+ " AND Manager.Spielrunde_ID=" + communityID;
			return Integer.valueOf(getUniqueValue( "Manager.ID",
					"Manager INNER JOIN Nutzer", condition).toString());
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	

	/**
	 * This function creates a query and sends it to the database. The ResultSet will show the 
	 * {@link Manager} ID for the given {@link User} in the given {@link Community};
	 * @param userID the ID of the {@link User}, that owns the {@link Manager}
	 * @param communityName the name of the {@link Community}, in which the {@link Manager} plays
	 * @return the ID of the searched {@link Manager} or -1, if no {@link Manager} was found.
	 */
	public int getManagerID(int userID,
			String communityName) {
			try {
				String condition = "Spielrunde.Name='"+ communityName+"'";
				int communityID = DatabaseRequests.getUniqueInt("Spielrunde.ID", "Spielrunde", condition);
				return getManagerID(userID, communityID);
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
	}

	/**
	 * This method searches for the name of the team with the given ID.
	 * @param id the ID of the team.
	 * @return the name of the team or "", when there was no team with the given ID
	 */
	public String getTeamNameForID( int id){
		String sqlQuery ="Select Name from Verein where Verein.ID="+id;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		if (!isResultSetEmpty(rs)) {
			try {
				while (rs.next()) {
					return  rs.getString("Name");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		return "";
	}
	
		
	/**
	 * This method can be used to get exactly one value from the database.
	 * @param coloumName the name of the coloum
	 * @param table the name of the table
	 * @param whereCondition the search condition (without where)
	 * @return the value as Object
	 * @throws IOException when there is no value, that matches the condition.
	 */
	public Object getUniqueValue( String coloumName,
			String table, String whereCondition) throws IOException {
		String sqlQuery = "SELECT " + coloumName + " FROM " + table + " WHERE "
				+ whereCondition;
		String[] splittedColoumName = coloumName.split("\\.");
		coloumName = splittedColoumName[splittedColoumName.length-1];
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			if (!isResultSetEmpty(rs)) {
				rs = sqlCon.sendQuery(sqlQuery);
				while (rs.next()) {
					return rs.getString(coloumName);
				}
			} else {
				throw new IOException("The value does not exist!");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method sends the Query to the database.
	 */
	public void sendSimpleQuery(String query) {
		sqlCon.sendQuery(query);
	}
}
