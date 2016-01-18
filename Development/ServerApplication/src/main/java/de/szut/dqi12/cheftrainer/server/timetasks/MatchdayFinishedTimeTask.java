package de.szut.dqi12.cheftrainer.server.timetasks;

import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;

public class MatchdayFinishedTimeTask {
	private final static Logger LOGGER = Logger.getLogger(MatchdayFinishedTimeTask.class);
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	Toolkit toolkit;

	Timer timer;
	
	private int matchday;

	public MatchdayFinishedTimeTask(Date date, int matchday) {
		this.matchday=matchday;
		toolkit = Toolkit.getDefaultToolkit();
		timer = new Timer();
		long nextTime = date.getTime() - (new Date()).getTime();
		timer.schedule(new ReceiverTask(), nextTime);
	}

	class ReceiverTask extends TimerTask {
		public void run() {
			LOGGER.info("Timetask to collect points for matchday "+matchday+" started.");
			long startOfNextMatchDay = DatabaseRequests.getStartOfMatchday(matchday + 1);
			Date newStartTimer = new Date(startOfNextMatchDay);
			Controller.getInstance().createMatchdayStartsTimer(newStartTimer);
			LOGGER.info("Created MatchdayStartTimeTask for: "+sdf.format(newStartTimer));
		}
	}
}
