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
	private final int NUMBER_OF_PLAYER = NUMBER_OF_DEFENDER
			+ NUMBER_OF_GOALKEEPER + NUMBER_OF_MIDDFIELDER
			+ NUMBER_OF_OFFENSIVE;
	private final int TEAM_WORTH = 20000000;
	private final double TEAM_WORTH_TOLERANZ = 0.25;

	private int teamWorth = 0;
	private int goalkeepers = 0;
	private int defenders = 0;
	private int middfielders = 0;
	private int offensives = 0;

	private List<Integer> idList;
	private List<Player> playerList;
	private int heighestPlayerID;
	private int communityID;
	private int managerID;

	private final static Logger LOGGER = Logger.getLogger(TeamGenerator.class);

	public void generateTeamForUser(int managerID, int communityID) {
		LOGGER.info("Generate team for manager with ID = " + managerID
				+ " for community " + communityID);

		reset();
		this.managerID = managerID;
		this.communityID = communityID;
		heighestPlayerID = DatabaseRequests.getHeighstPlayerID();
		idList = new ArrayList<>();
		playerList = new ArrayList<>();

		while (goalkeepers + defenders + middfielders + offensives < NUMBER_OF_PLAYER
				&& idList.size() < heighestPlayerID - 1) {
			Player p = getNewRandomPlayer();
			if (!idList.contains(p.getID())) {
				idList.add(p.getID());
				if (!isPlayerInUse(p.getID())
						&& playerFitsInTeam(p.getPosition())) {
					playerList.add(p);
					updatePlayerPerPosition(p.getPosition(), 1);
					teamWorth += p.getWorth();
					LOGGER.info("Team generation: "
							+ ((goalkeepers + defenders + middfielders + offensives) * 6.5)
							+ " % done");
				}
			}
		}

		while (!checkWorth()) {
			System.out.println("Incorrect worth: "+teamWorth);
			correctWorth();
		}
		System.out.println("Correct wort: "+teamWorth);

		playerList.forEach(p -> updateDatabaseWithPlayers(p));

		LOGGER.info("Team generation: 100% done - completed!");
	}

	private void correctWorth() {
		if (teamWorth < TEAM_WORTH * (1 + TEAM_WORTH_TOLERANZ)) {
			findMoreExpensivePlayerForCheapestPlayer();
		} else if (teamWorth > TEAM_WORTH * (1 - TEAM_WORTH_TOLERANZ)) {
			findCheaperPlayerForMostExpensivePlayer();
		}
	}

	private void findMoreExpensivePlayerForCheapestPlayer() {
		Player cheapestPlayer = null;

		List<Integer> currentPlayerIDs = new ArrayList<>();
		for (int i = 0; i < playerList.size(); i++) {
			Player p = playerList.get(i);
			currentPlayerIDs.add(p.getID());
			if (cheapestPlayer == null
					|| p.getWorth() < cheapestPlayer.getWorth()) {
				cheapestPlayer = p;
			}
		}

		boolean playerFound = false;
		List<Integer> idList = new ArrayList<>();

		while (!playerFound && idList.size() < heighestPlayerID - 1) {
			Player newPlayer = getNewRandomPlayer();

			if (!idList.contains(newPlayer.getID())) {
				idList.add(newPlayer.getID());

				boolean betterWorth = newPlayer.getWorth() > cheapestPlayer
						.getWorth();
				boolean samePosition = newPlayer.getPosition().equals(
						cheapestPlayer.getPosition());
				boolean notInUse = !isPlayerInUse(newPlayer.getID())
						&& !currentPlayerIDs.contains(newPlayer.getID());

				if (betterWorth && samePosition && notInUse) {
					if (newPlayer.getPosition().equals(
							cheapestPlayer.getPosition())) {
						teamWorth -= cheapestPlayer.getWorth();
						teamWorth += newPlayer.getWorth();
						playerList.remove(cheapestPlayer);
						playerList.add(newPlayer);
						playerFound = true;
					}
				}
			}
		}
	}

	private void findCheaperPlayerForMostExpensivePlayer() {
		Player mostExpensive = null;

		List<Integer> currentPlayerIDs = new ArrayList<>();
		for (int i = 0; i < playerList.size(); i++) {
			Player p = playerList.get(i);
			currentPlayerIDs.add(p.getID());
			if (mostExpensive == null
					|| p.getWorth() > mostExpensive.getWorth()) {
				mostExpensive = p;
			}
		}

		boolean playerFound = false;
		List<Integer> idList = new ArrayList<>();

		while (!playerFound && idList.size() < heighestPlayerID / 2) {
			Player newPlayer = getNewRandomPlayer();

			if (!idList.contains(newPlayer.getID())) {
				idList.add(newPlayer.getID());

				boolean betterWorth = newPlayer.getWorth() < mostExpensive
						.getWorth();
				boolean samePosition = newPlayer.getPosition().equals(
						mostExpensive.getPosition());
				boolean notInUse = !isPlayerInUse(newPlayer.getID())
						&& !currentPlayerIDs.contains(newPlayer.getID());

				if (betterWorth && samePosition && notInUse) {
					if (newPlayer.getPosition().equals(
							mostExpensive.getPosition())) {
						teamWorth -= mostExpensive.getWorth();
						teamWorth += newPlayer.getWorth();
						playerList.remove(mostExpensive);
						playerList.add(newPlayer);
						playerFound = true;
					}
				}
			}
		}
	}

	private boolean checkWorth() {
		if (teamWorth < TEAM_WORTH * (1 + TEAM_WORTH_TOLERANZ)
				&& teamWorth > TEAM_WORTH * (1 - TEAM_WORTH_TOLERANZ)) {
			return true;
		}
		return false;
	}

	private void updateDatabaseWithPlayers(Player p) {
		DatabaseRequests.addPlayerToManager(managerID, p.getID());
	}

	private void updatePlayerPerPosition(String position, int update) {
		switch (position) {
		case "Torwart":
			goalkeepers += update;
			break;
		case "Abwehr":
			defenders += update;
			break;
		case "Mittelfeld":
			middfielders += update;
			break;
		case "Sturm":
			offensives += update;
			break;
		}
	}

	// TODO: Überprüfung, ob Spieler auf dem Transfermarkt ist.
	private boolean isPlayerInUse(int playerID) {
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

	private Player getNewRandomPlayer() {
		Random rn = new Random();
		int playerID = 0 + rn.nextInt(heighestPlayerID - 1);

		return DatabaseRequests.getPlayer(playerID);
	}

	private void reset() {
		teamWorth = 0;
		goalkeepers = 0;
		defenders = 0;
		middfielders = 0;
		offensives = 0;
	}
}
