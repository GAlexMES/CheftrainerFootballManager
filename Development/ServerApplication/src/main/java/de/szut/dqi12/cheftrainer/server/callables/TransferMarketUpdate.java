package de.szut.dqi12.cheftrainer.server.callables;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class TransferMarketUpdate extends CallableAbstract {

	@Override
	public void messageArrived(Message message) {
		JSONObject messageContent = new JSONObject(message.getMessageContent());
		String type = messageContent.getString("type");
		switch (type) {
		case "NewOffer":
			newOffer(messageContent);
			break;
		}
	}

	private void newOffer(JSONObject messageContent) {
		// TODO Auto-generated method stub

	}
}
