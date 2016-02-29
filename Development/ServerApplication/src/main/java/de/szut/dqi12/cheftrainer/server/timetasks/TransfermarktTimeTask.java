package de.szut.dqi12.cheftrainer.server.timetasks;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;

/**
 * This time tasks checks the {@link Market} for offers and transfers {@link Player}s, when there are offers.
 * @author Alexander Brennecke
 *
 */
public class TransfermarktTimeTask {

	Timer timer;

	/**
	 * Constructor
	 * @param date time, when the task will be executed.
	 */
	public TransfermarktTimeTask(Date date) {
		timer = new Timer();
		long nextTime = date.getTime() - (new Date()).getTime();
		timer.schedule(new ReceiverTask(), nextTime);
	}

	/**
	 * The time task itself will create a {@link DatabaseRequests}, which will do the necessary {@link Transaction}s, according to the offers of any playing {@link Manager}
	 * @author Alexander Brennecke
	 *
	 */
	class ReceiverTask extends TimerTask {
		public void run() {
			System.out.println("do transactions");
			DatabaseRequests.doTransactions();
			Controller.getInstance().newTimerTask();
		}
	}
}
