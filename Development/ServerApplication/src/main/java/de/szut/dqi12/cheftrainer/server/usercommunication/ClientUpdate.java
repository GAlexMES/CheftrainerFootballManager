package de.szut.dqi12.cheftrainer.server.usercommunication;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;

public class ClientUpdate {

	public static JSONObject createCommunityMessage(int communityID){
		JSONObject retval = new JSONObject();
		Community community =DatabaseRequests.getCummunityForID(communityID);
		retval.put("ID", community.getCommunityID());
		retval.put("Name", community.getName());
		JSONArray managersJSON = new JSONArray();
		for (Manager m : community.getManagers()) {
			managersJSON.put(managerToJson(m));
		}
		retval.put("Managers", managersJSON);
		retval.put("ExchangeMarket",community.getMarket().toJSON());
		return retval;
	}
	
	
	/**
	 * This method creates a JSONObject out of a Manager Object
	 * @param m the manager object, that should be transformed to a JSON
	 * @return the JSONObject witl all information out of the manager object.
	 */
	private static JSONObject managerToJson(Manager m) {
		JSONObject managerJSON = new JSONObject();
		managerJSON.put("Points", m.getPoints());
		managerJSON.put("Money", m.getMoney());
		managerJSON.put("Name", m.getName());
		managerJSON.put("ID", m.getID());
		managerJSON.put("Team",teamToJson(m.getPlayers()));
		managerJSON.put("Formation", m.getFormation().toJSON());
		return managerJSON;
	}
	
	private static JSONArray teamToJson(List<Player> playerList){
		JSONArray teamJSON = new JSONArray();
		for(Player p : playerList){
			teamJSON.put(p.toJSON());
		}
		return teamJSON;
	}
}
