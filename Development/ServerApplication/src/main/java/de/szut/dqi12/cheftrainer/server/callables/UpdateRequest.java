package de.szut.dqi12.cheftrainer.server.callables;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class UpdateRequest extends CallableAbstract {

	public void messageArrived(Message message) {
		JSONObject messageContent = new JSONObject(message.getMessageContent());
		String update = messageContent.getString("update");
		switch (update) {
		case "CommunityList":
			sendCommunityUpdate();
			break;
		}
	}

	private void sendCommunityUpdate() {
		HashMap<Integer, Community> communityMap = mesController.getSession()
				.getCommunityMap();
		Message comminityMessage = new Message(
				ServerToClient_MessageIDs.USER_COMMUNITY_LIST);
		JSONArray communityListJSON = new JSONArray();
		for (Integer i : communityMap.keySet()) {
			JSONObject communityJSON = new JSONObject();
			Community currentCommunity = communityMap.get(i);
			communityJSON.put("ID", currentCommunity.getCommunityID());
			communityJSON.put("Name", currentCommunity.getName());
			JSONArray managersJSON = new JSONArray();
			for (Manager m : currentCommunity.getManagers()) {
				managersJSON.put(managerToJson(m));
			}
			communityJSON.put("Managers", managersJSON);
			communityListJSON.put(communityJSON);
		}
		comminityMessage.setMessageContent(communityListJSON.toString());
		mesController.sendMessage(comminityMessage);
	}

	private JSONObject managerToJson(Manager m) {
		JSONObject managerJSON = new JSONObject();
		managerJSON.put("Points", m.getPoints());
		managerJSON.put("Money", m.getMoney());
		managerJSON.put("Name", m.getName());
		managerJSON.put("ID", m.getID());
		return managerJSON;
	}
}
