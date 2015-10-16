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
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Position;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;

public class FormationController {
	@FXML
	private GridPane formationFrame;

	private ArrayList<Player> players;
	
	public ArrayList<Player> getCurrentPlayers(){
		return players;
	}

	public FormationController() {
		players = new ArrayList<Player>();
	}

	public GridPane getFrame() {
		return formationFrame;
	}

	public void loadPlayers() {
		Session session = Controller.getInstance().getSession();
		players = (ArrayList<Player>) session.getCommunityMap()
				.get(session.getCommunityMap())
				.getManager(session.getCurrentManager()).getLineUp();
	}

	public ArrayList<Label> getPlayers() {
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
				
		
		for (Node n : formationFrame.getChildren()) {
			((Label) n).setOnMouseClicked(new EventHandler<Event>() {
				Player currentPlayer = players.get(0);

				@Override
				public void handle(Event event) {

					loadPlayers();

					String playername = ((Label) n).getText().split(";")[0];
					for (Player p : players) {
						if (p.getName().equals(playername)) {
							currentPlayer = p;
							break;
						}
					}

					GridPane dialog;
					Stage dialogStage = new Stage();
					try {
						dialog = new GridPane();
						Label l;
						int i = 0;
						for (Player player : players) {
							if (player.getPosition() == currentPlayer
									.getPosition()) {
								l = new Label();
								dialog.add(l, 0, i);
								i++;
								l.setOnMouseClicked(new EventHandler<Event>() {
									@Override
									public void handle(Event event) {
										players.remove(currentPlayer);
										players.add(player);
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
