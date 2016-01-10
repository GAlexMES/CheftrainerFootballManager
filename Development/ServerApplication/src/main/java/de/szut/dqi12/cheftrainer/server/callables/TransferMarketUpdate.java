package de.szut.dqi12.cheftrainer.server.callables;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.server.databasecommunication.DatabaseRequests;

/**
 * This callable is used to handle changes on the exchange market. It is used, when a {@link Manager} makes a new {@link Transaction}, 
 * puts a {@link Player} on the market, removes it, or accepts/denies and {@link Transaction}
 * from an other {@link Manager}
 * @author Alexander Brennecke
 *
 * @custom.position /F0230/ </br> /F2040/ </br> /F0260/
 */
public class TransferMarketUpdate extends CallableAbstract {

	@Override
	public void messageArrived(Message message) {
		JSONObject messageContent = new JSONObject(message.getMessageContent());
		String type = messageContent.getString(MIDs.TYPE);
		switch (type) {
		case MIDs.NEW_OFFER:
			newOffer(messageContent);
			break;
		case MIDs.TRANSACTION:
			transaction(messageContent);
			break;
		}
	}

	/**
	 * this function is called, when a new {@link Transaction} should be created.
	 * @param messageContent the JSONObject, sended by the client.
	 * @custom.position /F0230/
	 */
	private void newOffer(JSONObject messageContent) {
		JSONObject information = messageContent.getJSONObject(MIDs.INFORMATION);
		Transaction transaction = new Transaction(information);
		DatabaseRequests.addTransaction(transaction);
	}

	/**
	 * This function is called, when a {@link Manager} accepted an {@link Transaction}.#
	 * It will delete the {@link Player} from the one {@link Manager}s team, and will add it to the other ones.
	 * Also it will transfer the paid money.
	 * @param messageContent the JSONObject, which was sended by the client, with all nested information to transfer a {@link Player}.
	 * @custom.position /F0260/
	 */
	private void transaction(JSONObject messageContent) {
		JSONObject information = messageContent.getJSONObject(MIDs.INFORMATION);
		boolean accept = information.getBoolean(MIDs.ACCEPT);
		boolean remove = information.getBoolean(MIDs.REMOVE);
		Transaction tr = new Transaction(information.getJSONObject(MIDs.TRANSACTION));

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
