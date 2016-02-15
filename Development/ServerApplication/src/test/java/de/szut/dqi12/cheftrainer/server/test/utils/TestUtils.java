package de.szut.dqi12.cheftrainer.server.test.utils;

import java.util.ArrayList;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Position;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;
import de.szut.dqi12.cheftrainer.server.databasecommunication.PlayerManagement;

public class TestUtils {

	public static void prepareDatabase(SQLConnection sqlCon) {
		clearTable(sqlCon,"NUTZER");
		clearTable(sqlCon,"Spielrunde");
		clearTable(sqlCon,"Manager");
		clearTable(sqlCon,"Mannschaft");
		clearTable(sqlCon,"Transfermarkt");
		clearTable(sqlCon,"Gebote");
		clearTable(sqlCon,"Spieler");
		clearTable(sqlCon,"Verein");
		clearTable(sqlCon,"sqlite_sequence");
	}
	
	public  static void clearTable(SQLConnection sqlCon, String tableName) {
		String query = "DELETE FROM " + tableName;
		sqlCon.sendQuery(query);
	}
	
	public static void preparePlayerTable(SQLConnection sqlCon){
		int playerNumber = 300;
		List<Player> players = generatePlayerList(playerNumber);
		PlayerManagement pm = new PlayerManagement(sqlCon);
		
		String teamName= "Testteam";
		String createTeam = "INSERT INTO VEREIN (Vereinsname, LogoPath, Liga_ID) VALUES ('"+teamName+"','www.example.com','1')";
		sqlCon.sendQuery(createTeam);
		for(Player p : players){
			pm.addPlayer(p, 1);
		}
		String updateSquence = "INSERT INTO sqlite_sequence (Name, seq) VALUES ('Spieler',"+playerNumber+")";
		sqlCon.sendQuery(updateSquence);
	}
	
	private static List<Player> generatePlayerList(int size){
		List<Player> retval = new ArrayList<>();
		for(int i = 0;i<size;i++){
				double worth = Math.random()*3000000;
				Player p = new Player();
				p.setSportalID(i);
				p.setWorth((int)worth);
				p.setName("Player "+i);
				p.setPosition(Position.getPositions().get(i%4));
				retval.add(p);
		}
		return retval;
	}
}
