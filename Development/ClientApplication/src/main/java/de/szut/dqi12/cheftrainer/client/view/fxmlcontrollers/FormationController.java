package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.images.ImageController;
import de.szut.dqi12.cheftrainer.client.images.ImageUpdate;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.PlayerLabel;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Position;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;

/**
 * This is the controller of the different Formations
 * 
 * @author Robin
 */
public class FormationController implements ImageUpdate {
	@FXML
	private GridPane formationFrame;

	private ArrayList<Player> players;
	private ArrayList<Player> currentPlayers;
	private ArrayList<Player> notPlayingPlayers;

	private Map<Integer, Image> imageUpdateStack;

	private Boolean putImageToStack = false;

	public FormationController() {
		players = new ArrayList<Player>();
		imageUpdateStack = new HashMap<>();
	}

	/**
	 * Ganerates an Image of different properties from an Player. The Image will setted in the PlayerLabel of the Player.
	 * @param player 
	 */
	public void generateImage(Player player) {
		String text = player.getName() + "\n" + player.getPosition() + " Points: " + player.getPoints();
		Label label = new Label(text);
		label.setMinSize(125, 125);
		label.setMaxSize(125, 125);
		label.setPrefSize(125, 125);
		label.setStyle("-fx-background-color: white; -fx-text-fill:black;");
		label.setWrapText(true);
		Scene scene = new Scene(new Group(label));
		WritableImage img = new WritableImage(125, 125);
		scene.snapshot(img);
		player.getLabel().setImage(img);

	}

	/**
	 * Generates an Image of an String
	 * @param text text, which is in the Image
	 * @return The generated Image
	 */
	public static WritableImage getImageOfString(String text) {
		Label label = new Label(text);
		label.setMinSize(125, 125);
		label.setMaxSize(125, 125);
		label.setPrefSize(125, 125);
		label.setStyle("-fx-background-color: white; -fx-text-fill:black;");
		label.setWrapText(true);
		Scene scene = new Scene(new Group(label));
		WritableImage img = new WritableImage(125, 125);
		scene.snapshot(img);
		return img;
	}

