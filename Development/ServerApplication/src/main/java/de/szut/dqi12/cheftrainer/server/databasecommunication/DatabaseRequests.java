package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;

/**
 * This class provides some Database utils.
 * There is no Javadoc, because the functions just forwarde to functions in the according *Management (e.g UserManagement) class.
 * @author Alexander Brennecke
 *
 */
public class DatabaseRequests {
	
	private static DatabaseRequests INSTANCE = null;
	
	
	//DATABASE MANAGERS
	private static UserManagement userManagement;
	private static CommunityManagement communityManagement;
	private static InitializationManagement initializationManagement;
	private static LogicManagement logicManagement;
	private static SchedulePointManagement schedulePointManagement;
	
	public static DatabaseRequests getInstance(){
		if(INSTANCE==null){
			INSTANCE = new DatabaseRequests();
		}
		return INSTANCE;
	}
	
	public void setSQLConnection(SQLConnection sqlCon){
		userManagement = new UserManagement(sqlCon);
		communityManagement = new CommunityManagement(sqlCon);
		initializationManagement = new InitializationManagement(sqlCon);;
		logicManagement = new LogicManagement(sqlCon);
		schedulePointManagement = new SchedulePointManagement(sqlCon);
	}

	
	public static HashMap<String, Boolean> registerNewUser(User newUser){
		return userManagement.register(newUser);
	}
	
	public static HashMap<String, Boolean> loginUser(User user){
		return userManagement.login(user);
	}
	
	public static User getUserData(String userName){
		return userManagement.getUserValues(userName);
	}
	
	public static boolean createNewCommunity(String name, String password, int adminID){
		return communityManagement.createNewCommunity(name,password, adminID);
	}
	
	public static List<Community> getCummunitiesForUser(int userID){
		return communityManagement.getCummunities(userID);
	}
	
	public static List<Manager> getManagers(int communityID){
		return communityManagement.getManagers(communityID);
	}

	public static HashMap<String, Boolean> enterCommunity(String communityName,
			String communityPassword, int userID) {
		return communityManagement.enterCommunity(communityName,communityPassword,userID);
	}
	
	public static boolean existRealPlayer(){
		return initializationManagement.existPlayer();
	}

	public static void loadRealPlayers(String leagueName, String leagueCountry, String leagueSource) throws IOException{
		initializationManagement.loadRealPlayers(leagueName, leagueCountry, leagueSource);
	}
	
	public static int getHeighstPlayerID(){
		return logicManagement.getHeighstPlayerID();
	}
	
	public static Player getPlayer(int playerID){
		return logicManagement.getPlayer(playerID);
	}

	public static boolean isPlayerOwened(int playerID, int communityID) {
		return logicManagement.isPlayerOwened(playerID, communityID);
	}

	public static void addPlayerToManager(int managerID, int playerID) {
		logicManagement.addPlayerToManager(managerID, playerID);
		
	}

	public static int getCurrentSeasonFromSportal() {
		return schedulePointManagement.getCurrentSeasonFromSportal();
	}

	public static void initializeScheduleForSeason(int currentSeason) {
		schedulePointManagement.initializeScheduleForSeason(currentSeason);
	}

}
