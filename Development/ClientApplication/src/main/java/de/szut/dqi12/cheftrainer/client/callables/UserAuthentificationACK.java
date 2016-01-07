package de.szut.dqi12.cheftrainer.client.callables;

import javafx.scene.control.Alert.AlertType;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerManager;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers.LoginController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers.RegistrationController;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.client.view.utils.UpdateUtils;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.AdditionalMessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * This class handles "UserAuthentificationACK" messages, which will be send
 * from the server to the client after a login or a registration.
 * 
 * @author Alexander Brennecke
 *
 */
public class UserAuthentificationACK extends CallableAbstract {

	/**
	 * Is called from the message controller, when a new message with the id
	 * "UserAuthentificationACK" arrived.
	 */
	public void messageArrived(Message message) {
		JSONObject authentificationAck = new JSONObject(
				message.getMessageContent());

		// Checks, if the message is a ACK for the registration or the login.
		String mode = authentificationAck.getString(AdditionalMessageIDs.MODE);
		switch (mode) {
		case AdditionalMessageIDs.REGISTRATION:
			register(authentificationAck);
			break;
		case AdditionalMessageIDs.LOGIN:
			login(authentificationAck);
			break;
		}
	}

	/**
	 * Is called, when the message was a ACK for the login.
	 * 
	 * @param authentificationAck
	 */
	private void login(JSONObject authentificationAck) {
		GUIController guiController = GUIController.getInstance();
		if (authentificationAck.getBoolean("userExist")
				&& authentificationAck.getBoolean("password")) {
			Controller.getInstance().getSession()
					.setUserID(authentificationAck.getInt("UserID"));
			guiController.showMainApplication();
			UpdateUtils.getCommunityUpdate();
		} else if (!authentificationAck.getBoolean("userExist")) {
			AlertUtils.createSimpleDialog("Login failed",
					"Ther occured a problem during your login.",
					AlertUtils.LOGIN_WRONG_USER, AlertType.ERROR);
		} else if (!authentificationAck.getBoolean("password")) {
			AlertUtils.createSimpleDialog("Login failed",
					"Ther occured a problem during your login.",
					AlertUtils.LOGIN_WRONG_PASSWORD, AlertType.ERROR);
		}
		
		ControllerManager.getInstance().onAction(LoginController.ON_ACTION_KEY);
	}

	/**
	 * Is called, when the message was a ACK for the registration.
	 * 
	 * @param authentificationAck
	 */
	private void register(JSONObject authentificationAck) {
		RegistrationController regController = GUIController.getInstance()
				.getGUIInitialator().getLoginController()
				.getRegistrationController();
		// when the registration was possible, the registration dialog will be
		// closed.
		if (authentificationAck.getBoolean("authentificate")) {
			regController.closeDialog();
		}
		// when the registration was not possible, a alert dialog shows a
		// message.
		else {
			String errorMessage = "";
			if (authentificationAck.getBoolean("existUser")
					&& authentificationAck.getBoolean("existEMail")) {
				errorMessage = "Your E-Mail Adress and your user name are already in use.";
			} else if (authentificationAck.getBoolean("existUser")
					&& !authentificationAck.getBoolean("existEMail")) {
				errorMessage = "Your user name is already in use. Please chose a other one.";
			} else if (!authentificationAck.getBoolean("existUser")
					&& authentificationAck.getBoolean("existEMail")) {
				errorMessage = "Your E-Mail is already in use. Do you already have an account?";
			} else {
				errorMessage = "A unknown error occured";
			}
			AlertUtils.createSimpleDialog("Registration error",
					"Something went wrong during your registration",
					errorMessage, AlertType.ERROR);
		}
		ControllerManager.getInstance().onAction(RegistrationController.ON_ACTION_KEY);
	}
}
