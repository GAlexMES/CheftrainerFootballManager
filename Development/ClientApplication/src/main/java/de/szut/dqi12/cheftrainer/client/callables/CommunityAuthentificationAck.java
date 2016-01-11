package de.szut.dqi12.cheftrainer.client.callables;

import javafx.scene.control.Alert.AlertType;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * This class is called when a message with the id "CommunityAuthentificationAck" arrived.
 * @author Alexander Brennecke
 *
 */
public class CommunityAuthentificationAck extends CallableAbstract {

	/**
	 * This method is called by the message controller, when a message with the id "CommunityAuthentificationAck" arrived at the message controller.
	 */
	@Override
	public void messageArrived(Message message) {
		JSONObject authentificationACK = new JSONObject(
				message.getMessageContent());
		String type = authentificationACK.getString(MIDs.TYPE);  
		switch (type){
		case MIDs.CREATION :
			handleCreation(authentificationACK);
			break;
		case MIDs.ENTER:
			handleEnter(authentificationACK);
			break;
		}
	}

	/**
	 * This method is called, when the arrived method has the type "enter".
	 * It shows a Alert Dialog appending on the information in the given JSON.
	 * @param authentificationACK the JSONObject with the required information to display one of the Alerts.
	 */
	private void handleEnter(JSONObject authentificationACK) {
		boolean communityExists = authentificationACK.getBoolean(MIDs.COMMUNITY_EXISTS);
		boolean correctPassword = authentificationACK.getBoolean(MIDs.CORRECT_PASSWORD);
		if (!communityExists
				|| !correctPassword) {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_ENTER_TITLE,
					AlertUtils.COMMUNITY_ENTER_WORKED_NOT_HEAD,
					AlertUtils.COMMUNITY_ENTER_WRONG_AUTHENTIFICATION,
					AlertType.ERROR);
		} else if (!authentificationACK.getBoolean(MIDs.USER_EXISTS)) {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_ENTER_TITLE,
					AlertUtils.COMMUNITY_ENTER_WORKED_NOT_HEAD,
					AlertUtils.COMMUNITY_ENTER_ALREADY_EXIST, AlertType.ERROR);
		} else {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_ENTER_TITLE,
					AlertUtils.COMMUNITY_ENTER_WORKED_HEAD,
					AlertUtils.COMMUNITY_ENTER_WORKED_MESSAGE,
					AlertType.INFORMATION);
			GUIController.getInstance().closeCurrentDialog();
//			UpdateUtils.getCommunityUpdate();
		}
	}

	/**
	 * This method is called, when the arrived method has the type "creation".
	 * It shows a Alert Dialog appending on the information in the given JSON.
	 * @param creationJSON the JSONObject with the required information to display one of the Alerts.
	 */
	private void handleCreation(JSONObject creationJSON) {
		if (creationJSON.getBoolean(MIDs.CREATED)) {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_CREATION_TITLE,
					AlertUtils.COMMUNITY_CREATION_WORKED_HEAD,
					AlertUtils.COMMUNITY_CREATION_WORKED_MESSAGE,
					AlertType.CONFIRMATION);
			GUIController.getInstance().closeCurrentDialog();
//			UpdateUtils.getCommunityUpdate();
		} else {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_CREATION_TITLE,
					AlertUtils.COMMUNITY_CREATION_WORKED_NOT_HEAD,
					AlertUtils.COMMUNITY_CREATION_WORKED_NOT_MESSAGE,
					AlertType.ERROR);
		}
	}
	
	
}
