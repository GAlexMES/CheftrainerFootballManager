package de.szut.dqi12.cheftrainer.client.view.utils;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class UpdateUtils {

	public static void getCommunityUpdate() {
		JSONObject messageContent = new JSONObject();
		messageContent.put("update", "CommunityList");
		Message updateMessage = new Message(
				ClientToServer_MessageIDs.REQUEST_UPDATE);
		updateMessage.setMessageContent(messageContent);
		Controller.getInstance().getSession().getClientSocket()
				.sendMessage(updateMessage);
	}
}
