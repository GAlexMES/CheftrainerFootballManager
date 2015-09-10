package de.szut.dqi12.cheftrainer.client.callables;

import org.json.JSONArray;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class UserCommunityList extends CallableAbstract{
	
	public void messageArrived(Message message) {
		JSONArray communityList = new JSONArray(message.getMessageContent());
		System.out.println(communityList.toString());
	}
}
