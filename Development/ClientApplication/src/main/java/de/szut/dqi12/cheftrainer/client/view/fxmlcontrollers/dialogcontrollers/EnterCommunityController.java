package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;
import de.szut.dqi12.cheftrainer.connectorlib.cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * This class is the controller class for the EnterCommunityDialog.fxml
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
	public void cancel(){
		Stage stage = (Stage)communityNameField.getScene().getWindow();
		stage.close();
	}
	
	/**
	 * THis method is called, when the enter button was pressed.
	 */
	@FXML
	public void enter(){
		TextField[] inputFields = {communityNameField,passwordField};
		List<String> errorList = DialogUtils.checkInputs(inputFields);
		
		if (errorList.size() == 0) {
			createEnterCommunityMessage();
		} else {
			String errorMessage = AlertUtils.WRONG_INPUTS;
			for (String s : errorList) {
				errorMessage += "\n " + s;
			}
			AlertUtils.createSimpleDialog("Creation failed",
					"Something went wrong while of entering the community!",
					errorMessage,
					AlertType.ERROR);
		}
	}
	
	/**
	 * This method is called, when a new community should be created. It creates a message with the required data and sends it to the server.
	 */
	private void createEnterCommunityMessage(){
		Message enterCommunityMessage = new Message(ClientToServer_MessageIDs.COMMUNITY_AUTHENTIFICATION);
		JSONObject enterJSON = new JSONObject();
		enterJSON.put("type", "enter");
		enterJSON.put("communityName", communityNameField.getText());
		String passwordmd5;
		try {
			passwordmd5 = CipherFactory.getMD5(passwordField.getText());
			enterJSON.put("password", passwordmd5);
			enterCommunityMessage.setMessageContent(enterJSON);
			Controller.getInstance().getSession().getClientSocket().sendMessage(enterCommunityMessage);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			AlertUtils.createExceptionDialog(e);
		}
	}
}
