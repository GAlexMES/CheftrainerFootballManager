package de.szut.dqi12.cheftrainer.server.callables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Formation;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Position;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;

/**
 * This callable handles everything according to the formation and Line-Up of a {@link Manager}.
 * @author Alexander Brennecke
 * @custom.position /F0110/ </br> /F0120/ </br> /F0130/ </br> /F0140/
 */
public class NewFormationUpdate extends CallableAbstract {

	Map<String, Integer> positionList;

	@Override
	public void messageArrived(Message message) {
		initMap();
		JSONObject managerJSON = new JSONObject(message.getMessageContent());

		Manager sendedManager = new Manager(managerJSON);

		List<Player> dbPlayers = DatabaseRequests.getTeam(sendedManager.getID());
		boolean successful = false;
		boolean checkedFormation = checkFormation(sendedManager.getFormation());
		if (checkedFormation) {
			boolean noNewPlayers = noNewPlayers(dbPlayers, sendedManager.getPlayers());
			if (noNewPlayers) {
				boolean correctAlloocation = correctAllocation(sendedManager.getPlayers());
				if (correctAlloocation) {
					DatabaseRequests.updateManager(sendedManager);
					successful = true;
				}
			}
		}
		updateClient(successful);
	}
	
	/**
	 * This function sends the acknowledge to the client. 
	 * @param successful true = sent formation was valid and saved, false = otherwise
	 */
	private void updateClient(Boolean successful){
		Message message = new Message(ServerToClient_MessageIDs.SAVE_FORMATION_ACK);
		JSONObject content = new JSONObject();
		content.put("successful", successful);
		message.setMessageContent(content);
		mesController.sendMessage(message);
	}

	/**
	 * This function catches all SportalIDs, from each {@link Player} in the playerList.
	 * @param playerList a List of {@link Player}s, which SportalIDs should be returned.
	 * @return a List of SportalIDs according to the playerList.
	 */
	private ArrayList<Integer> getSportalIDs(List<Player> playerList) {
		ArrayList<Integer> retval = new ArrayList<Integer>();
		for (Player p : playerList) {
			if (retval.contains(p.getSportalID())) {
				return null;
			} else {
				retval.add(p.getSportalID());
			}
		}
		return retval;
	}

	/**
	 * This function checks, if the {@link Position} of the {@link Player}s, 
	 * matches to the given {@link Formation}. 
	 * @param playerList the List of {@link Player}s, that should match to the {@link Formation}, saved in the positionList.
	 * @return true = for each {@link Position} in the positionList, are enough {@link Player}s in the given playerList.
	 */
	private boolean correctAllocation(List<Player> playerList) {
		for (Player p : playerList) {
			if (p.isPlays()) {
				String position = p.getPosition();
				int currentValue = positionList.get(position);
				positionList.put(position, currentValue - 1);
			}
		}
		for (String s : positionList.keySet()) {
			if (positionList.get(s) != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This function compares the to given {@link Player} lists, and checks, if they contain the same SportalIDs.
	 * @param dbTeam the team, which is stored in the database
	 * @param sendedTeam the team, which was sent by the client.
	 * @return true = same SportalIDs in both lists, false = otherwise
	 */
	private boolean noNewPlayers(List<Player> dbTeam, List<Player> sendedTeam) {
		if (dbTeam.size() != sendedTeam.size()) {
			return false;
		} else {
			Collection<Integer> sendedIDs = getSportalIDs(sendedTeam);
			Collection<Integer> dbIDs = getSportalIDs(dbTeam);
			sendedIDs.retainAll(dbIDs);
			if (sendedIDs.size() == dbIDs.size()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This function checks, if the given Formation is valid.
	 * @param formation the {@link Formation}, that should be checked.
	 * @return true = {@link Formation} is valid, false = otherwise
	 */
	private boolean checkFormation(Formation formation) {
		positionList.put(Position.DEFENCE, formation.getDefenders());
		positionList.put(Position.MIDDLE, formation.getMiddfielders());
		positionList.put(Position.OFFENCE, formation.getOffensives());
		int sum = 0;
		for (String s : positionList.keySet()) {
			sum = sum + positionList.get(s);
		}
		return sum == 11;
	}

	/**
	 * This function initializes the positionList for further instructions.
	 */
	private void initMap() {
		positionList = new HashMap<String, Integer>();
		positionList.put(Position.DEFENCE, 0);
		positionList.put(Position.MIDDLE, 0);
		positionList.put(Position.OFFENCE, 0);
		positionList.put(Position.KEEPER, 1);
	}

}
