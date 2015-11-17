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
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
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
	
	private Map<Integer,Image> imageUpdateStack;
	
	private Boolean putImageToStack = false;
	
	public FormationController() {
		players = new ArrayList<Player>();
		imageUpdateStack = new HashMap<>();
	}
	public void generateImage(Player player) {
		String text = player.getName() + "\n" + player.getPosition()
				+ " Points: " + player.getPoints();
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

	@SuppressWarnings("unchecked")
	public void init() {
		// Initialisation of an not-playing-Players
		// ArrayList
		notPlayingPlayers = (ArrayList<Player>) getAllPlayers().clone();

		ArrayList<Player> bufferArray = (ArrayList<Player>) notPlayingPlayers
				.clone();
		for (Player player : bufferArray) {
			if (player.isPlays()) {
				notPlayingPlayers.remove(player);
			}
		}
		currentPlayers = new ArrayList<Player>();
		putImageToStack = true;
		for (Player player : getAllPlayers()) {
			PlayerLabel l = new PlayerLabel();
			l.setPlayerId(player.getID());
			l.setPosition(player.getPosition());
			player.setLabel(l);
//			generateImage(player);
			ImageController c = new ImageController(this);
			player.getLabel().setImage(c.getPicture(player));
		}
		putImageToStack = false;
		checkForImageUpdate();

		ArrayList<Node> copy = new ArrayList<Node>();
		for (Node n : formationFrame.getChildren()) {
			copy.add(n);
		}
		ArrayList<Player> orderPlayers = (ArrayList<Player>) loadPlayingPlayers()
				.clone();
		currentPlayers = (ArrayList<Player>) orderPlayers.clone();

		ArrayList<Node> buffer = (ArrayList<Node>) copy.clone();
		ArrayList<Player> playerArray = (ArrayList<Player>) loadPlayingPlayers()
				.clone();
		int i = 0;
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
			String position = null;

			switch (row) {
			case 0:
				position = Position.OFFENCE;
				break;
			case 1:
				position = Position.MIDDLE;
				break;

			case 2:
				position = Position.DEFENCE;
				break;
			case 3:
				position = Position.KEEPER;
				break;
			default:
				break;
			}
			i = 0;
			if (loadPlayingPlayers().size() < 11) {
				for (Player p : playerArray) {
					if (p.getPosition().equals(position)) {

						formationFrame.add(
								(Node) playerArray.get(i).getLabel(), col, row);
						formationFrame.getChildren().remove(n);
						playerArray.remove(p);
						break;
					}
					i++;
					if (i == playerArray.size()){
						for(Player pl : notPlayingPlayers){
							if(pl.getPosition().equals(position)){
								formationFrame.add((Node)pl.getLabel(), col, row);
								pl.setPlays(true);
								notPlayingPlayers.remove(pl);
								break;
							}
						}
					}
				}
			} else {
				for (Player p : playerArray) {
					if (p.getPosition().equals(position)) {

						formationFrame.add(
								(Node) playerArray.get(i).getLabel(), col, row);
						formationFrame.getChildren().remove(n);
						playerArray.remove(p);
						break;
					}
					i++;
				}
			}
		}

		// i = 0;
		// copy = new ArrayList<Node>();
		// for (Node n : formationFrame.getChildren()) {
		// copy.add(n);
		// }
		// buffer = (ArrayList<Node>) copy.clone();
		// for (Node n : buffer) {
		// int row;
		// try {
		// row = formationFrame.getRowIndex(n);
		// } catch (NullPointerException e) {
		// row = 0;
		// }
		// int col;
		// try {
		// col = formationFrame.getColumnIndex(n);
		// } catch (Exception e) {
		// col = 0;
		// }
		//
		// if (((PlayerLabel) n).getPlayerId() == p.getID()) {
		// String position = null;
		//
		// if (position != null) {
		// for (Player nP : notPlayingPlayers) {
		// if (nP.getPosition().equals(position)) {
		// notPlayingPlayers.set(
		// notPlayingPlayers.indexOf(nP),
		// orderPlayers.get(i));
		// currentPlayers.set(currentPlayers
		// .indexOf(orderPlayers.get(i)), nP);
		// System.out.println(position + row);
		// formationFrame.add((Node) nP.getLabel(), col,
		// row);
		// formationFrame.getChildren().remove(n);
		// break;
		// }
		// }
		// } else {
		// System.out.println("haaalo");
		// }
		// i++;
		// }
		// }

		// }
	}

	public ArrayList<Player> getCurrentPlayers() {
		return currentPlayers;
	}


	public GridPane getFrame() {
		return formationFrame;
	}

	public ArrayList<Player> getAllPlayers() {
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
	public ArrayList<Player> loadPlayingPlayers() {
		players.clear();
		Session session = Controller.getInstance().getSession();
		Community currentCommunity = session.getCurrentCommunity();
		int currentManagerID = session.getCurrentManagerID();
		int i = 0;
		for (Player player : (ArrayList<Player>) currentCommunity.getManager(
				currentManagerID).getPlayers()) {
			if (player.plays()) {
				players.add(player);
				i++;
			}
			if (players.size() >= 11) {
				break;
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

	// public void reDraw() {
	// int i = 0;
	// ArrayList<Node> copy = new ArrayList<Node>();
	// for (Node n : formationFrame.getChildren()) {
	// copy.add(n);
	// }
	// ArrayList<Node> buffer = (ArrayList<Node>) copy.clone();
	// for (Node n : buffer) {
	// try {
	// formationFrame.add((Node) currentPlayers.get(i).getLabel(),
	// formationFrame.getColumnIndex(n),
	// formationFrame.getRowIndex(n));
	// } catch (Exception e) {
	// formationFrame.add((Node) currentPlayers.get(i).getLabel(),
	// formationFrame.getColumnIndex(n), 0);
	// }
	// formationFrame.getChildren().remove(n);
	// i++;
	// }
	// }

	/**
	 * This method adds an listener for every Label, which opens an dialog to
	 * change a player
	 */
	public void setClickedListener() {
		for (Node currentNode : formationFrame.getChildren()) {
			((PlayerLabel) currentNode)
					.setOnMouseClicked(new EventHandler<Event>() {
						Player currentPlayer;

						@Override
						public void handle(Event event) {
							int mId = Controller.getInstance().getSession()
									.getCurrentManagerID();

							// Load CurrentPlayer
							for (Player p : getAllPlayers()) {
								if (p.getID() == ((PlayerLabel) currentNode)
										.getPlayerId()) {
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
										if (player.getPosition().equals(
												currentPlayer.getPosition())) {
											l = player.getLabel();
											dialog.add(l, 0, i);
											i++;
											l.setOnMouseClicked(new EventHandler<Event>() {
												@Override
												public void handle(Event event) {
													// /
													int i = 0;
													ArrayList<Node> copy = new ArrayList<Node>();
													for (Node n : formationFrame
															.getChildren()) {
														copy.add(n);
													}
													ArrayList<Node> buffer = (ArrayList<Node>) copy
															.clone();
													for (Node n : buffer) {
														if (((PlayerLabel) n)
																.getPlayerId() == currentPlayer
																.getID()) {

															int row;
															int col;
															try {
																col = formationFrame
																		.getColumnIndex(n);
															} catch (Exception e) {
																col = 0;
															}
															try {
																row = formationFrame
																		.getRowIndex(n);
															} catch (NullPointerException e) {
																row = 0;
															}

															formationFrame
																	.add((Node) player
																			.getLabel(),
																			col,
																			row);

															formationFrame
																	.getChildren()
																	.remove(n);
															i++;
														}
													}
													// /
													notPlayingPlayers.set(
															notPlayingPlayers
																	.indexOf(player),
															currentPlayer);
													// currentPlayers.remove(currentPlayer);
													// currentPlayers.add(player);
													currentPlayers.set(
															currentPlayers
																	.indexOf(currentPlayer),
															player);
													dialogStage.close();
													setClickedListener();

												}
											});
										}
									}
									if (dialog.getChildren().size() > 0) {
										dialogStage.setResizable(false);
										dialogStage.setTitle("Player");
										dialogStage
												.initModality(Modality.WINDOW_MODAL);
										Scene scene = new Scene(dialog);

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
		if(putImageToStack){
			imageUpdateStack.put(id,image);
		}
		else{
			setImageToPlayer(id, image);
		}
	}
	
	private void setImageToPlayer(int playerID, Image image){
		for(Player player : getAllPlayers()){
			if(player.getSportalID() == playerID){
				player.getLabel().setImage(image);
				break;
			}
		}
	}
	
	private void checkForImageUpdate() {
		for(int i : imageUpdateStack.keySet()){
			setImageToPlayer(i, imageUpdateStack.get(i));
		}
		imageUpdateStack=new HashMap<>();
	}
}
