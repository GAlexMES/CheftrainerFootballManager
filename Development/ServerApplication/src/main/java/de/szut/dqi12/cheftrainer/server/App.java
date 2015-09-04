package de.szut.dqi12.cheftrainer.server;


/**
 * Hello world!
 *
 */
public class App {
	
	private final static String PACKAGE_PATH = "de.szut.dqi12.cheftrainer.server.callables.test";
	private final static String DB_NAME = "Database";
	private final static String DB_PATH = App.class.getResource("../../../../../Database").toString();
    public static void main( String[] args )
    {
    	Controller controller = Controller.getInstance();
    	controller.creatDatabaseCommunication(DB_NAME, DB_PATH);
    	controller.startServerSocket(PACKAGE_PATH);
    }
}
