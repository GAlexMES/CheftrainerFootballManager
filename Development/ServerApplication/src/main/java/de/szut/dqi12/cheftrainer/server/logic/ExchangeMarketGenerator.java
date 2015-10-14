package de.szut.dqi12.cheftrainer.server.logic;

import de.szut.dqi12.cheftrainer.server.databasecommunication.SQLConnection;

/**
 * This class is used to create a new Exchange Market and to update it.
 * @author Alexander
 *
 */
public class ExchangeMarketGenerator {
	private static final int PLAYERS_ON_MARKET = 10;

	public static void createNewMarket(SQLConnection sqlCon, int communityID){
		deleteExistingMarket(sqlCon, communityID);
		List<Player> playerList = createRandomPlayerList(PLAYERS_ON_MARKET);
	}
	
	private static List<Player> createRandomPlayerList(int playersOnMarket) {
		List<Player> retval = new ArrayList<Player>;
		int heighestPlayerID = DatabaseRequest.getHeighestPlayerID();
		for(int i = 0; i<playersOnMarket;i++){
			retval.add(TeamGenerator.newRandomPlayer(heighestPlayerID));
		}
	}

	private static void deleteExistingMarket(SQLConnection sqlCon, int communityID){
		//TODO: Insert SQL Query
		String sqlQuery = "";
		sqlCon.sendQuery(sqlQuery);
	}
}
