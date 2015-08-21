package de.szut.dqi12.cheftrainer.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.transform.Result;

public class databaseController {
	Connection connection;
	
	
	
	
	
	//Datenbankspezifikation sind aus der Luft gegriffen!
	public float getMarktwert(String spielername, int saison){
		
		PreparedStatement statement;
	    ArrayList<Result> results = new ArrayList<Result>();
	    String query =  "SELECT Punkte FROM Table WHERE Name = ? and Saison = ?";
	    ArrayList<Integer> punkte = new ArrayList<Integer>();
	    float average = 0;
	    
	    try {
			statement = connection.prepareStatement(query);
			statement.setString(1, spielername);
			statement.setInt(2, saison);
			ResultSet rs = statement.executeQuery();
			
			//maybe rows
			if(rs.getMetaData().getColumnCount() > 2){
				query = "SELECT Punkte FROM Table WHERE Name = ?";
				statement = connection.prepareStatement(query);
				statement.setString(1, spielername);

				rs = statement.executeQuery();
			}
			
			while(rs.next()){
			        int i = 1;
					//maybe rows
			        while(i <= rs.getMetaData().getColumnCount()) {
			        	punkte.add(rs.getInt(i++));
			        }
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	    if(punkte.size() > 2){
	    	average = (float) (Math.random() * 1000000);
	    }else{
	    	for(int punkt : punkte){
	    		average += punkt;
	    	}
	    	average = (average  / punkte.size()) * 1000000;
	    }
	    	
	    return average;
		
	}

}