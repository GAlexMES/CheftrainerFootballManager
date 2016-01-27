package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
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
	 *            a Map, where the key is a String and the value is a
	 *            {@link Player} object. The Keys are not relevant.
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

	/**
	 * This function updates the points of a player in the database.
	 * 
	 * @param points
	 *            the points, that should be added (positive int) or subtracted
	 *            (negative int)
	 * @param sportalID
	 *            the sportal ID of the {@link Player}
	 * @return the sql query as String
	 */
	private String createUpdatePlayerPointsQuery(int points, int sportalID) {
		String filledQuery = UPDATE_POINT_QUERY + "WHERE SportalID = '" + sportalID + "'";
		filledQuery = filledQuery.replace("%POINTS%", String.valueOf(points));
		return filledQuery;
	}

	/**
	 * This function saves the given points in the "Mannschaft Copy" table.
	 * 
	 * @param playerList
	 *            a {@link List} where a String is the key (name of the
	 *            {@link Player}) and the {@link Player} object is the value.
	 */
	public void addPointsToPlayingPlayers(Map<String, Player> playerList) {
		for (String s : playerList.keySet()) {
			Player p = playerList.get(s);
			String sqlQuery = "UPDATE 'Mannschaft Copy' SET Punkte = " + p.getPoints() + " WHERE Spieler_ID = " + p.getSportalID();
			sqlCon.sendQuery(sqlQuery);
		}
	}

	/**
	 * This function reads the "Mannschaft Copy" table, after the points were
	 * added. It then adds the points to the managers via addManagerPoints()
	 * @param matchday the current matchday
	 */
	public void addTempPointsToManager(int matchday) {
		List<Integer> managerIDs = DatabaseRequests.getAllManagerIDs();
		for (int id : managerIDs) {
			String collectQuery = "SELECT Punkte FROM 'Mannschaft Copy' WHERE Manager_ID =  " + id;
			ResultSet rs = sqlCon.sendQuery(collectQuery);
			try {
				List<Integer> points = DatabaseUtils.getListFromResultSet(rs, "Punkte");
				int managerPoints = 0;
				for (int i = 0; i < 11; i++) {
					if (points.size() > i) {
						managerPoints += points.get(i);
					} else {
						managerPoints -= 4;
					}
				}
				addManagerPoints(matchday,id,managerPoints);
			} catch (SQLException sqe) {
				sqe.printStackTrace();
			}
		}
	}

	/**
	 * This function creates two queries by the given data. One for the Manager table and one for the Manager_Statistik table.
	 * @param matchday the current matchday (used for the statistics)
	 * @param managerID the ID of a {@link Manager}
	 * @param points the points, which were earned by the {@link Manager} at that matchday.
	 * @throws SQLException
	 */
	public void addManagerPoints(int matchday, int managerID, int points) throws SQLException {
		// Add to statistic table
		String addPointsToStatQuery = "INSERT INTO Manager_Statistik (Spieltag,Manager_ID,Punkte) VALUES (?,?,?)";
		PreparedStatement pStatement = sqlCon.prepareStatement(addPointsToStatQuery);
		pStatement.setInt(1, matchday);
		pStatement.setInt(2, managerID);
		pStatement.setInt(3, points);
		pStatement.execute();

		// Add to manager table
		String addToManagerTableQuery = "UPDATE Manager Set Punkte = Punkte + ? where ID =?";
		pStatement = sqlCon.prepareStatement(addToManagerTableQuery);
		pStatement.setInt(1, points);
		pStatement.setInt(2, managerID);
		pStatement.execute();
	}
}
