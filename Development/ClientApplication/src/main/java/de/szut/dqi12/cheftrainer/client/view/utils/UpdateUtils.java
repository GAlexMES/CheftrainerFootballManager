package de.szut.dqi12.cheftrainer.client.view.utils;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * This class is used  to send update Messages to the server.
 * @author Alexander Brennecke
 *
 */
public class UpdateUtils {

	/**
	 * Requests the actual list of Communities
	 * Server will send a UserCommunityList Message
	 */
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
