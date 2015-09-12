package de.szut.dqi12.cheftrainer.server.callables;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseUtils;

public class CommunityAuthentification extends CallableAbstract{
	
	@Override
	public void messageArrived(Message message){
		JSONObject communityJSON = new JSONObject(message.getMessageContent());
		switch(communityJSON.getString("type")){
		case "creation": createNewCommunity(communityJSON);
			break;
		}
	}

	private void createNewCommunity(JSONObject communityJSON){
		String communityName = communityJSON.getString("communityName");
		String communityPassword = communityJSON.getString("communityPassword");
		int adminID = mesController.getSession().getUserID();
		boolean communityCreated = DatabaseUtils.createNewCommunity(communityName, communityPassword,adminID);
		
		Message creationACK = new Message(ServerToClient_MessageIDs.COMMUNITY_AUTHENTIFICATION_ACK);
		
		JSONObject creationACKJSON = new JSONObject();
		creationACKJSON.put("type", "creation");
		creationACKJSON.put("created", communityCreated);
		
		creationACK.setMessageContent(creationACKJSON);
		
		mesController.sendMessage(creationACK);
	}
}
