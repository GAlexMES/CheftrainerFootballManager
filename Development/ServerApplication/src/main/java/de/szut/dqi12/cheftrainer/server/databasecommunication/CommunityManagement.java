package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.logic.TeamGenerator;
import de.szut.dqi12.cheftrainer.server.utils.DatabaseUtils;

/**
 * This class is used to communicate with the database.
 * It has Querys for the topic "Community"
 * @author Alexander Brennecke
 *
 */
public class CommunityManagement {

	private SQLConnection sqlCon;

	/**
	 * Constructor.
	 * @param sqlCon
	 */
	public CommunityManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
	}

	/**
	 *This method collects all communities for the given userID and returns it.
	 * @param userID the ID, of the user, that communities should be returns.
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
	
	public List<Integer> getCommunityIDsForUser(int userID){
		String sqlQuery = "SELECT Spielrunde.ID FROM Spielrunde INNER JOIN Manager WHERE Manager.Nutzer_ID ="
				+ userID + " AND Manager.Spielrunde_ID=Spielrunde.ID";
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
	 * Creates a Community Object for the given community ID with all information, that could be collect in the database.
	 * @param communityID the ID of the Community, that information should be collect in the database.
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
		retval.addManagers(getManagers(communityID));
		return retval;
	}

	/**
	 * This method collects all managers, that play in the given community
	 * @param communityID the ID of the community
	 * @return a List of Manager Objects.
	 */
	public List<Manager> getManagers(int communityID) {
		List<Manager> retval = new ArrayList<>();
		String sqlQuery = "SELECT Manager.ID, Nutzer.Nutzername, Manager.Budget, Manager.Punkte "
				+ "FROM  Manager INNER JOIN  Nutzer WHERE Spielrunde_ID="
				+ communityID + " AND Manager.Nutzer_ID=Nutzer.ID";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				String managerName = rs.getString("Nutzername");
				double money = rs.getDouble("Budget");
				int points = rs.getInt("Punkte");
				Manager manager = new Manager(managerName, money, points);
				manager.setID(rs.getInt("ID"));
				retval.add(manager);
			}
		} catch (SQLException e) {
		}
		for(Manager m : retval){
			List<Player> playerList = getTeam(m.getID());
			Player [] playerArray = playerList.toArray(new Player[playerList.size()]);
			m.addPlayer(playerArray);
		}
		return retval;
	}

	/**
	 * This method is used to create a new community in the database, when there is no database with the given name.
	 * @param name the name of the new community.
	 * @param password the password of the new community
	 * @param adminID the user ID of the user, that creates the community.
	 * @return true = commmuniy was created.
	 */
	public boolean createNewCommunity(String name, String password, int adminID) {
		String sqlQuery = "SELECT Name FROM Spielrunde WHERE Name='" + name
				+ "'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		if (DatabaseUtils.isResultSetEmpty(rs)) {
			return createCommunity(name, password, adminID);
		}
		return false;
	}

	/**
	 * This method is used to create a new community in the database.
	 * @param name the name of the new community.
	 * @param password the password of the new community
	 * @param adminID the user ID of the user, that creates the community.
	 * @return true = commmuniy was created.
	 */
	private boolean createCommunity(String name, String password, int adminID) {
		String sqlQuery = "INSERT INTO Spielrunde (Name, Administrator_ID, Passwort) VALUES ( '"
				+ name + "', '" + adminID + "', '" + password + "')";
		sqlCon.sendQuery(sqlQuery);
		return true;
	}

	/**
	 * This method is called, when a user tries to enter an existing community.
	 * @param communityName the name of the community.
	 * @param communityPassword the md5 password of the community
	 * @param userID the user ID of the user, that wants to join the community.
	 * @return a HashMap with booleans, that describes, if the entering was successful.
	 */
	public HashMap<String, Boolean> enterCommunity(String communityName,
			String communityPassword, int userID) {
		HashMap<String, Boolean> retval = new HashMap<String, Boolean>();
		retval.put("userDoesNotExist", false);
		retval.put("existCommunity", false);
		retval.put("correctPassword", false);
		retval.put("managerCreated", false);
		if (existCommunity(communityName)) {
			retval.put("existCommunity", true);
			if (checkPassword(communityPassword, communityName)) {
				retval.put("correctPassword", true);
				if (!existUserInCommunity(userID, communityName)) {
					retval.put("userDoesNotExist", true);
					boolean created = createNewManager(communityName, userID);
					retval.put("managerCreated", created);
				}
			}
		}
		return retval;
	}

	/**
	 * This method checks, if the given community already exists in the database.
	 * @param communityName Name of the Community, that should be checked.
	 * @return true = Community exists.
	 */
	private boolean existCommunity(String communityName) {
		String sqlQueryExistCommunity = "SELECT * FROM Spielrunde WHERE Spielrunde.Name= '"
				+ communityName + "'";

		ResultSet rs = sqlCon.sendQuery(sqlQueryExistCommunity);
		return !DatabaseUtils.isResultSetEmpty(rs);
	}

	/**
	 * This method is called, when a new manager should be added to the database.
	 * @param communityName the name of the community, which the manager joins.
	 * @param userID the ID of the user, that wants to join the community.
	 * @return true = manager was created successful.
	 */
	private boolean createNewManager(String communityName, int userID) {
		ResultSet rs;
		String sqlQueryCommunityID = "SELECT ID FROM Spielrunde WHERE Name='"
				+ communityName + "'";
		rs = sqlCon.sendQuery(sqlQueryCommunityID);
		try {
			int communityID = rs.getInt("ID");
			String sqlQuery = "INSERT INTO Manager (Nutzer_ID, Spielrunde_ID) VALUES ('"
					+ userID + "','" + communityID + "')";
			sqlCon.sendQuery(sqlQuery);
			
			int managerID = DatabaseUtils.getManagerID(sqlCon, userID, communityID);
			TeamGenerator tg = new TeamGenerator();
			tg.generateTeamForUser(managerID, communityID);
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	/**
	 * This method checks, if the given password and community match together.
	 * @param password the md5 password of the community.
	 * @param communityName the name of the community.
	 * @return true = password and name match together.
	 */
	private boolean checkPassword(String password, String communityName) {
		String sqlQuery = "SELECT Passwort FROM Spielrunde" + " WHERE Name='"
				+ communityName + "'" + " AND Passwort='" + password + "'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		return !DatabaseUtils.isResultSetEmpty(rs);
	}

	/**
	 * This method checks, of the user has already a manager, which plays in the given community.
	 * @param userID the ID of the user
	 * @param communityName the name of the community
	 * @return true =  the user has a manager in the given community.
	 */
	private boolean existUserInCommunity(int userID, String communityName) {
		String sqlQueryExistUser = "SELECT * FROM Manager INNER JOIN Spielrunde "
				+ "WHERE Manager.Nutzer_ID="
				+ userID
				+ " And Spielrunde.Name='"
				+ communityName
				+ "'"
				+ " AND Spielrunde.ID=Manager.Spielrunde_ID";
		ResultSet rs = sqlCon.sendQuery(sqlQueryExistUser);
		return !DatabaseUtils.isResultSetEmpty(rs);
	}
	
	public List<Player> getTeam(int managerID){
		List<Player> playerList = new ArrayList<>();
		String sqlQuery = "SELECT * FROM Spieler INNER JOIN Mannschaft INNER JOIN Verein"
				+ " WHERE Mannschaft.Manager_ID="+managerID
				+ " AND Spieler.Verein_ID = Verein.ID"
				+ " AND Mannschaft.Spieler_ID=Spieler.ID";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while (rs.next()) {
				Player p = new Player();
				p.setID(rs.getInt("ID"));
				p.setName(rs.getString("Name"));
				p.setTeamName(rs.getString("Vereinsname"));
				p.setPosition(rs.getString("Position"));
				p.setNumber(rs.getInt("Nummer"));
				p.setPlays(rs.getBoolean("Aufgestellt"));
				p.setWorth(rs.getInt("Marktwert"));
				p.setPoints(rs.getInt("Punkte"));
				playerList.add(p);
			}
		} catch (SQLException e) {
		}
		return playerList;
	}
}
