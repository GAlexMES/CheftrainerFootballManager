package de.szut.dqi12.cheftrainer.server.utils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.szut.dqi12.cheftrainer.server.databasecommunication.SQLConnection;

public class DatabaseUtils {

	/**
	 * This method checks, if the given ResultSet is empty|has zero rows
	 * 
	 * @param rs
	 *            the ResultSet, that should be checked.
	 * @return true = the ResultSet is empty.
	 */
	public static boolean isResultSetEmpty(ResultSet rs) {
		try {
			if (!rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getManagerID(SQLConnection sqlCon, int userID,
			int communityID) {
		try {
			return DatabaseUtils.getUniqueValue(sqlCon, "Manager.ID",
					"Manager INNER JOIN Nutzer", "Manager.Nutzer_ID=" + userID
							+ " AND Manager.Spielrunde_ID=" + communityID);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static int getManagerID(SQLConnection sqlCon, int userID,
			String communityName) {
			try {
				int communityID = getUniqueValue(sqlCon,"Spielrunde.ID", "Spielrunde", "Spielrunde.Name='"+ communityName+"'");
				return getManagerID(sqlCon, userID, communityID);
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
	}

	public static String getTeamNameForID(SQLConnection sqlCon, int id){
		String sqlQuery ="Select Name from Verein where Verein.ID="+id;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		if (!DatabaseUtils.isResultSetEmpty(rs)) {
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
	public static int getUniqueValue(SQLConnection sqlCon, String coloumName,
			String table, String whereCondition) throws IOException {
		int retval = 0;
		String sqlQuery = "Select " + coloumName + " FROM " + table + " where "
				+ whereCondition;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			if (!DatabaseUtils.isResultSetEmpty(rs)) {
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
