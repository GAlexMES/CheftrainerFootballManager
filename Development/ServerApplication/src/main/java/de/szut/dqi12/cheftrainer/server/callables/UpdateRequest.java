package de.szut.dqi12.cheftrainer.server.callables;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.usercommunication.ClientUpdate;

/**
 * This class is used to handle "UpdateRequest" messages, which were send
 * by a client.
 * 
 * @author Alexander Brennecke
 *
 */
public class UpdateRequest extends CallableAbstract {

	/**
	 * This method is called whenever a message with the ID "UpdateRequest" is arrived at the message controller.
	 */
	@Override
	public void messageArrived(Message message) {
		JSONObject messageContent = new JSONObject(message.getMessageContent());
		String update = messageContent.getString("update");
		switch (update) {
		case "CommunityList":
			sendCommunityList();
			break;
		}
	}
	
	/**
	 * Is called, when the UpdateRequest message has the type "CommunityList".
	 * It collects all communities, in which the given user has managers.
	 * After that it creates a ACK message and sends it to the client.
	 */
	public void sendCommunityList(){
		JSONArray communityListJSON = new JSONArray();
		Message comminityMessage = new Message(
				ServerToClient_MessageIDs.USER_COMMUNITY_LIST);
		
		Session s  = mesController.getSession();
		int userID = s.getUserID();
		List<Integer> communityIDs = DatabaseRequests.getCummunityIDsForUser(userID);
		for(Integer c : communityIDs){
			JSONObject communityJSON = ClientUpdate.createCommunityMessage(Integer.valueOf(c));
			communityListJSON.put(communityJSON);
		}
		
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("type", "init");
		jsonMessage.put("information",communityListJSON );
		
		comminityMessage.setMessageContent(jsonMessage.toString());
		mesController.sendMessage(comminityMessage);
	}
}
