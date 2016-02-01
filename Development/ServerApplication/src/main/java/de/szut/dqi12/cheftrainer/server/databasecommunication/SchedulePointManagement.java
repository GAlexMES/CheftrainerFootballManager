package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

	private List<Integer> currentMatchIDs = new ArrayList<>();

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
			currentMatchIDs = new ArrayList<>();
			List<Match> matchList = matchDays.get(i);
			if (wasMatchdayPlayed(matchList, date)) {
				LOGGER.info("Adding points for players for matchday " + i);
				List<HashMap<String, HashMap<String, Player>>> parsedPoints = readPointsForMatches(matchList);
				for (int match = 0; match < parsedPoints.size(); match++) {
					HashMap<String, HashMap<String, Player>> playerPoints = parsedPoints.get(match);
					playerPoints.keySet().forEach(s -> DatabaseRequests.writePointsToDatabase(playerPoints.get(s)));
				}
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
	 * This function collects the points for the given {@link List} of {@link Match}es.
	 * @param matches a {@link List} of {@link Match} objects.
	 * @return A {@link List} of {@link HashMap}s, where the Key is the name of a team and the value is a HashMap, 
	 * 			where the Key is a {@link Player} name and the value is a {@link Player} object.
	 */
	public List<HashMap<String, HashMap<String, Player>>> readPointsForMatches(List<Match> matches) {
		List<HashMap<String, HashMap<String, Player>>> retval = new ArrayList<HashMap<String, HashMap<String, Player>>>();
		int matchday = matches.get(0).getMatchDay();
		int season = matches.get(0).getSeason();

		for (Match m : matches) {
			retval.add(readPointsForMatch(m));
		}

		while (currentMatchIDs.size() < 9) {
			int minID = Collections.min(currentMatchIDs);
			int maxID = Collections.max(currentMatchIDs);
			if (maxID - minID == 8) {
				int newID = -1;
				for (int i = minID; i < maxID; i++) {
					if (!currentMatchIDs.contains(i)) {
						newID = i;
						break;
					}
				}

				if (newID > 0) {
					HashMap<String, HashMap<String, Player>> points = PointsParser.getPlayerPoints(season, matchday, newID);
					Iterator<String> iterator = points.keySet().iterator();
					LOGGER.info("Found ID for the match: "+iterator.next()+"-"+iterator.next());
					retval.add(points);
					currentMatchIDs.add(newID);
				} else {
					break;
				}
			} else {
				break;

			}
		}

		return retval;

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
	private HashMap<String, HashMap<String, Player>> readPointsForMatch(Match m) {
		HashMap<String, HashMap<String, Player>> retval = new HashMap<String, HashMap<String, Player>>();

		int matchID = -1;
		try {
			matchID = ScheduleParser.getSportalID(m.getDetailURL());
		} catch (Exception e) {

		}

		if (matchID > 0) {
			currentMatchIDs.add(matchID);
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

	/**
	 * This function returns the next matchday. It searches in the database for the nearest date, where no result was set for a match.
	 * @param d the current date as long in ms
	 * @return a int, which represents a matchday (e.g.: 14)
	 */
	public int getCurrentMatchDay(Date d) {
		long time = d.getTime();
		String sqlQuery = "select min(Spieltag) from Spieltag where Datum > " + time + " and Ergebnis = '-1:-1'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);

		return getIntFromRS(rs, "min(Spieltag)");
	}

	/**
	 * This function searches in the database for the last match of a matchday.
	 * @param matchday for example 14 for matchday 14
	 * @return
	 */
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

	/**
	 * This function returns the start of a matchday, which is the start of the first match of the matchday
	 * @param matchDay for example 14, for matchday 14
	 * @return the start date of the earliest match for the given matchday.
	 */
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

	/**
	 * This function updates the database. It updates the dates, results etc. of each {@link Match} given to this function.
	 * @param matches a {@link List} of {@link Match} objects
	 * @param season the season (use 2015 for 2015-2016)
	 * @param matchday the matchday of the matches.
	 */
	public void updateSchedule(List<Match> matches, int season, int matchday) {
		String sqlQuery = "UPDATE Spieltag SET Datum = ?, Ergebnis = ?, URL= ? WHERE Spieltag = ? AND Saison=? AND Heim_Verein_ID = ? AND Gast_Verein_ID = ? ;";
		for (Match m : matches) {

			try {
				int homeTeamID = DatabaseRequests.getTeamIDForName(m.getHome());
				int guestTeamID = DatabaseRequests.getTeamIDForName(m.getGuest());

				PreparedStatement ps = sqlCon.prepareStatement(sqlQuery);
				Date d = dateFormat.parse(m.getDate() + " " + m.getTime());
				ps.setLong(1, d.getTime());
				ps.setString(2, m.getGoalsHome() + "-" + m.getGoalsGuest());
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
