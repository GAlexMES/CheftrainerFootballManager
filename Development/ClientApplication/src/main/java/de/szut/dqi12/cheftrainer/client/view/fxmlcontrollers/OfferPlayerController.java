package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
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
		
		JSONObject offerInformation = new JSONObject();
		offerInformation.put("playerSportalID", displayedPlayer.getSportalID());
		offerInformation.put("price", offerPrice);
		offerInformation.put("userID", userID);
		offerInformation.put("communityID", communityID);
		
		JSONObject messageContent = new JSONObject();
		messageContent.put("type", "NewOffer");
		messageContent.put("information", offerInformation);
		
		Message offerMessage = new Message(ClientToServer_MessageIDs.TRANSFER_MARKET);
		offerMessage.setMessageContent(messageContent);
		s.getClientSocket().sendMessage(offerMessage);
	}

	public void setPlayer(Player p) {
		displayedPlayer = p;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived() {
		// TODO Auto-generated method stub

	}

}