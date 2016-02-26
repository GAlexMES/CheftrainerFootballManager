package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerManager;
import de.szut.dqi12.cheftrainer.client.images.ImageController;
import de.szut.dqi12.cheftrainer.client.images.ImageUpdate;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers.AddPlayerToMarketController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers.ChangeFormationController;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Formation;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.FormationFactory;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.PlayerLabel;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Position;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.NewFormationMessage;

/**
 * This is the controller for the gui-module LineUp
 * 
 * @author Robin
 *
 */
public class LineUpController implements ControllerInterface, ImageUpdate {
	@FXML
	private GridPane lineUpFrame;

	private Formation currentFormation;
	// private FormationController fController;
	// private GridPane oldPane;

	@FXML
	private ChoiceBox formationBox;

	private Manager tempSendingManager;

	private FormationFactory ff;

	public static final String RESET_MANAGER = "Reset the managers.";

	private int i = 0;

	private double frameWidth = 0D;
	private double frameHeight = 0D;

	private boolean putImageToStack;
	private Map<Integer, Image> imageUpdateStack;

	private List<Player> allPlayers;
	private List<Player> playingPlayers;
	private List<Player> notPlayingPlayers;

	private Pane lineUpPane;

	private Image background;

	private Stage changePlayerStage;

	public LineUpController() {
		ControllerManager cm = ControllerManager.getInstance();
		cm.registerController(this, RESET_MANAGER);
		allPlayers = new ArrayList<>();
		imageUpdateStack = new HashMap<>();
	}

	public GridPane getFrame() {
		return lineUpFrame;
	}

