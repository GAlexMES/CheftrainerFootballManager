package de.szut.dqi12.cheftrainer.server.logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Position;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;

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
	public static final int TEAM_WORTH = 20000000;
	public static final double TEAM_WORTH_TOLERANZ = 0.25;
	
	private final int DEFAULT_NUMBER_KEEPERS = 1;
	private final int DEFAULT_NUMBER_OFFENSIVES = 2;
	private final int DEFAULT_NUMBER_MIDDFIELDERS = 4;
	private final int DEFAULT_NUMBER_DEFENDERS = 4;
	
	
	private Map<String, Integer> formation;

	private int teamWorth = 0;
	private int goalkeepers = 0;
	private int defenders = 0;
	private int middfielders = 0;
	private int offensives = 0;

	private List<Player> playerList;
	private int heighestPlayerID;
	private int communityID;
	private int managerID;
	


	private boolean breakFlag = false;

	private final static Logger LOGGER = Logger.getLogger(TeamGenerator.class);

	/**
	 * This method creates a new team for the given manager in the given
	 * community
	 * 
	 * @param managerID
	 *            the ID of the manager, that should get the new team
	 * @param communityID
	 *            the ID, in which the manager is active
	 * @return returns the worth of the created team
	 */
	public int generateTeamForUser(int managerID, int communityID) {
		LOGGER.info("Generate team for manager with ID = " + managerID
				+ " for community " + communityID);

		reset();
		this.managerID = managerID;
		this.communityID = communityID;
		playerList = createRandomTeam();
		
		if (correctWorth()) {
			playerList= matchPlayersInFormation(playerList);
			playerList.forEach(p -> updateDatabaseWithPlayers(p));
			DatabaseRequests.setManagersFormation(managerID, DEFAULT_NUMBER_DEFENDERS,DEFAULT_NUMBER_MIDDFIELDERS,DEFAULT_NUMBER_OFFENSIVES);
			LOGGER.info("Team generation: 100% done - completed!");
		} else {
			LOGGER.error("Team generation: failed, something went wrong!");
		}
		return teamWorth;
	}
	
	public List<Player> matchPlayersInFormation(List<Player> playerList){
		for(Player p : playerList){
			String position = p.getPosition();
			if (formation.get(position)>0){
				p.setPlays(true);
				formation.put(position, formation.get(position) -1);
			}
		}
		return playerList;
	}

	/**
	 * This method creates a new random team
	 * 
	 * @return a List of all Players, playing in this new team
	 */
	private List<Player> createRandomTeam() {
		heighestPlayerID = DatabaseRequests.getHeighstPlayerID();
		List<Integer> idList = new ArrayList<>();
		List<Player> playerList = new ArrayList<>();

		while (goalkeepers + defenders + middfielders + offensives < NUMBER_OF_PLAYER
				&& idList.size() < heighestPlayerID - 1) {
			Player p = getNewRandomPlayer();
			if (p != null && !idList.contains(p.getID())) {
				idList.add(p.getID());
				if (!isPlayerInUse(p.getID())&& playerFitsInTeam(p.getPosition())) {
					playerList.add(p);
					updatePlayerPerPosition(p.getPosition(), 1);
					teamWorth += p.getWorth();
					LOGGER.info("Team generation: "
							+ ((goalkeepers + defenders + middfielders + offensives) * 6.5)
							+ " % done");
				}
			}
		}
		return playerList;
	}

	/**
	 * Compares two int
	 */
	private static final Comparator<Integer> INT_COMPARATOR = (a, b) -> {
		if (a < b) {
			return -1;
		}
		if (a > b) {
			return 1;
		}
		return 0;
	};

	/**
	 * This method checks, if the team worth is correct and calls the
	 * findBetterPlayer function to improve the team worth
	 */
	private boolean correctWorth() {
		boolean smallerThanMax = teamWorth < TEAM_WORTH
				* (1 + TEAM_WORTH_TOLERANZ);
		boolean greaterThanMin = teamWorth > TEAM_WORTH
				* (1 - TEAM_WORTH_TOLERANZ);
		if (smallerThanMax && greaterThanMin) {
			return true;
		} else if (smallerThanMax) {
			findBetterPlayer((a, b) -> -INT_COMPARATOR.compare(a, b));
		} else if (greaterThanMin) {
			findBetterPlayer(INT_COMPARATOR);
		}
		if (!breakFlag) {
			return correctWorth();
		} else {
			return true;
		}
	}

	/**
	 * Tries to find a better player for the user
	 * 
	 * @param com
	 *            the comparator, which is used to find a cheaper or more
	 *            expensive player
	 */
	private void findBetterPlayer(Comparator<Integer> com) {
		Player currentPlayer = null;

		int noPlayerFoundCounter = 0;

		List<Integer> currentPlayerIDs = new ArrayList<>();
		for (int i = 0; i < playerList.size(); i++) {
			Player p = playerList.get(i);
			currentPlayerIDs.add(p.getID());

			if (currentPlayer == null
					|| com.compare(currentPlayer.getWorth(), p.getWorth()) < 0) {
				currentPlayer = p;
			}
		}

		boolean playerFound = false;
		List<Integer> idList = new ArrayList<>();

		while (!playerFound && idList.size() < heighestPlayerID - 1) {
			Player newPlayer = getNewRandomPlayer();

			if (newPlayer == null) {
				noPlayerFoundCounter++;
				if (noPlayerFoundCounter >= 10) {
					System.out
							.println("Could not find a better player. Team worth is: "
									+ teamWorth);
					breakFlag = true;
					break;
				}
			}

			if (newPlayer != null && !idList.contains(newPlayer.getID())) {
				idList.add(newPlayer.getID());

				boolean betterWorth = com.compare(newPlayer.getWorth(),
						currentPlayer.getWorth()) < 0;
				boolean samePosition = newPlayer.getPosition().equals(
						currentPlayer.getPosition());
				boolean notInUse = !isPlayerInUse(newPlayer.getID())
						&& !currentPlayerIDs.contains(newPlayer.getID());

				if (betterWorth && samePosition && notInUse) {
					if (newPlayer.getPosition().equals(
							currentPlayer.getPosition())) {
						teamWorth -= currentPlayer.getWorth();
						teamWorth += newPlayer.getWorth();
						playerList.remove(currentPlayer);
						playerList.add(newPlayer);
						playerFound = true;
					}
				}
			}
		}
	}

	/**
	 * This method maps the given Player to the manager, that was given to the
	 * generateTeamForUser method
	 * 
	 * @param p
	 *            the player, that should be mapped to the manager
	 */
	private void updateDatabaseWithPlayers(Player p) {
		DatabaseRequests.addPlayerToManager(managerID, p.getID(), p.plays());
	}

	/**
	 * Updates the number of players, which plays on the given position
	 * 
	 * @param position
	 *            the position, that should be updated
	 * @param update
	 *            the value, that should be added to the position
	 */
	private void updatePlayerPerPosition(String position, int update) {
		switch (position) {
		case Position.KEEPER:
			goalkeepers += update;
			break;
		case Position.DEFENCE:
			defenders += update;
			break;
		case Position.MIDDLE:
			middfielders += update;
			break;
		case Position.OFFENCE:
			offensives += update;
			break;
		}
	}

	// TODO: Überprüfung, ob Spieler auf dem Transfermarkt ist.
	private boolean isPlayerInUse(int playerID) {
		return DatabaseRequests.isPlayerOwened(playerID, communityID);
	}

	/**
	 * Checks if the position, on which the given plays, is free
	 * 
	 * @param position
	 *            the position of the player
	 * @return true = position is free, false=position is not free
	 */
	private boolean playerFitsInTeam(String position) {
		switch (position) {
		case Position.KEEPER:
			return goalkeepers < NUMBER_OF_GOALKEEPER;
		case Position.DEFENCE:
			return defenders < NUMBER_OF_DEFENDER;
		case Position.MIDDLE:
			return middfielders < NUMBER_OF_MIDDFIELDER;
		case Position.OFFENCE:
			return offensives < NUMBER_OF_OFFENSIVE;
		default:
			return false;
		}
	}

	/**
	 * Creates a random int and takes the player with this ID out of the
	 * database
	 * 
	 * @return a new random Player object with values from the database
	 */
	private Player getNewRandomPlayer() {
		Random rn = new Random();
		int playerID = 0 + rn.nextInt(heighestPlayerID - 1);

		return DatabaseRequests.getPlayer(playerID);
	}

	/**
	 * Resets all parameters for next generation
	 */
	private void reset() {
		teamWorth = 0;
		goalkeepers = 0;
		defenders = 0;
		middfielders = 0;
		offensives = 0;
		formation = new HashMap<>();
		formation.put(Position.KEEPER,DEFAULT_NUMBER_KEEPERS);
		formation.put(Position.DEFENCE,DEFAULT_NUMBER_DEFENDERS);
		formation.put(Position.MIDDLE,DEFAULT_NUMBER_MIDDFIELDERS);
		formation.put(Position.OFFENCE,DEFAULT_NUMBER_MIDDFIELDERS);
	}
}
