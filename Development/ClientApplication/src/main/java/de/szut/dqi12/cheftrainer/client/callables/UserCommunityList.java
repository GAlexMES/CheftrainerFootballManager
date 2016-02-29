package de.szut.dqi12.cheftrainer.client.callables;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.CommunitiesController;
import de.szut.dqi12.cheftrainer.client.view.utils.UpdateUtils;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.UserCommunityListMessage;

/**
 * This class is the callable for messages with the id "UserCommunityList".
 * 
 * @author Alexander Brennecke
 *
 */
public class UserCommunityList extends CallableAbstract {

	private final static Logger LOGGER = Logger.getLogger(UserCommunityList.class);

	/**
	 * This method is called, when a new message with the id "UserCommunityList"
	 * arrived at the message controller.
	 */
	@Override
	public void messageArrived(Message message) {
		JSONObject jsonMessage = new JSONObject(message.getMessageContent());
		UserCommunityListMessage uclMessage = new UserCommunityListMessage(jsonMessage);
		switch (uclMessage.getType()) {
		case MIDs.INIT:
			newList(uclMessage);
			UpdateUtils.initUpdateReceived();
			break;
		case MIDs.UPDATE_COMMUNITY:
			updateTransactions(uclMessage);
			break;
		case MIDs.NEW_COMMUNITY:
			addCommunityToList(uclMessage);
			break;
		default:
			LOGGER.error("Undefined message type (" + jsonMessage.getString("type") + ")");
		}
	}

	
	private void updateTransactions(UserCommunityListMessage uclMessage){
		List<Transaction> transactions = uclMessage.getTransactions();
		int communityID = transactions.get(0).getCommunityID();
		Community community = mesController.getSession().getCommunity(communityID);
		community.getMarket().setTransactions(transactions);
	}

	/**
	 * This method adds the community, stored in the given JSONObject. To the
	 * {@link Session} and to the {@link CommunitiesController}.
	 * 
	 * @param message
	 *            a JSONObject, which should contain a JSONObject with the key
	 *            "community".
	 */
	private void addCommunityToList(UserCommunityListMessage uclMessage) {
		Community community = uclMessage.getCommunity();
		community.findeUsersManager(mesController.getSession().getUser().getUserName());
		Controller.getInstance().getSession().addCommunity(community);
	}

	/**
	 * This method is used to parse a {@link JSONArray}, which contains a
	 * {@link JSONObject} with the key "information". This Element should
	 * contain a {@link JSONArray} with information about at least one
	 * {@link Community}
	 * 
	 * @param message
	 *            the {@link JSONObject}, that fits with the conditions given
	 *            above.
	 */
	private void newList(UserCommunityListMessage uclMessage) {
		List<Community> communities = uclMessage.getCommunityList();
		Controller.getInstance().getSession().addCommunities(communities);
	}
}
