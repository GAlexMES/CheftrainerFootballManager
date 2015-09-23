package de.szut.dqi12.cheftrainer.server.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.databasecommunication.InitializationManagement;

/**
 * <b>//F0040<b> Generates a new random team for a user.
 * 
 * @author Alexander Brennecke
 *
 */
public class TeamGenerator {

	private final int NUMBER_OF_GOALKEEPER = 2;
	private final int NUMBER_OF_DEFENDER = 5;
	private final int NUMBER_OF_MIDDFIELDER = 5;
	private final int NUMBER_OF_OFFENSIVE = 3;
	private final int NUMBER_OF_PLAYER = NUMBER_OF_DEFENDER+NUMBER_OF_GOALKEEPER+NUMBER_OF_MIDDFIELDER+NUMBER_OF_OFFENSIVE;
	private final int TEAM_WORTH = 20000000;
	private final int TEAM_WORTH_TOLERANZ = 25;

	private int teamWorth = 0;
	private int goalkeepers = 0;
	private int defenders = 0;
	private int middfielders = 0;
	private int offensives = 0;

	private final static Logger LOGGER = Logger.getLogger(TeamGenerator.class);
	
	public void generateTeamForUser(int managerID, int communityID) {
		LOGGER.info("Generate team for manager with ID = "+managerID+" for community "+ communityID);
		int heighestPlayerID = DatabaseRequests.getHeighstPlayerID();
		List<Integer> playerList = new ArrayList<>();

		while (goalkeepers + defenders + middfielders + offensives < NUMBER_OF_PLAYER ) {
			Player player = getNewRandomPlayer(heighestPlayerID);
			if (checkConditions(player,communityID)) {
				DatabaseRequests.addPlayerToManager(managerID, player.getID());
				updatePlayerPerPosition(player.getPosition(),1);
				LOGGER.info("Team generation: "+((goalkeepers+defenders+middfielders+offensives)*6.5)+" % done");
			}
		}

		reset();
		LOGGER.info("Team generation: 100% done - completed!");
	}
	
	private void updatePlayerPerPosition(String position, int update){
		switch (position) {
		case "Torwart":
			goalkeepers += update; break;
		case "Abwehr":
			defenders += update; break;
		case "Mittelfeld":
			middfielders += update; break;
		case "Sturm":
			offensives += update; break;
		}
	}

	private boolean checkConditions(Player p, int communityID) {
		boolean fits = true;
		for (int i = 0; i < 3; i++) {

			if (!fits) {
				return false;
			}

			switch (i) {
			case 0:
				fits = !isPlayerInUse(p.getID(), communityID);
				break;
			case 1:
				fits = playerFitsInTeam(p.getPosition());
				break;
			case 3:
				break;
			}
		}

		return fits;
	}

	// TODO: Überprüfung, ob Spieler auf dem Transfermarkt ist.
	private boolean isPlayerInUse(int playerID, int communityID) {
		return DatabaseRequests.isPlayerOwened(playerID, communityID);
	}

	private boolean playerFitsInTeam(String position) {
		switch (position) {
		case "Torwart":
			return goalkeepers < NUMBER_OF_GOALKEEPER;
		case "Abwehr":
			return defenders < NUMBER_OF_DEFENDER;
		case "Mittelfeld":
			return middfielders < NUMBER_OF_MIDDFIELDER;
		case "Sturm":
			return offensives < NUMBER_OF_OFFENSIVE;
		default:
			return false;
		}
	}

	private Player getNewRandomPlayer(int heighestID) {
		Random rn = new Random();
		int playerID = 0 + rn.nextInt(heighestID - 1);
		Player p = DatabaseRequests.getPlayer(playerID);
		if (p == null) {
			return getNewRandomPlayer(heighestID);
		} else {
			return p;
		}
	}

	private void reset() {
		teamWorth = 0;
		goalkeepers = 0;
		defenders = 0;
		middfielders = 0;
		offensives = 0;
	}
}
