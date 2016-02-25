package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.PUBLIC_MEMBER;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
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
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.NewFormationMessage;

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

	@FXML
	private ChoiceBox formationBox;

	private Manager tempSendingManager;
	
	private FormationFactory ff;

	public static final String RESET_MANAGER = "Reset the managers.";

	private int i = 0;

	public LineUpController() {
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
	 * This method have to be called before all other methods. It initializes
	 * every gui-components
	 */
	@Override
	public void init() {
		try {
			Image image = new Image(getClass().getResourceAsStream("/images/football_field.png"));

			BackgroundSize bs = new BackgroundSize(100, 1000, true, true, false, true);
			BackgroundImage bi = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, bs);

			lineUpFrame.setBackground(new Background(bi));

			Session session = Controller.getInstance().getSession();
			Community community = session.getCurrentCommunity();
			int managerID = session.getCurrentManagerID();
			Formation formation = community.getManager(managerID).getFormation();
			initFormationChoiceBox();
			formationBox.getSelectionModel().select(formation.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initFormationChoiceBox() {
		ObservableList<Object> formationBoxOptions = FXCollections.observableArrayList();
		ff = new FormationFactory();
		List<Formation> formations = ff.getFormations();
		formations.forEach(f -> formationBoxOptions.add(f.getName()));
		formationBox.setItems(formationBoxOptions);
		formationBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>(){
			
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				String newFormation = (String) formationBox.getItems().get((int) newValue);
				Formation f = ff.getFormation(newFormation);
				changeFormation(f);
				
			}
			
		});
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
			currentFormation = formation;
			FXMLLoader currentContentLoader = getLoader(formation);
			GridPane newContentPane = (GridPane) currentContentLoader.load();
			fController = ((FormationController) currentContentLoader.getController());
			fController.init();
			fController.setClickedListener();

			if (i > 0) {
				lineUpFrame.getChildren().remove(oldPane);
			}
			lineUpFrame.add(newContentPane, 0, 0);

			i++;

			oldPane = newContentPane;

			// if ( (i % 2) != 0 ) {
			// changeFormation(formation);
			// }
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

		// fController.createResizeListener();

		Session s = Controller.getInstance().getSession();
		int currentManagerID = s.getCurrentManagerID();
		Manager manager = s.getCurrentCommunity().getManager(currentManagerID);
		List<Player> guiLineUp = fController.getCurrentPlayers();
		boolean formationChanged = guiLineUp.equals(manager.getLineUp(false));
		if (!currentFormation.getName().equals(manager.getFormation().getName()) || !formationChanged) {
			tempSendingManager = new Manager();
			tempSendingManager.setID(manager.getID());
			tempSendingManager.addPlayer(manager.getPlayers());
			tempSendingManager.setLineUp(guiLineUp);
			tempSendingManager.setFormation(currentFormation);
			tempSendingManager.setName(manager.getName());

			Message updateMessage = new NewFormationMessage(tempSendingManager);
			Controller.getInstance().getSession().getClientSocket().sendMessage(updateMessage);
		} else {
			AlertUtils.createSimpleDialog("Nothing to save", "There are no changes!", "", AlertType.CONFIRMATION);
		}
	}

	@Override
	public void enterPressed() {
		this.saveButtonClicked();

	}

	@Override
	public void messageArrived(Boolean flag) {
		if (flag) {
			Session s = Controller.getInstance().getSession();
			int currentManagerID = s.getCurrentManagerID();
			Manager manager = s.getCurrentCommunity().getManager(currentManagerID);
			manager.setFormation(tempSendingManager.getFormation());
			manager.setLineUp(tempSendingManager.getLineUp(false));
		} else {
			tempSendingManager = null;
		}
	}

	@Override
	public void initializationFinihed(Scene scene) {
		fController.createResizeListener(scene);
	}
}