package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import de.szut.dqi12.cheftrainer.server.database.SQLConnection;

/**
 * This class is used to read/write server specific properties to the database.
 * @author Alexander Brennecke
 *
 */
public class ServerPropertiesManagement {

	private SQLConnection sqlCon;

	public static final String COLOUM_NAME = "Name";
	public static final String COLOUM_VALUE = "Wert";

	public static final String SERVER_PROPS_TABLE = "ServerProperties";

	public static final String FINISHED_PLAYER_PARSING = "SpielerEinlesenBeendet";
	public static final String FINISHED_POINT_PARSING = "PunkteNachServerStartEingelesen";

	/**
	 * Constructor
	 * @param sqlCon
	 */
	public ServerPropertiesManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
	}
	
	/**
	 * This function will write the given property and the given value to the database.
	 * If the property already exist, than it will be updated. If it does not exist, it will be created.
	 * @param property the name of the property
	 * @param value the value of the property
	 */
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
	
	/**
	 * This function checks, if the property with the given name exists in the database.
	 * @param property the name of the property
	 * @return true = property exists.
	 */
	public boolean existProperty(String property){
		try {
			getServerProps(property);
			return true;
		} 
		catch (NoSuchElementException nsee) {
			return false;
		}
	}

	/**
	 * This function reads the property with the given name and will return it as a boolean.
	 * The value of the given property must be a Integer
	 * @param property name of the property
	 */
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

	/**
	 * This function reads the property with the given name and will return it as a String.
	 * @param property the name of the property
	 * @return the value as String
	 * @throws NoSuchElementException when there is no property with the given name.
	 */
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

	/**
	 * This function reads the property with the given name and will return it as a Integer.
	 * @param property the name of the property
	 * @return the value of the property as Integer
	 */
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

	/**
	 * This function creates a SQLQuery, to read the given property from the database.
	 * @param property The name of the property, that should be read from the database.
	 * @return a ResultSet, that was returned from the SQLite driver after sending the query.
	 * @throws NoSuchElementException when there is no property with the given name.
	 */
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
