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

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;

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

	public void putPlayerOnExchangeMarket(Player p, String communityName) {
		try {
			String condition = "Name='" + communityName + "'";
			int communityID = Integer.valueOf(DatabaseRequests.getUniqueValue("ID", "Spielrunde", condition).toString());
			String sqlQuery = "INSERT INTO Transfermarkt (Spielrunde_ID, Spieler_ID, Min_Preis) VALUES(?,?,?)";
			PreparedStatement pStatement = sqlCon.prepareStatement(sqlQuery);
			pStatement.setInt(1, communityID);
			pStatement.setInt(2, p.getSportalID());
			pStatement.setInt(3, p.getWorth());
			pStatement.executeUpdate();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}

	}

	public void addTransaction(Transaction transaction) {
		String sqlQuery = "INSERT INTO Gebote (Manager_ID, Spieler_ID, Gebot, Spielrunde_ID) " + "VALUES (?,?,?,?)";
		int manager_ID = DatabaseRequests.getManagerID(transaction.getUserID(), transaction.getCommunityID());
		try {
			PreparedStatement pStatement = sqlCon.prepareStatement(sqlQuery);
			pStatement.setInt(1, manager_ID);
			pStatement.setInt(2, transaction.getPlayerSportalID());
			pStatement.setInt(3, transaction.getOfferedPrice());
			pStatement.setInt(4, transaction.getCommunityID());
			pStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

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
				int currentPrice = transactionList.get(playerSportalID).getOfferedPrice();
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

	private void transferPlayer(Transaction t) {
		String sqlQuery = "SELECT Manager_ID FROM Mannschaft INNER JOIN Manager " + " WHERE Mannschaft.Manager_ID = Manager.ID " + " AND Manager.Spielrunde_ID = ? " + " AND Mannschaft.Spieler_ID = ?";
		PreparedStatement pStatement;
		try {
			int communityID = t.getCommunityID();
			int playerSportalID = t.getPlayerSportalID();
			int price = t.getOfferedPrice();
			
			pStatement = sqlCon.prepareStatement(sqlQuery);
			pStatement.setInt(1, communityID);
			pStatement.setInt(2, playerSportalID);
			ResultSet rs = pStatement.executeQuery();
			boolean notOwned  = DatabaseRequests.isResultSetEmpty(rs);
			if(!notOwned){
				rs = pStatement.executeQuery();
				int managerID = rs.getInt("Manager_ID");
				
				deletPlayerFromTeam(managerID, playerSportalID);
				updateManagerMoney(managerID,  price);
				LOGGER.info("Removed Player "+playerSportalID+" from manager "+managerID+" and added "+price+" to him");
			}
			DatabaseRequests.addPlayerToManager(t.getManagerID(),playerSportalID, false);
			updateManagerMoney(t.getManagerID(), price*-1);
			LOGGER.info("Added Player "+playerSportalID+" to manager "+t.getManagerID()+" and subtracted him  "+price);
			deleteTransactions(playerSportalID, communityID);
			deletePlayerFromExchangeMarket(playerSportalID, communityID);
			LOGGER.info("Deleted  Player "+playerSportalID+" market and deleted offers");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void deletePlayerFromExchangeMarket(int playerSportalID, int communityID) throws SQLException {
		String sqlQuery = "DELETE FROM Transfermarkt WHERE Spielrunde_ID = ? AND Spieler_ID = ?";
		PreparedStatement pStatement = sqlCon.prepareStatement(sqlQuery);
		pStatement.setInt(1, communityID);
		pStatement.setInt(2, playerSportalID);
		pStatement.executeUpdate();
	}

	private void deletPlayerFromTeam(int managerID, int playerSportalID) throws SQLException {
		String deleteQuery = "DELETE FROM Mannschaft WHERE Manager_ID = ? AND Spieler_ID = ?";
		PreparedStatement deleteStatement = sqlCon.prepareStatement(deleteQuery);
		deleteStatement.setInt(1, managerID);
		deleteStatement.setInt(2, playerSportalID);
		deleteStatement.executeUpdate();
	}

	private void updateManagerMoney(int managerID, int money) {
		String addMoney = "UPDATE Manager" + " SET Budget = Budget + " + money + " WHERE ID = " + managerID;
		sqlCon.sendQuery(addMoney);
	}
	
	private void deleteTransactions(int playerSportalID, int communityID) throws SQLException{
		String sqlQuery = "DELETE FROM Gebote WHERE Spieler_ID = ? AND Spielrunde_ID = ?";
		PreparedStatement pStatement = sqlCon.prepareStatement(sqlQuery);
		pStatement.setInt(1, playerSportalID);
		pStatement.setInt(2,communityID);
		pStatement.executeUpdate();
		
	}
}
