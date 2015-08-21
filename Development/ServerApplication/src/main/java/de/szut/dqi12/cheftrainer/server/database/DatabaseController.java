package de.szut.dqi12.cheftrainer.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.transform.Result;

public class DatabaseController {
	Connection connection;
	
	public boolean connect(String url, String password, String user, String driver) {
		
        try {
            //Treiber der Datenbank wird geladen.
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            //Verbindung zur Datenbank wird aufgebaut
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

	public Connection getConnection() {
		return connection;
	}

	

	
	
	
	
}