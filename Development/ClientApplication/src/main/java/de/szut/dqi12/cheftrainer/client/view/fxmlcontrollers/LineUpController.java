package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class LineUpController {
	@FXML
	private GridPane lineUpFrame;

	public LineUpController() {

	}

	public javafx.scene.layout.GridPane getFrame() {
		return lineUpFrame;
	}

	public boolean init() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			FXMLLoader currentContentLoader = new FXMLLoader();
			Session session = Controller.getInstance().getSession();
			URL fxmlFile;
			switch (session.getCommunityMap().get(session.getCommunityMap())
					.getManager(session.getCurrentManager()).getFormation()) {
			case forfortwo:
				fxmlFile = classLoader.getResource("sourcesFXML/442.fxml");
				break;
			case vorfiveone:
				fxmlFile = classLoader.getResource("sourcesFXML/451.fxml");
				break;
			default:
				return false;
			}
			currentContentLoader.setLocation(fxmlFile);
			GridPane newContentPane = (GridPane) currentContentLoader.load();
			((FormationController) currentContentLoader.getController())
					.setClickedListener();
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

	public void changeFormation(String path) {
		// UMFERTIG LABELS NAMEN UEBERGEBEN
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			FXMLLoader currentContentLoader = new FXMLLoader();
			Session session = Controller.getInstance().getSession();
			// URL fxmlFile;
			// switch (session.getCommunityMap().get(session.getCommunityMap())
			// .getManager(session.getCurrentManager()).getFormation()) {
			// case forfortwo:
			// fxmlFile = classLoader.getResource("sourcesFXML/" + path);
			// break;
			// case vorfiveone:
			// fxmlFile = classLoader.getResource("sourcesFXML/" + path);
			// break;
			// default:
			// break;
			// }
			URL fxmlFile = classLoader.getResource("sourcesFXML/" + path);
			currentContentLoader.setLocation(fxmlFile);
			GridPane newContentPane = (GridPane) currentContentLoader.load();
			((FormationController) currentContentLoader.getController())
					.setClickedListener();

			for (Node node : newContentPane.getChildren()) {
				System.out.println(((Label) node).getText());
			}
			lineUpFrame.add(newContentPane, 0, 0);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveButtonClicked() {

	}

	public void formationButtonClicked() {

	}

}
