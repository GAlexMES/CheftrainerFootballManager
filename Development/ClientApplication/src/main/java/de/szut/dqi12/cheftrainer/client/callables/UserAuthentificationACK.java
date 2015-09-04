package de.szut.dqi12.cheftrainer.client.callables;


import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.RegistrationController;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class UserAuthentificationACK extends CallableAbstract {
	
	public void messageArrived(Message message) {
		JSONObject authentificationAck = new JSONObject(message.getMessageContent());
		switch(authentificationAck.getString("mode")){
		case "registration":
			register(authentificationAck);
			break;
		case "login":
			login(authentificationAck);
			break;
		}
	}
	
	private void login(JSONObject authentificationAck){
		if(authentificationAck.getBoolean("userExist")&&authentificationAck.getBoolean("password")){
			GUIController.getInstance().showMainApplication();
		}
	}
	
	private void register(JSONObject authentificationAck){
		RegistrationController regController = GUIController.getInstance().getGUIInitialator().getLoginController().getRegistrationController();
		if(authentificationAck.getBoolean("authentificate")){
			regController.closeDialog();
		}
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
			regController.showError("Registration error", "Something went wrong during your registration", errorMessage);
		}
	}
	
	
	public static CallableAbstract newInstance() {
		return new UserAuthentificationACK();
	}
}
