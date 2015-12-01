package de.szut.dqi12.cheftrainer.server.databasecommunication;

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
import de.szut.dqi12.cheftrainer.server.parsing.PointsParser;
import de.szut.dqi12.cheftrainer.server.parsing.ScheduleParser;


/** 
 * This class is used to write points of players to the databse.
 * @author Alexander Brennecke
 *
 */
public class SchedulePointManagement {
	
	private final long WAIT_AFTER_GAME = 15000000;

	@SuppressWarnings("unused")
	private SQLConnection sqlCon;
	private ScheduleParser scheduleParser;

	private DateFormat dateFormat;
	
	private final static Logger LOGGER = Logger.getLogger(SchedulePointManagement.class);

	/**
	 * Constructor
	 * @param sqlCon a active {@link SQLConnection}
	 */
	public SchedulePointManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
		scheduleParser = new ScheduleParser();
		dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
	}

	/**
	 * This function uses the {@link ScheduleParser} to find to current season of the bundesliga.
	 * @return
	 */
	public int getCurrentSeasonFromSportal() {
		return scheduleParser.getCurrentSeason();
	}

	
	/**
	 * This function is called to load the points of all {@link Player} till now. 
	 * @param currentSeason use 2015 for 2015-2016
	 */
	public void initializeScheduleForSeason(int currentSeason) {
		Map<Integer,List<Match>> matchDays = scheduleParser.getMatchesForSeason(currentSeason);
		Date date = new Date();
		for(Integer i : matchDays.keySet()){
			if(wasMatchdayPlayed(matchDays.get(i),date)){
				LOGGER.info("Adding points for players for matchday "+i);
				Map<String, Map<String, Player>> playerPoints = new HashMap<String, Map<String,Player>>();
				for(Match m: matchDays.get(i)){
					playerPoints.putAll(readPointsForMatch(m));
				}
				playerPoints.keySet().forEach( s -> DatabaseRequests.writePointsToDatabase(playerPoints.get(s)));
			}
			else{
				break;
			}
		}
	}
	
	/**
	 * This function uses the {@link PointsParser} to read all information (points, goals, cards etc.) from a match.
	 * @param m a {@link Match} object, that has at least the DetailedURL, the season and the matchday
	 * @return a Map, where the key is the name of a football team. The value is a map, where the key is the name of a player.
	 * The value of this inner Map is a full filled {@link Player} object.
	 */
	private Map<String, Map<String, Player>> readPointsForMatch(Match m){
		Map<String, Map<String, Player>> retval = new HashMap<String, Map<String,Player>>();
		
		int matchID = ScheduleParser.getSportalID(m.getDetailURL());
		if(matchID>0){
			m.setSportalMatchID(matchID);
			retval = PointsParser.getPlayerPoints(m);
		}
		return retval;
	}
	
	
	/**
	 * This function checks, if a matchday was completely played.
	 * @param matches a List of {@link Match} objects.
	 * @param currentDate the function will check, if all matches were played 4 hours before the currentDate.
	 * @return true = all matches were played at least 4 hours before the currentDate.
	 */
	private Boolean wasMatchdayPlayed(List<Match> matches, Date currentDate) {
		for (Match m : matches) {
			try {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateFormat.parse(m.getDate()+" "+m.getTime()));
				cal.setTimeInMillis(cal.getTimeInMillis()+WAIT_AFTER_GAME);
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
	
}
