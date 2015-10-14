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
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.ManagerTeam;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * This class is the callable for messages with the id "UserCommunityList".
 * @author Alexander Brennecke
 *
 */
public class UserCommunityList extends CallableAbstract {

	private final static Logger LOGGER = Logger.getLogger(UserCommunityList.class);
	
	
	/**
	 * This method is called, when a new message with the id "UserCommunityList" arrived at the message controller.
	 */
	@Override
	public void messageArrived(Message message) {
		JSONObject jsonMessage = new JSONObject(message.getMessageContent()); 
		switch(jsonMessage.getString("type")){
		case "init": newList(jsonMessage); break;
		case "updateCommunity": updateList(jsonMessage); break;
		case "newCommunity": addCommunityToList(jsonMessage); break;
		default: 
			LOGGER.error("Undefined message type ("+jsonMessage.getString("type")+")");
		}
	}
	
	private void updateList(JSONObject message){
		System.out.println("lol");
	}
	
	private void addCommunityToList(JSONObject message){
		Community community = jsonToCommunity(message.getJSONObject("community"));
		Controller.getInstance().getSession().addCommunity(community);
		updateView();
	}
	
	private void newList(JSONObject message){
		String userName = mesController.getSession().getUser().getUserName();
		JSONArray communityList = message.getJSONArray("information");
		List<Community> communities = jsonArrayToCommnityList(communityList,userName);
		Controller.getInstance().getSession().addCommunities(communities);
		updateView();
	}
	
	private void updateView(){
		FXMLLoader loader = GUIController.getInstance().getCurrentContentLoader();
		List<Community> communities = Controller.getInstance().getSession().getCommunities();
		try {
			CommunitiesController cc = loader.getController();
			displayCommunities(communities, cc);
		} catch (Exception e) {
		}
	}
	
	/**
	 * This method creates a List>Community< out of a JSON Array. The JSON Array should have all required data to create a community object with it.
	 * @param communityList a JSONArray with communities inside it.
	 * @param userName the UserName of the registered user.
	 * @return a List with all Communities, that could be created with the information in the JSONArray.
	 */
	private List<Community> jsonArrayToCommnityList(JSONArray communityList, String userName){
		new ArrayList<>();
		List<Community> retval = new ArrayList<>();
		for (int i = 0; i < communityList.length(); i++) {
			Community com = jsonToCommunity(new JSONObject(communityList.get(i).toString()));
			com.findeUsersManager(userName);
			retval.add(com);
			
			//TODO: remove wenn funzt
//			Community com = new Community();
//			JSONObject communityJSON = new JSONObject(communityList.get(i)
//					.toString());
//			com.setCommunityID(communityJSON.getInt("ID"));
//			com.setName(communityJSON.getString("Name"));
//			JSONArray managersJSON = new JSONArray(
//					communityJSON.get("Managers").toString());
//			com.addManagers(createManagerList(managersJSON));
//			com.findeUsersManager(userName);
//			retval.add(com);
		}
		return retval;
	}
	
	private Community jsonToCommunity(JSONObject communityJSON){
		Community retval = new Community();
		try{
		retval.setCommunityID(communityJSON.getInt("ID"));
		}
		catch(org.json.JSONException jse){
			System.out.println("lol");
		}
		retval.setName(communityJSON.getString("Name"));
		JSONArray managersJSON = new JSONArray(
				communityJSON.get("Managers").toString());
		retval.addManagers(createManagerList(managersJSON));
		return retval;
	}

	/**
	 * Displays the given list to the table of communities in the communities frame
	 * @param communities the List of the Communities, that should be displayed.
	 * @param cc the CommunitiesController, which is the Controller of the fxml, that should be updated.
	 */
	private void displayCommunities(List<Community> communities,
			CommunitiesController cc) {
		List<ManagerTeam> teamList = new ArrayList<>();
		for (Community c : communities) {
			String name = c.getName();
			double money = c.getUsersManager().getMoney();
			int rang = 0;
			ManagerTeam t = new ManagerTeam(name, String.valueOf(money),String.valueOf(rang));
			teamList.add(t);
		}
		cc.reloadTable(teamList);
	}

	/**
	 * This method creates a List>Manager< out of the given JSONArray.
	 * @param managersJSON a JSONArray with all required information to create a Manager object out of it.
	 * @return a List with all Managers in it, that could be created with the given JSONArray
	 */
	private List<Manager> createManagerList(JSONArray managersJSON) {
		List<Manager> retval = new ArrayList<>();
		for (int i = 0; i < managersJSON.length(); i++) {
			JSONObject managerJSON = new JSONObject(managersJSON.get(i).toString());
			String name = managerJSON.getString("Name");
			double money = managerJSON.getDouble("Money");
			int points = managerJSON.getInt("Points");
			Manager manager = new Manager(name, money, points);
			manager.setID(managerJSON.getInt("ID"));
			JSONArray managersTeam = managerJSON.getJSONArray("Team");
			List<Player> playerList = new ArrayList<>();
			for (int m = 0; m < managersTeam.length(); m++) {
				  JSONObject playerJSON = managersTeam.getJSONObject(i);
				  playerList.add(new Player(playerJSON));
			}
			manager.addPlayer(playerList);
			retval.add(manager);
		}
		return retval;
	}
}
