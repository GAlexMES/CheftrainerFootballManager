package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;

import de.szut.dqi12.cheftrainer.server.database.SQLConnection;

abstract class SQLManagement {
	
	protected SQLConnection sqlCon;

	protected static Integer getIntFromRS(ResultSet rs, String coloumName) {
		try {
			int retval = rs.getInt(coloumName);
			return retval;
		} catch (Exception e) {
			return 0;
		}
	}

	protected static String getStringFromRS(ResultSet rs, String coloumName) {
		try {
			String retval = rs.getString(coloumName);
			return retval;
		} catch (Exception e) {
			return "";
		}
	}

	protected static String getDefault(Object o) {
		return getDefault(o, "0");
	}

	protected static String getDefault(Object o, String defaultValue) {
		if (o != null) {
			return String.valueOf(o);
		}
		return defaultValue;
	}
}
