package de.szut.dqi12.cheftrainer.server.usercommunication;


import org.json.JSONArray;
import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;

public class ClientUpdate {

	public static JSONObject createCommunityMessage(int communityID){
		JSONObject retval = new JSONObject();
		Community community = DatabaseRequests.getCummunityForID(communityID);
		retval.put("ID", community.getCommunityID());
		retval.put("Name", community.getName());
		JSONArray managersJSON = new JSONArray();
		for (Manager m : community.getManagers()) {
			managersJSON.put(m.toJSON());
		}
		retval.put("Managers", managersJSON);
		return retval;
	}
	
	
	

}
