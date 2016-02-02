package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Formation;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.FormationFactory;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;
import de.szut.dqi12.cheftrainer.server.logic.TeamGenerator;

/**
 * This class is used to communicate with the database. It has Querys for the
 * topic "Community"
 * 
 * @author Alexander Brennecke
 *
 */
public class CommunityManagement {

	private final static Logger LOGGER = Logger.getLogger(CommunityManagement.class);

	private SQLConnection sqlCon;
	private FormationFactory formationFactory;

	private static final int BUDGET = 15000000;

	/**
	 * Constructor.
	 * 
	 * @param sqlCon
	 *            the current {@link SQLConnection}
	 */
	public CommunityManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
		this.formationFactory = new FormationFactory();
	}

	/**
	 * This method collects all communities for the given userID and returns it.
	 * 
	 * @param userID
	 *            the ID, of the user, that communities should be returns.
	 * @return a list of all communities in which the given user has a manager.
	 */
	public List<Community> getCummunities(int userID) {
		List<Community> retval = new ArrayList<>();
		List<Integer> idList = getCommunityIDsForUser(userID);
		for (Integer i : idList) {
			retval.add(getCommunity(i));
		}
		return retval;
	}

	/**
	 * This method fetches all CommunityIDs from the database, in which a User
	 * with the given ID has a {@link Manager}.
	 * 
	 * @param userID
	 *            the ID of the User, that {@link Community}IDs will be returned
	 * @return a List of Integer with IDs for {@link Community} inside.
	 */
	public List<Integer> getCommunityIDsForUser(int userID) {
		String sqlQuery = "SELECT Spielrunde.ID FROM Spielrunde ";
		if (userID >= 0) {
			sqlQuery += " INNER JOIN Manager WHERE Manager.Nutzer_ID =" + userID + " AND Manager.Spielrunde_ID=Spielrunde.ID";
		}
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		List<Integer> retval = new ArrayList<>();
		try {
			while (rs.next()) {
				int id = rs.getInt("ID");
				retval.add(id);
			}
		} catch (SQLException e) {

		}

		return retval;
	}

	/**
	 * Creates a Community Object for the given community ID with all
	 * information, that could be collect in the database.
	 * 
	 * @param communityID
	 *            the ID of the Community, that information should be collect in
	 *            the database.
	 * @return a Community Object for the given community ID
	 */
	public Community getCommunity(int communityID) {
		Community retval = new Community();
		String sqlQuery = "Select * FROM Spielrunde WHERE ID = " + communityID;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				retval.setCommunityID(communityID);
				retval.setName(rs.getString("Name"));
			}
		} catch (SQLException e) {
		}
		retval.addManagers(getManagers(communityID, retval.getName()));
		retval.setMarket(getMarket(communityID));
		return retval;
	}

	/**
	 * This function sends a Query to to the database to fetch the transfer
	 * {@link Market} for the given {@link Community}.
	 * 
	 * @param communityID
	 *            the community ID of the market, that will be read out of the
	 *            database.
	 * @return a {@link Market} object, which is filled by the database results.
	 */
	private Market getMarket(int communityID) {
		Market retval = new Market();

		String sqlQuery = "SELECT * FROM Transfermarkt " + "INNER JOIN Spieler INNER JOIN Verein" + " WHERE Spieler.SportalID = Spieler_ID" + " AND Verein_ID = Verein.ID" + " AND Spielrunde_ID = "
				+ communityID;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			List<Player> playerList = PlayerManagement.getPlayersFromResultSet(rs);
			playerList.forEach(p -> retval.addPlayer(p));
			List<Transaction> transactionList = getTransactions(communityID);
			retval.setTransactions(transactionList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}

	/**
	 * This function creates a query to fetch all {@link Transaction}s for the
	 * given {@link Community}.
	 * 
	 * @param communityID
	 *            the ID of the {@link Community}
	 * @return a List of {@link Transaction} objects, which were filled by the
	 *         result of the database.
	 * @throws SQLException
	 */
	private List<Transaction> getTransactions(int communityID) throws SQLException {
		List<Transaction> retval = new ArrayList<>();

		String transactionQuery = "Select * From Gebote Where Spielrunde_ID = " + communityID;
		ResultSet rs = sqlCon.sendQuery(transactionQuery);
		while (rs.next()) {
			Transaction t = new Transaction();
			t.setManagerID(rs.getInt("Manager_ID"));
			t.setPlayerSportalID(rs.getInt("Spieler_ID"));
			t.setOfferedPrice(rs.getInt("Gebot"));
			t.setCommunityID(communityID);
			retval.add(t);
		}
		return retval;
	}

	/**
	 * This method collects all managers, that play in the given
	 * {@link Community}.
	 * 
	 * @param communityID
	 *            the ID of the {@link Community}
	 * @param communityName
	 *            the Name of the {@link Community}.
	 * @return a List of {@link Manager} Objects.
	 */

	public List<Manager> getManagers(int communityID, String communityName) {
		List<Manager> retval = new ArrayList<>();
		String sqlQuery = "SELECT Manager.*, Nutzer.Nutzername " + "FROM  Manager INNER JOIN Nutzer WHERE Spielrunde_ID=" + communityID + " AND Manager.Nutzer_ID=Nutzer.ID";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				try {
					String managerName = rs.getString("Nutzername");
					int points = rs.getInt("Punkte");
					int place = rs.getInt("Platz");
					Manager manager = new Manager(managerName, null, place, communityName);
					int defenders = rs.getInt("Anzahl_Abwehr");
					int middfielders = rs.getInt("Anzahl_Mittelfeld");
					int offensives = rs.getInt("Anzahl_Stuermer");
					Formation formation = formationFactory.getFormation(defenders, middfielders, offensives);
					manager.setFormation(formation);
					manager.setID(rs.getInt("ID"));
					retval.add(manager);
				} catch (IOException ioe) {
					LOGGER.error("Invalid formation was read out of the database!   " + ioe.getStackTrace());
				}
			}
		} catch (SQLException e) {
		}
		for (Manager m : retval) {
			List<Player> playerList = getTeam(m.getID());
			Player[] playerArray = playerList.toArray(new Player[playerList.size()]);
			m.addPlayer(playerArray);
			Map<Integer,Integer> stats = getManagerStats(m.getID());
			m.setHistory(stats);
		}
		return retval;
	}
	
	/**
	 * This function collects the statistic from the database for the given manager and creates a {@link Map} from it.
	 * @param managerID the ID of the manager
	 * @return A Map, where the matchday is the key and the points are the value.
	 */
	private Map<Integer,Integer> getManagerStats(int managerID){
		Map<Integer,Integer> retval = new HashMap<Integer, Integer>();
		String sqlQuery = "SELECT * FROM Manager_Statistik WHERE Manager_ID = "+managerID;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while(rs.next()){
				retval.put(rs.getInt("Spieltag"), rs.getInt("Punkte"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retval;
	}

	/**
	 * This method is used to create a new community in the database, when there
	 * is no database with the given name.
	 * 
	 * @param name
	 *            the name of the new community.
	 * @param password
	 *            the password of the new community
	 * @param adminID
	 *            the user ID of the user, that creates the community.
	 * @return true = commmuniy was created.
	 */
	public boolean createNewCommunity(String name, String password, int adminID) {
		String sqlQuery = "SELECT Name FROM Spielrunde WHERE Name='" + name + "'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		if (DatabaseRequests.isResultSetEmpty(rs)) {
			return createCommunity(name, password, adminID);
		}
		return false;
	}

	/**
	 * This method is used to create a new community in the database.
	 * 
	 * @param name
	 *            the name of the new community.
	 * @param password
	 *            the password of the new community
	 * @param adminID
	 *            the user ID of the user, that creates the community.
	 * @return true = commmuniy was created.
	 */
	private boolean createCommunity(String name, String password, int adminID) {
		String sqlQuery = "INSERT INTO Spielrunde (Name, Administrator_ID, Passwort) VALUES ( '" + name + "', '" + adminID + "', '" + password + "')";
		sqlCon.sendQuery(sqlQuery);
		return true;
	}

	/**
	 * This method is called, when a user tries to enter an existing community.
	 * 
	 * @param communityName
	 *            the name of the community.
	 * @param communityPassword
	 *            the md5 password of the community
	 * @param userID
	 *            the user ID of the user, that wants to join the community.
	 * @return a HashMap with booleans, that describes, if the entering was
	 *         successful.
	 */
	public HashMap<String, Boolean> enterCommunity(String communityName, String communityPassword, int userID) {
		HashMap<String, Boolean> retval = new HashMap<String, Boolean>();
		retval.put(MIDs.USER_EXISTS, false);
		retval.put(MIDs.COMMUNITY_EXISTS, false);
		retval.put(MIDs.CORRECT_PASSWORD, false);
		retval.put(MIDs.MANAGER_CREATED, false);
		if (existCommunity(communityName)) {
			retval.put(MIDs.COMMUNITY_EXISTS, true);
			if (checkPassword(communityPassword, communityName)) {
				retval.put(MIDs.CORRECT_PASSWORD, true);
				if (!existUserInCommunity(userID, communityName)) {
					retval.put(MIDs.USER_EXISTS, true);
					boolean created = createNewManager(communityName, userID);
					retval.put(MIDs.MANAGER_CREATED, created);
				}
			}
		}
		return retval;
	}

	/**
	 * This method checks, if the given community already exists in the
	 * database.
	 * 
	 * @param communityName
	 *            Name of the Community, that should be checked.
	 * @return true = Community exists.
	 */
	private boolean existCommunity(String communityName) {
		String sqlQueryExistCommunity = "SELECT * FROM Spielrunde WHERE Spielrunde.Name= '" + communityName + "'";

		ResultSet rs = sqlCon.sendQuery(sqlQueryExistCommunity);
		return !DatabaseRequests.isResultSetEmpty(rs);
	}

	/**
	 * This method is called, when a new manager should be added to the
	 * database.
	 * 
	 * @param communityName
	 *            the name of the community, which the manager joins.
	 * @param userID
	 *            the ID of the user, that wants to join the community.
	 * @return true = manager was created successful.
	 */
	public boolean createNewManager(String communityName, int userID) {
		try {
			String condition = "Name='" + communityName + "'";
			int communityID = Integer.valueOf(DatabaseRequests.getUniqueValue("ID", "Spielrunde", condition).toString());
			String sqlQuery = "INSERT INTO Manager (Nutzer_ID, Spielrunde_ID) VALUES ('" + userID + "','" + communityID + "')";
			sqlCon.sendQuery(sqlQuery);

			int managerID = DatabaseRequests.getManagerID(userID, communityID);
			TeamGenerator tg = new TeamGenerator();
			int teamWorth = tg.generateTeamForUser(managerID, communityID);
			int budget = BUDGET;

			int maxTeamWorth = (int) (TeamGenerator.TEAM_WORTH * (1 + TeamGenerator.TEAM_WORTH_TOLERANZ));
			int minTeamWorth = (int) (TeamGenerator.TEAM_WORTH * (1 - TeamGenerator.TEAM_WORTH_TOLERANZ));

			if (teamWorth > maxTeamWorth) {
				budget = BUDGET - (maxTeamWorth - teamWorth);
			} else if (teamWorth < minTeamWorth) {
				budget = BUDGET + (minTeamWorth - teamWorth);
			}

			sqlQuery = "UPDATE Manager SET Budget='" + budget + "'" + "WHERE ID=" + managerID;
			sqlCon.sendQuery(sqlQuery);

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * This method checks, if the given password and community match together.
	 * 
	 * @param password
	 *            the md5 password of the community.
	 * @param communityName
	 *            the name of the community.
	 * @return true = password and name match together.
	 */
	private boolean checkPassword(String password, String communityName) {
		String sqlQuery = "SELECT Passwort FROM Spielrunde" + " WHERE Name='" + communityName + "'" + " AND Passwort='" + password + "'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		return !DatabaseRequests.isResultSetEmpty(rs);
	}

	/**
	 * This method checks, of the user has already a manager, which plays in the
	 * given community.
	 * 
	 * @param userID
	 *            the ID of the user
	 * @param communityName
	 *            the name of the community
	 * @return true = the user has a manager in the given community.
	 */
	private boolean existUserInCommunity(int userID, String communityName) {
		String sqlQueryExistUser = "SELECT * FROM Manager INNER JOIN Spielrunde " + "WHERE Manager.Nutzer_ID=" + userID + " And Spielrunde.Name='" + communityName + "'"
				+ " AND Spielrunde.ID=Manager.Spielrunde_ID";
		ResultSet rs = sqlCon.sendQuery(sqlQueryExistUser);
		return !DatabaseRequests.isResultSetEmpty(rs);
	}

	/**
	 * This function creates a Query to read the ID of a {@link User} from the
	 * database.
	 * 
	 * @param userName
	 *            the name of the user (login name)
	 * @return see getTeam(int managerID)
	 */
	public List<Player> getTeam(String userName) {
		String condition = "Manager.Nutzer_ID = Nutzer.ID AND Nutzer.Nutzername = '" + userName + "'";
		int managerID;
		try {
			managerID = DatabaseRequests.getUniqueInt("Manager.ID", "Manager INNER JOIN Nutzer", condition);
			return getTeam(managerID);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method creates a List of {@link Player} for the given
	 * {@link Manager} out of the database values.
	 * 
	 * @param managerID
	 *            the id of the {@link Manager}, that team should be fetched
	 *            from the database.
	 * @return a List of {@link Player} Objects, which presents the team of the
	 *         {@link Manager}
	 */
	public List<Player> getTeam(int managerID) {
		List<Player> playerList = new ArrayList<>();
		String sqlQuery = "SELECT * FROM Spieler INNER JOIN Mannschaft INNER JOIN Verein" + " WHERE Mannschaft.Manager_ID=" + managerID + " AND Spieler.Verein_ID = Verein.ID"
				+ " AND Mannschaft.Spieler_ID=Spieler.SportalID";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			playerList.addAll(PlayerManagement.getPlayersFromResultSet(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return playerList;
	}

	/**
	 * This function collects all {@link Manager} IDs from the Database.
	 * 
	 * @return a {@link List} of IDs of {@link Manager}s.
	 */
	public List<Integer> getAllManagerIDs() {
		String managerIDsQuery = "SELECT ID FROM Manager";
		ResultSet rs = sqlCon.sendQuery(managerIDsQuery);
		try {
			return DatabaseUtils.getListFromResultSet(rs, "ID");
		} catch (SQLException sqe) {
			sqe.printStackTrace();
			return null;
		}
	}

	private List<Integer> getAllCommunityIDs() throws SQLException {
		String sqlQuery = "Select ID FROM Spielrunde";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		return DatabaseUtils.getListFromResultSet(rs, "ID");
	}

	public void updatePlacement() {
		try {
			List<Integer> communityIDs = getAllCommunityIDs();
			for (int id : communityIDs) {
				List<Integer[]> orderedManagers = getOrdererManagerIDs(id);
				
				int counter = 1;
				int duplicatedCounter = 1;
				int lastPoints = 0;
				for(Integer[] manager : orderedManagers){
					if(manager[1] == lastPoints){
						updateManagerPlace(manager[0],counter-duplicatedCounter);
						duplicatedCounter ++;
					}
					else{
						updateManagerPlace(manager[0],counter);
						duplicatedCounter = 1;
					}
					
					lastPoints = manager[1];
					counter ++;
				}

			}
		} catch (SQLException sqe) {
			sqe.printStackTrace();
		}
	}
	
	private void updateManagerPlace(Integer managerID, int place) throws SQLException {
		String sqlQuery =  "UPDATE Manager Set Platz = ? where ID = ?";
		PreparedStatement pStatement = sqlCon.prepareStatement(sqlQuery);
		pStatement.setInt(1,place);
		pStatement.setInt(2, managerID);
		pStatement.execute();
	}

	private List<Integer[]> getOrdererManagerIDs(int communityID) throws SQLException{
		List<Integer[]> retval = new ArrayList<>();
		String orderedPointsQuery = "Select Punkte, ID From Manager where Spielrunde_ID = ? ORDER BY Punkte DESC";
		PreparedStatement pStatement = sqlCon.prepareStatement(orderedPointsQuery);
		pStatement.setInt(1, communityID);
		ResultSet rs = pStatement.executeQuery();
		while(rs.next()){
			int managerID = rs.getInt("ID");
			int points = rs.getInt("Punkte");
			Integer[] entry ={managerID,points};
			retval.add(entry);
		}
		return retval;
	}
}
