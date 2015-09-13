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
 * This class is the Controller for the CreateCommunityDialog.
 * @author Alexander Brennecke
 *
 */
public class CreateCommunityController {

	@FXML
	TextField communityNameField;
	@FXML
	PasswordField passwordField;
	@FXML
	PasswordField passwordConfirmationField;
	
	/**
	 * This method is called, when the cancel button was pressed.
	 */
	@FXML
	public void cancel(){
		Stage stage = (Stage)communityNameField.getScene().getWindow();
		stage.close();
	}
	
	/**
	 * This method is called, when the register button was pressed.
	 */
	@FXML
	public void create(){
		TextField[] inputFields = {communityNameField,passwordField,passwordConfirmationField};
		List<String> errorList = DialogUtils.checkInputs(inputFields);
		
		// Checks if the passwords are identical
		if (!(passwordField.getText().equals(passwordConfirmationField
				.getText()))) {
			passwordField.setText("");
			passwordConfirmationField.setText("");
			passwordField.setStyle("-fx-text-box-border: red;");
			passwordConfirmationField.setStyle("-fx-text-box-border: red;");
			errorList.add("Passwords are not the same");
		}
		
		if (errorList.size() == 0) {
			createNewCommunityMessage();
		} else {
			String errorMessage = AlertUtils.WRONG_INPUTS;
			for (String s : errorList) {
				errorMessage += "\n " + s;
			}
			AlertUtils.createSimpleDialog("Creation failed",
					"Something went wrong during the community creation!",
					errorMessage,
					AlertType.ERROR);
		}
	}
	
	/**
	 * Creates a message with the required data to create a new community and sends it to the server.
	 */
	private void createNewCommunityMessage(){
		Message communityMessage = new Message(ClientToServer_MessageIDs.COMMUNITY_AUTHENTIFICATION);
		JSONObject communitJSON = new JSONObject();
		communitJSON.put("type", "creation");
		String passwordMD5;
		try {
			passwordMD5 = CipherFactory.getMD5(passwordField.getText());
			communitJSON.put("communityName", communityNameField.getText());
			communitJSON.put("communityPassword", passwordMD5);
			communityMessage.setMessageContent(communitJSON);
			Controller.getInstance().getSession().getClientSocket().sendMessage(communityMessage);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			AlertUtils.createExceptionDialog(e);
		}
	}
}
