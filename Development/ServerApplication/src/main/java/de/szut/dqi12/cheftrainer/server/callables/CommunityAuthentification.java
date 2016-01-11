package de.szut.dqi12.cheftrainer.server.callables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.logic.ExchangeMarketGenerator;
import de.szut.dqi12.cheftrainer.server.utils.JSONUtils;

/**
 * This class handles messaged with the id "CommunityAuthentification".
 * 
 * @author Alexander Brennecke
 * @custom.position /F0012/ </br> /F0040/
 */
public class CommunityAuthentification extends CallableAbstract {

	/**
	 * This method is called by the message controller, whenever the message
	 * controller arrives a message with the ID "CommunityAuthentification".
	 */
	@Override
	public void messageArrived(Message message) {
		JSONObject communityJSON = new JSONObject(message.getMessageContent());
		String type = communityJSON.getString(MIDs.TYPE); 
		switch (type) {
		case MIDs.CREATION:
			createNewCommunity(communityJSON);
			break;
		case MIDs.ENTER:
			enterCommunity(communityJSON);
			break;
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
	 * @custom.position /F0040/
	 */
	private void enterCommunity(JSONObject communityJSON) {
		String communityName = communityJSON.getString(MIDs.COMMUNITY_NAME);
		String communityPassword = communityJSON.getString(MIDs.PASSWORD);
		int userID = mesController.getSession().getUserID();
		HashMap<String, Boolean> enterFeedback = DatabaseRequests.enterCommunity(communityName, communityPassword,
				userID);
		Message enterACK = new Message(ServerToClient_MessageIDs.COMMUNITY_AUTHENTIFICATION_ACK);
		JSONObject enterACKJSON = JSONUtils.mapToJSON(enterFeedback);

		updateSessionAndClient();

		enterACKJSON.put(MIDs.TYPE, MIDs.ENTER);
		enterACK.setMessageContent(enterACKJSON);
		mesController.sendMessage(enterACK);
	}

	/**
	 * This function checks, if the CommunityList in the Session object is up to date.
	 */
	private void updateSessionAndClient() {
		int userID = mesController.getSession().getUserID();
		List<Integer>  communityID = getNewCommunityID(userID);
		for(Integer i : communityID){
			updateSession(i);
			sendUpdateToClient(i);
		}
	}

	/**
	 * This method takes the Community with the given ID and sets it to the session.
	 * @param communityID
	 */
	private void updateSession(int communityID) {
		Community community = DatabaseRequests.getCummunityForID(communityID);
		mesController.getSession().addCommunity(community);
	}

	/**
	 * This method compares the communities in the session object with the communities in the database,
	 * @param userID the ID of the user, that has a manager in the compared communities
	 * @return a List of Integers. Each Integer displays the ID of a {@link Community} , that is in the Database but not in the {@link Session}
	 */
	private List<Integer> getNewCommunityID(int userID) {
		Set<Integer> knownIDs = mesController.getSession().getCommunityIDMap().keySet();
		List<Integer> allIDs = DatabaseRequests.getCummunityIDsForUser(userID);

		List<Integer> retval = new ArrayList<Integer>();
		for (Integer i : allIDs) {
			if (!knownIDs.contains(i)) {
				retval.add(i);
			}
		}
		return retval;
	}

	/**
	 * This method creates a new Update Message, containing an Community, for the Client and sends it.
	 * @param communityID The ID of the Community, that will be send to the client.
	 */
	private void sendUpdateToClient(int communityID) {
		Message communityListUpdate = new Message(ServerToClient_MessageIDs.USER_COMMUNITY_LIST);

		JSONObject updateJSON = new JSONObject();
		updateJSON.put(MIDs.TYPE, MIDs.NEW_COMMUNITY);
		Community community = DatabaseRequests.getCummunityForID(communityID);
		updateJSON.put(MIDs.COMMUNITY, community.toJSON());
		communityListUpdate.setMessageContent(updateJSON);
		
		mesController.sendMessage(communityListUpdate);
	}


	/**
	 * This method is called, when the CommunityAuthentification Message has the
	 * type "creation". It uses the database to create a new community, if it is
	 * possible.
	 * 
	 * @param communityJSON
	 *            a JSONObject with all required information to create a new
	 *            community.
	 * @custom.position /F0012/
	 */
	private void createNewCommunity(JSONObject communityJSON) {
		String communityName = communityJSON.getString(MIDs.COMMUNITY_NAME);
		String communityPassword = communityJSON.getString(MIDs.PASSWORD);
		int userID = mesController.getSession().getUserID();
		boolean communityCreated = DatabaseRequests.createNewCommunity(communityName, communityPassword, userID);

		if (communityCreated) {
			ExchangeMarketGenerator.createNewMarket(communityName);
			DatabaseRequests.enterCommunity(communityName, communityPassword, userID);
			updateSessionAndClient();
		}
		Message creationACK = new Message(ServerToClient_MessageIDs.COMMUNITY_AUTHENTIFICATION_ACK);

		JSONObject creationACKJSON = new JSONObject();
		creationACKJSON.put(MIDs.TYPE, MIDs.CREATION);
		creationACKJSON.put(MIDs.CREATED, communityCreated);

		creationACK.setMessageContent(creationACKJSON);

		mesController.sendMessage(creationACK);
	}

}
