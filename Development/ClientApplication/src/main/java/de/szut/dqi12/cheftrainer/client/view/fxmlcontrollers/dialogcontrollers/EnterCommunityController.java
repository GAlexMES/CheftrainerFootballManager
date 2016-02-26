package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.CommunityAuthenticationMessage;

/**
 * This class is the controller class for the EnterCommunityDialog.fxml
 * 
 * @author Alexander Brennecke
 *
 */
public class EnterCommunityController {

	@FXML
	TextField communityNameField;
	@FXML
	PasswordField passwordField;

	/**
	 * This method is called, when the cancel button was pressed
	 */
	@FXML
	public void cancel() {
		Stage stage = (Stage) communityNameField.getScene().getWindow();
		stage.close();
	}

	/**
	 * THis method is called, when the enter button was pressed.
	 */
	@FXML
	public void enter() {
		TextField[] inputFields = { communityNameField, passwordField };
		List<String> errorList = DialogUtils.checkInputs(inputFields);

		if (errorList.size() == 0) {
			createEnterCommunityMessage();
		} else {
			String errorMessage = AlertUtils.WRONG_INPUTS;
			for (String s : errorList) {
				errorMessage += "\n " + s;
			}
			AlertUtils.createSimpleDialog("Erstellung fehlgeschlagen", "Etwas hat nicht funktioniert. Versuchen sie es erneut.", errorMessage, AlertType.ERROR);
		}
	}

	/**
	 * This method is called, when a new community should be created. It creates
	 * a message with the required data and sends it to the server.
	 */
	private void createEnterCommunityMessage() {
		CommunityAuthenticationMessage caMessage = new CommunityAuthenticationMessage(MIDs.ENTER);
		caMessage.setName(communityNameField.getText());
		caMessage.setPassword(passwordField.getText());
		Controller.getInstance().getSession().getClientSocket().sendMessage(caMessage);
	}
}
