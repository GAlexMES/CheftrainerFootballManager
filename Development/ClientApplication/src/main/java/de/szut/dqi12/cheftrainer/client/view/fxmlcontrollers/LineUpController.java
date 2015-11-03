package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Formation;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.FormationFactory;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.PlayerLabel;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;

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
		// BUG: es muss geschaut werden ob die reihenfolge richtig ist, wie die
		// spieler den PlayerLabels zugeordnet werden. Gegebenfalls wie in der
		// anderen
		// Funktion machen, wo auf PlayerLabelnamen geprueft wird
		try {
			Session session = Controller.getInstance().getSession();
			Community community = session.getCurrentCommunity();

			Formation formation = community.getManagers()
					.get(session.getCurrentManagerID()).getFormation();

			ClassLoader classLoader = getClass().getClassLoader();
			FXMLLoader currentContentLoader = getLoader(formation);

			GridPane newContentPane = (GridPane) currentContentLoader.load();
			fController = ((FormationController) currentContentLoader
					.getController());
			fController.setClickedListener();
			changeFormation(formation);
			// try {
			// ArrayList<Player> players = fController.loadPlayers();
			// ArrayList<Player> defence = new ArrayList<Player>();
			// ArrayList<Player> middel = new ArrayList<Player>();
			// ArrayList<Player> offence = new ArrayList<Player>();
			// Player keeper = players.get(0);
			// for (Player p : players) {
			// switch (p.getPosition()) {
			// case Position.DEFENCE:
			// defence.add(p);
			// break;
			// case Position.MIDDLE:
			// middel.add(p);
			// break;
			// case Position.OFFENCE:
			// offence.add(p);
			// case Position.KEEPER:
			// keeper = p;
			// break;
			// default:
			// break;
			// }
			// }
			// players.clear();
			// players.addAll(offence);
			// players.addAll(middel);
			// players.addAll(defence);
			// players.add(keeper);
			// int index = 0;
			// for (Node node : newContentPane.getChildren()) {
			// ((PlayerLabel) node).setText(players.get(index).getName()
			// + "; " + players.get(index).getPoints());
			// index++;
			// }
			// lineUpFrame.add(newContentPane, 0, 0);
			// } catch (NullPointerException e) {
			// e.printStackTrace();
			// }

		} catch (IOException e) {
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
			Manager currentManager = currentCommunity.getManager(session
					.getCurrentManagerID());
			FXMLLoader currentContentLoader = getLoader(currentManager
					.getFormation());
			GridPane newContentPane = (GridPane) currentContentLoader.load();
			fController = ((FormationController) currentContentLoader
					.getController());
			fController.init();

			
			ArrayList<Node> labels = new ArrayList<Node>();
			for(Node n : newContentPane.getChildren()){
				labels.add(n);
			}
			ArrayList<Node> labelscopy = (ArrayList<Node>) labels.clone();
			ArrayList<Player> players;
			if (formation == currentManager.getFormation()) {
				players = fController.loadPlayers();
			} else {
				players = (ArrayList<Player>) currentManager.getPlayers();

			}
			int index;
			for (Player player : players) {
				index = 0;
				for (Node node : labels) {
					try {
						PlayerLabel label = ((PlayerLabel) node);
						Node cNode = labelscopy.get(index);
						cNode = null;
						if (label.getPosition().equals(player.getPosition())) {
							label = player.getLabel();
							break;
						}
						index++;
					} catch (NullPointerException e) {
					}
				}
			}
			index = 0;
			for (Node n : labelscopy) {
				if (n != null) {
					Node l = labels.get(index);
					((PlayerLabel) l).setText("kein Spieler");
				}
				index++;
			}
			// ArrayList<Player> defence = new ArrayList<Player>();
			// ArrayList<Player> middel = new ArrayList<Player>();
			// ArrayList<Player> offence = new ArrayList<Player>();
			// Player keeper = players.get(0);
			// for (Player p : players) {
			// switch (p.getPosition()) {
			// case String.DEFENCE:
			// defence.add(p);
			// break;
			// case String.MIDDLE:
			// middel.add(p);
			// break;
			// case String.OFFENCE:
			// offence.add(p);
			// break;
			// case String.KEEPER:
			// keeper = p;
			// break;
			// default:
			// break;
			// }
			// }
			// int[] index = { 0, 0, 0 };
			// PlayerLabel l;
			// for (Node node : newContentPane.getChildren()) {
			// l = (PlayerLabel) node;
			// try {
			// if (l.getText().contains("Verteifigung")) {
			// l.setText(defence.get(index[0]).getName() + "; "
			// + defence.get(index[0]).getPoints());
			// index[0]++;
			// } else if (l.getText().contains("Mittelfeld")) {
			// l.setText(middel.get(index[1]).getName() + "; "
			// + middel.get(index[1]).getPoints());
			// index[1]++;
			// } else if (l.getText().contains("Keeper")) {
			// l.setText(keeper.getName() + "; " + keeper.getPoints());
			// } else if (l.getText().contains("Sturm")) {
			// l.setText(offence.get(index[2]).getName() + "; "
			// + offence.get(index[2]).getPoints());
			// index[2]++;
			//
			// }
			//
			// } catch (NullPointerException e) {
			// e.printStackTrace();
			// // DIALOG NICHT GENUG VETEDIGER ODERSO
			// l.setText("kein Spieler");
			//
			// }
			// }

			fController.setClickedListener();
			lineUpFrame.add(newContentPane, 0, 0);
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
		Manager m = s.getCurrentCommunity().getManager(currentManagerID);
		if (currentFormation != m.getFormation()
				|| fController.getCurrentPlayers() != m.getLineUp()) {
			Controller.getInstance().save(fController.getCurrentPlayers(),
					currentFormation);
		} else {
			// Nix zu speichern
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
			l.setText(formation.getName());
			dialog.add(l, 0, i);
			i++;
			l.setOnMouseClicked(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					dialogStage.close();
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
	public void messageArrived() {
		// TODO Auto-generated method stub
		
	}

}