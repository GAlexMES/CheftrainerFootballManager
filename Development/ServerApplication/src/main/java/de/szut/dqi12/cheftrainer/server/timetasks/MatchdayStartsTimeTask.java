package de.szut.dqi12.cheftrainer.server.timetasks;

import java.awt.Toolkit;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MatchdayStartsTimeTask {
	private Toolkit toolkit;

	private Timer timer;
	
	private MatchdayFinishedTimeTask mdftt;

	public MatchdayStartsTimeTask(Date date) {
		toolkit = Toolkit.getDefaultToolkit();
		timer = new Timer();
		long nextTime = date.getTime() - (new Date()).getTime();
		timer.schedule(new ReceiverTask(), nextTime);
	}

	class ReceiverTask extends TimerTask {
		public void run() {
			System.out.println("I must do the preperations for a matchday");
			Date d = null;
			mdftt = new MatchdayFinishedTimeTask(d);
		}
	}
}

