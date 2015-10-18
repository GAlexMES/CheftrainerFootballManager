package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.RealTeam;
import de.szut.dqi12.cheftrainer.server.parsing.PlayerParser;
import de.szut.dqi12.cheftrainer.server.parsing.TeamParser;

/**
 * This class is used to Initialize the Database, when the server starts the first time.
 * @author Alexander Brennecke
 *
 */
public class PlayerManagement {

	private SQLConnection sqlCon;
	
	private final static Logger LOGGER = Logger.getLogger(PlayerManagement.class);
	private int teamCounter = 0;

	public PlayerManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
	}

	/**
	 * This method tries to collect {@link Player}. It uses the {@link PlayerParser} and {@link TeamParser} to fetch these players from different websides.
	 * Later it will call another function to write the collected players inside the database.
	 * 
	 * @param leagueName the name of the league. Should be "Bundesliga" at the moment.
	 * @param leagueCountry the country of the league. Should be "Deutschland" at the moment.
	 * @throws IOException
	 */
	public void loadRealPlayers(String leagueName, String leagueCountry) throws IOException{
		try {
			addLeague(leagueName, leagueCountry);
			LOGGER.info("Validating database: 0% Done");
			List<RealTeam> teamList = TeamParser.getTeams();
			LOGGER.info("Validating database: 10% Done");
			String condition = "Name='"+leagueName+"'";
			int leagueID = Integer.valueOf(DatabaseRequests.getUniqueValue("ID", "Liga",condition).toString());
			teamList.forEach(t -> addTeam(t, leagueID));
		} catch (IOException e) {
			throw e;
		}
		
	}

	/**
	 * This method creates a SQLQuerry out of the given Player and sends it to the database.
	 * @param p the {@link Player} that should be send to the database.
	 * @param teamID the id of the {@link RealTeam}, in which this player plays.
	 */
	private void addPlayer(Player p, int teamID){
		int worth = (int) (Math.random() * 5000000);
		String sqlQuery = "INSERT INTO Spieler (Name,Verein_ID, Position, Punkte, Marktwert, Nummer) "
						+ "VALUES ('"+p.getName()+ "','"
						+ teamID +"','"
						+ p.getPositionString() +"','"
						+ "0','"
						+ worth + "','"
						+ p.getNumber() +"')";
		sqlCon.sendQuery(sqlQuery);
	}

	/**
	 * This method adds a {@link RealTeam}, including all {@link Player}, playing in this {@link RealTeam}, to the database.
	 * @param t the {@link RealTeam}, that should be added to the database.
	 * @param leagueID the id of the League, in which the team plays.
	 */
	private void addTeam(RealTeam t, int leagueID) {
		teamCounter++;
		try {
			String sqlQuery = "INSERT INTO Verein (Vereinsname, Liga_ID) Values ('"
					+ t.getTeamName() + "','" + leagueID + "')";
			sqlCon.sendQuery(sqlQuery);
			String condition = "Vereinsname='"+t.getTeamName()+"'";
			int teamID = Integer.valueOf(DatabaseRequests.getUniqueValue("ID", "Verein",condition ).toString());
			List<Player> playerList = t.getPlayerList();
			playerList.forEach(p -> addPlayer(p, teamID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOGGER.info("Validating database: "+(10+(teamCounter*5))+"% Done");
	}

	/**
	 * This method adds a new League to the database.
	 * @param name the name of the league
	 * @param country the country, in which the league is hosted.
	 */
	private void addLeague(String name, String country) {
		String sqlString = "INSERT INTO Liga ('Name','Land') VALUES('"+name+"','"+country+"')";
		sqlCon.sendQuery(sqlString);
	}
}
