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
