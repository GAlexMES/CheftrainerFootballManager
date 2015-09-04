package de.szut.dqi12.cheftrainer.server.callables;

import java.util.HashMap;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.Controller;
import de.szut.dqi12.cheftrainer.server.databasecommunication.UserManagement;
import de.szut.dqi12.cheftrainer.server.usercommunication.User;

public class UserRegistration extends CallableAbstract {

	private static Controller controller;
	private static UserManagement userManagement;
	
	public void messageArrived(Message message) {
		initialize();
		JSONObject registrationInfo = new JSONObject(message.getMessageContent());
		User newUser = new User();
		newUser.setWithJSON(registrationInfo);
		createAnswer(userManagement.register(newUser));
	}
	
	private void initialize(){
		if(controller==null){
			controller = Controller.getInstance();
		}
		if(userManagement==null){
			userManagement = new UserManagement(controller.getSQLConnection());
		}
	}
	
	private void createAnswer(HashMap<String,Boolean> answer){
		Message answerMessage = new Message(ServerToClient_MessageIDs.USER_AUTHENTIFICATION_ACK);
		JSONObject authentification = new JSONObject();
		authentification.put("mode", "registration");
		authentification.put("authentificate", answer.get("authentificate"));
		authentification.put("existUser", answer.get("existUser"));
		authentification.put("existEMail", answer.get("existEMail"));
		answerMessage.setMessageContent(authentification);
		this.mesController.sendMessage(answerMessage);
	}

}
