package de.szut.dqi12.cheftrainer.server.timetasks;

import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.databasecommunication.SchedulePointManagement;
import de.szut.dqi12.cheftrainer.server.parsing.ScheduleParser;

/**
 * This class is a {@link TimerTask}. It should be called after a matchday was played, and the points are available.
 * It also creates a new {@link MatchdayStartsTimeTask} for the next matchday.
 * @author Alexander Brennecke
 *
 */
public class MatchdayFinishedTimeTask {
	private final static Logger LOGGER = Logger.getLogger(MatchdayFinishedTimeTask.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	Toolkit toolkit;

	Timer timer;

	private int season;
	private int matchday;

	/**
	 * Constructor
	 * @param date the date, when the {@link ReceiverTask} should be executed.
	 * @param matchday the current matchday (1-34 for bundesliga)
	 * @param season the current season (2015 for 2015-2016)
	 */
	public MatchdayFinishedTimeTask(Date date, int matchday, int season) {
		LOGGER.info("Created MatchdayFinishedTimeTask for: " + sdf.format(date));
		this.matchday = matchday;
		toolkit = Toolkit.getDefaultToolkit();
		timer = new Timer();
		long nextTime = date.getTime() - (new Date()).getTime();
		timer.schedule(new ReceiverTask(), nextTime);
	}

	/**
	 * The {@link ReceiverTask} will be called by the {@link MatchdayStartsTimeTask}. Itself calls Parsing and {@link DatabaseRequests} functions to get the newest points.
	 */
	class ReceiverTask extends TimerTask {
		public void run() {
			LOGGER.info("Timetask to collect points for matchday " + matchday + " started.");
			
			readPoints();
			
			Date newStartTimer = DatabaseRequests.getStartOfMatchday(matchday + 1);
			Controller.getInstance().createMatchdayStartsTimer(newStartTimer);
			LOGGER.info("Created MatchdayStartTimeTask for: " + sdf.format(newStartTimer));
		}

		/**
		 * This functions uses a List of {@link Match} to call the {@link SchedulePointManagement}, to get the newest points for each player of each match in the list. 
		 */
		private void readPoints() {
			List<Match> matches = updateSchedule();
			SchedulePointManagement spm = DatabaseRequests.getSchedulePointManagement();

			List<HashMap<String, HashMap<String, Player>>> parsedPoints = spm.readPointsForMatches(matches);
			for(int match = 0; match<parsedPoints.size();match++){
				HashMap<String, HashMap<String, Player>> playerPoints = parsedPoints.get(match);
				for (String s : playerPoints.keySet()) {
					DatabaseRequests.writePointsToDatabase(playerPoints.get(s));
					DatabaseRequests.addPointsToPlayingPlayers(playerPoints.get(s));
				}
			}
			DatabaseRequests.addTempPointsToManager(matchday);
			DatabaseRequests.updatedPlacement();
		}

		/**
		 * This function is called, to parse the current matchday and the next one again. This is used to updated dates.
		 * @return returns a List of {@link Match}es for the current matchday.
		 */
		private List<Match> updateSchedule() {
			LOGGER.info("Updateing the shedule.");
			ScheduleParser sp = new ScheduleParser();
			List<Match> currentMatches = new ArrayList<>();
			List<Match> nextMatches = new ArrayList<>();
			try {
				nextMatches = sp.createSchedule(matchday + 1, season);
				currentMatches = sp.createSchedule(matchday, season);
				DatabaseRequests.updateSchedule(nextMatches, matchday + 1, season);
				DatabaseRequests.updateSchedule(currentMatches, matchday, season);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			return currentMatches;
		}
	}
}
