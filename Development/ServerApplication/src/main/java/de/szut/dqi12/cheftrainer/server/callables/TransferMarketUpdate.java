package de.szut.dqi12.cheftrainer.server.callables;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;

public class TransferMarketUpdate extends CallableAbstract {

	@Override
	public void messageArrived(Message message) {
		JSONObject messageContent = new JSONObject(message.getMessageContent());
		String type = messageContent.getString("type");
		switch (type) {
		case "NewOffer":
			newOffer(messageContent);
			break;
		case "Transaction":
			transaction(messageContent);
			break;
		}
	}

	private void newOffer(JSONObject messageContent) {
		JSONObject information = messageContent.getJSONObject("information");
		Transaction transaction = new Transaction(information);
		DatabaseRequests.addTransaction(transaction);
	}

	private void transaction(JSONObject messageContent) {
		JSONObject information = messageContent.getJSONObject("information");
		boolean accept = information.getBoolean("Annehmen");
		boolean remove = information.getBoolean("Entfernen");
		Transaction tr = new Transaction(information.getJSONObject("Gebot"));

		//Remove will be done in transferPlayer
		if (accept) {
			DatabaseRequests.transferPlayer(tr);
		} else {
			if (remove) {
				DatabaseRequests.removeTransaction(tr);
			}
		}
	}
}
