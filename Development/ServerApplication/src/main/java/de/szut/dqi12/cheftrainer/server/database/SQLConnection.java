package de.szut.dqi12.cheftrainer.server.database;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.server.logic.ServerInitialator;

/**
 * This class is used to connect to a existing database.
 * 
 * @author Alexander Brennecke
 *
 */
public class SQLConnection {
	
	private final String RELATIVE_DB_PATH ="/Database.db";

	// INITIALISATION
	private final static String SQLEXCEPTION_NORESULT = "query does not return ResultSet";
	private final static String SQLEXCEPTION_ERROR = "[SQLITE_ERROR]";
	private final static String SQLEXCEPTION_BUSY = "[SQLITE_BUSY]";

	private Connection con = null;
	private Statement statement = null;
	private String name = "";

	private final static Logger LOGGER = Logger.getLogger(SQLConnection.class);

	private ArrayList<String> tableNames = new ArrayList<String>();

	/**
	 * Constructor
	 * 
	 * @param name
	 *            of the database
	 */
	public SQLConnection(String name, String sqlPath, boolean init)
			throws IOException {
		this.name = name;
		DatabaseRequests.getInstance().setSQLConnection(this);

		loadDB();
		
		if (init) {
			ServerInitialator.databaseInitalisation();
		}
	}
	
	private String prepareString(String databasePath) throws MalformedURLException{
		databasePath = databasePath.replace("\\.\\", "\\");
		return databasePath;
	}
	
	
	private String getDatabasePath() throws IOException{
		File databaseFile = new File("."+RELATIVE_DB_PATH);
		Boolean databaseExist = databaseFile.exists();
		String path =databaseFile.getAbsolutePath(); 
		String databaseURL = prepareString(path);
		
		if(!databaseExist){
			DatabaseCreator.cretae(databaseURL);
		}
		
		return databaseURL;
	}

	
	/**
	 * Tries to connect to the given db file
	 * 
	 * @param path
	 *            to the db file
	 * @throws IOException 
	 */
	private void loadDB() throws IOException {
		String databaseURL = getDatabasePath();
		LOGGER.info("Connecting to the database file!");
		final String url = "jdbc:sqlite:"+databaseURL;

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// Cannot finde driver!
			e.printStackTrace();
		}

		try {
			con = DriverManager.getConnection(url);
			DatabaseMetaData md = con.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
				String type = rs.getString(4);
				if (type.equals("TABLE")) {
					tableNames.add(rs.getString(3));
				}
			}

			statement = con.createStatement();

			statement.executeQuery("ATTACH '" + name + "' as "
					+ name.substring(0, name.length() - 3));
			LOGGER.info("Connecting to the database file was succesfull!");
		} catch (SQLException e) {
			handleSQLException(e);
		}
	}

	/**
	 * Tries to send a query to the database
	 * 
	 * @param command
	 *            query as String
	 * @return ResultSet with the retval of the database
	 */
	public ResultSet sendQuery(String command) {
		ResultSet currentSet = null;
		try {
			currentSet = statement.executeQuery(command);
			return currentSet;
		} catch (SQLException sqle) {
			handleSQLException(sqle);
			return null;
		}
	}

	/**
	 * This method handles SQLExceptions.
	 * 
	 * @param sqle
	 *            SQLException
	 */
	public static void handleSQLException(SQLException sqle) {
		if (sqle.getMessage().contains(SQLEXCEPTION_NORESULT)) {
		} else if (sqle.getMessage().contains(SQLEXCEPTION_ERROR)) {
			String sqLiteError = sqle.getMessage().split("]")[1];
			LOGGER.error(sqLiteError);
		} else if (sqle.getMessage().contains(SQLEXCEPTION_BUSY)) {
			String sqLiteError = sqle.getMessage().split("]")[1];
			LOGGER.error(sqLiteError);
		} else {
			LOGGER.error(sqle.getLocalizedMessage());
		}
	}
	
	public PreparedStatement prepareStatement(String sqlQuery) throws SQLException{
		return con.prepareStatement(sqlQuery);
	}
	
	// GETTER&SETTER
	// /////////////
	public String getName() {
		return this.name;
	}

	public ArrayList<String> getTableNames() {
		return this.tableNames;
	}

	public Connection getConnection() {
		return con;
	}
}
