package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class ServerPropertiesManagement {

	private SQLConnection sqlCon;

	public static final String COLOUM_NAME = "Name";
	public static final String COLOUM_VALUE = "Wert";

	public static final String SERVER_PROPS_TABLE = "ServerProperties";

	public static final String FINISHED_PLAYER_PARSING = "SpielerEinlesenBeendet";

	public ServerPropertiesManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
	}
	
	public <E> void setProperty(String property, E value){
		String writeableValue = value.toString();
		if(value.getClass()==Boolean.class){
			if(writeableValue.equals("true")){
				writeableValue = "1";
			}
			else{
				writeableValue = "0";
			}
		}
		String sqlQuery = "";
		if(existProperty(property)){
			sqlQuery = "UPDATE "+SERVER_PROPS_TABLE
					+" SET "+COLOUM_VALUE+" = '"+writeableValue+"'"
					+" WHERE "+COLOUM_NAME+" = '"+property+"'";
		}
		else{
			sqlQuery = "INSERT INTO "+SERVER_PROPS_TABLE+ " VALUES ('"
					+ property +"', '"+writeableValue +"')";
		}
		sqlCon.sendQuery(sqlQuery);
	}
	
	public boolean existProperty(String property){
		try {
			getServerProps(property);
			return true;
		} 
		catch (NoSuchElementException nsee) {
			return false;
		}
	}

	public boolean getPropAsBoolean(String property){
		try{
			int value = getPropAsInt(property);
			if(value >0){
				return true;
			}
			return false;
		}
		catch(NoSuchElementException nsee){
			return false;
		}
		

	}

	public String getPropAsString(String property) throws NoSuchElementException {
		try {
			ResultSet rs = getServerProps(property);
			return rs.getString(COLOUM_VALUE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		catch (NoSuchElementException nsee) {
			throw nsee;
		}
		return null;
	}

	public Integer getPropAsInt(String property) {
		try {
			ResultSet rs = getServerProps(property);
			return rs.getInt(COLOUM_VALUE);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchElementException nsee) {
			throw nsee;
		}
		return null;
	}

	public ResultSet getServerProps(String property)
			throws NoSuchElementException {
		String sqlQuery = "SELECT * FROM " + SERVER_PROPS_TABLE
				+ " WHERE Name = '" + property + "'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			if (rs.next()) {
				return rs;
			} else {
				throw new NoSuchElementException(
						"There is no property with the name '" + property
								+ "' in the database!");
			}
		} catch (SQLException sqe) {
			sqe.printStackTrace();
		}

		return null;
	}
}
