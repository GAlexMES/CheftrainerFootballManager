package de.szut.dqi12.cheftrainer.server.callables;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.UserCommunityListMessage;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;

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
		String update = messageContent.getString(MIDs.UPDATE);
		switch (update) {
		case MIDs.COMMUNITY_LIST:
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
		Session s  = mesController.getSession();
		int userID = s.getUserID();
		
		
		List<Integer> communityIDs = DatabaseRequests.getCummunityIDsForUser(userID);
		List<Community> communityList = new ArrayList<>();
		for(Integer c : communityIDs){
			Community community = DatabaseRequests.getCummunityForID(c);
			communityList.add(community);
		}
		
		UserCommunityListMessage uclMessage = new UserCommunityListMessage(MIDs.INIT);
		uclMessage.setCommunityList(communityList);
		
		mesController.sendMessage(uclMessage);
	}
}
