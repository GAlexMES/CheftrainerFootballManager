package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;
import de.szut.dqi12.cheftrainer.server.parsing.PointsParser;
import de.szut.dqi12.cheftrainer.server.parsing.ScheduleParser;

/**
 * This class is used to write points of players to the databse.
 * 
 * @author Alexander Brennecke
 *
 */
public class SchedulePointManagement extends SQLManagement {

	private final long WAIT_AFTER_GAME = 15000000;

	private ScheduleParser scheduleParser;

	private DateFormat dateFormat;

	private final static Logger LOGGER = Logger.getLogger(SchedulePointManagement.class);

	/**
	 * Constructor
	 * 
	 * @param sqlCon
	 *            a active {@link SQLConnection}
	 */
	public SchedulePointManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
		scheduleParser = new ScheduleParser();
		dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
	}

	/**
	 * This function uses the {@link ScheduleParser} to find to current season
	 * of the bundesliga.
	 * 
	 * @return
	 */
	public int getCurrentSeasonFromSportal() {
		return scheduleParser.getCurrentSeason();
	}

	/**
	 * This function is called to load the points of all {@link Player} till
	 * now.
	 * 
	 * @param currentSeason
	 *            use 2015 for 2015-2016
	 */
	public void initializeScheduleForSeason(int currentSeason) {
		Map<Integer, List<Match>> matchDays = scheduleParser.getMatchesForSeason(currentSeason);
		Date date = new Date();
		for (Integer i : matchDays.keySet()) {
			List<Match> matchList = matchDays.get(i);
			if (wasMatchdayPlayed(matchList, date)) {
				LOGGER.info("Adding points for players for matchday " + i);
				Map<String, Map<String, Player>> playerPoints = new HashMap<String, Map<String, Player>>();
				for (Match m : matchList) {
					playerPoints.putAll(readPointsForMatch(m));
				}
				playerPoints.keySet().forEach(s -> DatabaseRequests.writePointsToDatabase(playerPoints.get(s)));
			}
			for (Match m : matchList) {
				try {
					DatabaseRequests.addMatch(m);
				} catch (SQLException | ParseException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * This function uses the {@link PointsParser} to read all information
	 * (points, goals, cards etc.) from a match.
	 * 
	 * @param m
	 *            a {@link Match} object, that has at least the DetailedURL, the
	 *            season and the matchday
	 * @return a Map, where the key is the name of a football team. The value is
	 *         a map, where the key is the name of a player. The value of this
	 *         inner Map is a full filled {@link Player} object.
	 */
	public Map<String, Map<String, Player>> readPointsForMatch(Match m) {
		Map<String, Map<String, Player>> retval = new HashMap<String, Map<String, Player>>();

		int matchID = ScheduleParser.getSportalID(m.getDetailURL());
		if (matchID > 0) {
			m.setSportalMatchID(matchID);
			retval = PointsParser.getPlayerPoints(m);
		}
		return retval;
	}

	/**
	 * This function checks, if a matchday was completely played.
	 * 
	 * @param matches
	 *            a List of {@link Match} objects.
	 * @param currentDate
	 *            the function will check, if all matches were played 4 hours
	 *            before the currentDate.
	 * @return true = all matches were played at least 4 hours before the
	 *         currentDate.
	 */
	private Boolean wasMatchdayPlayed(List<Match> matches, Date currentDate) {
		for (Match m : matches) {
			try {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateFormat.parse(m.getDate() + " " + m.getTime()));
				cal.setTimeInMillis(cal.getTimeInMillis() + WAIT_AFTER_GAME);
				Date matchDate = cal.getTime();
				if (matchDate.compareTo(currentDate) >= 0) {
					return false;
				}
			} catch (ParseException e) {
				return false;
			}

		}
		return true;
	}

	public void addMatch(Match m) throws SQLException, ParseException {
		String sqlQuery = "INSERT INTO Spieltag ('Saison', 'Spieltag', 'Heim_Verein_ID','Gast_Verein_ID','Ergebnis','URL','Datum') VALUES (?,?,?,?,?,?,?);";
		PreparedStatement preparedStatement = sqlCon.prepareStatement(sqlQuery);

		int homeTeamID = DatabaseRequests.getTeamIDForName(m.getHome());
		int guestTeamID = DatabaseRequests.getTeamIDForName(m.getGuest());
		String result = m.getGoalsHome() + ":" + m.getGoalsGuest();
		preparedStatement.setInt(1, m.getSeason());
		preparedStatement.setInt(2, m.getMatchDay());
		preparedStatement.setInt(3, homeTeamID);
		preparedStatement.setInt(4, guestTeamID);
		preparedStatement.setString(5, result);
		preparedStatement.setString(6, m.getDetailURL());

		Date d = dateFormat.parse(m.getDate() + " " + m.getTime());
		preparedStatement.setLong(7, d.getTime());

		preparedStatement.execute();
	}

	public int getCurrentMatchDay(Date d) {
		long time = d.getTime();
		String sqlQuery = "select min(Spieltag) from Spieltag where Datum > " + time + " and Ergebnis = '-1:-1'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);

		return getIntFromRS(rs, "min(Spieltag)");
	}
	
	public Date getLastMatchDate(int matchday) {
		String sqlQuery = "SELECT max(Datum) FROM Spieltag WHERE Spieltag= " + matchday + " AND Ergebnis = '-1:-1'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		long dateTime = 0;
		try {
			dateTime = rs.getLong("max(Datum)");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date(dateTime);
	}

	public Date getStartOfmatchday(int matchDay) {
		String sqlQuery = "SELECT min(Datum) FROM Spieltag WHERE Spieltag = " + matchDay;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		long dateTime = 0;
		try {
			dateTime = rs.getLong("min(Datum)");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date(dateTime);
	}

	public void updateSchedule(List<Match> matches, int season, int matchday) {
		String sqlQuery = "UPDATE Spieltag SET Datum = ?, Ergebnis = ?, URL= ? WHERE Spieltag = ? AND Saison=? AND Heim_Verein_ID = ? AND Gast_Verein_ID = ? ;";
		for (Match m : matches) {
			
			try {
				int homeTeamID = DatabaseRequests.getTeamIDForName(m.getHome());
				int guestTeamID = DatabaseRequests.getTeamIDForName(m.getGuest());
				
				PreparedStatement ps = sqlCon.prepareStatement(sqlQuery);
				Date d = dateFormat.parse(m.getDate() + " " + m.getTime());
				ps.setLong(1, d.getTime());
				ps.setString(2, m.getGoalsHome()+"-"+m.getGoalsGuest());
				ps.setString(3, m.getDetailURL());
				ps.setInt(4, matchday);
				ps.setInt(5, season);
				ps.setInt(6, homeTeamID);
				ps.setInt(7, guestTeamID);
				ps.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
}
