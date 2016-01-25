package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Position;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;
import de.szut.dqi12.cheftrainer.server.parsing.PointsParser;

/**
 * This cless is used to send Query to the database to update the points of
 * players.
 * 
 * @author Alexander Brennecke
 *
 */
public class PointManagement {
	private final static Logger LOGGER = Logger.getLogger(PointManagement.class);

	private final String UPDATE_POINT_QUERY = "UPDATE Spieler SET Punkte=Punkte + %POINTS% ";
	private final String WHERE_PLAYER_QUERY = "Name LIKE '%%PLAYERNAME%%' ";
	private final String WHERE_TEAM_QUERY = "Verein_ID IN (SELECT ID FROM Verein WHERE Verein.Vereinsname LIKE '%%TEAMNAME%%') ";

	SQLConnection sqlCon;

	/**
	 * Constructor
	 * 
	 * @param sqlCon
	 */
	public PointManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
	}

	/**
	 * This function updates the Points of the Players in the given list. The
	 * points, defined in the given {@link Player} object will be added to the
	 * points stored in the database.
	 * 
	 * @param playerList
	 *            a Map, where the key is a String and the value is a {@link Player} object. The Keys are not
	 *            relevant.
	 */
	public void updatePointsOfPlayers(Map<String, Player> playerList) {
		for (String s : playerList.keySet()) {
			Player p = playerList.get(s);
			int pointsForGoal = getPointsForGoals(p);
			p.setPoints(p.getPoints() + pointsForGoal);
			String sqlQuery = "";
			if (p.getSportalID() > 0) {
				sqlQuery = createUpdatePlayerPointsQuery(p.getPoints(), p.getSportalID());
			} else {
				sqlQuery = createUpdatePlayerPointsQuery(p.getPoints(), p.getName(), p.getTeamName());
			}
			sqlCon.sendQuery(sqlQuery);
		}

	}

	/**
	 * This method return the points, that a {@link Player} will receive for his
	 * goals. The points were multiplied by a factor, that depends on the
	 * position of the {@link Player}
	 * 
	 * @param p
	 *            a {@link Player} object, that has a Name and a TeamName
	 * @return the points for the goals as int
	 */
	private int getPointsForGoals(Player p) {
		String position = "";
		int goals = p.getGoals();
		if (goals > 0) {
			try {
				String condition = "";

				if (p.getSportalID() > 0) {
					condition = "SportalID = '" + p.getSportalID() + "'";
				} else {
					condition = createWhereQuery(p.getName(), p.getTeamName());
				}

				position = DatabaseRequests.getUniqueString("Position", "Spieler", condition);
				return goals * getPointsForPosition(position);
			} catch (IOException e) {
				String conditionWithoutTeam = WHERE_PLAYER_QUERY.replace("%PLAYERNAME%", p.getName());
				try {
					position = DatabaseRequests.getUniqueString("Position", "Spieler", conditionWithoutTeam);
					return goals * getPointsForPosition(position);
				} catch (IOException e1) {
					LOGGER.info(p.getName() + " does not longer play in the league. He will get no points for old goals.");
				}
			}
		}
		return 0;
	}

	/**
	 * This function is searching for the right factor to multiple goals.
	 * 
	 * @param position
	 *            Should be "Torwart", "Abwehr", "Mittelfeld", "Sturm"
	 * @return the right factor for the given position.
	 * @throws IOException
	 *             when the given position was not valid.
	 */
	private int getPointsForPosition(String position) throws IOException {
		switch (position) {
		case Position.KEEPER:
			return PointsParser.POINTS_GOAL_KEEPER;
		case Position.DEFENCE:
			return PointsParser.POINTS_GOAL_DEFENDER;
		case Position.MIDDLE:
			return PointsParser.POINTS_GOAL_MIDDFIELDER;
		case Position.OFFENCE:
			return PointsParser.POINTS_GOAL_OFFENSIVE;
		default:
			throw new IOException("Unknown position '" + position + "'!");
		}
	}

	/**
	 * Creates the WHERE part of a query to find a {@link Player} in the
	 * database.
	 * 
	 * @param playerName
	 *            the name of the {@link Player}, that should be found.
	 * @param teamName
	 *            the TeamName of the {@link Player}, that should be found.
	 * @return the correct Query as String
	 */
	private String createWhereQuery(String playerName, String teamName) {
		String filledQuery = WHERE_PLAYER_QUERY + " AND " + WHERE_TEAM_QUERY;
		filledQuery = filledQuery.replace("%PLAYERNAME%", playerName);
		filledQuery = filledQuery.replace("%TEAMNAME%", teamName);
		return filledQuery;
	}

	/**
	 * This function creates a SQLite Query, that will update the points of a
	 * {@link Player} in the database.
	 * 
	 * @param points
	 *            the points, that will be added to the points standing in the
	 *            database.
	 * @param playerName
	 *            the name of the player, that should be updated.
	 * @param teamName
	 *            the name of the team, in which the player plays.
	 * @return
	 */
	private String createUpdatePlayerPointsQuery(int points, String playerName, String teamName) {
		String filledQuery = UPDATE_POINT_QUERY + " WHERE " + createWhereQuery(playerName, teamName);
		filledQuery = filledQuery.replace("%POINTS%", String.valueOf(points));
		return filledQuery + ";";
	}

	private String createUpdatePlayerPointsQuery(int points, int sportalID) {
		String filledQuery = UPDATE_POINT_QUERY + "WHERE SportalID = '" + sportalID+"'";
		filledQuery = filledQuery.replace("%POINTS%", String.valueOf(points));
		return filledQuery;
	}

	public void addPointsToPlayingPlayers(Map<String, Player> playerList) {
		for(String s : playerList.keySet()){
			Player p = playerList.get(s);
			String sqlQuery = "UPDATE 'Mannschaft Copy' SET Punkte = "+p.getPoints()+" WHERE Spieler_ID = "+p.getSportalID();
			sqlCon.sendQuery(sqlQuery);
		}
	}
}
