package de.szut.dqi12.cheftrainer.server.callables;

import java.util.HashMap;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;

/**
 * This class is used to handle "UserAuthentification" messages, which were send
 * by a client.
 * 
 * @author Alexander Brennecke
 * @custom.position /F0011/ </br> /F0020/
 */
public class UserAuthentification extends CallableAbstract {

	private static Controller controller;

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
		controller = Controller.getInstance();
	}

	/**
	 * Is called, when the message was a "register" message
	 * 
	 * @param registrationInfo
	 *            JSONObject, including the user data
	 * @custom.position /F0011/
	 */
	private void register(JSONObject registrationInfo) {
		initialize();
		User newUser = new User();
		newUser.setWithJSON(registrationInfo);
		HashMap<String, Boolean> dbInfo = DatabaseRequests.registerNewUser(newUser);
		createRegistrationAnswer(dbInfo.get("existEMail"),
				dbInfo.get("existUser"), dbInfo.get("authentificate"));
	}

	/**
	 * Is called, when the message was a "login" message
	 * 
	 * @param loginInfo
	 *            JSONObject, including the user data
	 * @custom.position /F0020/
	 */
	public void login(JSONObject loginInfo) {
		initialize();
		User loginUser = new User();
		String username = loginInfo.getString(MIDs.USERNAME);
		String password = loginInfo.getString(MIDs.PASSWORD);
		loginUser.setUserName(username);
		loginUser.setPassword(password);

		HashMap<String, Boolean> dbInfo = DatabaseRequests.loginUser(loginUser);
		boolean correctPassword = dbInfo.get(MIDs.PASSWORD);
		boolean userExist = dbInfo.get("userExist");
		if (userExist && correctPassword) {
			User databaseUser = DatabaseRequests.getUserData(loginUser
					.getUserName());
			Session session = new Session();
			session.setUser(databaseUser);
			session.setUserID(databaseUser.getUserID());
			int userID = databaseUser.getUserID();
			session.addCommunities(DatabaseRequests.getCummunitiesForUser(userID));
			session.setClientHandler(mesController.getClientHandler());
			mesController.setSession(session);
			controller.getSocketController().addSession(session);
		}

		createLoginAnswer(correctPassword, userExist);
	}

	/**
	 * Creates a USER_AUTHENTIFICATION_ACK message with information about the
	 * login status and sends it back to the client.
	 * 
	 * @param correctPassword
	 *            true = password was correct
	 * @param existUser
	 *            true= user exists
	 * @custom.position /F0020/
	 */
	private void createLoginAnswer(boolean correctPassword, boolean existUser) {
		Message authentificationMessage = new Message(
				ServerToClient_MessageIDs.USER_AUTHENTIFICATION_ACK);
		JSONObject authentificationInfo = new JSONObject();
		authentificationInfo.put(MIDs.MODE, MIDs.LOGIN);
		authentificationInfo.put(MIDs.PASSWORD, correctPassword);
		authentificationInfo.put(MIDs.USER_EXISTS, existUser);
		if (correctPassword && existUser) {
			authentificationInfo.put(MIDs.USER_ID, mesController.getSession()
					.getUserID());
		}
		authentificationMessage.setMessageContent(authentificationInfo);
		mesController.sendMessage(authentificationMessage);
	}

	/**
	 * * Creates a USER_AUTHENTIFICATION_ACK message wit information about the
	 * registration status and sends it back to the client.
	 * 
	 * @param existEMail
	 *            true = eMail exists
	 * @param existUser
	 *            true = user exists
	 * @param registrationCompleted
	 *            true = registration completed
	 * @custom.position /F0011/
	 */
	private void createRegistrationAnswer(boolean existEMail,
			boolean existUser, boolean registrationCompleted) {
		Message answerMessage = new Message(
				ServerToClient_MessageIDs.USER_AUTHENTIFICATION_ACK);
		JSONObject authentification = new JSONObject();
		authentification.put(MIDs.MODE, MIDs.REGISTRATION);
		authentification.put(MIDs.AUTHENTIFICATE, registrationCompleted);
		authentification.put(MIDs.USER_EXISTS, existUser);
		authentification.put(MIDs.EMAIL_EXISTS, existEMail);
		answerMessage.setMessageContent(authentification);
		this.mesController.sendMessage(answerMessage);
	}
}
