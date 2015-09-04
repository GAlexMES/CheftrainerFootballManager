package de.szut.dqi12.cheftrainer.server.callables;

import java.util.HashMap;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.databasecommunication.UserManagement;
import de.szut.dqi12.cheftrainer.server.usercommunication.User;

public class UserLogin extends CallableAbstract{
	private static Controller controller;
	private static UserManagement userManagement;

	public void messageArrived(Message message) {
		initialize();
		JSONObject loginInfo = new JSONObject(message.getMessageContent());
		User user = new User();
		user.setUserName(loginInfo.getString("username"));
		user.setPassword(loginInfo.getString("password"));
		handleLogin(userManagement.login(user));
	}
	
	private void handleLogin(HashMap<String,Boolean> dbInfo){
		Message authentificationMessage = new Message(ServerToClient_MessageIDs.USER_AUTHENTIFICATION_ACK);
		JSONObject authentificationInfo = new JSONObject();
		authentificationInfo.put("mode", "login");
		authentificationInfo.put("password", dbInfo.get("password"));
		authentificationInfo.put("userExist",dbInfo.get("userExist"));
		authentificationMessage.setMessageContent(authentificationInfo);
		mesController.sendMessage(authentificationMessage);
	}

	private void initialize() {
		if (controller == null) {
			controller = Controller.getInstance();
		}
		if (userManagement == null) {
			userManagement = new UserManagement(controller.getSQLConnection());
		}
	}

}