	/**
	 * This method have to be called before all other methods. It initializes
	 * every gui-components
	 */
	@Override
	public void init(double width, double height) {
		frameWidth = width;
		frameHeight = height;
		lineUpFrame.resize(width, height);
		try {
			background = new Image(getClass().getResourceAsStream("/images/football_field.png"));

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
		formationBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				String newFormation = (String) formationBox.getItems().get((int) newValue);
				Formation f = ff.getFormation(newFormation);
				currentFormation = f;
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
		Manager manager = Controller.getInstance().getSession().getCurrentManager();
		allPlayers = manager.getPlayers();
		playingPlayers = new ArrayList<>();
		notPlayingPlayers = new ArrayList<>();

		allPlayers.forEach(p -> {
			if (p.isPlays()) {
				playingPlayers.add(p);
			} else {
				notPlayingPlayers.add(p);
			}
		});

		checkForImageUpdate();

		List<Player> lineUpPlayers = new ArrayList<>();
		List<String> positions = Position.getPositions();
		for (int i = 0; i < positions.size(); i++) {
			String position = positions.get(i);
			List<Player> pPlayers = getPlayerForPosition(position, currentFormation.getPlayersForPosition(position));
			lineUpPlayers.addAll(pPlayers);
		}

		playingPlayers.clear();
		playingPlayers.addAll(lineUpPlayers);

		putImageToStack = true;
		playingPlayers.forEach(p -> preparePlayerLabel(p));
		putImageToStack = false;

		redrawFrame();

		return true;
	}

	private List<Player> getPlayerForPosition(String position, int size) {
		List<Player> retval = new ArrayList<>();

		playingPlayers.forEach(p -> {
			if (p.getPosition().equals(position) && retval.size() < size)
				retval.add(p);
		});

		int listSize = retval.size();
		for (int i = 0; listSize + i < size; i++) {
			Player player = null;
			for(Player p : notPlayingPlayers){
				if (p.getPosition().equals(position) && retval.size() < size) {
					retval.add(p);
					player = p;
				}
			}
			notPlayingPlayers.remove(player);
		}
		return retval;
	}

	private void preparePlayerLabel(Player player) {
		PlayerLabel l = new PlayerLabel();
		l.setPlayer(player);
		l.setPlayerId(player.getID());
		l.setPosition(player.getPosition());
		player.setLabel(l);
		ImageController c = new ImageController(this);
		Image image = c.getPicture(player);
		player.getLabel().setImage(image);
		l.setOnMouseClicked(e -> openChangeDialog(player));
	}

	private void openChangeDialog(Player player) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("dialogFXML/ChangePlayerTable.fxml"));
			GridPane root = (GridPane) fxmlLoader.load();
			ChangeFormationController cfc = ((ChangeFormationController) fxmlLoader.getController());
			cfc.setLUP(this);
			cfc.setSelectedPlayer(player, notPlayingPlayers);
			changePlayerStage = new Stage();
			changePlayerStage.setTitle("Deine Spieler");
			changePlayerStage.setScene(new Scene(root));
			changePlayerStage.setWidth(300);
			changePlayerStage.setResizable(true);
			changePlayerStage.show();
		} catch (Exception e) {
			e.printStackTrace();
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
		tempSendingManager = new Manager();
		tempSendingManager.setID(manager.getID());
		tempSendingManager.addPlayer(manager.getPlayers());
		playingPlayers.forEach(p -> p.setPlays(true));
		tempSendingManager.setLineUp(playingPlayers);
		tempSendingManager.setFormation(currentFormation);
		tempSendingManager.setName(manager.getName());

		Message updateMessage = new NewFormationMessage(tempSendingManager);
		Controller.getInstance().getSession().getClientSocket().sendMessage(updateMessage);
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
		redrawFrame();
		
		scene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
				double dif = oldSceneWidth.doubleValue() - newSceneWidth.doubleValue();
				frameWidth = lineUpFrame.getWidth() + dif;
				redrawFrame();
			}
		});
		
		scene.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
				frameHeight = lineUpFrame.getHeight();
				redrawFrame();
			}
		});
	}

	@Override
	public void resize(double width) {
		frameWidth = width;
		redrawFrame();
	}

	private void redrawFrame() {
		resizeElements();
		relocatePlayers(frameWidth, frameHeight);
		resizeBackground(frameWidth, frameHeight);
	}

	private void resizeElements() {
		double newSize;
		if (frameHeight != 0D && frameWidth != 0D) {
			if (frameHeight / 7 < frameWidth / 7) {
				newSize = frameHeight / 7;
			} else {
				newSize = frameWidth / 7;
			}
			for (Player p : playingPlayers) {
				p.getLabel().setSize(newSize);
			}
		}
	}

	private void relocatePlayers(double width, double height) {
		Pane newLineUpPane = new Pane();
		double labelHeight = height / 5;

		List<String> positions = Position.getPositions();
		for (int i = 0; i < positions.size(); i++) {
			String position = positions.get(i);
			List<Player> pPlayers = new ArrayList<>();
			playingPlayers.forEach(p -> {
				if (p.getPosition().equals(position))
					pPlayers.add(p);
			});

			for (int num = 0; num < pPlayers.size(); num++) {
				Label l = pPlayers.get(num).getLabel();
				double boxHeight = (formationBox.getHeight()==0.0)? 20.0:formationBox.getHeight();
				
				double useableSpace = height - (2 * boxHeight);
				double y = useableSpace - (labelHeight / 6) - ((i + 1) * labelHeight);
				double x = ((num + 1) * width / (pPlayers.size() + 1)) - (l.getWidth() / 2);
				newLineUpPane.getChildren().add(l);
				l.relocate(x, y);
			}
		}

		lineUpFrame.getChildren().remove(lineUpPane);
		lineUpFrame.add(newLineUpPane, 0, 0);
		lineUpPane = newLineUpPane;
	}

	private void resizeBackground(double width, double height) {
		BackgroundSize bs = new BackgroundSize(width, height, false, false, false, false);
		BackgroundImage bi = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, bs);
		lineUpFrame.setBackground(new Background(bi));

	}

	@Override
	public void updateImage(Image image, int id) {
		if (putImageToStack) {
			imageUpdateStack.put(id, image);
		} else {
			setImageToPlayer(id, image);
		}
	}

	/**
	 * Loads the Image of the matching Player and set it into the Label of the
	 * Player.
	 * 
	 * @param playerID
	 *            ID of the Player
	 * @param image
	 *            Image of the Player
	 */
	private void setImageToPlayer(int playerID, Image image) {
		for (Player player : playingPlayers) {
			if (player.getSportalID() == playerID) {
				player.getLabel().setImage(image);
				break;
			}
		}

	}

	private void checkForImageUpdate() {
		for (int i : imageUpdateStack.keySet()) {
			setImageToPlayer(i, imageUpdateStack.get(i));
		}
		imageUpdateStack = new HashMap<>();
	}

	public void setPlayer(Player player, Player selectedPlayer) {
		playingPlayers.remove(selectedPlayer);
		notPlayingPlayers.add(selectedPlayer);

		preparePlayerLabel(player);
		playingPlayers.add(player);
		notPlayingPlayers.remove(player);

		redrawFrame();
	}
}