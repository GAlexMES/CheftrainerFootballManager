package de.szut.dqi12.cheftrainer.client.callables;


import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.AlertDialog;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.LoginController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.RegistrationController;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * This class handles "UserAuthentificationACK" messages, which will be send from the server to the client after a login or a registration.
 * @author Alexander Brennecke
 *
 */
public class UserAuthentificationACK extends CallableAbstract {
	
	/**
	 * Is called from the message controller, when a new message with the id "UserAuthentificationACK" arrived.
	 */
	public void messageArrived(Message message) {
		JSONObject authentificationAck = new JSONObject(message.getMessageContent());
		
		//Checks, if the message is a ACK for the registration or the login.
		switch(authentificationAck.getString("mode")){
		case "registration":
			register(authentificationAck);
			break;
		case "login":
			login(authentificationAck);
			break;
		}
	}
	
	/**
	 * Is called, when the message was a ACK for the login.
	 * @param authentificationAck
	 */
	private void login(JSONObject authentificationAck){
		GUIController guiController = GUIController.getInstance();
		if(authentificationAck.getBoolean("userExist")&&authentificationAck.getBoolean("password")){
			Controller.getInstance().getSession().setUserID(authentificationAck.getInt("UserID"));
			guiController.showMainApplication();
		}
		else if(!authentificationAck.getBoolean("userExist")){
			LoginController loginController = guiController.getGUIInitialator().getLoginController();
			DialogUtils.showError("Login failed", "Ther occured a problem during your login.", AlertDialog.LOGIN_WRONG_USER);
		}
		else if(!authentificationAck.getBoolean("password")){
			LoginController loginController = guiController.getGUIInitialator().getLoginController();
			DialogUtils.showError("Login failed", "Ther occured a problem during your login.", AlertDialog.LOGIN_WRONG_PASSWORD);
		}
	}
	
	/**
	 * Is called, when the message was a ACK for the registration.
	 * @param authentificationAck
	 */
	private void register(JSONObject authentificationAck){
		RegistrationController regController = GUIController.getInstance().getGUIInitialator().getLoginController().getRegistrationController();
		// when the registration was possible, the registration dialog will be closed.
		if(authentificationAck.getBoolean("authentificate")){
			regController.closeDialog();
		}
		// when the registration was not possible, a alert dialog shows a message.
		else{
			String errorMessage = "";
			if(authentificationAck.getBoolean("existUser")&&authentificationAck.getBoolean("existEMail")){
				errorMessage = "Your E-Mail Adress and your user name are already in use.";
			}
			else if(authentificationAck.getBoolean("existUser")&&!authentificationAck.getBoolean("existEMail")){
				errorMessage = "Your user name is already in use. Please chose a other one.";
			}
			else if(!authentificationAck.getBoolean("existUser")&&authentificationAck.getBoolean("existEMail")){
				errorMessage = "Your E-Mail is already in use. Do you already have an account?";
			}
			else{
				errorMessage = "A unknown error occured";
			}
			DialogUtils.showError("Registration error", "Something went wrong during your registration", errorMessage);
		}
	}
}
