package de.szut.dqi12.cheftrainer.server.timetasks;

import java.awt.Toolkit;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MatchdayFinishedTimeTask {
	Toolkit toolkit;

	Timer timer;

	public MatchdayFinishedTimeTask(Date date) {
		toolkit = Toolkit.getDefaultToolkit();
		timer = new Timer();
		long nextTime = date.getTime() - (new Date()).getTime();
		timer.schedule(new ReceiverTask(), nextTime);
	}

	class ReceiverTask extends TimerTask {
		public void run() {
			System.out.println("I must do the point calculation after a match day");
		}
	}
}

