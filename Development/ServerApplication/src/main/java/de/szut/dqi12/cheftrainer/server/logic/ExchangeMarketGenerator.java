package de.szut.dqi12.cheftrainer.server.logic;

import java.util.ArrayList;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.databasecommunication.SQLConnection;

/**
 * This class is used to create a new Exchange Market and to update it.
 * 
 * @author Alexander
 *
 */
public class ExchangeMarketGenerator {
	private static final int PLAYERS_ON_MARKET = 10;

	public static Market createNewMarket(String communityName) {
		deleteExistingMarket(Controller.getInstance().getSQLConnection(), communityName);
		List<Player> playerList = createRandomPlayerList(PLAYERS_ON_MARKET);
		playerList.forEach(p -> updateDatabaseWithPlayers(p, communityName));
		Market retval = new Market();
		Player[] playerArray = playerList.toArray(new Player[playerList.size()]);
		retval.addPlayer(playerArray);
		return retval;
	}

	private static void updateDatabaseWithPlayers(Player p, String communityName) {
		DatabaseRequests.putPlayerOnExchangeMarket(p, communityName);
	}

	// TODO: Is player in use?
	private static List<Player> createRandomPlayerList(int playersOnMarket) {
		List<Player> retval = new ArrayList<Player>();
		int heighestPlayerID = DatabaseRequests.getHeighstPlayerID();
		for (int i = 0; i < playersOnMarket; i++) {
			retval.add(TeamGenerator.getNewRandomPlayer(heighestPlayerID));
		}
		return retval;
	}

	private static void deleteExistingMarket(SQLConnection sqlCon,
			String communityName) {
		String sqlQuery =  "DELETE FROM Transfermarkt WHERE Spielrunde_ID in " 
				+ "( SELECT Spielrunde_ID FROM Gebote INNER JOIN Spielrunde "
				+ " WHERE Spielrunde.ID = Spielrunde_ID "
				+ " AND  Name = '"+communityName+"')";
		sqlCon.sendQuery(sqlQuery);

		sqlQuery 	= "DELETE FROM Gebote WHERE Spielrunde_ID in " 
					+ "( SELECT Spielrunde_ID FROM Gebote INNER JOIN Spielrunde "
					+ " WHERE Spielrunde.ID = Spielrunde_ID "
					+ " AND Name = '"+communityName+"')";
		sqlCon.sendQuery(sqlQuery);
	}
}
