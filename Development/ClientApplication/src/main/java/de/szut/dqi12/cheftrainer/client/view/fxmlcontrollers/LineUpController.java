package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerManager;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Formation;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.FormationFactory;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.PlayerLabel;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * This is the controller for the gui-module LineUp
 * 
 * @author Robin
 *
 */
public class LineUpController implements ControllerInterface {
	@FXML
	private GridPane lineUpFrame;

	private Formation currentFormation;
	private FormationController fController;
	private GridPane oldPane;
	
	private Manager tempSendingManager;
	
	public static final String RESET_MANAGER = "Reset the managers.";

	private int i = 0;
	
	public LineUpController(){
		ControllerManager cm = ControllerManager.getInstance();
		cm.registerController(this, RESET_MANAGER);
	}

	public GridPane getFrame() {
		return lineUpFrame;
	}

	/**
	 * Loads the matching FXMLLoader for the used Formation
	 * 
	 * @param formation
	 *            used Formation
	 * @return the matching FXMLLoader for the used Formation
	 */
	private FXMLLoader getLoader(Formation formation) {
		currentFormation = formation;
		ClassLoader classLoader = getClass().getClassLoader();
		FXMLLoader currentContentLoader = new FXMLLoader();

		String path = "formations/Formation";
		URL fxmlFile;
		switch (formation.getName()) {
		case FormationFactory.FOUR_FOUR_TWO:
			fxmlFile = classLoader.getResource(path + "442.fxml");
			break;
		case FormationFactory.FOUR_FIVE_ONE:
			fxmlFile = classLoader.getResource(path + "451.fxml");
			break;
		default:
			return null;
		}
		currentContentLoader.setLocation(fxmlFile);
		return currentContentLoader;

	}

	/**
	 * This method have to be called before all other methods. It initializates
	 * every gui-components
	 * 
	 * @return success or not
	 */
	@Override
	public void init() {
		try {
			Session session = Controller.getInstance().getSession();
			Community community = session.getCurrentCommunity();
			int managerID = session.getCurrentManagerID();
			Formation formation = community.getManager(managerID).getFormation();
			ClassLoader classLoader = getClass().getClassLoader();
			FXMLLoader currentContentLoader = getLoader(formation);

			GridPane newContentPane = (GridPane) currentContentLoader.load();
			fController = ((FormationController) currentContentLoader.getController());
			fController.setClickedListener();
			changeFormation(formation);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Changes the shown Formation
	 * 
	 * @param formation
	 *            the new Formation
	 * @return success or not
	 */
	public boolean changeFormation(Formation formation) {
		try {
			Session session = Controller.getInstance().getSession();
			Community currentCommunity = session.getCurrentCommunity();
			Manager currentManager = currentCommunity.getManager(session.getCurrentManagerID());
			FXMLLoader currentContentLoader = getLoader(formation);
			GridPane newContentPane = (GridPane) currentContentLoader.load();
			fController = ((FormationController) currentContentLoader.getController());
			fController.init();

			ArrayList<Node> labels = new ArrayList<Node>();
			for (Node n : newContentPane.getChildren()) {
				labels.add(n);
			}
			ArrayList<Node> labelscopy = (ArrayList<Node>) labels.clone();
			ArrayList<Player> players;
			if (formation == currentManager.getFormation()) {
				players = fController.loadPlayingPlayers();
			} else {
				players = (ArrayList<Player>) currentManager.getPlayers();

			}
			int index;
			for (Player player : players) {
				index = 0;
				for (Node node : labels) {
					try {
						PlayerLabel label = ((PlayerLabel) node);
						if (label.getPosition().equals(player.getPosition())) {
							label = player.getLabel();
							break;
						}
						index++;
					} catch (NullPointerException e) {
					}
				}
			}
			// index = 0;
			// for (Node n : labelscopy) {
			// if (n != null) {
			// Node l = labels.get(index);
			// ((PlayerLabel) l).setText("kein Spieler");
			// }
			// index++;
			// }
			fController.setClickedListener();
			if (i > 0) {

				lineUpFrame.getChildren().remove(oldPane);
				lineUpFrame.add(newContentPane, 0, 0);
			} else {
				i++;
				lineUpFrame.add(newContentPane, 0, 0);

			}
			oldPane = newContentPane;
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Is Called when the Button "save" is clicked. Saves the current Formation
	 * and line-up.
	 */
	@FXML
	public void saveButtonClicked() {
		Session s = Controller.getInstance().getSession();
		int currentManagerID = s.getCurrentManagerID();
		Manager manager = s.getCurrentCommunity().getManager(currentManagerID);
		ArrayList<Player> guiLineUp = fController.getCurrentPlayers();
		boolean formationChanged = guiLineUp.equals(manager.getLineUp());
		if (!currentFormation.getName().equals(manager.getFormation().getName()) || !formationChanged) {
			tempSendingManager = new Manager();
			tempSendingManager.setID(manager.getID());
			tempSendingManager.addPlayer(manager.getPlayers());
			tempSendingManager.setLineUp(guiLineUp);
			tempSendingManager.setFormation(currentFormation);
			
			Message updateMessage = new Message(ClientToServer_MessageIDs.NEW_FORMATION);
			updateMessage.setMessageContent(manager.toJSON());
			Controller.getInstance().getSession().getClientSocket().sendMessage(updateMessage);
		} else {
			AlertUtils.createSimpleDialog("Nothing to save",
					"There are no changes!",
					"", AlertType.CONFIRMATION);
		}
	}

	/**
	 * Is called when the Button "change formation" is clicked. Opens a dialog
	 * to choose a new Formation.
	 */
	@FXML
	public void formationButtonClicked() {
		GridPane dialog;
		Stage dialogStage = new Stage();

		dialog = new GridPane();
		Label l;
		int i = 0;
		FormationFactory ff = new FormationFactory();
		List<Formation> formations = ff.getFormations();
		for (Formation formation : formations) {
			l = new Label();
			l.setGraphic(new ImageView(FormationController.getImageOfString(formation.getName())));
			dialog.add(l, 0, i);
			i++;
			l.setOnMouseClicked(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					dialogStage.close();
					// lineUpFrame.getChildren().clear();
					changeFormation(formation);
				}
			});
		}

		dialogStage.setResizable(false);
		dialogStage.setTitle("Formationen");
		dialogStage.initModality(Modality.WINDOW_MODAL);
		Scene scene = new Scene(dialog);

		dialogStage.setScene(scene);
		dialogStage.showAndWait();

	}

	@Override
	public void enterPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(Boolean flag) {
		if(flag){
			Session s = Controller.getInstance().getSession();
			int currentManagerID = s.getCurrentManagerID();
			Manager manager = s.getCurrentCommunity().getManager(currentManagerID);
			manager.setFormation(tempSendingManager.getFormation());
			manager.setLineUp(tempSendingManager.getLineUp());
		}
		else{
			tempSendingManager = null;
		}
	}

}