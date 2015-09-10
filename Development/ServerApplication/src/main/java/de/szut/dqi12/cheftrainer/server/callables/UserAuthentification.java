package de.szut.dqi12.cheftrainer.server.callables;

import java.util.HashMap;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.databasecommunication.ManagerManagement;
import de.szut.dqi12.cheftrainer.server.databasecommunication.UserManagement;

/**
 * This class is used to handle "UserAuthentification" messages, which were send by a client.
 * @author Alexander Brennecke
 *
 */
public class UserAuthentification extends CallableAbstract {

	private static Controller controller;
	private static UserManagement userManagement;
	private static ManagerManagement managerManagement;

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
		if (managerManagement == null) {
			managerManagement= new ManagerManagement(controller.getSQLConnection());
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
		HashMap<String,Boolean> dbInfo  = userManagement.register(newUser);
		createRegistrationAnswer(dbInfo.get("existEMail"), dbInfo.get("existUser"), dbInfo.get("authentificate"));
	}

	/**
	 * Is called, when the message was a "login" message
	 * @param registrationInfo JSONObject, including the user data
	 */
	public void login(JSONObject loginInfo) {
		initialize();
		User loginUser = new User();
		loginUser.setUserName(loginInfo.getString("username"));
		loginUser.setPassword(loginInfo.getString("password"));
		
		HashMap<String, Boolean> dbInfo = userManagement.login(loginUser);
		boolean correctPassword = dbInfo.get("password");
		boolean userExist = dbInfo.get("userExist");
		createLoginAnswer(correctPassword,userExist);
		if(userExist&&correctPassword){
			User databaseUser = userManagement.getUserValues(loginUser.getUserName());
			Session session = new Session();
			session.setUser(databaseUser);
			session.setClientHandler(mesController.getClientHandler());
			controller.getSocketController().addSession(session);
		}
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
