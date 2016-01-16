package de.szut.dqi12.cheftrainer.server.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

public class DatabaseCreator {

	private final static Logger LOGGER = Logger.getLogger(DatabaseCreator.class);
	
	private static Connection con = null;
	private static Statement statement = null;
	private static String name = "";

	public static void cretae(String path) throws IOException {
		LOGGER.info("Creating database, because it does not exist.");

		createName(path);
		
		File databaseFile = new File(path);
		databaseFile.getParentFile().mkdirs(); 
		databaseFile.createNewFile();
		
		final String url = "jdbc:sqlite:" + path;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// Cannot finde driver!
			e.printStackTrace();
		}

		try {
			con = DriverManager.getConnection(url);
			statement = con.createStatement();
			createTables(statement);
			statement.executeQuery("ATTACH '" + name + "' as " + name);
			con.close();
		} catch (SQLException e) {
			SQLConnection.handleSQLException(e);
		}
		
		LOGGER.info("database was created under: " + path);
	}
	
	private static void createTables(Statement statement){
		List<String> tableQueries = TableQueries.getTableQueries();
		for(String s : tableQueries){
			try {
				statement.execute(s);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void createName(String path){
		Path p = Paths.get(path);
		name = p.getFileName().toString();
		name = name.split("\\.")[0];
	}

}
