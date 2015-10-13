package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.utils.DatabaseUtils;

public class LogicManagement {
	SQLConnection sqlCon;

	public LogicManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
	}

	public int getHeighstPlayerID() {
		try {
			String condition = "name='Spieler'";
			return Integer.valueOf(DatabaseUtils.getUniqueValue(sqlCon, "seq", "sqlite_sequence", condition));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public Player getPlayer(int playerID) {
		Player retval = null;
		String sqlQuery = "SELECT * FROM Spieler WHERE ID='"+playerID+"'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		try {
			while(rs.next()){
				retval = new Player();
				retval.setWorth(rs.getInt("Marktwert"));
				retval.setPosition(rs.getString("Position"));
				retval.setID(playerID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}

	public boolean isPlayerOwened(int playerID, int communityID) {
		String sqlQuery = "SELECT * FROM Mannschaft INNER JOIN Manager "
						+ "WHERE Manager.Spielrunde_ID='"+communityID+"'"
						+ " AND Mannschaft.Spieler_ID='"+playerID+"'";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		return !DatabaseUtils.isResultSetEmpty(rs);
	}

	public void addPlayerToManager(int managerID, int playerID) {
		String sqlQuery = "INSERT INTO Mannschaft ('Manager_ID','Spieler_ID','Aufgestellt') "
						+	" VALUES ('"+managerID+"','"+playerID+"','false')";
		sqlCon.sendQuery(sqlQuery);
	}

}
