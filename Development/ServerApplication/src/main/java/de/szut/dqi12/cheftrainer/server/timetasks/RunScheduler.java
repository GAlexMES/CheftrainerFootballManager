package de.szut.dqi12.cheftrainer.server.timetasks;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;

@Component
public class RunScheduler {

	@Autowired
	private JobLauncher jobLauncher;


	public void run() {
		try {	
			DatabaseRequests.doTransactions();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}