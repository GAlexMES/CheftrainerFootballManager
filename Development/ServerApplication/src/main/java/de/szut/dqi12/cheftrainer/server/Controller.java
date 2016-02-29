package de.szut.dqi12.cheftrainer.server;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ServerProperties;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;
import de.szut.dqi12.cheftrainer.server.timetasks.MatchdayFinishedTimeTask;
import de.szut.dqi12.cheftrainer.server.timetasks.MatchdayStartsTimeTask;
import de.szut.dqi12.cheftrainer.server.timetasks.TransfermarktTimeTask;
import de.szut.dqi12.cheftrainer.server.usercommunication.SocketController;

/**
 * This class is the controller class, which is very important for the start of
 * the application. The class tries to create a connection to the database, it
 * creates the {@link SocketController}, and the {@link TransfermarktTimeTask}.
 * 
 * @author Alexander Brennecke
 *
 */
public class Controller {

	private static Controller instance;
	private SocketController socketController;
	private SQLConnection sqlConnection;

	@SuppressWarnings("unused")
	private TransfermarktTimeTask timerTask;
	@SuppressWarnings("unused")
	private MatchdayStartsTimeTask matchdayStartsTimeTask;
	@SuppressWarnings("unused")
	private MatchdayFinishedTimeTask matchdayFinishedTimeTask;

	private final static Logger LOGGER = Logger.getLogger(Controller.class);

	/**
	 * This function is used for singleton pattern.
	 * 
	 * @return each time the same instance of a {@link Controller} object.
	 */
	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}

	/**
	 * This function creates {@link ServerProperties} and with them a new
	 * {@link SocketController}. To create valid {@link ServerProperties}, it
	 * needs to map the IDs, defined in the {@link ClientToServer_MessageIDs},
	 * to classes, which extends {@link CallableAbstract}.
	 * 
	 * @param packagePath
	 *            the package path of the classes, which should be mapped to the
	 *            IDs
	 * @param callableDir
	 *            the director, in which the {@link CallableAbstract} classes
	 *            are saved.
	 */
	public void startServerSocket(String packagePath, String callableDir) {
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

	/**
	 * This function creates a new {@link SQLConnection} object and saves it in
	 * the {@link Controller} for further instructions.
	 * 
	 * @param sqlName
	 *            the name of the database
	 * @param sqlPath
	 *            the path to the director of the database
	 * @throws IOException
	 *             when there is a database, but it is empty, and the
	 *             initialization doesn't work.
	 */
	public void creatDatabaseCommunication(boolean init) throws IOException {
		try {
			sqlConnection = new SQLConnection(init);
		} catch (IOException io) {
			LOGGER.error("Creating access to database failed.");
			throw io;
		}
	}

	/**
	 * Creates a {@link MatchdayFinishedTimeTask} with the given parameter
	 * @param date should be a few hours after the end of the last game of the matchday
	 * @param matchDay the matchday (should be 1-34 for bundesliga)
	 * @param season the season (use 2015 for 2015-2016)
	 */
	public void createMatchdayFinishedTimer(Date date, int matchDay, int season) {
		matchdayFinishedTimeTask = new MatchdayFinishedTimeTask(date, matchDay, season);
	}

	/**
	 * Creates a {@link MatchdayStartsTimeTask} with the given parameter
	 * @param date should be the start date of the first {@link Match} at the matchday
	 * @param matchDay the matchday (should be 1-34 for bundesliga)
	 * @param season the season (use 2015 for 2015-2016)
	 */
	public void createMatchdayStartsTimer(Date date) {
		matchdayStartsTimeTask = new MatchdayStartsTimeTask(date);
	}

	/**
	 * This function is used, when the application starts. It fetches the next matchday from the database and creates the necessary time tasks for it.
	 */
	public void createMatchdayTimeTask() {
		int matchday = DatabaseRequests.getCurrentMatchDay(new Date());
		Date startTime = DatabaseRequests.getStartOfMatchday(matchday);

		Date currentTime = new Date();
		if (startTime.after(currentTime)) {
			createMatchdayStartsTimer(startTime);
		}
		else{
			Date endTime = DatabaseRequests.getLastMatchDate(matchday);
			
			if(endTime.after(currentTime)){
				int currentSeason = DatabaseRequests.getCurrentSeasonFromSportal();
				createMatchdayFinishedTimer(endTime,matchday,currentSeason);
			}
			
		}
	}

	/**
	 * Creates a new {@link TransfermarktTimeTask} object, with tomorrow 1:00 am
	 * as trigger time.
	 */
	public void newTimerTask() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		cal.add(Calendar.DAY_OF_MONTH, 1);

		Date newTimer = cal.getTime();
		timerTask = new TransfermarktTimeTask(newTimer);
	}
	
	//GETTER AND SETTER
	public SQLConnection getSQLConnection() {
		return sqlConnection;
	}
	
	public SocketController getSocketController() {
		return socketController;
	}
}
