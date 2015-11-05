package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.PlayerLabel;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This is the controller of the different Formations
 * 
 * @author Robin
 */
public class FormationController {
	@FXML
	private GridPane formationFrame;

	private ArrayList<Player> players;

	public void generateImage(Player player){
		String text = player.getName();
		 Label label = new Label(text);
		    label.setMinSize(125, 125);
		    label.setMaxSize(125, 125);
		    label.setPrefSize(125, 125);
		    label.setStyle("-fx-background-color: white; -fx-text-fill:black;");
		    label.setWrapText(true);
		    Scene scene = new Scene(new Group(label));
		    WritableImage img = new WritableImage(125, 125) ;
		    scene.snapshot(img);
		    player.getLabel().setImage(img);

    
	}
	public void init() {
		for (Player player : getAllPlayers()) {
			PlayerLabel l = new PlayerLabel();
			l.setPlayerId(player.getID());
			l.setPosition(player.getPosition());
			player.setLabel(l);
			generateImage(player);
		}
		
		
		/////
		int i = 0;
		ArrayList<Node> copy = new ArrayList<Node>();
		for(Node n : formationFrame.getChildren()){
			copy.add(n);
		}
		ArrayList<Node> buffer = (ArrayList<Node>) copy.clone();
		for (Node n : buffer){
			try{
//			n = (Node)loadPlayers().get(i).getLabel();
//			System.out.println(formationFrame.getRowIndex(n));
			formationFrame.add((Node)loadPlayers().get(i).getLabel(), formationFrame.getColumnIndex(n), formationFrame.getRowIndex(n));
			formationFrame.getChildren().remove(n);
			}catch(Exception e){
				formationFrame.add((Node)loadPlayers().get(i).getLabel(), formationFrame.getColumnIndex(n), 0);
			}
			i++;
		}
//		for (Node n : formationFrame.getChildren()){
//			System.out.println("ID= " +((PlayerLabel)n).getPlayerId());
//		}
		////
		
		
		// for (Node n : formationFrame.getChildren()){
		// switch(((Label)n).getText()){
		// case "Verteidigung":
		// n = new PlayerLabel();
		// ((PlayerLabel)n).setPosition(Position.DEFENCE);
		// // ((Label)n).setText("11");
		// break;
		// case "Sturm":
		// n = new PlayerLabel();
		// ((PlayerLabel)n).setPosition(Position.OFFENCE);
		// break;
		// case "Mittelfeld":
		// n = new PlayerLabel();
		// ((PlayerLabel)n).setPosition(Position.MIDDLE);
		// break;
		// case "Keeper":
		// n = new PlayerLabel();
		// ((PlayerLabel)n).setPosition(Position.KEEPER);
		// break;
		// default:
		// n = new PlayerLabel();
		// break;
		// }
		//
		// }
	}

	public ArrayList<Player> getCurrentPlayers() {
		return players;
	}

	public FormationController() {
		players = new ArrayList<Player>();
	}

	public GridPane getFrame() {
		return formationFrame;
	}
	public ArrayList<Player> getAllPlayers(){
		players.clear();
		Session session = Controller.getInstance().getSession();
		Community currentCommunity = session.getCurrentCommunity();
		int currentManagerID = session.getCurrentManagerID();
		for (Player player : (ArrayList<Player>) currentCommunity.getManager(
				currentManagerID).getPlayers()) {
				players.add(player);
		}
		// players = (ArrayList<Player>)
		// currentCommunity.getManager(currentManagerID).getLineUp();
		return players;
	}

	/**
	 * This method loads the players of the current Manager
	 */
	public ArrayList<Player> loadPlayers() {
		players.clear();
		Session session = Controller.getInstance().getSession();
		Community currentCommunity = session.getCurrentCommunity();
		int currentManagerID = session.getCurrentManagerID();
		for (Player player : (ArrayList<Player>) currentCommunity.getManager(
				currentManagerID).getPlayers()) {
			if (player.plays()) {
				players.add(player);
			}
		}
		// players = (ArrayList<Player>)
		// currentCommunity.getManager(currentManagerID).getLineUp();
		return players;
	}

	/**
	 * This method generates a Label for every player
	 * 
	 * @return ArrayList which contains all Labels for every player
	 * @deprecated
	 */
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

	/**
	 * This method adds an listener for every Label, which opens an dialog to
	 * change a player
	 */
	public void setClickedListener() {
		loadPlayers();
		for (Node currentNode : formationFrame.getChildren()) {
			((PlayerLabel) currentNode).setOnMouseClicked(new EventHandler<Event>() {
				Player currentPlayer;

				@Override
				public void handle(Event event) {
					int mId = Controller.getInstance().getSession()
							.getCurrentManagerID();
					// Initialisation of an not-playing-Players ArrayList
					ArrayList<Player> notPlayingPlayers = (ArrayList<Player>) getAllPlayers().clone();

					ArrayList<Player> buffer = (ArrayList<Player>) notPlayingPlayers
							.clone();
					for (Player player : buffer) {
						if (player.isPlays()) {
							notPlayingPlayers.remove(player);
						}
					}
					buffer = null;
					
					// Load CurrentPlayer
					for (Player p : getAllPlayers()) {
						if (p.getID() == ((PlayerLabel) currentNode).getPlayerId()) {
							System.out.println("aktueller Player= " + p.getName());
							currentPlayer = p;
							break;
						}
					}
					if (notPlayingPlayers.contains(currentPlayer)) {
						notPlayingPlayers.remove(currentPlayer);
					}
					if (notPlayingPlayers.size() != 0) {
						GridPane dialog;
						Stage dialogStage = new Stage();
						try {
							dialog = new GridPane();
							PlayerLabel l;
							int i = 0;
							for (Player player : notPlayingPlayers) {
								System.out.println(player.getPosition());
								if (player.getPosition().equals(currentPlayer.getPosition())) {
									l = player.getLabel();
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
							if (dialog.getChildren().size() > 0) {
								dialogStage.setResizable(false);
								dialogStage.setTitle("Player");
								dialogStage.initModality(Modality.WINDOW_MODAL);
								Scene scene = new Scene(dialog);

								dialogStage.setScene(scene);
								// GUIController.getInstance().setCurrentDialogStage(dialogStage);
								dialogStage.showAndWait();
							}
							// GUIController.getInstance().setCurrentDialogStage(null);
						} catch (Exception e) {
							e.printStackTrace();
						}
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
