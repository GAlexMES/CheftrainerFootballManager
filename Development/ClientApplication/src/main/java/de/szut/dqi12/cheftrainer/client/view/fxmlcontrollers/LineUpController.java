package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Formation;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LineUpController {
	@FXML
	private GridPane lineUpFrame;

	private Formation currentFormation;
	private FormationController fController;

	public GridPane getFrame() {
		return lineUpFrame;
	}

	public FXMLLoader getLoader(Formation formation) {
		currentFormation = formation;
		ClassLoader classLoader = getClass().getClassLoader();
		FXMLLoader currentContentLoader = new FXMLLoader();

		URL fxmlFile;
		switch (formation) {
		case forfortwo:
			fxmlFile = classLoader.getResource("sourcesFXML/442.fxml");
			break;
		case vorfiveone:
			fxmlFile = classLoader.getResource("sourcesFXML/451.fxml");
			break;
		default:
			return null;
		}
		currentContentLoader.setLocation(fxmlFile);
		return currentContentLoader;

	}

	public boolean init() {
		// BUG: es muss auf namen des Labels geprueft werden. Position muss
		// stimmen.
		// Aktuell werden spieler einfach nacheinander reingeschmissen
		try {
			Session session = Controller.getInstance().getSession();
			ClassLoader classLoader = getClass().getClassLoader();
			FXMLLoader currentContentLoader = getLoader(session
					.getCommunityMap().get(session.getCurrentCommunity())
					.getManagers().get(session.getCurrentManager())
					.getFormation());

			GridPane newContentPane = (GridPane) currentContentLoader.load();
			fController = ((FormationController) currentContentLoader
					.getController());
			fController.setClickedListener();
			try {
				ArrayList<Player> players = (ArrayList<Player>) session
						.getCommunityMap().get(session.getCommunityMap())
						.getManager(session.getCurrentManager()).getLineUp();
				ArrayList<Player> defence = new ArrayList<Player>();
				ArrayList<Player> middel = new ArrayList<Player>();
				ArrayList<Player> offence = new ArrayList<Player>();
				Player keeper = players.get(0);
				for (Player p : players) {
					switch (p.getPosition()) {
					case Defence:
						defence.add(p);
						break;
					case Middel:
						middel.add(p);
						break;
					case Offence:
						offence.add(p);
						defence: break;
					case Keeper:
						keeper = p;
						break;
					default:
						break;
					}
				}
				players.clear();
				players.addAll(offence);
				players.addAll(middel);
				players.addAll(defence);
				players.add(keeper);
				int index = 0;
				for (Node node : newContentPane.getChildren()) {
					((Label) node).setText(players.get(index).getName() + "; "
							+ players.get(index).getPoints());
					index++;
				}
				lineUpFrame.add(newContentPane, 0, 0);
				return true;
			} catch (NullPointerException e) {
				e.printStackTrace();
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean changeFormation(Formation formation) {
		// UMFERTIG LABELS NAMEN UEBERGEBEN
		try {
			Session session = Controller.getInstance().getSession();
			ClassLoader classLoader = getClass().getClassLoader();
			FXMLLoader currentContentLoader = getLoader(session
					.getCommunityMap().get(session.getCurrentCommunity())
					.getManagers().get(session.getCurrentManager())
					.getFormation());
			GridPane newContentPane = (GridPane) currentContentLoader.load();

			// ///////vieleicht falsche sachen, falsche player weil er aus
			// controller holt
			fController = ((FormationController) currentContentLoader
					.getController());
			fController.setClickedListener();
			// /////////////////////
			ArrayList<Player> players = (ArrayList<Player>) session
					.getCommunityMap().get(session.getCommunityMap())
					.getManager(session.getCurrentManager()).getPlayers();
			ArrayList<Player> defence = new ArrayList<Player>();
			ArrayList<Player> middel = new ArrayList<Player>();
			ArrayList<Player> offence = new ArrayList<Player>();
			Player keeper = players.get(0);
			for (Player p : players) {
				switch (p.getPosition()) {
				case Defence:
					defence.add(p);
					break;
				case Middel:
					middel.add(p);
					break;
				case Offence:
					offence.add(p);
					defence: break;
				case Keeper:
					keeper = p;
					break;
				default:
					break;
				}
			}
			int[] index = { 0, 0, 0 };
			Label l;
			for (Node node : newContentPane.getChildren()) {
				l = (Label) node;
				try {
					if (l.getText().contains("Verteifigung")) {
						l.setText(defence.get(index[0]).getName() + "; "
								+ defence.get(index[0]).getPoints());
						index[0]++;
					} else if (l.getText().contains("Mittelfeld")) {
						l.setText(middel.get(index[1]).getName() + "; "
								+ middel.get(index[1]).getPoints());
						index[1]++;
					} else if (l.getText().contains("Keeper")) {
						l.setText(keeper.getName() + "; " + keeper.getPoints());
					} else if (l.getText().contains("Sturm")) {
						l.setText(offence.get(index[2]).getName() + "; "
								+ offence.get(index[2]).getPoints());
						index[2]++;

					}

				} catch (NullPointerException e) {
					e.printStackTrace();
					// DIALOG NICHT GENUG VETEDIGER ODERSO
					l.setText("");

				}
			}

			lineUpFrame.add(newContentPane, 0, 0);
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void saveButtonClicked() {
		Session s = Controller.getInstance().getSession();
		Manager m = s.getCommunityMap().get(s.getCurrentCommunity())
				.getManagers().get(s.getCurrentManager());
		if (currentFormation != m.getFormation()
				|| fController.getCurrentPlayers() != m.getLineUp()) {
			Controller.getInstance().save(fController.getCurrentPlayers(),
					currentFormation);
		} else {
			// Nix zu speichern
		}
	}

	public void formationButtonClicked() {
		GridPane dialog;
		Stage dialogStage = new Stage();

		dialog = new GridPane();
		Label l;
		int i = 0;
		for (Formation formation : Formation.values()) {

			l = new Label(formation.name());
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

}
