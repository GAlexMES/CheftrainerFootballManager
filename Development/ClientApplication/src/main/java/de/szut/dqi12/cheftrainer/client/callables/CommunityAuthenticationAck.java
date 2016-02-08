package de.szut.dqi12.cheftrainer.client.callables;

import javafx.scene.control.Alert.AlertType;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.CommunityAutenticationAckMessage;

/**
 * This class is called when a message with the id "CommunityAuthentificationAck" arrived.
 * @author Alexander Brennecke
 *
 */
public class CommunityAuthenticationAck extends CallableAbstract {

	/**
	 * This method is called by the message controller, when a message with the id "CommunityAuthentificationAck" arrived at the message controller.
	 */
	@Override
	public void messageArrived(Message message) {
		JSONObject json = new JSONObject(message.getMessageContent());
		CommunityAutenticationAckMessage caaMessage = new CommunityAutenticationAckMessage(json);
		switch (caaMessage.getType()){
		case MIDs.CREATION :
			handleCreation(caaMessage);
			break;
		case MIDs.ENTER:
			handleEnter(caaMessage);
			break;
		}
	}

	/**
	 * This method is called, when the arrived method has the type "enter".
	 * It shows a Alert Dialog appending on the information in the given JSON.
	 * @param authentificationACK the JSONObject with the required information to display one of the Alerts.
	 */
	private void handleEnter(CommunityAutenticationAckMessage caaMessage) {
		if (!caaMessage.communityExists()|| !caaMessage.correctPassword()) {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_ENTER_TITLE,
					AlertUtils.COMMUNITY_ENTER_WORKED_NOT_HEAD,
					AlertUtils.COMMUNITY_ENTER_WRONG_AUTHENTIFICATION,
					AlertType.ERROR);
		} else if (!caaMessage.userExists()) {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_ENTER_TITLE,
					AlertUtils.COMMUNITY_ENTER_WORKED_NOT_HEAD,
					AlertUtils.COMMUNITY_ENTER_ALREADY_EXIST, AlertType.ERROR);
		} else {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_ENTER_TITLE,
					AlertUtils.COMMUNITY_ENTER_WORKED_HEAD,
					AlertUtils.COMMUNITY_ENTER_WORKED_MESSAGE,
					AlertType.INFORMATION);
			GUIController.getInstance().closeCurrentDialog();
		}
	}

	/**
	 * This method is called, when the arrived method has the type "creation".
	 * It shows a Alert Dialog appending on the information in the given JSON.
	 * @param creationJSON the JSONObject with the required information to display one of the Alerts.
	 */
	private void handleCreation(CommunityAutenticationAckMessage caaMessage) {
		if (caaMessage.managerCreated()) {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_CREATION_TITLE,
					AlertUtils.COMMUNITY_CREATION_WORKED_HEAD,
					AlertUtils.COMMUNITY_CREATION_WORKED_MESSAGE,
					AlertType.CONFIRMATION);
			GUIController.getInstance().closeCurrentDialog();
		} else {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_CREATION_TITLE,
					AlertUtils.COMMUNITY_CREATION_WORKED_NOT_HEAD,
					AlertUtils.COMMUNITY_CREATION_WORKED_NOT_MESSAGE,
					AlertType.ERROR);
		}
	}
	
	
}
