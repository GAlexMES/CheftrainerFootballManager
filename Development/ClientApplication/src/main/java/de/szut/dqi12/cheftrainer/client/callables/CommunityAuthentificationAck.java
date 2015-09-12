package de.szut.dqi12.cheftrainer.client.callables;

import javafx.scene.control.Alert.AlertType;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.guicontrolling.AlertDialog;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers.CreateCommunityController;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class CommunityAuthentificationAck extends CallableAbstract {

	
	
	
	@Override
	public void messageArrived(Message message){
		JSONObject authentificationACK = new JSONObject(message.getMessageContent());
		switch(authentificationACK.getString("type")){
		case "creation": handleCreation(authentificationACK);
		break;
		}
	}
	
	private void handleCreation(JSONObject creationJSON){ 
		if(creationJSON.getBoolean("created")){
			DialogUtils.showAlert(AlertDialog.COMMUNITY_CREATION_TITLE,
								AlertDialog.COMMUNITY_CREATION_WORKED_HEAD, 
								AlertDialog.COMMUNITY_CREATION_WORKED_MESSAGE
								, AlertType.CONFIRMATION);
			GUIController.getInstance().closeCurrentDialog();
		}
		else{
			DialogUtils.showAlert(AlertDialog.COMMUNITY_CREATION_TITLE,
					AlertDialog.COMMUNITY_CREATION_WORKED_NOT_HEAD, 
					AlertDialog.COMMUNITY_CREATION_WORKED_NOT_MESSAGE
					, AlertType.ERROR);
		}
	}
}
