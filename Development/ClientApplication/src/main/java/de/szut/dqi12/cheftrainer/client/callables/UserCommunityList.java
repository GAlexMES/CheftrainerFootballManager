package de.szut.dqi12.cheftrainer.client.callables;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXMLLoader;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.CommunitiesController;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Formation;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.FormationFactory;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * This class is the callable for messages with the id "UserCommunityList".
 * 
 * @author Alexander Brennecke
 *
 */
public class UserCommunityList extends CallableAbstract {

	private final static Logger LOGGER = Logger
			.getLogger(UserCommunityList.class);

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
			break;
		case "updateCommunity":
			updateList(jsonMessage);
			break;
		case "newCommunity":
			addCommunityToList(jsonMessage);
			break;
		default:
			LOGGER.error("Undefined message type ("
					+ jsonMessage.getString("type") + ")");
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
		Community community = jsonToCommunity(message
				.getJSONObject("community"));
		community.findeUsersManager(mesController.getSession().getUser()
				.getUserName());
		;
		Controller.getInstance().getSession().addCommunity(community);
		addCommunityToView(community);
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
		List<Community> communities = jsonArrayToCommnityList(communityList,
				userName);
		Controller.getInstance().getSession().addCommunities(communities);
		communities.forEach(c -> addCommunityToView(c));
	}

	/**
	 * Displays the given list to the table of communities in the communities
	 * frame
	 * 
	 * @param communities
	 *            the List of the Communities, that should be displayed.
	 * 
	 */

	private void addCommunityToView(Community community) {
		FXMLLoader loader = GUIController.getInstance()
				.getCurrentContentLoader();
		CommunitiesController cc = loader.getController();
		String name = community.getName();
		// double money = community.getUsersManager().getMoney();
		double teamWorth = community.getUsersManager().getTeamWorth();
		// TODO: RANG!!
		int rang = 0;
		cc.addRow(name, teamWorth, rang);

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
	private List<Community> jsonArrayToCommnityList(JSONArray communityList,
			String userName) {
		new ArrayList<>();
		List<Community> retval = new ArrayList<>();
		for (int i = 0; i < communityList.length(); i++) {
			Community com = jsonToCommunity(new JSONObject(communityList.get(i)
					.toString()));
			com.findeUsersManager(userName);
			retval.add(com);
		}
		return retval;
	}

	/**
	 * This method parses a {@link JSONObject} to a {@link Community} object.
	 * Following keys must be available: <li>ID -> Int <li>Name -> String <li>
	 * Managers -> {@link JSONArray}
	 * 
	 * @param communityJSON
	 *            a {@link JSONObject}, that contains all of the keys above.
	 * @return a {@link Community} object, created out of the data, given in the
	 *         communityJSON.
	 */
	private Community jsonToCommunity(JSONObject communityJSON) {
		Community retval = new Community();
		retval.setCommunityID(communityJSON.getInt("ID"));
		retval.setName(communityJSON.getString("Name"));
		JSONArray managersJSON = communityJSON.getJSONArray("Managers");
		retval.addManagers(createManagerList(managersJSON));
		return retval;
	}

	/**
	 * This method creates a List>Manager< out of the given JSONArray.
	 * 
	 * @param managersJSON
	 *            a JSONArray with all required information to create a Manager
	 *            object out of it.
	 * @return a List with all Managers in it, that could be created with the
	 *         given JSONArray
	 */
	private List<Manager> createManagerList(JSONArray managersJSON) {
		List<Manager> retval = new ArrayList<>();
		for (int i = 0; i < managersJSON.length(); i++) {
			JSONObject managerJSON = new JSONObject(managersJSON.get(i)
					.toString());
			String name = managerJSON.getString("Name");
			double money = new Double(managerJSON.getDouble("Money"));
			int points = managerJSON.getInt("Points");
			Manager manager = new Manager(name, money, points);
			JSONObject formationJSON = managerJSON.getJSONObject("Formation");
			manager.setFormation(new Formation(formationJSON));
			manager.setID(managerJSON.getInt("ID"));
			JSONArray managersTeam = managerJSON.getJSONArray("Team");
			List<Player> playerList = new ArrayList<>();
			for (int m = 0; m < managersTeam.length(); m++) {
				JSONObject playerJSON = managersTeam.getJSONObject(m);
				playerList.add(new Player(playerJSON));
			}
			manager.addPlayer(playerList);
			retval.add(manager);
		}
		return retval;
	}
}
