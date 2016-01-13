package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.server.databasecommunication.ServerPropertiesManagement;

/**
 * This class provides some Database utils. There is no Javadoc, because the
 * functions just forwarde to functions in the according *Management (e.g
 * UserManagement) class.
 * 
 * @author Alexander Brennecke
 *
 */
public class DatabaseRequests {

	private static DatabaseRequests INSTANCE = null;

	// DATABASE MANAGERS
	private static UserManagement userManagement;
	private static CommunityManagement communityManagement;
	private static PlayerManagement playerManagement;
	private static LogicManagement logicManagement;
	private static SchedulePointManagement schedulePointManagement;
	private static ServerPropertiesManagement serverPropertiesManagement;
	private static DatabaseUtils databaseUtils;
	private static PointManagement pointManagement;
	private static TransfermarketManagement transfermarktManagement;
	
	public static DatabaseRequests getInstance(){
		if(INSTANCE==null){
			INSTANCE = new DatabaseRequests();
		}
		return INSTANCE;
	}

	public void setSQLConnection(SQLConnection sqlCon) {
		userManagement = new UserManagement(sqlCon);
		communityManagement = new CommunityManagement(sqlCon);
		playerManagement = new PlayerManagement(sqlCon);
		logicManagement = new LogicManagement(sqlCon);
		schedulePointManagement = new SchedulePointManagement(sqlCon);
		serverPropertiesManagement = new ServerPropertiesManagement(sqlCon);
		databaseUtils = new DatabaseUtils(sqlCon);
		pointManagement = new PointManagement(sqlCon);
		transfermarktManagement = new TransfermarketManagement(sqlCon);
	}

	public static HashMap<String, Boolean> registerNewUser(User newUser) {
		return userManagement.register(newUser);
	}

	public static HashMap<String, Boolean> loginUser(User user) {
		return userManagement.login(user);
	}

	public static User getUserData(String userName) {
		return userManagement.getUserValues(userName);
	}

	public static boolean createNewCommunity(String name, String password, int adminID) {
		return communityManagement.createNewCommunity(name, password, adminID);
	}

	public static List<Community> getCummunitiesForUser(int userID) {
		return communityManagement.getCummunities(userID);
	}

	public static List<Integer> getCummunityIDsForUser(int userID) {
		return communityManagement.getCommunityIDsForUser(userID);
	}

	public static Community getCummunityForID(int communityID) {
		return communityManagement.getCommunity(communityID);
	}

	public static List<Manager> getManagers(int communityID, String communityName) {
		return communityManagement.getManagers(communityID, communityName);
	}

	public static HashMap<String, Boolean> enterCommunity(String communityName, String communityPassword, int userID) {
		return communityManagement.enterCommunity(communityName, communityPassword, userID);
	}

	public static void loadRealPlayers(String leagueName, String leagueCountry) throws IOException {
		playerManagement.loadRealPlayers(leagueName, leagueCountry);
	}

	public static int getHeighstPlayerID() {
		return logicManagement.getHeightsPlayerID();
	}

	public static Player getPlayer(int playerID) {
		return playerManagement.getPlayer(playerID);
	}

	public static boolean isPlayerOwened(int playerID, int communityID) {
		return logicManagement.isPlayerOwened(playerID, communityID);
	}
	
	public static Boolean isPlayerOnExchangeMarket(int playerID, int communityID) throws SQLException {
		return logicManagement.isPlayerOnExchangeMarket(playerID, communityID);
	}

	public static void addPlayerToManager(int managerID, int playerID, boolean plays) {
		playerManagement.addPlayerToManager(managerID, playerID, plays);
	}

	public static int getCurrentSeasonFromSportal() {
		return schedulePointManagement.getCurrentSeasonFromSportal();
	}

	public static void initializeScheduleForSeason(int currentSeason) {
		schedulePointManagement.initializeScheduleForSeason(currentSeason);
	}

	public static List<Player> getTeam(int managerID) {
		return communityManagement.getTeam(managerID);
	}
	
	public static List<Player> getTeam(String managerID) {
		return communityManagement.getTeam(managerID);
	}

	public static Boolean getServerPropsAsBoolean(String propertie) {
		return serverPropertiesManagement.getPropAsBoolean(propertie);
	}

	public static String getServerPropsAsString(String propertie) throws NoSuchElementException {
		return serverPropertiesManagement.getPropAsString(propertie);
	}

	public static Integer getServerPropsAsInt(String property) throws NoSuchElementException {
		return serverPropertiesManagement.getPropAsInt(property);
	}

	public static <E> void setServerProperty(String property, E value) {
		serverPropertiesManagement.setProperty(property, value);
	}

	public static void clearTable(String tableName) {
		databaseUtils.clearTable(tableName);
	}

	public static boolean isResultSetEmpty(ResultSet rs) {
		return databaseUtils.isResultSetEmpty(rs);
	}

	public static int getManagerID(int userID, int communityID) {
		return databaseUtils.getManagerID(userID, communityID);
	}

	public static int getManagerID(int userID, String communityID) {
		return databaseUtils.getManagerID(userID, communityID);
	}
	

	public static String getTeamNameForID(int id) {
		return databaseUtils.getTeamNameForID(id);
	}

	public static void sendSimpleQuery(String query) {
		databaseUtils.sendSimpleQuery(query);
	}

	public static int getUniqueInt(String coloumName, String table, String whereCondition) throws IOException {
		String result = getUniqueString(coloumName, table, whereCondition);
		return Integer.valueOf(result);
	}

	public static String getUniqueString(String coloumName, String table, String whereCondition) throws IOException {
		Object result = getUniqueValue(coloumName, table, whereCondition);
		return result.toString();
	}

	public static Object getUniqueValue(String coloumName, String table, String whereCondition) throws IOException {
		return databaseUtils.getUniqueValue(coloumName, table, whereCondition);
	}

	public static void writePointsToDatabase(Map<String, Player> playerList) {
		pointManagement.updatePointsOfPlayers(playerList);
	}

	public static void setManagersFormation(int managerID, int defenders, int middfielders, int offensives) {
		playerManagement.setManagersFormation(managerID, defenders, middfielders, offensives);
	}

	public static void putPlayerOnExchangeMarket(Player p, int communityID, int ownerID) {
		transfermarktManagement.putPlayerOnExchangeMarket(p,  communityID, ownerID);
	}

	public static void addTransaction(Transaction transaction) {
		transfermarktManagement.addTransaction(transaction);
	}
	
	public static void doTransactions(){
		transfermarktManagement.doTransactions();
	}
	
	public static void transferPlayer(Transaction tr){
		transfermarktManagement.transferPlayer(tr);
	}
	
	public static void deletePlayerFromMarket(Player p, int communityID) throws SQLException {
		transfermarktManagement.deletePlayerFromExchangeMarket(p.getSportalID(),communityID);
		transfermarktManagement.deleteTransactions(p.getSportalID(), communityID);
	}
	public static void removeTransaction(Transaction tr) {
		try {
			transfermarktManagement.deleteTransaction(tr.getPlayerSportalID(), tr.getCommunityID(),tr.getManagerID());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createNewManager(String communityName, int userID) {
		communityManagement.createNewManager(communityName, userID);
	}
	
	public static String getUserName(int managerID){
		return userManagement.getUserName(managerID);
	}

	public static void updateManager(Manager manager) {
		playerManagement.updateManager(manager);
	}

}
