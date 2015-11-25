package de.szut.dqi12.cheftrainer.server;

import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class ServerApplication {
	private final static String PACKAGE_PATH = "de.szut.dqi12.cheftrainer.server.callables.test";
	private final static String DB_NAME = "Database";
	private final static String DB_PATH = "/Database";
	
	private final static Logger LOGGER = Logger.getLogger(ServerApplication.class);

	public static void main(String[] args) {
		LOGGER.info("Server will start now!");
		try {
			Controller controller = Controller.getInstance();
			controller.creatDatabaseCommunication(DB_NAME, DB_PATH);
			controller.startServerSocket(PACKAGE_PATH);
			controller.newTimerTask();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("A fatal error occured. Server will shut down!");
		}
	}
}
