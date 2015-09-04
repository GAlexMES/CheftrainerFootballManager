package de.szut.dqi12.cheftrainer.client.callables;


import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class UserAuthentificationACK extends CallableAbstract {

	
	public void messageArrived(Message message) {
		JSONObject registrationInfo = new JSONObject(message.getMessageContent());
		System.out.println(message.getMessageContent());
	}
	
	public static CallableAbstract newInstance() {
		return new UserAuthentificationACK();
	}
}
