package de.szut.dqi12.cheftrainer.client.callables;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXMLLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.CommunitiesController;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Team;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class UserCommunityList extends CallableAbstract {

	public void messageArrived(Message message) {
		String userName = mesController.getSession().getUser().getUserName();
		JSONArray communityList = new JSONArray(message.getMessageContent());
		List<Community> communities = new ArrayList<>();
		for (int i = 0; i < communityList.length(); i++) {
			Community com = new Community();
			JSONObject communityJSON = new JSONObject(communityList.get(i)
					.toString());
			com.setCommunityID(communityJSON.getInt("ID"));
			com.setName(communityJSON.getString("Name"));
			JSONArray managersJSON = new JSONArray(
					communityJSON.get("Managers").toString());
			com.addManagers(createManagerList(managersJSON));
			com.findeUsersManager(userName);
			communities.add(com);
		}
		Controller.getInstance().getSession().addCommunities(communities);
		FXMLLoader loader = GUIController.getInstance().getCurrentContentLoader();
		try {
			CommunitiesController cc = loader.getController();
			displayCommunities(communities, cc);
		} catch (Exception e) {
		}
	}

	private void displayCommunities(List<Community> communities,
			CommunitiesController cc) {
		List<Team> teamList = new ArrayList<>();
		for (Community c : communities) {
			String name = c.getName();
			double money = c.getUsersManager().getMoney();
			int rang = 0;
			Team t = new Team(name, String.valueOf(money), String.valueOf(rang));
			teamList.add(t);
			System.out.println("added:  "+name+money+rang);
		}
		cc.initTable(teamList);
	}

	private List<Manager> createManagerList(JSONArray managersJSON) {
		List<Manager> retval = new ArrayList<>();
		for (int i = 0; i < managersJSON.length(); i++) {
			JSONObject managerJSON = new JSONObject(managersJSON.get(i).toString());
			String name = managerJSON.getString("Name");
			double money = managerJSON.getDouble("Money");
			int points = managerJSON.getInt("Points");
			Manager manager = new Manager(name, money, points);
			manager.setID(managerJSON.getInt("ID"));
			retval.add(manager);
		}
		return retval;
	}
}
