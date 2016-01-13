package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers;

import java.util.List;

import org.json.JSONObject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class OfferDialog {

	public void showOfferDialog() {
		Session session = Controller.getInstance().getSession();
		Community con = session.getCurrentCommunity();
		List<Transaction> transactions = con.getMarket().getTransactions();
		GridPane dialog = new GridPane();
		Button but;
		int index = 1;
		dialog.add(new Label("player"), 0, 0);
		dialog.add(new Label("price"), 1, 0);
		dialog.add(new Label("action"), 2, 0);
		for (Transaction tr : transactions) {
			tr.takeInformation(session);
			if (tr.isOutgoing()) {
				but = new Button("remove offer");
				but.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						sendAnswerOffer(tr, false, true);
						Button source = ((Button) event.getSource());
						((Stage) source.getScene().getWindow()).close();
						
						OfferDialog od = new OfferDialog();
						od.showOfferDialog();
					}
				});
			} else {
				but = new Button("accept offer");
				but.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {

						sendAnswerOffer(tr, true, true);
					}
				});
			}
			// TODO: gebot ablehnen k?nnen
			// else if(tr.isOutgoing()) {
			// but = new Button("denie offer");
			// but.setOnAction(new EventHandler<ActionEvent>() {
			//
			// @Override
			// public void handle(ActionEvent event) {
			//
			// //sendAnswerOffer(tr, true, true);
			// }
			// });
			// }
			dialog.add(new Label(tr.getPlayer().getName()), 0, index);
			dialog.add(new Label(String.valueOf(tr.getOfferedPrice())), 1, index);
			dialog.add(but, 2, index);
			index++;
		}

		Stage dialogStage = new Stage();
		dialogStage.setResizable(false);
		dialogStage.setTitle("offers");
		dialogStage.initModality(Modality.WINDOW_MODAL);
		Scene scene = new Scene(dialog);
		dialogStage.setScene(scene);
		dialogStage.showAndWait();
	}
	
	private void sendAnswerOffer(Transaction tr, boolean accept, boolean remove) {
		JSONObject transactionJSON = new JSONObject();
		transactionJSON.put(MIDs.TRANSACTION, tr.toJSON());
		transactionJSON.put(MIDs.ACCEPT, accept);
		transactionJSON.put(MIDs.REMOVE, remove);

		JSONObject messageContent = new JSONObject();
		messageContent.put(MIDs.TYPE, MIDs.TRANSACTION);
		messageContent.put(MIDs.INFORMATION, transactionJSON);

		Message message = new Message(ClientToServer_MessageIDs.TRANSFER_MARKET);
		message.setMessageContent(messageContent);
		
		Controller controller = Controller.getInstance();
		controller.sendMessageToServer(message);
		Market market = controller.getSession().getCurrentCommunity().getMarket();
		market.removeTransactions(tr);
	}
}
