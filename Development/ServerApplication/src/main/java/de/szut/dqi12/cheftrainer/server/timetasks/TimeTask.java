package de.szut.dqi12.cheftrainer.server.timetasks;

import java.awt.Toolkit;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;

public class TimeTask {
	Toolkit toolkit;

	Timer timer;

	public TimeTask(Date date) {
		toolkit = Toolkit.getDefaultToolkit();
		timer = new Timer();
		long nextTime = date.getTime() - (new Date()).getTime();
		timer.schedule(new ReceiverTask(), nextTime);
	}

	class ReceiverTask extends TimerTask {
		public void run() {
			System.out.println("do transactions");
			DatabaseRequests.doTransactions();
			Controller.getInstance().newTimerTask();
		}
	}
}
