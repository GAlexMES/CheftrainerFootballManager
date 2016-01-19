package de.szut.dqi12.cheftrainer.server.timetasks;

import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.databasecommunication.SchedulePointManagement;
import de.szut.dqi12.cheftrainer.server.parsing.ScheduleParser;

public class MatchdayFinishedTimeTask {
	private final static Logger LOGGER = Logger.getLogger(MatchdayFinishedTimeTask.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	Toolkit toolkit;

	Timer timer;

	private int season;
	private int matchday;

	public MatchdayFinishedTimeTask(Date date, int matchday, int season) {
		this.matchday = matchday;
		toolkit = Toolkit.getDefaultToolkit();
		timer = new Timer();
		long nextTime = date.getTime() - (new Date()).getTime();
		timer.schedule(new ReceiverTask(), nextTime);
	}

	class ReceiverTask extends TimerTask {
		public void run() {
			LOGGER.info("Timetask to collect points for matchday " + matchday + " started.");
			readPoints();
			Date newStartTimer = DatabaseRequests.getStartOfMatchday(matchday + 1);
			Controller.getInstance().createMatchdayStartsTimer(newStartTimer);
			LOGGER.info("Created MatchdayStartTimeTask for: " + sdf.format(newStartTimer));
		}

		private void readPoints() {
			List<Match> matches = updateSchedule();
			SchedulePointManagement spm = DatabaseRequests.getSchedulePointManagement();

			for (Match m : matches) {
				Map<String, Map<String, Player>> newPoints = spm.readPointsForMatch(m);
				for (String s : newPoints.keySet()) {
					DatabaseRequests.writePointsToDatabase(newPoints.get(s));
					DatabaseRequests.addPointsToPlayingPlayers(newPoints.get(s));
				}

			}

		}

		private List<Match> updateSchedule() {
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
