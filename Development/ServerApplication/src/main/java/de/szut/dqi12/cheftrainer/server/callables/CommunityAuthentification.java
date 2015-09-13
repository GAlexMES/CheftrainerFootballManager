package de.szut.dqi12.cheftrainer.server.callables;

import java.util.HashMap;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseUtils;
import de.szut.dqi12.cheftrainer.utils.JSONUtils;

public class CommunityAuthentification extends CallableAbstract {

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

	private void createNewCommunity(JSONObject communityJSON) {
		String communityName = communityJSON.getString("communityName");
		String communityPassword = communityJSON.getString("communityPassword");
		int adminID = mesController.getSession().getUserID();
		boolean communityCreated = DatabaseUtils.createNewCommunity(
				communityName, communityPassword, adminID);

		Message creationACK = new Message(
				ServerToClient_MessageIDs.COMMUNITY_AUTHENTIFICATION_ACK);

		JSONObject creationACKJSON = new JSONObject();
		creationACKJSON.put("type", "creation");
		creationACKJSON.put("created", communityCreated);

		creationACK.setMessageContent(creationACKJSON);

		mesController.sendMessage(creationACK);
	}
}
