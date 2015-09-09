package de.szut.dqi12.cheftrainer.server.callables;

import java.util.HashMap;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.databasecommunication.UserManagement;
import de.szut.dqi12.cheftrainer.server.usercommunication.User;

/**
 * This class is used to handle "UserAuthentification" messages, which were send by a client.
 * @author Alexander Brennecke
 *
 */
public class UserAuthentification extends CallableAbstract {

	private static Controller controller;
	private static UserManagement userManagement;

	/**
	 * Is called from the message controller, when a new message arrived.
	 */
	public void messageArrived(Message message) {
		JSONObject authentification = new JSONObject(
				message.getMessageContent());
		// switches the type of the authentification to "login" or "register".
		switch (authentification.getString("authentificationType")) {
		case "register":
			register(authentification);
			break;
		case "login":
			login(authentification);
			break;
		}
	}
	/**
	 * initializes a controller and UserManagement object.
	 */
	private void initialize() {
		if (controller == null) {
			controller = Controller.getInstance();
		}
		if (userManagement == null) {
			userManagement = new UserManagement(controller.getSQLConnection());
		}
	}

	/**
	 * Is called, when the message was a "register" message
	 * @param registrationInfo JSONObject, including the user data
	 */
	private void register(JSONObject registrationInfo) {
		initialize();
		User newUser = new User();
		newUser.setWithJSON(registrationInfo);
		HashMap<String,Boolean> dbInfo  =userManagement.register(newUser);
		createRegistrationAnswer(dbInfo.get("existEMail"), dbInfo.get("existUser"), dbInfo.get("authentificate"));
	}

	/**
	 * Is called, when the message was a "login" message
	 * @param registrationInfo JSONObject, including the user data
	 */
	public void login(JSONObject loginInfo) {
		initialize();
		User user = new User();
		user.setUserName(loginInfo.getString("username"));
		user.setPassword(loginInfo.getString("password"));
		
		HashMap<String, Boolean> dbInfo = userManagement.login(user);
		createLoginAnswer(dbInfo.get("password"),dbInfo.get("userExist"));
	}

	/**
	 * Creates a USER_AUTHENTIFICATION_ACK message with information about the login status and sends it back to the client.
	 * @param correctPassword true = password was correct
	 * @param existUser true= user exists
	 */
	private void createLoginAnswer(boolean correctPassword, boolean existUser) {
		Message authentificationMessage = new Message(
				ServerToClient_MessageIDs.USER_AUTHENTIFICATION_ACK);
		JSONObject authentificationInfo = new JSONObject();
		authentificationInfo.put("mode", "login");
		authentificationInfo.put("password", correctPassword);
		authentificationInfo.put("userExist", existUser);
		authentificationMessage.setMessageContent(authentificationInfo);
		mesController.sendMessage(authentificationMessage);
	}
	
	/**
	 * * Creates a USER_AUTHENTIFICATION_ACK message wit information about the registration status and sends it back to the client.
	 * @param existEMail true = eMail exists
	 * @param existUser true = user exists
	 * @param registrationCompleted true = registration completed
	 */
	private void createRegistrationAnswer(boolean existEMail, boolean existUser, boolean registrationCompleted) {
		Message answerMessage = new Message(
				ServerToClient_MessageIDs.USER_AUTHENTIFICATION_ACK);
		JSONObject authentification = new JSONObject();
		authentification.put("mode", "registration");
		authentification.put("authentificate", registrationCompleted);
		authentification.put("existUser",existUser);
		authentification.put("existEMail", existEMail);
		answerMessage.setMessageContent(authentification);
		this.mesController.sendMessage(answerMessage);
	}
}
