package de.szut.dqi12.cheftrainer.server.callables;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.NewOfferMessage;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.NewPlayerOnMarketMessage;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.SaveOfferAckMessage;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.TransactionMessage;
import de.szut.dqi12.cheftrainer.server.database.DatabaseRequests;

/**
 * This callable is used to handle changes on the exchange market. It is used,
 * when a {@link Manager} makes a new {@link Transaction}, puts a {@link Player}
 * on the market, removes it, or accepts/denies and {@link Transaction} from an
 * other {@link Manager}
 * 
 * @author Alexander Brennecke
 *
 * @see /F0230/ </br> /F2040/ </br> /F0260/
 */
public class TransferMarketUpdate extends CallableAbstract {

	@Override
	public void messageArrived(Message message) {
		JSONObject messageContent = new JSONObject(message.getMessageContent());
		String type = messageContent.getString(MIDs.TYPE);
		JSONObject information = messageContent.getJSONObject(MIDs.INFORMATION);
		switch (type) {
		case MIDs.NEW_OFFER:
			newOffer(information);
			break;
		case MIDs.TRANSACTION:
			transaction(messageContent);
			break;
		case MIDs.NEW_MARKET_PLAYER:
			newMarketPlayer(information);
		}
	}

	/**
	 * this function is called, when a new {@link Transaction} should be
	 * created.
	 * 
	 * @param messageContent
	 *            the JSONObject, sended by the client.
	 * @see /F0230/
	 */
	private void newOffer(JSONObject messageContent) {
		NewOfferMessage noMessage = new NewOfferMessage(messageContent);
		Transaction transaction = noMessage.getTransaction();
		boolean successful = DatabaseRequests.addTransaction(transaction);
		SaveOfferAckMessage soam = new SaveOfferAckMessage(successful);
		mesController.sendMessage(soam);
	}

	/**
	 * This function is called, when a {@link Manager} accepted an
	 * {@link Transaction}.# It will delete the {@link Player} from the one
	 * {@link Manager}s team, and will add it to the other ones. Also it will
	 * transfer the paid money.
	 * 
	 * @param messageContent
	 *            the JSONObject, which was sended by the client, with all
	 *            nested information to transfer a {@link Player}.
	 * @see /F0260/
	 */
	private void transaction(JSONObject messageContent) {
		TransactionMessage tMessage = new TransactionMessage(messageContent);

		// Remove will be done in transferPlayer
		if (tMessage.isAccept()) {
			DatabaseRequests.transferPlayer(tMessage.getTransaction());
		} else if (tMessage.isRemove()) {
			DatabaseRequests.removeTransaction(tMessage.getTransaction());
		}

	}

	/**
	 * This function is called, when a client wants to add a new {@link Player}
	 * to the {@link Market}. It will call a SQL Class, which will check, if the
	 * transmitted value is valid and will add the {@link Player} to the
	 * {@link Market}.
	 * 
	 * @param messageContent
	 *            the JSON, that was sent by the {@link Client}
	 */
	private void newMarketPlayer(JSONObject messageContent) {
		NewPlayerOnMarketMessage npomm = new NewPlayerOnMarketMessage(messageContent);
		boolean addPlayer = npomm.isAddPlayer();
		if (addPlayer) {
			DatabaseRequests.putPlayerOnExchangeMarket(npomm.getPlayer(), npomm.getCommunityID(), npomm.getManagerID());
		} else {
			try {
				DatabaseRequests.deletePlayerFromMarket(npomm.getPlayer(), npomm.getCommunityID());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
