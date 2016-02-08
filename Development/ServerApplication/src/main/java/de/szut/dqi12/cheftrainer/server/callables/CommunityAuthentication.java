package de.szut.dqi12.cheftrainer.server.callables;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.CommunityAutenticationAckMessage;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.CommunityAuthenticationMessage;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.UserCommunityListMessage;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;
import de.szut.dqi12.cheftrainer.server.logic.ExchangeMarketGenerator;

/**
 * This class handles messaged with the id "CommunityAuthentification".
 * 
 * @author Alexander Brennecke
 * @custom.position /F0012/ </br> /F0040/
 */
public class CommunityAuthentication extends CallableAbstract {

	/**
	 * This method is called by the message controller, whenever the message
	 * controller arrives a message with the ID "CommunityAuthentification".
	 */
	@Override
	public void messageArrived(Message message) {
		JSONObject communityJSON = new JSONObject(message.getMessageContent());
		CommunityAuthenticationMessage caMessage = new CommunityAuthenticationMessage(communityJSON);
		switch (caMessage.getType()) {
		case MIDs.CREATION:
			createNewCommunity(caMessage);
			break;
		case MIDs.ENTER:
			enterCommunity(caMessage);
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
	private void enterCommunity(CommunityAuthenticationMessage caMessage) {
		String communityName = caMessage.getName();
		String communityPassword = caMessage.getPassword();
		int userID = mesController.getSession().getUserID();
		
		CommunityAutenticationAckMessage caaMessage = DatabaseRequests.enterCommunity(communityName, communityPassword,
				userID);
		caaMessage.setEnterType();
		updateSessionAndClient();

		mesController.sendMessage(caaMessage);
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
		Community community = DatabaseRequests.getCummunityForID(communityID);
		UserCommunityListMessage uclMessage = new UserCommunityListMessage(MIDs.NEW_COMMUNITY);
		uclMessage.addCommunity(community);
		mesController.sendMessage(uclMessage);
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
	private void createNewCommunity(CommunityAuthenticationMessage caMessage) {
		String communityName = caMessage.getName();
		String communityPassword = caMessage.getPassword();
		
		int userID = mesController.getSession().getUserID();
		boolean communityCreated = DatabaseRequests.createNewCommunity(communityName, communityPassword, userID);

		if (communityCreated) {
			ExchangeMarketGenerator.createNewMarket(communityName);
			DatabaseRequests.enterCommunity(communityName, communityPassword, userID);
			updateSessionAndClient();
		}
		
		CommunityAutenticationAckMessage caaMessage = new CommunityAutenticationAckMessage();
		caaMessage.setCreationType();
		caaMessage.setManagerCreated(communityCreated);

		mesController.sendMessage(caaMessage);
	}

}
