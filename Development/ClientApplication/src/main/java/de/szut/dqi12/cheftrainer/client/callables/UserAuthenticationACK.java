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
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.UserAuthenticationAckMessage;

/**
 * This class handles "UserAuthentificationACK" messages, which will be send
 * from the server to the client after a login or a registration.
 * 
 * @author Alexander Brennecke
 */
public class UserAuthenticationACK extends CallableAbstract {

	/**
	 * Is called from the message controller, when a new message with the id
	 * "UserAuthentificationACK" arrived.
	 */
	public void messageArrived(Message message) {
		JSONObject authentificationAck = new JSONObject(
				message.getMessageContent());

		UserAuthenticationAckMessage uaaMessage= new UserAuthenticationAckMessage(authentificationAck);
		switch (uaaMessage.getType()) {
		case MIDs.REGISTRATION:
			register(uaaMessage);
			break;
		case MIDs.LOGIN:
			login(uaaMessage);
			break;
		}
	}

	/**
	 * Is called, when the message was a ACK for the login.
	 * 
	 * @param authentificationAck
	 */
	private void login(UserAuthenticationAckMessage uaaMessage) {
		GUIController guiController = GUIController.getInstance();
		if (uaaMessage.existsUser()&& uaaMessage.isPasswordCorrect()) {
			Controller.getInstance().getSession().setUserID(uaaMessage.getUserID());
			guiController.showMainApplication();
			UpdateUtils.getCommunityUpdate();
		} else if (!uaaMessage.existsUser()) {
			AlertUtils.createSimpleDialog("Login failed",
					"Ther occured a problem during your login.",
					AlertUtils.LOGIN_WRONG_USER, AlertType.ERROR);
		} else if (!uaaMessage.isPasswordCorrect()) {
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
	private void register(UserAuthenticationAckMessage uaaMessage) {
		RegistrationController regController = GUIController.getInstance()
				.getGUIInitialator().getLoginController()
				.getRegistrationController();
		// when the registration was possible, the registration dialog will be
		// closed.
		if (uaaMessage.isAuthentication()) {
			regController.closeDialog();
		}
		// when the registration was not possible, a alert dialog shows a
		// message.
		else {
			String errorMessage = "";
			if (uaaMessage.existsUser()&& uaaMessage.existsEMail()) {
				errorMessage = "Your E-Mail Adress and your user name are already in use.";
			} else if (uaaMessage.existsUser()
					&& !uaaMessage.existsEMail()) {
				errorMessage = "Your user name is already in use. Please chose a other one.";
			} else if (!uaaMessage.existsUser()&& uaaMessage.existsEMail()) {
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
