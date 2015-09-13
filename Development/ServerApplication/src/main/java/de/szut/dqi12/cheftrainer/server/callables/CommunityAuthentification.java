package de.szut.dqi12.cheftrainer.server.callables;

import java.util.HashMap;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseUtils;
import de.szut.dqi12.cheftrainer.utils.JSONUtils;

/**
 * This class handles messaged with the id "CommunityAuthentification".
 * @author Alexander Brennecke
 *
 */
public class CommunityAuthentification extends CallableAbstract {

	/**
	 * This method is called by the message controller, whenever the message controller arrives a message with the ID "CommunityAuthentification".
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
	 * This method is called, when the CommunityAuthentification Message has the type "enter".
	 * It uses the database to check, if the user can enter the given community. It sends and ACK to the client.
	 * @param communityJSON a JSONObject with all required information to enter a community.
	 */
	private void enterCommunity(JSONObject communityJSON){
		String communityName = communityJSON.getString("communityName");
		String communityPassword = communityJSON.getString("password");
		int userID = mesController.getSession().getUserID();
		HashMap<String,Boolean> enterFeedback = DatabaseUtils.enterCommunity(communityName, communityPassword, userID);
		Message enterACK = new Message(ServerToClient_MessageIDs.COMMUNITY_AUTHENTIFICATION_ACK);
		JSONObject enterACKJSON = JSONUtils.mapToJSON(enterFeedback);
		enterACKJSON.put("type", "enter");
		enterACK.setMessageContent(enterACKJSON);
		mesController.sendMessage(enterACK);
	}

	/**
	 * This method is called, when the CommunityAuthentification Message has the type "creation".
	 * It uses the database to create a new community, if it is possible.
	 * @param communityJSON a JSONObject with all required information to create a new community.
	 */
	private void createNewCommunity(JSONObject communityJSON) {
		String communityName = communityJSON.getString("communityName");
		String communityPassword = communityJSON.getString("communityPassword");
		int adminID = mesController.getSession().getUserID();
		boolean communityCreated = DatabaseUtils.createNewCommunity(
				communityName, communityPassword, adminID);

		if(communityCreated){
			int userID = mesController.getSession().getUserID();
			DatabaseUtils.enterCommunity(communityName, communityPassword, userID);
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
