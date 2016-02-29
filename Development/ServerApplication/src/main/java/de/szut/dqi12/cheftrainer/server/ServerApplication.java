package de.szut.dqi12.cheftrainer.server;

import org.apache.log4j.Logger;

/**
 * Main class of the Server Application.
 *
 */
public class ServerApplication {
	private final static String PACKAGE_PATH = "de.szut.dqi12.cheftrainer.server.callables.test";
	private final static String DIR_PATH = "de/szut/dqi12/cheftrainer/server/callables/";
	
	private final static Logger LOGGER = Logger.getLogger(ServerApplication.class);

	/**
	 * Main function of the application. Initializes a few things.
	 */
	public static void main(String[] args) {
		LOGGER.info("Server will start now!");
		try {
			Controller controller = Controller.getInstance();
			controller.creatDatabaseCommunication(true);
			controller.startServerSocket(PACKAGE_PATH,DIR_PATH);
			controller.newTimerTask();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("A fatal error occured. Server will shut down!");
		}
	}
}
