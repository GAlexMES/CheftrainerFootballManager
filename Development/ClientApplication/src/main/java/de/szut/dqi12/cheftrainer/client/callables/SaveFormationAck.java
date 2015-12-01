package de.szut.dqi12.cheftrainer.client.callables;

import javafx.scene.control.Alert.AlertType;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerManager;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.LineUpController;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class SaveFormationAck extends CallableAbstract {

	@Override
	public void messageArrived(Message message) {
		JSONObject saveFormationAck = new JSONObject(message.getMessageContent());
		boolean successful = saveFormationAck.getBoolean("successful");
		if(!successful){
			AlertUtils.createSimpleDialog("Saved failed",
					"Ther occured a problem!.",
					AlertUtils.FORMATION_NOT_SAVED, AlertType.ERROR);
		}
		else{
			AlertUtils.createSimpleDialog("Successful saved",
					"Everything is okay!.",
					AlertUtils.FORMATION_SAVED, AlertType.CONFIRMATION);
		}
		
		ControllerManager.getInstance().onAction(LineUpController.RESET_MANAGER,successful);
	}
}
