package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerManager;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.images.ImageController;
import de.szut.dqi12.cheftrainer.client.images.ImageUpdate;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;

/**
 * Shows detailed informations of an Player
 */
public class PlayerDetailedController implements ControllerInterface, ImageUpdate {

	private Player displayedPlayer;
	
	public static final String ON_ACTION_KEY = "playerDetailedControllerMessage";

	@FXML
	private GridPane mainPane;
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

	public PlayerDetailedController(){
		ControllerManager cm = ControllerManager.getInstance();
		cm.registerController(this, ON_ACTION_KEY);
	}

	public void setPlayer(Player p) {
		displayedPlayer = p;

		playerNameText.setText("Name: " + p.getName());
		playerBirthdayText.setText("Geburtstag: " + p.getBirthdateString());
		playerPointsText.setText("Punkte: " + p.getPoints());
		playerWorthText.setText("Wert: " + p.getWorth() + "€");

		ImageController ic = new ImageController(this);
		Image playerPic = ic.getPicture(p);
		playerPicture.setImage(playerPic);
	}

	public void showOffer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("sourcesFXML/OfferPlayerFrame.fxml"));
			VBox offerBox = (VBox) fxmlLoader.load();
			OfferPlayerController opc = fxmlLoader.getController();
			opc.setPlayer(displayedPlayer);
			mainPane.add(offerBox, 1, 1);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(double width, double height) {

	}

	@Override
	public void enterPressed() {

	}

	@Override
	public void updateImage(Image image, int id) {
		playerPicture.setImage(image);
	}

	@Override
	public void messageArrived(Boolean flag) {
		if(flag){
			GUIController.getInstance().closeCurrentDialog();
		}
	}

	@Override
	public void initializationFinihed(Scene scene) {
		GUIController.getInstance().setCurrentDialogStage(((Stage)mainPane.getScene().getWindow()));
	}

	@Override
	public void resize(double sizeDifferent) {
		// TODO Auto-generated method stub
		
	}

}
