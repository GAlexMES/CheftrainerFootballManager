package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Formation;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.RealTeam;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;
import de.szut.dqi12.cheftrainer.server.parsing.PlayerParser;
import de.szut.dqi12.cheftrainer.server.parsing.TeamParser;

/**
 * This class is used to Initialize the Database, when the server starts the
 * first time.
 * 
 * @author Alexander Brennecke
 */
public class PlayerManagement extends SQLManagement {

	private SQLConnection sqlCon;

	private final static Logger LOGGER = Logger
			.getLogger(PlayerManagement.class);
	private int teamCounter = 0;

	public PlayerManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
	}

	/**
	 * This method tries to collect {@link Player}. It uses the
	 * {@link PlayerParser} and {@link TeamParser} to fetch these players from
	 * different websides. Later it will call another function to write the
	 * collected players inside the database.
	 * 
	 * @param leagueName
	 *            the name of the league. Should be "Bundesliga" at the moment.
	 * @param leagueCountry
	 *            the country of the league. Should be "Deutschland" at the
	 *            moment.
	 * @throws IOException
	 */
	public void loadRealPlayers(String leagueName, String leagueCountry)
			throws IOException {
		try {
			addLeague(leagueName, leagueCountry);
			LOGGER.info("Validating database: 0% Done");
			List<RealTeam> teamList = TeamParser.getTeams();
			LOGGER.info("Validating database: 10% Done");
			String condition = "Name='"+leagueName+"'";
			int leagueID = DatabaseRequests.getUniqueInt("ID", "Liga",condition);
			teamList.forEach(t -> addTeam(t, leagueID));
		} catch (IOException e) {
			throw e;
		}

	}

	/**
	 * This method creates a SQLQuerry out of the given Player and sends it to
	 * the database.
	 * 
	 * @param p
	 *            the {@link Player} that should be send to the database.
	 * @param teamID
	 *            the id of the {@link RealTeam}, in which this player plays.
	 */
	private void addPlayer(Player p, int teamID) {
		int worth = (int) (Math.random() * 5000000);
		String sqlQuery = "INSERT INTO Spieler (Name,Verein_ID, Position, Punkte, Marktwert, Nummer, SportalID, Birthday, PicturePath) "
						+ "VALUES ('"+p.getName()+ "','"
						+ teamID +"','"
						+ p.getPosition() +"','"
						+ "0','"
						+ worth + "','"
						+ p.getNumber() + "','"
						+ p.getSportalID() + "','"
						+ p.getBirthdateString() + "','"
						+ p.getAbsolutePictureURL()
						+"')";
		sqlCon.sendQuery(sqlQuery);
	}

	/**
	 * This method adds a {@link RealTeam}, including all {@link Player},
	 * playing in this {@link RealTeam}, to the database.
	 * 
	 * @param t
	 *            the {@link RealTeam}, that should be added to the database.
	 * @param leagueID
	 *            the id of the League, in which the team plays.
	 */
	private void addTeam(RealTeam t, int leagueID) {
		teamCounter++;
		try {
			String sqlQuery = "INSERT INTO Verein (Vereinsname, Liga_ID, LogoPath) Values ('"
					+ t.getTeamName() + "','" + leagueID + "','"+t.getLogoURL()+"')";
			sqlCon.sendQuery(sqlQuery);
			String condition = "Vereinsname='"+t.getTeamName()+"'";
			int teamID = DatabaseRequests.getUniqueInt("ID", "Verein",condition );
			List<Player> playerList = t.getPlayerList();
			playerList.forEach(p -> addPlayer(p, teamID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOGGER.info("Validating database: " + (10 + (teamCounter * 5))
				+ "% Done");
	}

	/**
	 * This method adds a new League to the database.
	 * 
	 * @param name
	 *            the name of the league
	 * @param country
	 *            the country, in which the league is hosted.
	 */
	private void addLeague(String name, String country) {
		String sqlString = "INSERT INTO Liga ('Name','Land') VALUES('" + name
				+ "','" + country + "')";
		sqlCon.sendQuery(sqlString);
	}
	
	/**
	 * This method creates a sqlQuery and sends it to the database. The query
	 * will attache the {@link Player}, with the given ID, to the
	 * {@link Manager}, with the given Id.
	 * 
	 * @param managerID
	 *            the ID of the {@link Manager}, that should own the
	 *            {@link Player};
	 * @param playerID
	 *            the ID of the {@link Player}, that should be owned by the
	 *            {@link Manager};
	 * @param plays true = player is in the formation, false = he is not.
	 */
	public void addPlayerToManager(int managerID, int playerID, boolean plays){
		int play = 0;
		if (plays) {
			play = 1;
		}
		String sqlQuery = "INSERT INTO Mannschaft ('Manager_ID','Spieler_ID','Aufgestellt') " + " VALUES (?,?,?)";
		PreparedStatement pStatement;
		try {
			pStatement = sqlCon.prepareStatement(sqlQuery);
			pStatement.setInt(1, managerID);
			pStatement.setInt(2, playerID);
			pStatement.setInt(3,play);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setManagersFormation(int managerID, int defenders,
			int middfielders, int offensives) {
		String sqlQuery = "UPDATE Manager Set Anzahl_Abwehr = " + defenders
				+ ", Anzahl_Mittelfeld=" + middfielders + ", Anzahl_Stuermer="
				+ offensives + " Where ID = " + managerID;
		sqlCon.sendQuery(sqlQuery);

	}

	public static Player getPlayerFromResult(ResultSet rs) {
		Player p = new Player();
		String id = getDefault(getIntFromRS(rs, "ID"));
		p.setID(Integer.valueOf(id));
		String name = getDefault(getStringFromRS(rs, "Name"));
		p.setName(name);
		String position = getDefault(getStringFromRS(rs, "Position"));
		p.setPosition(position);
		String number = getDefault(getIntFromRS(rs, "Nummer"));
		p.setNumber(Integer.valueOf(number));
		String worth = getDefault(getIntFromRS(rs, "Marktwert"));
		p.setWorth(Integer.valueOf(worth));
		String points = getDefault(getIntFromRS(rs, "Punkte"));
		p.setPoints(Integer.valueOf(points));
		String temName = getDefault(getStringFromRS(rs, "Vereinsname"));
		p.setTeamName(temName);
		String sportalID = getDefault(getIntFromRS(rs, "SportalID"));
		p.setSportalID(Integer.valueOf(sportalID));
		String birthday = getDefault(getStringFromRS(rs, "Birthday"), "1.1.1900");
		p.setBirthdate(birthday);
		String picturePath = getDefault(getStringFromRS(rs, "PicturePath"));
		p.setAbsolutePictureURL(picturePath);

		int play = Integer.valueOf(getDefault(getIntFromRS(rs, "Aufgestellt")));
		if (play == 1) {
			p.setPlays(true);
		} else {
			p.setPlays(false);

		}
		return p;
	}

	public static List<Player> getPlayersFromResultSet(ResultSet rs) throws SQLException {
		List<Player> retval = new ArrayList<>();
		while (rs.next()) {
			Player p = getPlayerFromResult(rs);
			retval.add(p);
		}
		return retval;
	}

	/**
	 * This method sends a query to the database to collect all information for
	 * the {@link Player} with the given ID.
	 * 
	 * @param playerID
	 *            the ID of a {@link Player}
	 * @return the {@link Player} object for the given ID from the "Spieler"
	 *         table of the database.
	 */
	public Player getPlayer(int playerID) {
		String sqlQuery = "SELECT * FROM Spieler WHERE Spieler.ID = "+playerID;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		return getPlayerFromResult(rs);
	}

	
	/**
	 * This function updates the SQLEntries for the given {@link Manager}.
	 * @param manager the {@link Manager}, that should be updated.
	 */
	public void updateManager(Manager manager) {
		Formation f = manager.getFormation();
		setManagersFormation(manager.getID(), f.getDefenders(), f.getMiddfielders(), f.getOffensives());
		
		for(Player p : manager.getPlayers()){
			int plays = p.isPlays() ? 1 : 0;
			String updateQuery = "UPDATE Mannschaft "
					+ "	SET Aufgestellt = " + plays
					+ " WHERE Manager_ID = "+manager.getID()
					+ " AND Spieler_ID = "+p.getID();
			sqlCon.sendQuery(updateQuery);
		}
				
	}

	/**
	 * This function clears the "Manschaft Copy" table and copies the "Mannschaft" table to the "Manschaft Copy" table.
	 */
	public void copymanagerTeam() {
		String deleteQuery = "DELETE FROM 'Mannschaft Copy'";
		String copyQuery = "INSERT INTO 'Mannschaft Copy' SELECT Manager_ID, Spieler_ID, NULL FROM Mannschaft WHERE Aufgestellt = 1;";
		
		sqlCon.sendQuery(deleteQuery);
		sqlCon.sendQuery(copyQuery);
	}
}
