package de.szut.dqi12.cheftrainer.server;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ServerProperties;
import de.szut.dqi12.cheftrainer.server.databasecommunication.SQLConnection;
import de.szut.dqi12.cheftrainer.server.timetasks.TimeTask;
import de.szut.dqi12.cheftrainer.server.usercommunication.SocketController;

public class Controller {

	private static Controller instance;
	private SocketController socketController;
	private SQLConnection sqlConnection;
	
	@SuppressWarnings("unused")
	private TimeTask timerTask;

	private final static Logger LOGGER = Logger.getLogger(Controller.class);

	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}

	public void startServerSocket(String packagePath) {
		ServerProperties serverProps = new ServerProperties();
		String pathAsString = "de/szut/dqi12/cheftrainer/server/callables/";
		ClientToServer_MessageIDs cts = new ClientToServer_MessageIDs();
		IDClass_Path_Mapper idMapper = new IDClass_Path_Mapper(cts, pathAsString, packagePath);
		serverProps.addClassPathMapper(idMapper);
		serverProps.setPort(5000);
		try {
			socketController = new SocketController(serverProps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
