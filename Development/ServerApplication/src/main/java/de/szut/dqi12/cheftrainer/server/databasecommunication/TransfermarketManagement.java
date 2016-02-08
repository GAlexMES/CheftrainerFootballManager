package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.UserCommunityListMessage;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;

/**
 * The {@link TransfermarketManagement} is used to send queries according or the transfermarket, the transactions and offers to the database and workup the results.
 * @author Alexander Brennecke
 *
 */
public class TransfermarketManagement {

	private final static Logger LOGGER = Logger.getLogger(TransfermarketManagement.class);

	// Given SGLConnection to database
	private static SQLConnection sqlCon = null;

	/**
	 * Constructor
	 * 
	 * @param sqlCon
	 *            active SQL Connection
	 */
	public TransfermarketManagement(SQLConnection sqlCon) {
		TransfermarketManagement.sqlCon = sqlCon;
	}

	/**
	 * This function sets the given {@link Player} on the exchange market of the given {@link Community}
	 * @param p the {@link Player} object, with at least the sportalID and the worth set
	 * @param communityID the ID of the {@link Community}, in which the owner plays
	 * @param ownerID the ID of the owner (the {@link Manager} ID)
	 */
	public void putPlayerOnExchangeMarket(Player p, int communityID, int ownerID) {
		
		try {
			String sqlQuery = "INSERT INTO Transfermarkt (Spielrunde_ID, Spieler_ID, Min_Preis, Inhaber_ID) VALUES(?,?,?,?)";
			PreparedStatement pStatement = sqlCon.prepareStatement(sqlQuery);
			pStatement.setInt(1, communityID);
			pStatement.setInt(2, p.getSportalID());
			pStatement.setInt(3, p.getWorth());
			pStatement.setInt(4, ownerID);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This function adds a new Transaction to the database.
	 * @param tr a {@link Transaction} object, where each parameter is set.
	 */
	public void addTransaction(Transaction tr) {
		boolean isOnMarket = isPlayerOnMarket(tr.getPlayerSportalID(), tr.getCommunityID());
		if (isOnMarket) {
			try {
				deleteTransaction(tr.getPlayerSportalID(), tr.getCommunityID(), tr.getManagerID());
				String sqlQuery = "INSERT INTO Gebote (Manager_ID, Spieler_ID, Gebot, Spielrunde_ID) " + "VALUES (?,?,?,?)";
				int manager_ID = DatabaseRequests.getManagerID(tr.getUserID(), tr.getCommunityID());
				PreparedStatement pStatement = sqlCon.prepareStatement(sqlQuery);
				pStatement.setInt(1, manager_ID);
				pStatement.setInt(2, tr.getPlayerSportalID());
				pStatement.setLong(3, tr.getOfferedPrice());
				pStatement.setInt(4, tr.getCommunityID());
				pStatement.executeUpdate();
				LOGGER.info(manager_ID + " offered " + tr.getOfferedPrice() + "� for player " + tr.getPlayerSportalID());
				sendTransactionsUpdate(tr.getCommunityID(), manager_ID);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This function can be called to create a {@link UserCommunityListMessage}, which will be send to the client.
	 * @param communityID the ID of the current {@link Community}
	 * @param managerID the ID of the receiving {@link Manager}
	 * @throws SQLException 
	 */
	private void sendTransactionsUpdate(int communityID, int managerID)throws SQLException{
		List<Transaction> transactions = getTransactions(communityID);
		
		UserCommunityListMessage uclMessage = new UserCommunityListMessage(MIDs.UPDATE_COMMUNITY);
		uclMessage.setTransactions(transactions);
		
		DatabaseRequests.getUserName(managerID);
		
		String username = DatabaseRequests.getUserName(managerID);
		Session session = Controller.getInstance().getSocketController().getSession(username);
		session.getClientHandler().sendMessage(uclMessage);
	}
	
	/**
	 * This function checks if a {@link Player} is on the exchange market.
	 * @param playerID the sportal ID of the {@link Manager}
	 * @param communityID the ID of the {@link Community}
	 * @return true = {@link Player} is on market, false otherwise
	 */
	private boolean isPlayerOnMarket(int playerID, int communityID) {
		String sqlQuery = "SELECT * FROM Transfermarkt WHERE Spieler_ID=? and Spielrunde_ID=?";
		PreparedStatement pStatement;
		try {
			pStatement = sqlCon.prepareStatement(sqlQuery);
			pStatement.setInt(1, playerID);
			pStatement.setInt(2, communityID);
			ResultSet rs = pStatement.executeQuery();
			return !DatabaseRequests.isResultSetEmpty(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * This function checks, if there is a {@link Transaction} in one of the {@link Community}s.
	 */
	public void doTransactions() {
		List<Integer> communityList = DatabaseRequests.getCummunityIDsForUser(-1);
		for (Integer i : communityList) {
			try {
				List<Transaction> transactions = getTransactions(i);
				transactions.forEach(t -> transferPlayer(t));
			} catch (SQLException sqe) {
				sqe.printStackTrace();
			}
		}
	}

	/**
	 * This function collects all {@link Transaction}s in the given {@link Community}.
	 * When there are multiple offers for the same {@link Player} only the highest offer will be in the retval.
	 * @param communityID the ID of the {@link Community}
	 * @return a {@link List} of {@link Transaction} objects
	 * @throws SQLException
	 */
	private List<Transaction> getTransactions(int communityID) throws SQLException {
		Map<Integer, Transaction> transactionList = new HashMap<>();
		String sqlQuery = "SELECT * FROM Gebote WHERE Spielrunde_ID = " + communityID;
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		while (rs.next()) {
			Transaction t = new Transaction();
			t.setCommunityID(communityID);
			t.setOfferedPrice(rs.getInt("Gebot"));
			t.setManagerID(rs.getInt("Manager_ID"));
			int playerSportalID = rs.getInt("Spieler_ID");
			t.setPlayerSportalID(playerSportalID);

			if (transactionList.containsKey(playerSportalID)) {
				long currentPrice = transactionList.get(playerSportalID).getOfferedPrice();
				if (currentPrice < t.getOfferedPrice()) {
					transactionList.put(playerSportalID, t);
				}
			} else {
				transactionList.put(playerSportalID, t);
			}
		}

		List<Transaction> retval = new ArrayList<>();
		for (int key : transactionList.keySet()) {
			retval.add(transactionList.get(key));
		}
		return retval;
	}

	/**
	 * The {@link Player} will be "transfered" which means, that the offer will be subtracted at the buyer and added at the last owner.
	 * Last but not least the buyer will be the new owner of the {@link Player}.
	 * @param t
	 */
	public void transferPlayer(Transaction t) {
		String sqlQuery = "SELECT Manager_ID FROM Mannschaft INNER JOIN Manager " + " WHERE Mannschaft.Manager_ID = Manager.ID " + " AND Manager.Spielrunde_ID = ? " + " AND Mannschaft.Spieler_ID = ?";
		PreparedStatement pStatement;
		try {
			int communityID = t.getCommunityID();
			int playerSportalID = t.getPlayerSportalID();
			long price = t.getOfferedPrice();

			pStatement = sqlCon.prepareStatement(sqlQuery);
			pStatement.setInt(1, communityID);
			pStatement.setInt(2, playerSportalID);
			ResultSet rs = pStatement.executeQuery();
			boolean notOwned = DatabaseRequests.isResultSetEmpty(rs);
			if (!notOwned) {
				rs = pStatement.executeQuery();
				int managerID = rs.getInt("Manager_ID");

				deletPlayerFromTeam(managerID, playerSportalID);
				updateManagerMoney(managerID, price);
				LOGGER.info("Removed Player " + playerSportalID + " from manager " + managerID + " and added " + price + " to him");
			}
			DatabaseRequests.addPlayerToManager(t.getManagerID(), playerSportalID, false);
			updateManagerMoney(t.getManagerID(), price * -1);
			LOGGER.info("Added Player " + playerSportalID + " to manager " + t.getManagerID() + " and subtracted him  " + price);
			deleteTransactions(playerSportalID, communityID);
			deletePlayerFromExchangeMarket(playerSportalID, communityID);
			LOGGER.info("Deleted  Player " + playerSportalID + " market and deleted offers");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * This function creates a query, which will delete the given {@link Player} from the exchange market of the given {@link Community}.
	 * @param playerSportalID the sportal ID of the {@link Player}, that should be removed
	 * @param communityID the {@link Community} ID of the {@link Community}, where the {@link Player} should be removed from the {@link Market}.
	 * @throws SQLException
	 */
	public void deletePlayerFromExchangeMarket(int playerSportalID, int communityID) throws SQLException {
		String sqlQuery = "DELETE FROM Transfermarkt WHERE Spielrunde_ID = ? AND Spieler_ID = ?";
		PreparedStatement pStatement = sqlCon.prepareStatement(sqlQuery);
		pStatement.setInt(1, communityID);
		pStatement.setInt(2, playerSportalID);
		pStatement.executeUpdate();
	}

	/**
	 * This function creates a query, which will remove the given {@link Player} from the team of a {@link Manager}
	 * @param managerID the ID of the {@link Manager}
	 * @param playerSportalID the sportal ID of the {@link Player}
	 * @throws SQLException
	 */
	private void deletPlayerFromTeam(int managerID, int playerSportalID) throws SQLException {
		String deleteQuery = "DELETE FROM Mannschaft WHERE Manager_ID = ? AND Spieler_ID = ?";
		PreparedStatement deleteStatement = sqlCon.prepareStatement(deleteQuery);
		deleteStatement.setInt(1, managerID);
		deleteStatement.setInt(2, playerSportalID);
		deleteStatement.executeUpdate();
	}

	/**
	 * This function updated the budget/money of the given {@link Manager} via a database query.
	 * @param managerID the ID of the {@link Manager}, that money should be updated
	 * @param money could be positive or negative
	 */
	private void updateManagerMoney(int managerID, long money) {
		String addMoney = "UPDATE Manager" + " SET Budget = Budget + " + money + " WHERE ID = " + managerID;
		sqlCon.sendQuery(addMoney);
	}

	/**
	 * This function should be called to delete all {@link Transaction}s for one {@link Player}.
	 * @param playerSportalID the sportal ID of the {@link Player}, 
	 * @param communityID the ID of the {@link Community}
	 * @throws SQLException
	 */
	public void deleteTransactions(int playerSportalID, int communityID) throws SQLException {
		String sqlQuery = "DELETE FROM Gebote WHERE Spieler_ID = ? AND Spielrunde_ID = ?";
		PreparedStatement pStatement = sqlCon.prepareStatement(sqlQuery);
		pStatement.setInt(1, playerSportalID);
		pStatement.setInt(2, communityID);
		pStatement.execute();
	}

	/**
	 * This function should be called to delete the offer of one {@link Manager} to one {@link Player} in one {@link Community}
	 * @param playerSportalID the sportal ID of the {@link Player}
	 * @param communityID the {@link Community} ID
	 * @param managerID the ID of the {@link Manager}
	 * @throws SQLException
	 */
	public void deleteTransaction(int playerSportalID, int communityID, int managerID) throws SQLException {
		String sqlQuery = "DELETE FROM Gebote WHERE Spieler_ID = ? AND Spielrunde_ID = ? AND Manager_ID = ?";
		PreparedStatement pStatement = sqlCon.prepareStatement(sqlQuery);
		pStatement.setInt(1, playerSportalID);
		pStatement.setInt(2, communityID);
		pStatement.setInt(3, managerID);
		pStatement.executeUpdate();
	}

}
