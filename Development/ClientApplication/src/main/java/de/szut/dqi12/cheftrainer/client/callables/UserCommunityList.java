package de.szut.dqi12.cheftrainer.client.callables;

import java.util.ArrayList;
import java.util.List;


import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.CommunitiesController;
import de.szut.dqi12.cheftrainer.client.view.utils.UpdateUtils;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

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
		switch (jsonMessage.getString("type")) {
		case "init":
			newList(jsonMessage);
			UpdateUtils.initUpdateReceived();
			break;
		case "updateCommunity":
			updateList(jsonMessage);
			break;
		case "newCommunity":
			addCommunityToList(jsonMessage);
			break;
		default:
			LOGGER.error("Undefined message type (" + jsonMessage.getString("type") + ")");
		}
	}

	private void updateList(JSONObject message) {
		// TODO implement!
		System.out.println("Not implemented yet");
	}

	/**
	 * This method adds the community, stored in the given JSONObject. To the
	 * {@link Session} and to the {@link CommunitiesController}.
	 * 
	 * @param message
	 *            a JSONObject, which should contain a JSONObject with the key
	 *            "community".
	 */
	private void addCommunityToList(JSONObject message) {
		Community community = new Community(message.getJSONObject("community"));
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
	private void newList(JSONObject message) {
		String userName = mesController.getSession().getUser().getUserName();
		JSONArray communityList = message.getJSONArray("information");
		List<Community> communities = jsonArrayToCommnityList(communityList, userName);
		Controller.getInstance().getSession().addCommunities(communities);
	}

	/**
	 * This method creates a List>Community< out of a JSON Array. The JSON Array
	 * should have all required data to create a community object with it.
	 * 
	 * @param communityList
	 *            a JSONArray with communities inside it.
	 * @param userName
	 *            the UserName of the registered user.
	 * @return a List with all Communities, that could be created with the
	 *         information in the JSONArray.
	 */
	private List<Community> jsonArrayToCommnityList(JSONArray communityList, String userName) {
		new ArrayList<>();
		List<Community> retval = new ArrayList<>();
		for (int i = 0; i < communityList.length(); i++) {
			JSONObject communityJSON = new JSONObject(communityList.get(i).toString());
			Community com = new Community(communityJSON);
			com.findeUsersManager(userName);
			retval.add(com);
		}
		return retval;
	}
}
