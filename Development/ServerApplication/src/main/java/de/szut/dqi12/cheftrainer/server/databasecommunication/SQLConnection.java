package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLConnection {

	//INITIALISATION
	private final String SQLEXCEPTION_NORESULT = "query does not return ResultSet";
	private final String SQLEXCEPTION_ERROR = "[SQLITE_ERROR]";
	private final String SQLEXCEPTION_BUSY = "[SQLITE_BUSY]";

	private Connection con = null;
	private Statement statement = null;
	private String name = "";

	
	private ArrayList<String> tableNames = new ArrayList<String>();

	/**
	 * Constructor
	 * @param name of the database
	 */
	public SQLConnection(String name) {
		this.name = name;
		DatabaseUtils.getInstance().setSQLConnection(this);
	}

	/**
	 * Tries to connect to the given db file
	 * 
	 * @param path to the db file
	 */
	public void loadDB(String path) {

		final String url = "jdbc:sqlite:" + path;

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

			statement.executeQuery("ATTACH '" + name + "' as " + name.substring(0, name.length() - 3));

		} catch (SQLException e) {
			handleSQLException(e);
		}
	}

	/**
	 * Tries to send a query to the database
	 * @param command query as String
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
	 * @param sqle SQLException
	 */
	private void handleSQLException (SQLException sqle){
		if (sqle.getMessage().contains(SQLEXCEPTION_NORESULT)) {
			System.err.println("No return Data. Use lastResult");
		} else if (sqle.getMessage().contains(SQLEXCEPTION_ERROR)) {
			String sqLiteError = sqle.getMessage().split("]")[1];
			System.err.print(sqLiteError);
		} else if (sqle.getMessage().contains(SQLEXCEPTION_BUSY)) {
			String sqLiteError = sqle.getMessage().split("]")[1];
			System.err.print(sqLiteError);
		} else {
			sqle.printStackTrace();
		}
	}

	
	//GETTER&SETTER
	///////////////
	public String getName() {
		return this.name;
	}

	public ArrayList<String> getTableNames() {
		return this.tableNames;
	}
	
	public Connection getConnection(){
		return con;
	}
}
