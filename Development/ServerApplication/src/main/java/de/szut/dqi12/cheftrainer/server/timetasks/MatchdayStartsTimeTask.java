package de.szut.dqi12.cheftrainer.server.timetasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;

/**
 * This class is used, when a matchday starts. It will call the {@link ReceiverTask}.
 * @author Alexander Brennecke
 *
 */
public class MatchdayStartsTimeTask {
	private final static Logger LOGGER = Logger.getLogger(MatchdayStartsTimeTask.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private Timer timer;

	/**
	 * Constructor
	 * @param date the constructor needs a date. The {@link ReceiverTask} will be executed on tha date.
	 */
	public MatchdayStartsTimeTask(Date date) {
		LOGGER.info("Created MatchdayStartsTimeTask for: " + sdf.format(date));
		timer = new Timer();

		long nextTime = date.getTime() - (new Date()).getTime();
		timer.schedule(new ReceiverTask(), nextTime);
	}

	
	/**
	 * This class will create a {@link MatchdayFinishedTimeTask}.
	 * It also copies all {@link Player}s, which are selected by their {@link Manager}s, to a new database table.
	 * @author Alexander Brennecke
	 *
	 */
	class ReceiverTask extends TimerTask {
		public void run() {
			LOGGER.info("Save current teams to another table for matchday!");
			DatabaseRequests.copyManagerTeams();
			int matchDay = DatabaseRequests.getCurrentMatchDay(new Date());
			int currentSeason = DatabaseRequests.getCurrentSeasonFromSportal();

			Date finishDate = DatabaseRequests.getLastMatchDate(matchDay);
			Calendar cal = Calendar.getInstance();
			cal.setTime(finishDate);
			cal.add(Calendar.HOUR_OF_DAY, 10);

			Date newTimerDate = cal.getTime();
			Controller.getInstance().createMatchdayFinishedTimer(newTimerDate, matchDay, currentSeason);
		}
	}
}
