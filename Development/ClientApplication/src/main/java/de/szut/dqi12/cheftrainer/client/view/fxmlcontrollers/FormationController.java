package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Position;

public class FormationController {
	@FXML
	private GridPane formationFrame;

	private ArrayList<Player> players;

	public FormationController() {
		players = new ArrayList<Player>();
	}

	public GridPane getFrame() {
		return formationFrame;
	}

	public ArrayList getPlayers() {
		ArrayList<Label> players = new ArrayList<Label>();
		try {
			for (Node n : formationFrame.getChildren()) {
				players.add(((Label) n));
			}
			return players;
		} catch (NullPointerException n) {
			return null;
		}
	}

	public void setClickedListener() {
		players.add(new Player(2423, "hans", 1243, Position.Keeper));

		for (Node n : formationFrame.getChildren()) {
			((Label) n).setOnMouseClicked(new EventHandler<Event>() {

				@Override
				public void handle(Event event) {

					// players = getPlayers

					// Die Klasse Player koennte eine Funktion: generate Label
					// enthalten.

					Position po = Position.Keeper;
					for (Player p : players) {
						// GetText muss durch etwas anderes ersetzt werden, denn
						// im Labelnamen steht nicht immer nur der Name
						if (p.getName().equals(((Label) n).getText())) {
							po = p.getPosition();
						}
					}

					GridPane dialog;
					Stage dialogStage = new Stage();
					try {
						dialog = new GridPane();
						Label l;
						int i = 0;
						for (Player player : players) {
							if (player.getPosition() == po) {
								l = new Label();
								dialog.add(l, 0, i);
								i++;
								l.setOnMouseClicked(new EventHandler<Event>() {
									@Override
									public void handle(Event event) {

										// Spieler der Aufstellung hinzufuegen!
										dialogStage.close();
										
									}
								});
							}
						}

						dialogStage.setResizable(false);
						dialogStage.setTitle("SPIELER");
						dialogStage.initModality(Modality.WINDOW_MODAL);
						Scene scene = new Scene(dialog);

						dialogStage.setScene(scene);
						// GUIController.getInstance().setCurrentDialogStage(dialogStage);
						dialogStage.showAndWait();
						// GUIController.getInstance().setCurrentDialogStage(null);
					} catch (Exception e) {
						e.printStackTrace();
					}

					//
					// optionstage.setScene(new Scene(vBox, 100, 100));
					// //stage.setScene(new Scene(new Group(new Text(50,50,
					// "my second window"))));
					//
					// optionstage.initStyle(StageStyle.UNDECORATED);
					// optionstage.showAndWait();;

				}
			});
		}
	}
}
