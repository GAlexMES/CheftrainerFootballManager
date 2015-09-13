package de.szut.dqi12.cheftrainer.client.callables;

import javafx.scene.control.Alert.AlertType;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.client.view.utils.UpdateUtils;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class CommunityAuthentificationAck extends CallableAbstract {

	@Override
	public void messageArrived(Message message) {
		JSONObject authentificationACK = new JSONObject(
				message.getMessageContent());
		switch (authentificationACK.getString("type")) {
		case "creation":
			handleCreation(authentificationACK);
			break;
		case "enter":
			handleEnter(authentificationACK);
			break;
		}
	}

	private void handleEnter(JSONObject authentificationACK) {
		if (!authentificationACK.getBoolean("existCommunity")
				|| !authentificationACK.getBoolean("correctPassword")) {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_ENTER_TITLE,
					AlertUtils.COMMUNITY_ENTER_WORKED_NOT_HEAD,
					AlertUtils.COMMUNITY_ENTER_WRONG_AUTHENTIFICATION,
					AlertType.ERROR);
		} else if (!authentificationACK.getBoolean("userDoesNotExist")) {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_ENTER_TITLE,
					AlertUtils.COMMUNITY_ENTER_WORKED_NOT_HEAD,
					AlertUtils.COMMUNITY_ENTER_ALREADY_EXIST, AlertType.ERROR);
		} else {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_ENTER_TITLE,
					AlertUtils.COMMUNITY_ENTER_WORKED_HEAD,
					AlertUtils.COMMUNITY_ENTER_WORKED_MESSAGE,
					AlertType.INFORMATION);
			GUIController.getInstance().closeCurrentDialog();
			updateTable();
		}
	}

	private void handleCreation(JSONObject creationJSON) {
		if (creationJSON.getBoolean("created")) {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_CREATION_TITLE,
					AlertUtils.COMMUNITY_CREATION_WORKED_HEAD,
					AlertUtils.COMMUNITY_CREATION_WORKED_MESSAGE,
					AlertType.CONFIRMATION);
			GUIController.getInstance().closeCurrentDialog();
			updateTable();
		} else {
			AlertUtils.createSimpleDialog(AlertUtils.COMMUNITY_CREATION_TITLE,
					AlertUtils.COMMUNITY_CREATION_WORKED_NOT_HEAD,
					AlertUtils.COMMUNITY_CREATION_WORKED_NOT_MESSAGE,
					AlertType.ERROR);
		}
	}

	private void updateTable() {
		UpdateUtils.getCommunityUpdate();
	}
}
