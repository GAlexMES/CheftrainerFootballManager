package de.szut.dqi12.cheftrainer.client.view.utils;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * This class is used to send update Messages to the server.
 * 
 * @author Alexander Brennecke
 *
 */
public class UpdateUtils {

	public static boolean init_update = false;
	

	/**
	 * Requests the actual list of Communities Server will send a
	 * UserCommunityList Message
	 */
	public static void getCommunityUpdate() {
		if (!init_update) {
			JSONObject messageContent = new JSONObject();
			messageContent.put(MIDs.UPDATE, MIDs.COMMUNITY_LIST);
			Message updateMessage = new Message(
					ClientToServer_MessageIDs.REQUEST_UPDATE);
			updateMessage.setMessageContent(messageContent);
			Controller.getInstance().getSession().getClientSocket()
					.sendMessage(updateMessage);
		}
	}
	
	public static void initUpdateReceived(){
		init_update = true;
	}
}
