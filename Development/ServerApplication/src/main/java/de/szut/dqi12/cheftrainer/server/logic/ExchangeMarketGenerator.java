package de.szut.dqi12.cheftrainer.server.logic;

import java.util.ArrayList;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.database.SQLConnection;

/**
 * This class is used to create a new Exchange Market and to update it.
 * 
 * @author Alexander Brennecke
 *
 */
public class ExchangeMarketGenerator {
	private static final int PLAYERS_ON_MARKET = 10;

	public static Market createNewMarket(String communityName) {
		try {
			String condition = "Name='" + communityName + "'";
			int communityID = Integer.valueOf(DatabaseRequests.getUniqueValue("ID", "Spielrunde", condition).toString());

			deleteExistingMarket(Controller.getInstance().getSQLConnection(), communityName);

			List<Player> playerList = createRandomPlayerList(PLAYERS_ON_MARKET, communityID);
			playerList.forEach(p -> updateDatabaseWithPlayers(p, communityID));

			Market retval = new Market();
			Player[] playerArray = playerList.toArray(new Player[playerList.size()]);
			retval.addPlayer(playerArray);
			return retval;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void updateDatabaseWithPlayers(Player p, int communityID) {
		DatabaseRequests.putPlayerOnExchangeMarket(p, communityID, -1);
	}

	private static List<Player> createRandomPlayerList(int playersOnMarket, int communityID) {
		List<Player> retval = new ArrayList<Player>();
		int heighestPlayerID = DatabaseRequests.getHeighstPlayerID();
		boolean enoughPlayers = false;
		while (!enoughPlayers) {
			Player p = TeamGenerator.getNewRandomPlayer(heighestPlayerID);
			boolean isPlayerInUse = TeamGenerator.isPlayerInUse(p.getSportalID(), communityID);
			if (!isPlayerInUse) {
				retval.add(p);
				enoughPlayers = retval.size() == playersOnMarket;
			}
		}
		return retval;
	}

	private static void deleteExistingMarket(SQLConnection sqlCon, String communityName) {
		String sqlQuery = "DELETE FROM Transfermarkt WHERE Spielrunde_ID in " + "( SELECT Spielrunde_ID FROM Gebote INNER JOIN Spielrunde " + " WHERE Spielrunde.ID = Spielrunde_ID "
				+ " AND  Name = '" + communityName + "')";
		sqlCon.sendQuery(sqlQuery);

		sqlQuery = "DELETE FROM Gebote WHERE Spielrunde_ID in " + "( SELECT Spielrunde_ID FROM Gebote INNER JOIN Spielrunde " + " WHERE Spielrunde.ID = Spielrunde_ID " + " AND Name = '"
				+ communityName + "')";
		sqlCon.sendQuery(sqlQuery);
	}
}
