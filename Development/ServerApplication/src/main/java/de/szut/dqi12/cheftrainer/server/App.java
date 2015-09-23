package de.szut.dqi12.cheftrainer.server;

import org.apache.log4j.Logger;


/**
 * Hello world!
 *
 */
public class App {
	private final static String PACKAGE_PATH = "de.szut.dqi12.cheftrainer.server.callables.test";
	private final static String DB_NAME = "Database";
	private final static String DB_PATH = App.class.getResource("../../../../../Database").toString();
	
	private final static Logger LOGGER = Logger.getLogger(App.class);
	
	
    public static void main( String[] args )
    {
    	LOGGER.info("Server will start now!");
    	Controller controller = Controller.getInstance();
    	controller.creatDatabaseCommunication(DB_NAME, DB_PATH);
    	controller.startServerSocket(PACKAGE_PATH);
    }
}
