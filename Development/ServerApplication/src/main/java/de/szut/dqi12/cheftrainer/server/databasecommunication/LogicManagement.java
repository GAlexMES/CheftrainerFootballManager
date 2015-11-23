package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.logic.TeamGenerator;

/**
 * This class provides functions to map a {@link Player} to a {@link Manager}
 * and some other simple functions, which are mostly used by the
 * {@link TeamGenerator}.
 * 
 * @author Alexander Brennecke
 *
 */
public class LogicManagement {
	SQLConnection sqlCon;

	/**
	 * Constructor
	 * 
	 * @param sqlCon
	 *            the active {@link SQLConnection}.
	 */
	public LogicManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
	}

	/**
	 * This method finds the heights player id, which is given to a player in
	 * the "Spieler" table of the database.
	 * 
	 * @return 0 = no ID was found. Everything else: The heights ID in the
	 *         "Spieler" table of the database.
	 */
	public int getHeightsPlayerID() {
		try {
			String condition = "name='Spieler'";
			return Integer.valueOf(DatabaseRequests.getUniqueValue("seq", "sqlite_sequence", condition).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * This method checks, if the {@link Player} with the given ID is already
	 * owned by any {@link Manager} in the {@link Community} with the given ID.
	 * 
	 * @param playerID
	 *            the ID of the {@link Player}, that should be checked.
	 * @param communityID
	 *            the ID of the {@link Community}, that should be checked.
	 * @return true = player is owned. false = player is free.
	 */
	public boolean isPlayerOwened(int playerID, int communityID) {
		String sqlQuery = "SELECT * FROM Mannschaft INNER JOIN Manager " + "WHERE Manager.Spielrunde_ID='" + communityID + "'" + " AND Mannschaft.Spieler_ID='" + playerID + "'"
				+ " AND Mannschaft.Manager_ID = Manager.ID";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		return !DatabaseRequests.isResultSetEmpty(rs);
	}
}
