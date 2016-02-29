package de.szut.dqi12.cheftrainer.client.callables;

import javafx.scene.control.Alert.AlertType;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerManager;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.PlayerDetailedController;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.SaveOfferAckMessage;

/**
 * This class is used, when a {@link SaveOfferAckMessage} was send by the server.
 * @author Alexander Brennecke
 *
 *@see /F0200/
 */
public class SaveOfferAck extends CallableAbstract {

	@Override
	public void messageArrived(Message message) {
		JSONObject saveFormationAck = new JSONObject(message.getMessageContent());
		SaveOfferAckMessage sfaMessage = new SaveOfferAckMessage(saveFormationAck);
		if(!sfaMessage.isSuccessful()){
			AlertUtils.createSimpleDialog(AlertUtils.ERROR,
					AlertUtils.OFFER_ERROR,
					AlertUtils.OFFER_ERROR_UNKNOWN, AlertType.ERROR);
		}
		else{
			AlertUtils.createSimpleDialog(AlertUtils.SUCCESS,
					AlertUtils.OFFER_SUCCESS,
					AlertUtils.OFFER_SUCCESS, AlertType.CONFIRMATION);
		}
		
		ControllerManager.getInstance().onAction(PlayerDetailedController.ON_ACTION_KEY,sfaMessage.isSuccessful());
	}
}
