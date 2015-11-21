package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.client.images.ImageController;
import de.szut.dqi12.cheftrainer.client.images.ImageUpdate;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;

public class PlayerDetailedController implements ControllerInterface, ImageUpdate {

	private Player displayedPlayer;
	
	@FXML
	private GridPane mainPane;
	@FXML
	private RowConstraints generalData;
	@FXML
	private Text playerNameText;
	@FXML
	private Text playerBirthdayText;
	@FXML
	private Text playerPointsText;
	@FXML
	private Text playerWorthText;
	@FXML
	private ImageView playerPicture;
	
	private Double frameHeight;
	
	
	public void setPlayer(Player p){
		displayedPlayer = p;
		
		playerNameText.setText("Name: "+p.getName());
		playerBirthdayText.setText("Birth: "+p.getBirthdateString());
		playerPointsText.setText("Points: "+p.getPoints());
		playerWorthText.setText("Worth: "+p.getWorth()+"€");
		
		ImageController ic = new ImageController(this);
		Image playerPic = ic.getPicture(p);
		playerPicture.setImage(playerPic);
		
		frameHeight = mainPane.getHeight();
		Double generalDataHeight= generalData.getPrefHeight();
		setFrameHeight(generalDataHeight);
		
	}
	
	public void showOffer(){
		setFrameHeight(frameHeight);
	}
	
	private void setFrameHeight(Double height){
		mainPane.setMaxHeight(height);
		mainPane.setPrefHeight(height);
		mainPane.setMinHeight(height);
	}

	@Override
	public void init() {

	}

	@Override
	public void enterPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateImage(Image image, int id) {
		playerPicture.setImage(image);
	}

}
