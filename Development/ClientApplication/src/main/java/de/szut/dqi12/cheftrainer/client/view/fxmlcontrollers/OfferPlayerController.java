package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;


import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.NewOfferMessage;

/**
 * This is an Controller for the FXML-Component which shows the Dialog to offer an Player on the Transfermarket
 */
public class OfferPlayerController implements ControllerInterface {

	private Player displayedPlayer;

	@FXML
	private TextField priceField;

	/**
	 * Is called, when the OfferButton is clicked and calles the sendOffer-Method.
	 */
	@FXML
	public void offerButtonClicked() {
		long offerPrice = Long.parseLong(priceField.getText(),10);
		if (offerPrice >= displayedPlayer.getWorth()) {
			sendOffer(offerPrice);
		} else {
			showWarningDialog();
		}
	}

	private void showWarningDialog() {
		AlertUtils.createSimpleDialog(AlertUtils.ERROR, AlertUtils.OFFER_ERROR, AlertUtils.OFFER_ERROR_TO_LESS, AlertType.ERROR);
	}

	/**
	 * Sends an offer for a Player to the Server
	 * @param offerPrice Price of the Offer
	 */
	private void sendOffer(long offerPrice) {
		Session s = Controller.getInstance().getSession();
		int communityID = s.getCurrentCommunityID();
		int userID = s.getUserID();
		
		Transaction tr = new Transaction();
		tr.setPlayerSportalID(displayedPlayer.getSportalID());
		tr.setOfferedPrice(offerPrice);
		tr.setUserID(userID);
		tr.setCommunityID(communityID);
		
		Message offerMessage = new NewOfferMessage(tr);
		
		s.getClientSocket().sendMessage(offerMessage);
		
		s.getCurrentCommunity().getMarket().addTransaction(tr);
	}

	public void setPlayer(Player p) {
		displayedPlayer = p;
	}

	@Override
	public void init(double width, double height) {

	}

	@Override
	public void enterPressed() {

	}

	@Override
	public void messageArrived(Boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializationFinihed(Scene scene) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(double sizeDifferent) {
		// TODO Auto-generated method stub
		
	}

}
