package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;
import java.util.List;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.RealTeam;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.utils.DatabaseUtils;
import de.szut.dqi12.cheftrainer.server.utils.ParserUtils;

public class InitializationManagement {

	private SQLConnection sqlCon;
	
	private final static Logger LOGGER = Logger.getLogger(InitializationManagement.class);
	private int teamCounter = 0;

	public InitializationManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
	}

	public boolean existPlayer() {
		String sqlQuery = "Select ID from Spieler";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		return !DatabaseUtils.isResultSetEmpty(rs);
	}

	public void loadRealPlayers(String leagueName, String leagueCountry,
			String sourceURL) {
		try {
			addLeague(leagueName, leagueCountry);
			LOGGER.info("Validating database: 0% Done");
			List<RealTeam> teamList = ParserUtils.getTeamList(sourceURL);
			LOGGER.info("Validating database: 10% Done");
			String condition = "Name='"+leagueName+"'";
			int leagueID = Integer.valueOf(DatabaseUtils.getUniqueValue(sqlCon,"ID", "Liga",condition));
			teamList.forEach(t -> addTeam(t, leagueID));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addPlayer(Player p, int teamID){
		int points = (int) (Math.random() * 10000000);
		String sqlQuery = "INSERT INTO Spieler (Name,Verein_ID, Position, Punkte, Marktwert, Nummer) "
						+ "VALUES ('"+p.getName()+ "','"
						+ teamID +"','"
						+ p.getPosition() +"','"
						+ "0','"
						+ points + "','"
						+ p.getNumber() +"')";
		sqlCon.sendQuery(sqlQuery);
	}

	private void addTeam(RealTeam t, int leagueID) {
		teamCounter++;
		try {
			String sqlQuery = "INSERT INTO Verein (Name, Liga_ID) Values ('"
					+ t.getTeamName() + "','" + leagueID + "')";
			sqlCon.sendQuery(sqlQuery);
			String condition = "Name='"+t.getTeamName()+"'";
			int teamID = Integer.valueOf(DatabaseUtils.getUniqueValue(sqlCon,"ID", "Verein",condition ));
			List<Player> playerList = t.getPlayerList();
			playerList.forEach(p -> addPlayer(p, teamID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOGGER.info("Validating database: "+(10+(teamCounter*5))+"% Done");
	}

	private void addLeague(String name, String country) {
		String sqlString = "INSERT INTO Liga ('Name','Land') VALUES('"+name+"','"+country+"')";
		sqlCon.sendQuery(sqlString);
	}
}
