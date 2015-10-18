package de.szut.dqi12.cheftrainer.server.logic;

import java.io.IOException;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.databasecommunication.ServerPropertiesManagement;

/**
 * This class is used, when the application starts. It checks, if everything is already initialized and initializes the database if needed.
 * @author Alexander Brennecke
 *
 */
public class ServerInitialator {
	
	private final static Logger LOGGER = Logger.getLogger(ServerInitialator.class);

	/**
	 * This function is used to initialize the database.
	 * @throws IOException
	 */
	public static void databaseInitalisation() throws IOException {
		readPlayer();
		readPointsForAvailableMatchDays();
	}
	
	/**
	 * This function is used to check, if the player were already parsed and stored in the database. If they are not, this function will do it.
	 * @throws IOException
	 */
	private static void readPlayer() throws IOException{
		LOGGER.info("Start validating Database!");
		Boolean finishedPlayerParsing = DatabaseRequests
				.getServerPropsAsBoolean(ServerPropertiesManagement.FINISHED_PLAYER_PARSING);
		if (!(finishedPlayerParsing)) {
			DatabaseRequests.clearTable("Spieler");
			try {
				DatabaseRequests.loadRealPlayers("Bundesliga", "Deutschland");
				LOGGER.info("Validating database: 100% done");
				DatabaseRequests.setServerProperty(
						ServerPropertiesManagement.FINISHED_PLAYER_PARSING, true);
			} catch (IOException io) {
				DatabaseRequests.setServerProperty(
						ServerPropertiesManagement.FINISHED_PLAYER_PARSING,
						false);
				LOGGER.error("Validating database failed: ");
				LOGGER.error(io);
				throw io;
			}
		}
	}
	
	/**
	 * This function will check, if the points for the current season are already parsed and added to the players in the database.
	 * If they are not, this function will start the parsing process of every matchday, that was played completely and will write the points to the database.
	 */
	private static void readPointsForAvailableMatchDays(){
		Boolean finishedPointParsing = DatabaseRequests.getServerPropsAsBoolean(ServerPropertiesManagement.FINISHED_POINT_PARSING);
		if(!finishedPointParsing){
			
			String sqlQuery = "UPDATE Spieler SET Punkte=0";
			DatabaseRequests.sendSimpleQuery(sqlQuery);
			
			int currentSeason = DatabaseRequests.getCurrentSeasonFromSportal();
			if (currentSeason > 2014) {
				LOGGER.info("Start collecting points for current season ("
						+ currentSeason + "-" + (currentSeason + 1));
				DatabaseRequests.initializeScheduleForSeason(currentSeason);
				DatabaseRequests.setServerProperty(ServerPropertiesManagement.FINISHED_POINT_PARSING,true);
			} else {
				DatabaseRequests.setServerProperty(ServerPropertiesManagement.FINISHED_POINT_PARSING,false);
				LOGGER.error("Failed collecting points, current season is invalid: "
						+ currentSeason);
			}
		}
	}
}
