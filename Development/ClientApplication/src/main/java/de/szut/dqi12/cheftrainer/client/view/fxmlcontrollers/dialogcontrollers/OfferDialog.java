package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.TransactionMessage;

public class OfferDialog {

	public void showOfferDialog() {
		Session session = Controller.getInstance().getSession();
		Community con = session.getCurrentCommunity();
		List<Transaction> transactions = con.getMarket().getTransactions();
		if(transactions.size()==0){
			showWarning();
		}
		else{
			showDialog(transactions,session);
		}

	}

	private void showWarning(){
		AlertUtils.createSimpleDialog(AlertUtils.ERROR, AlertUtils.NO_OFFERS_ERROR, AlertUtils.NO_OFFERS_ERROR_DETAILS, AlertType.INFORMATION);
	}
	
	private void showDialog(List<Transaction> transactions, Session session){
		GridPane dialog = new GridPane();
		Button but;
		int index = 1;
		dialog.add(new Label("Spieler"), 0, 0);
		dialog.add(new Label("Preis"), 1, 0);
		dialog.add(new Label("Aktion"), 2, 0);
		
		for (Transaction tr : transactions) {
			tr.takeInformation(session);
			if (tr.isOutgoing()) {
				but = new Button("Gebot Ablehnen");
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
				but = new Button("Gebot Annehmen");
				but.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {

						sendAnswerOffer(tr, true, true);
					}
				});
			}
			dialog.add(new Label(tr.getPlayer().getName()), 0, index);
			dialog.add(new Label(String.valueOf(tr.getOfferedPrice())), 1, index);
			dialog.add(but, 2, index);
			index++;
		}

		Stage dialogStage = new Stage();
		Image icon = GUIController.getInstance().getGUIInitialator().getIcon();
		dialogStage.getIcons().add(icon);
		dialogStage.setResizable(false);
		dialogStage.setTitle("offers");
		dialogStage.initModality(Modality.WINDOW_MODAL);
		Scene scene = new Scene(dialog);
		dialogStage.setScene(scene);
		dialogStage.showAndWait();
	}
	
	private void sendAnswerOffer(Transaction tr, boolean accept, boolean remove) {
		Message message = new TransactionMessage(tr,accept,remove);
		
		Controller controller = Controller.getInstance();
		controller.sendMessageToServer(message);
		
		Market market = controller.getSession().getCurrentCommunity().getMarket();
		market.removeTransactions(tr);
	}
}
