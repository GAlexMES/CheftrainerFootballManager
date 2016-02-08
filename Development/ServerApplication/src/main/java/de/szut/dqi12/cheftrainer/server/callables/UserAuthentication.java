package de.szut.dqi12.cheftrainer.server.callables;

import java.util.HashMap;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.UserAuthenticationAckMessage;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.UserAuthenticationMessage;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;

/**
 * This class is used to handle "UserAuthentication" messages, which were send
 * by a client.
 * 
 * @author Alexander Brennecke
 * @custom.position /F0011/ </br> /F0020/
 */
public class UserAuthentication extends CallableAbstract {

	private static Controller controller;

	/**
	 * Is called from the message controller, when a new message arrived.
	 */
	public void messageArrived(Message message) {
		JSONObject authentification = new JSONObject(message.getMessageContent());
		UserAuthenticationMessage uaMessage = new UserAuthenticationMessage(authentification);
		// switches the type of the authentication to "login" or "register".
		switch (uaMessage.getAuthentificationType()) {
		case MIDs.REGISTRATION:
			register(uaMessage);
			break;
		case MIDs.LOGIN:
			login(uaMessage);
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
	private void register(UserAuthenticationMessage uaMessage) {
		initialize();
		User newUser = uaMessage.getUser();
		HashMap<String, Boolean> dbInfo = DatabaseRequests.registerNewUser(newUser);
		createRegistrationAnswer(dbInfo.get(MIDs.EMAIL_EXISTS), dbInfo.get(MIDs.USER_EXISTS), dbInfo.get(MIDs.AUTHENTICATE));
	}

	/**
	 * Is called, when the message was a "login" message
	 * @param uaMessage a {@link UserAuthenticationMessage}, where at least the user is set
	 * 
	 * @custom.position /F0020/
	 */
	public void login(UserAuthenticationMessage uaMessage) {
		initialize();
		User loginUser = uaMessage.getUser();

		HashMap<String, Boolean> dbInfo = DatabaseRequests.loginUser(loginUser);
		boolean correctPassword = dbInfo.get(MIDs.PASSWORD);
		boolean userExist = dbInfo.get("userExist");
		if (userExist && correctPassword) {
			User databaseUser = DatabaseRequests.getUserData(loginUser.getUserName());
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
		UserAuthenticationAckMessage uaaMessage = new UserAuthenticationAckMessage(MIDs.LOGIN);
		uaaMessage.setCorrectPassword(correctPassword);
		uaaMessage.setUserExists(existUser);
		if (correctPassword && existUser) {
			int userID = mesController.getSession().getUserID();
			uaaMessage.setUserID(userID);
		}
		mesController.sendMessage(uaaMessage);
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
	private void createRegistrationAnswer(boolean existEMail, boolean existUser, boolean registrationCompleted) {
		UserAuthenticationAckMessage uaaMessage = new UserAuthenticationAckMessage(MIDs.REGISTRATION);
		uaaMessage.setAuthentication(registrationCompleted);
		uaaMessage.setUserExists(existUser);
		uaaMessage.setEMailExists(existEMail);
		this.mesController.sendMessage(uaaMessage);
	}
}