	/**
	 * Initialisation of the graphical formation whith the matching players.
	 */
	@SuppressWarnings("unchecked")
	public void init() {
		// Initialisation of an not-playing-Players
		notPlayingPlayers = new ArrayList<Player>();

		currentPlayers = new ArrayList<Player>();
		ArrayList<Player> playingPlayers = new ArrayList<Player>();
		for (Player player : getAllPlayers()) {
			if (player.isPlays()) {
				playingPlayers.add(player);
				currentPlayers.add(player);
			} else {
				notPlayingPlayers.add(player);

			}
		}
		putImageToStack = true;
		Image image;
		for (Player player : getAllPlayers()) {
			PlayerLabel l = new PlayerLabel();
			l.setPlayerId(player.getID());
			l.setPosition(player.getPosition());
			player.setLabel(l);
			ImageController c = new ImageController(this);
			image = c.getPicture(player);
			player.getLabel().setImage(image);
		}
		putImageToStack = false;
		checkForImageUpdate();

		ArrayList<Node> copy = new ArrayList<Node>();
		for (Node n : formationFrame.getChildren()) {
			copy.add(n);
		}

		ArrayList<Node> buffer = (ArrayList<Node>) copy.clone();
		boolean found;
		// Iteration durch alle Labels
		for (Node n : buffer) {
			int row;
			int col;
			try {
				col = formationFrame.getColumnIndex(n);
			} catch (Exception e) {
				col = 0;
			}
			try {
				row = formationFrame.getRowIndex(n);
			} catch (NullPointerException e) {
				row = 0;
			}
			String position = Position.getPositions().get(3 - row);

			found = false;
			for (Player p : playingPlayers) {
				if (p.getPosition().equals(position)) {
					formationFrame.add((Node) p.getLabel(), col, row);
					formationFrame.getChildren().remove(n);
					playingPlayers.remove(p);
					found = true;
					break;

				}
				if (!found) {
					for (Player pl : notPlayingPlayers) {

						if (pl.getPosition().equals(position)) {
							formationFrame.add((Node) pl.getLabel(), col, row);
							formationFrame.getChildren().remove(n);
							pl.setPlays(true);
							notPlayingPlayers.remove(pl);
							currentPlayers.add(pl);
							found = true;
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * @return an Array of all Players of the current Manager, which are playing.
	 */
	public ArrayList<Player> getCurrentPlayers() {
		currentPlayers = new ArrayList<Player>();
		for (Node n : formationFrame.getChildren()) {
			int id = ((PlayerLabel) n).getPlayerId();
			for (Player p : getAllPlayers()) {
				if (p.getID() == id) {
					currentPlayers.add(p);
					break;
				}
			}
		}
		return currentPlayers;
	}

	public GridPane getFrame() {
		return formationFrame;
	}

	/**
	 * Loads all existing Players of the current Manager in the current Leauge
	 * 
	 * @return List of all Players
	 */
	public ArrayList<Player> getAllPlayers() {
		players.clear();
		Session session = Controller.getInstance().getSession();
		Community currentCommunity = session.getCurrentCommunity();
		int currentManagerID = session.getCurrentManagerID();
		for (Player player : (ArrayList<Player>) currentCommunity.getManager(currentManagerID).getPlayers()) {
			players.add(player);
		}
		return players;
	}

	/**
	 * @return An ArrayList of all Player of the current Manager, which are not playing.
	 */
	public ArrayList<Player> getNotPlayingPlayers() {
		boolean found;
		getCurrentPlayers();
		getAllPlayers();
		notPlayingPlayers = new ArrayList<Player>();
		for (Player p : players) {
			found = false;
			for (Player pl : currentPlayers) {
				if (pl == p) {
					found = true;
					break;
				}
			}
			if (!found) {
				notPlayingPlayers.add(p);
			}
		}
		return notPlayingPlayers;
	}

	/**
	 * This method adds an listener for every Label, which opens an dialog to
	 * change a player
	 */
	public void setClickedListener() {
		getCurrentPlayers();
		ScrollPane scrollPane = new ScrollPane();
		VBox content = new VBox();
		
		// Iteration durch alle Nodes
		for (Node currentNode : formationFrame.getChildren()) {

			// Listener fuer geklickes Label
			((PlayerLabel) currentNode).setOnMouseClicked(new EventHandler<Event>() {
				Player currentPlayer;

				@Override
				public void handle(Event event) {
					content.getChildren().clear();
					// Load CurrentPlayer
					for (Player p : getAllPlayers()) {
						if (p.getID() == ((PlayerLabel) currentNode).getPlayerId()) {
							currentPlayer = p;
							break;
						}
					}
					getNotPlayingPlayers();
					if (notPlayingPlayers.contains(currentPlayer)) {
						notPlayingPlayers.remove(currentPlayer);
					}
					// Erstellung eines Dialogs zur Auswahl eines Spielers
					if (notPlayingPlayers.size() != 0) {
						Stage dialogStage = new Stage();
						try {
							PlayerLabel l;
							int i = 0;
							// Iteration durch alle nicht-spielenden Spieler
							for (Player player : notPlayingPlayers) {
								if (player.getPosition().equals(currentPlayer.getPosition())) {
									l = player.getLabel();
									content.getChildren().add(l);
									i++;
									// Listener fuer das klicken eines Labels im
									// Dialog
									l.setOnMouseClicked(new EventHandler<Event>() {
										@Override
										public void handle(Event event) {
											// /
											int i = 0;
											ArrayList<Node> copy = new ArrayList<Node>();
											for (Node n : formationFrame.getChildren()) {
												copy.add(n);
											}
											ArrayList<Node> buffer = (ArrayList<Node>) copy.clone();
											for (Node n : buffer) {
												if (((PlayerLabel) n).getPlayerId() == currentPlayer.getID()) {

													int row;
													int col;
													try {
														col = formationFrame.getColumnIndex(n);
													} catch (Exception e) {
														col = 0;
													}
													try {
														row = formationFrame.getRowIndex(n);
													} catch (NullPointerException e) {
														row = 0;
													}

													formationFrame.add((Node) player.getLabel(), col, row);

													formationFrame.getChildren().remove(n);
													i++;
												}
											}
											// /
											notPlayingPlayers.set(notPlayingPlayers.indexOf(player), currentPlayer);
											// currentPlayers.remove(currentPlayer);
											// currentPlayers.add(player);
											currentPlayers.set(currentPlayers.indexOf(currentPlayer), player);
											dialogStage.close();
											setClickedListener();

										}
									});
								}
							}
							if (content.getChildren().size() > 0) {
								scrollPane.setContent(content);
//								dialogStage.setResizable(false);
								dialogStage.setTitle("Player");
								dialogStage.initModality(Modality.WINDOW_MODAL);
								Scene scene = new Scene(scrollPane);

								dialogStage.setScene(scene);
								dialogStage.showAndWait();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			});
		}
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
	 * Loads the Image of the matching Player and set it into the Label of the Player.
	 * @param playerID ID of the Player
	 * @param image Image of the Player
	 */
	private void setImageToPlayer(int playerID, Image image) {
		for (Player player : getAllPlayers()) {
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
}
