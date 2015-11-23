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
		}
	}

	private void newOffer(JSONObject messageContent) {
		JSONObject information = messageContent.getJSONObject("information");
		int playerID = information.getInt("playerSportalID");
		int price = information.getInt("price");
		int userID = information.getInt("userID");
		int communityID = information.getInt("communityID");

		Transaction transaction = new Transaction(price,playerID,communityID,userID);
		
		DatabaseRequests.addTransaction(transaction);
	}
}
