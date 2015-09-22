package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.sql.ResultSet;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.RealTeam;
import de.szut.dqi12.cheftrainer.server.utils.DatabaseUtils;
import de.szut.dqi12.cheftrainer.server.utils.ParserUtils;

public class InitializationManagement {

	private SQLConnection sqlCon;

	public InitializationManagement(SQLConnection sqlCon) {
		this.sqlCon = sqlCon;
	}

	public boolean existPlayer() {
		String sqlQuery = "Select ID from Spieler";
		ResultSet rs = sqlCon.sendQuery(sqlQuery);
		return !DatabaseUtils.isResultSetEmpty(rs);
	}

	public void loadRealPlayers(String leagueName, String leagueCountry,
			String sourceURL) {
		try {
			addLeague(leagueName, leagueCountry);
			List<RealTeam> teamList = ParserUtils.getTeamList(sourceURL);
			String condition = "Name='"+leagueName+"'";
			int leagueID = DatabaseUtils.getUniqueValue(sqlCon,"ID", "Liga",condition );
			teamList.forEach(t -> addTeam(t, leagueID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void addPlayer(Player p, int teamID){
		int points = (int) (Math.random() * 10000000);
		String sqlQuery = "INSERT INTO Spieler (Name,Verein_ID, Position, Punkte, Marktwert, Nummer) "
						+ "VALUES ('"+p.getName()+ "','"
						+ teamID +"','"
						+ p.getPosition() +"','"
						+ "0','"
						+ points + "','"
						+ p.getNumber() +"')";
		sqlCon.sendQuery(sqlQuery);
	}

	private void addTeam(RealTeam t, int leagueID) {
		try {
			String sqlQuery = "INSERT INTO Verein (Name, Liga_ID) Values ('"
					+ t.getTeamName() + "','" + leagueID + "')";
			sqlCon.sendQuery(sqlQuery);
			String condition = "Name='"+t.getTeamName()+"'";
			int teamID = DatabaseUtils.getUniqueValue(sqlCon,"ID", "Verein",condition );
			List<Player> playerList = t.getPlayerList();
			playerList.forEach(p -> addPlayer(p, teamID));
		} catch (Exception e) {
		}
	}

	private void addLeague(String name, String country) {
		String sqlString = "INSERT INTO Liga ('Name','Land') VALUES('"+name+"','"+country+"')";
		sqlCon.sendQuery(sqlString);
	}
}
