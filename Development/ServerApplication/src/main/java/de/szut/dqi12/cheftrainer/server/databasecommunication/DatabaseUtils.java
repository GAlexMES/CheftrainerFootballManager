package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

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

	public void clearTable(String tableName){
		String sqlQuery = "DELETE FROM "+tableName;
		sqlCon.sendQuery(sqlQuery);
		
	}
	
	public int getManagerID(int userID,
			int communityID) {
		try {
			return getUniqueValue( "Manager.ID",
					"Manager INNER JOIN Nutzer", "Manager.Nutzer_ID=" + userID
							+ " AND Manager.Spielrunde_ID=" + communityID);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int getManagerID(int userID,
			String communityName) {
			try {
				int communityID = getUniqueValue("Spielrunde.ID", "Spielrunde", "Spielrunde.Name='"+ communityName+"'");
				return getManagerID(userID, communityID);
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
	}

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
	
	public int getUniqueValue( String coloumName,
			String table, String whereCondition) throws IOException {
		int retval = 0;
		String sqlQuery = "Select " + coloumName + " FROM " + table + " where "
				+ whereCondition;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			if (!isResultSetEmpty(rs)) {
				rs = sqlCon.sendQuery(sqlQuery);
				while (rs.next()) {
					retval = rs.getInt(1);
				}
			} else {
				throw new IOException("The value does not exist!");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}
}
