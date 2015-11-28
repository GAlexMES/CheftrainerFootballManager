package de.szut.dqi12.cheftrainer.server;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ServerProperties;
import de.szut.dqi12.cheftrainer.server.databasecommunication.SQLConnection;
import de.szut.dqi12.cheftrainer.server.timetasks.TimeTask;
import de.szut.dqi12.cheftrainer.server.usercommunication.SocketController;

/**
 * This class is the controller class, which is very important for the start of the application.
 * The class tries to create a connection to the database, it creates the {@link SocketController}, and the {@link TimeTask}.
 * @author Alexander Brennecke
 *
 */
public class Controller {

	private static Controller instance;
	private SocketController socketController;
	private SQLConnection sqlConnection;
	
	@SuppressWarnings("unused")
	private TimeTask timerTask;

	private final static Logger LOGGER = Logger.getLogger(Controller.class);

	/**
	 * This function is used for singleton pattern.
	 * @return each time the same instance of a {@link Controller} object.
	 */
	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}

	/**
	 * This function creates {@link ServerProperties} and with them a new {@link SocketController}.
	 * To create valid {@link ServerProperties}, it needs to map the IDs, defined in the {@link ClientToServer_MessageIDs},
	 * to classes, which extends {@link CallableAbstract}.
	 * @param packagePath the package path of the classes, which should be mapped to the IDs
	 * @param callableDir the director, in which the {@link CallableAbstract} classes are saved.
	 */
	public void startServerSocket(String packagePath, String callableDir) {
		ServerProperties serverProps = new ServerProperties();
		ClientToServer_MessageIDs cts = new ClientToServer_MessageIDs();
		IDClass_Path_Mapper idMapper = new IDClass_Path_Mapper(cts, callableDir, packagePath);
		serverProps.addClassPathMapper(idMapper);
		serverProps.setPort(5000);
		try {
			socketController = new SocketController(serverProps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function creates a new {@link SQLConnection} object and saves it in the {@link Controller} for further instructions.
	 * @param sqlName the name of the database
	 * @param sqlPath	the path to the director of the database
	 * @throws IOException when there is a database, but it is empty, and the initialization doesn't work.
	 */
	public void creatDatabaseCommunication(String sqlName, String sqlPath) throws IOException {
		try {
			sqlConnection = new SQLConnection(sqlName, sqlPath, true);
		} catch (IOException io) {
			LOGGER.error("Creating access to database failed.");
			throw io;
		}
	}

	public SQLConnection getSQLConnection() {
		return sqlConnection;
	}

	public SocketController getSocketController() {
		return socketController;
	}

	/**
	 * Creates a new {@link TimeTask} object, with tomorrow 1:00 am as trigger time.
	 */
	public void newTimerTask() {
		Calendar cal =  Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		cal.add(Calendar.DAY_OF_MONTH, 1);
		
		Date newTimer = cal.getTime();
		timerTask = new TimeTask(newTimer);
	}
}
