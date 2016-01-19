package de.szut.dqi12.cheftrainer.server.timetasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;

public class MatchdayStartsTimeTask {
	private final static Logger LOGGER = Logger.getLogger(MatchdayStartsTimeTask.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private Timer timer;

	public MatchdayStartsTimeTask(Date date) {
		timer = new Timer();

		long nextTime = date.getTime() - (new Date()).getTime();
		timer.schedule(new ReceiverTask(), nextTime);
	}

	class ReceiverTask extends TimerTask {
		public void run() {
			LOGGER.info("Save current teams to another table for matchday!");
			DatabaseRequests.copyManagerTeams();
			int matchDay = DatabaseRequests.getCurrentMatchDay(new Date());
			int currentSeason = DatabaseRequests.getCurrentSeasonFromSportal();
			
			Date finishDate = DatabaseRequests.getLastMatchDate(matchDay);
			Controller.getInstance().createMatchdayFinishedTimer(finishDate, matchDay, currentSeason);
			LOGGER.info("Created MatchdayFinishedTimeTask for: "+sdf.format(finishDate));
		}
	}
}
