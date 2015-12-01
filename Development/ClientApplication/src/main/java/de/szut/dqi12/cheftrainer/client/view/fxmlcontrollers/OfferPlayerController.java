package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class OfferPlayerController implements ControllerInterface {

	private Player displayedPlayer;

	@FXML
	private TextField priceField;

	@FXML
	public void offerButtonClicked() {
		int offerPrice = Integer.valueOf(priceField.getText());
		if (offerPrice >= displayedPlayer.getWorth()) {
			sendOffer(offerPrice);
		} else {
			showWarningDialog();
		}
	}

	private void showWarningDialog() {
		// TODO Auto-generated method stub
		
	}

	private void sendOffer(int offerPrice) {
		Session s = Controller.getInstance().getSession();
		int communityID = s.getCurrentCommunityID();
		int userID = s.getUserID();
		
		Transaction tr = new Transaction();
		tr.setPlayerSportalID(displayedPlayer.getSportalID());
		tr.setOfferedPrice(offerPrice);
		tr.setUserID(userID);
		tr.setCommunityID(communityID);
		
		JSONObject offerInformation = tr.toJSON();
		
		JSONObject messageContent = new JSONObject();
		messageContent.put("type", "NewOffer");
		messageContent.put("information", offerInformation);
		
		Message offerMessage = new Message(ClientToServer_MessageIDs.TRANSFER_MARKET);
		offerMessage.setMessageContent(messageContent);
		s.getClientSocket().sendMessage(offerMessage);
		
		s.getCurrentCommunity().getMarket().addTransaction(tr);
	}

	public void setPlayer(Player p) {
		displayedPlayer = p;
	}

	@Override
	public void init() {

	}

	@Override
	public void enterPressed() {

	}

	@Override
	public void messageArrived(Boolean flag) {
		// TODO Auto-generated method stub
		
	}

}
