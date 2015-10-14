package de.szut.dqi12.cheftrainer.server.callables;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.usercommunication.ClientUpdate;
import de.szut.dqi12.cheftrainer.server.utils.JSONUtils;

/**
 * This class handles messaged with the id "CommunityAuthentification".
 * 
 * @author Alexander Brennecke
 *
 */
public class CommunityAuthentification extends CallableAbstract {

	/**
	 * This method is called by the message controller, whenever the message
	 * controller arrives a message with the ID "CommunityAuthentification".
	 */
	@Override
	public void messageArrived(Message message) {
		JSONObject communityJSON = new JSONObject(message.getMessageContent());
		switch (communityJSON.getString("type")) {
		case "creation":
			createNewCommunity(communityJSON);
			break;
		case "enter":
			enterCommunity(communityJSON);
		}
	}

	/**
	 * This method is called, when the CommunityAuthentification Message has the
	 * type "enter". It uses the database to check, if the user can enter the
	 * given community. It sends and ACK to the client.
	 * 
	 * @param communityJSON
	 *            a JSONObject with all required information to enter a
	 *            community.
	 */
	private void enterCommunity(JSONObject communityJSON) {
		String communityName = communityJSON.getString("communityName");
		String communityPassword = communityJSON.getString("password");
		int userID = mesController.getSession().getUserID();
		HashMap<String, Boolean> enterFeedback = DatabaseRequests
				.enterCommunity(communityName, communityPassword, userID);
		Message enterACK = new Message(
				ServerToClient_MessageIDs.COMMUNITY_AUTHENTIFICATION_ACK);
		JSONObject enterACKJSON = JSONUtils.mapToJSON(enterFeedback);

		updateSessionAndClient(userID);

		enterACKJSON.put("type", "enter");
		enterACK.setMessageContent(enterACKJSON);
		mesController.sendMessage(enterACK);
	}

	private void updateSessionAndClient(int userID) {
		int communityID = getNewCommunityID(userID);
		if (communityID > 0) {
			updateSession(communityID);
			sendUpdateToClient(communityID);
		}
	}

	private void updateSession(int communityID) {
		Community community = DatabaseRequests.getCummunityForID(communityID);
		mesController.getSession().addCommunity(community);
	}

	private int getNewCommunityID(int userID) {
		Set<Integer> knownIDs = mesController.getSession().getCommunityMap()
				.keySet();
		List<Integer> allIDs = DatabaseRequests.getCummunityIDsForUser(userID);

		for (Integer i : allIDs) {
			if (!knownIDs.contains(i)) {
				return i;
			}
		}
		return -1;
	}

	private void sendUpdateToClient(int communityID) {
		Message communityListUpdate = new Message(
				ServerToClient_MessageIDs.USER_COMMUNITY_LIST);

		JSONObject updateJSON = new JSONObject();
		updateJSON.put("type", "newCommunity");
		updateJSON.put("community", ClientUpdate.createCommunityMessage(communityID));
		communityListUpdate.setMessageContent(updateJSON);
		mesController.sendMessage(communityListUpdate);
	}

	// private JSONArray createJSONForManagerTeam(int userID,
	// String communityName, boolean feedback) {
	// if (feedback) {
	// return createJSONForTeam(userID, communityName);
	// }
	// return new JSONArray();
	// }
	//
	// private JSONArray createJSONForManagerTeam(int userID,
	// String communityName, HashMap<String, Boolean> feedback) {
	// boolean feedbackFlag = false;
	// ;
	//
	// for (String s : feedback.keySet()) {
	// feedbackFlag = feedback.get(s);
	// if (!feedbackFlag) {
	// break;
	// }
	// }
	//
	// return createJSONForManagerTeam(userID, communityName, feedbackFlag);
	// }
	//
	// private JSONArray createJSONForTeam(int userID, String communityName) {
	// JSONArray retval = new JSONArray();
	// SQLConnection sqlCon = Controller.getInstance().getSQLConnection();
	// int managerID = DatabaseUtils.getManagerID(sqlCon, userID,
	// communityName);
	// List<Player> playerList = DatabaseRequests.getTeam(managerID);
	// for (Player p : playerList) {
	// retval.put(p.getJSONFromPlayer());
	// }
	// return retval;
	// }

	/**
	 * This method is called, when the CommunityAuthentification Message has the
	 * type "creation". It uses the database to create a new community, if it is
	 * possible.
	 * 
	 * @param communityJSON
	 *            a JSONObject with all required information to create a new
	 *            community.
	 */
	private void createNewCommunity(JSONObject communityJSON) {
		String communityName = communityJSON.getString("communityName");
		String communityPassword = communityJSON.getString("communityPassword");
		int userID = mesController.getSession().getUserID();
		boolean communityCreated = DatabaseRequests.createNewCommunity(
				communityName, communityPassword, userID);

		if (communityCreated) {
			DatabaseRequests.enterCommunity(communityName, communityPassword,
					userID);
			updateSessionAndClient(userID);
		}
		Message creationACK = new Message(
				ServerToClient_MessageIDs.COMMUNITY_AUTHENTIFICATION_ACK);

		JSONObject creationACKJSON = new JSONObject();
		creationACKJSON.put("type", "creation");
		creationACKJSON.put("created", communityCreated);

		creationACK.setMessageContent(creationACKJSON);

		mesController.sendMessage(creationACK);
	}

}
