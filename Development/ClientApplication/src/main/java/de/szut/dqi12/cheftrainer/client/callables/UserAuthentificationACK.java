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
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
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
		String mode = authentificationAck.getString(MIDs.MODE);
		switch (mode) {
		case MIDs.REGISTRATION:
			register(authentificationAck);
			break;
		case MIDs.LOGIN:
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
		if (authentificationAck.getBoolean(MIDs.USER_EXISTS)
				&& authentificationAck.getBoolean(MIDs.PASSWORD)) {
			Controller.getInstance().getSession()
					.setUserID(authentificationAck.getInt(MIDs.USER_ID));
			guiController.showMainApplication();
			UpdateUtils.getCommunityUpdate();
		} else if (!authentificationAck.getBoolean(MIDs.USER_EXISTS)) {
			AlertUtils.createSimpleDialog("Login failed",
					"Ther occured a problem during your login.",
					AlertUtils.LOGIN_WRONG_USER, AlertType.ERROR);
		} else if (!authentificationAck.getBoolean(MIDs.PASSWORD)) {
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
		if (authentificationAck.getBoolean(MIDs.AUTHENTIFICATE)) {
			regController.closeDialog();
		}
		// when the registration was not possible, a alert dialog shows a
		// message.
		else {
			String errorMessage = "";
			if (authentificationAck.getBoolean(MIDs.USER_EXISTS)
					&& authentificationAck.getBoolean(MIDs.EMAIL_EXISTS)) {
				errorMessage = "Your E-Mail Adress and your user name are already in use.";
			} else if (authentificationAck.getBoolean(MIDs.USER_EXISTS)
					&& !authentificationAck.getBoolean(MIDs.EMAIL_EXISTS)) {
				errorMessage = "Your user name is already in use. Please chose a other one.";
			} else if (!authentificationAck.getBoolean(MIDs.USER_EXISTS)
					&& authentificationAck.getBoolean(MIDs.EMAIL_EXISTS)) {
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
