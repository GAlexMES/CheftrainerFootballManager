package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.client.images.ImageController;
import de.szut.dqi12.cheftrainer.client.images.ImageUpdate;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.MarketPlayer;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;

/**
 * This is the controller of the transfer-market gui-component.
 * 
 * @author Robin and Alexander
 *
 */

public class TransferMarketController implements ControllerInterface, ImageUpdate {
	// @FXML
	// GridPane market;
	@FXML
	TableView<MarketPlayer> marketTable;
	@FXML
	private TableColumn<MarketPlayer, Player> nameCol;
	@FXML
	private TableColumn<MarketPlayer, String> pointsCol;
	@FXML
	private TableColumn<MarketPlayer, String> werthCol;

	private ArrayList<Player> players;
	private ArrayList<Transaction> transactions;

	private MarketPlayer selectedMarketPlayer;

	private ImageController imageController;

	private boolean updateIsBlocked = false;

	/**
	 * init() function, which comes from the {@link ControllerInterface}. It is
	 * not used here.
	 */
	@Override
	public void init() {
	}

	/**
	 * This function is the initialize funktion of FXML. It is called from FXML,
	 * when this view should be displayed. This function fetches the players,
	 * which are on the {@link Market}, for the selected {@link Community}.
	 * After that, the {@link Player} will be added to the table.
	 */
	@FXML
	public void initialize() {
		imageController = new ImageController(this);
		marketTable.setItems(Controller.getInstance().getSession().getMarketPlayerObservable());
		nameCol.setCellValueFactory(new PropertyValueFactory<>("Player"));
		pointsCol.setCellValueFactory(data -> data.getValue().getPoints());
		werthCol.setCellValueFactory(data -> data.getValue().getWerth());

		marketTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> playerPressed(newValue));

		marketTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					onDoubleClick();
				}
			}
		});

		updateIsBlocked = true;

		// Sets the CellFactory to the image cells
		nameCol.setCellFactory(params -> {
			TableCell<MarketPlayer, Player> cell = new TableCell<MarketPlayer, Player>() {
				ImageView imageview = new ImageView();

				@Override
				public void updateItem(Player item, boolean empty) {
					if (item != null) {
						HBox box = new HBox();
						box.setSpacing(10);
						VBox vbox = new VBox();
						vbox.getChildren().add(new Label(item.getName()));

						imageview.setFitHeight(100);
						imageview.setFitWidth(100);

						Image image = imageController.getPicture(item);
						imageview.setImage(image);

						box.getChildren().addAll(imageview, vbox);

						setGraphic(box);
					}
				}
			};
			return cell;
		});
		updateIsBlocked = false;
		updateTableView();
	}

	/**
	 * Sets the visible of a element to false and back to true, because that
	 * triggers the update Event of the TableView.
	 */
	private void updateTableView() {
		TableColumn<MarketPlayer, ?> tc = marketTable.getColumns().get(0);
		if (tc != null) {
			tc.setVisible(false);
			tc.setVisible(true);
		}
	}

	private void onDoubleClick() {

		// INDEXE MUESSTEN MOEGLICHERWEISE UEBERARBEITET WERDEN
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("sourcesFXML/PlayerDetailedFrame.fxml"));
			GridPane root = (GridPane) fxmlLoader.load();
			Stage stage = new Stage();
			stage.setTitle(selectedMarketPlayer.getPlayer().getName());
			stage.setScene(new Scene(root));
			PlayerDetailedController pdc = fxmlLoader.getController();
			pdc.setPlayer(selectedMarketPlayer.getPlayer());
			pdc.showOffer();
			stage.setResizable(false);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// dialog.add(new Label("Name of the Player "), 0, 0);
		// dialog.add(new Label(selectedMarketPlayer.getPlayerName().get()), 0,
		// 1);
		// dialog.add(new Label("Actual Points "), 1, 0);
		// dialog.add(new Label(selectedMarketPlayer.getPoints().get()), 1, 1);
		// dialog.add(new Label("Worth "), 2, 0);
		// dialog.add(new Label(selectedMarketPlayer.getWerth().get()), 2, 1);
		// TextField field = new TextField();
		// field.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>()
		// {
		// public void handle(KeyEvent t) {
		// char ar[] = t.getCharacter().toCharArray();
		// char ch = ar[t.getCharacter().toCharArray().length - 1];
		// if (!(ch >= '0' && ch <= '9')) {
		// t.consume();
		// }
		// }
		// });
		// Button but = new Button("offer");
		// but.setOnAction(new EventHandler<ActionEvent>() {
		// @Override
		// public void handle(ActionEvent event) {
		// dialogStage.close();
		// Player currentPlayer;
		// if (Integer.valueOf(field.getText()) >=
		// Integer.valueOf(selectedMarketPlayer.getWerth().toString())) {
		// for (Player player : players) {
		// if
		// (player.getName().equals(selectedMarketPlayer.getPlayerName().toString()))
		// {
		// currentPlayer = player;
		// Controller.getInstance().sendOffer(currentPlayer,
		// Integer.valueOf(field.getText()));
		// break;
		// }
		// }
		// } else {
		// // Hinweis anzeigen: Falsches angebot
		// }
		// //
		// }
		// });
		// dialog.add(field, 3, 1);
		// dialog.add(but, 4, 1);
		// dialogStage.setResizable(false);
		// dialogStage.setTitle("Players");
		// dialogStage.initModality(Modality.WINDOW_MODAL);
		// Scene scene = new Scene(dialog);
		//
		// dialogStage.setScene(scene);
		// dialogStage.showAndWait();
	}

	private void playerPressed(MarketPlayer marketPlayer) {
		this.selectedMarketPlayer = marketPlayer;
	}

	/**
	 * Is called when the Button "add player" is clicked. Opens a dialog for
	 * adding a player to transfer-marked.
	 */
	@FXML
	public void addPlayer() {
		GridPane dialog = new GridPane();
		Button but;

		int i = 0;
		for (Player player : players) {
			but = new Button("put on market");
			but.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {

					GridPane dlog = new GridPane();
					dlog.add(new Label(player.getName()), 1, 0);
					TextField field = new TextField();
					field.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
						public void handle(KeyEvent t) {
							char ar[] = t.getCharacter().toCharArray();
							char ch = ar[t.getCharacter().toCharArray().length - 1];
							if (!(ch >= '0' && ch <= '9')) {
								t.consume();
							}
						}
					});
					dlog.add(field, 2, 0);
					Button butt = new Button("send");
					butt.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {

							Controller.getInstance().setPlayeronMarket(player, Integer.valueOf(field.getText()));

						}
					});
					Stage stage = new Stage();
					stage.setResizable(false);
					stage.setTitle("offer");
					stage.initModality(Modality.WINDOW_MODAL);
					Scene dialogScene = new Scene(dialog);
					stage.setScene(dialogScene);
					stage.showAndWait();

				}
			});
			dialog.add(new Label(player.getName()), i, 0);

		}

		Stage dialogStage = new Stage();
		dialogStage.setResizable(false);
		dialogStage.setTitle("your players");
		dialogStage.initModality(Modality.WINDOW_MODAL);
		Scene scene = new Scene(dialog);
		dialogStage.setScene(scene);
		dialogStage.showAndWait();
	}

	/**
	 * Is called when the Button "show offers" is called. Opens a dialog which
	 * shows all offers possibility's for every player.
	 */
	@FXML
	public void showOffers() {

		GridPane dialog = new GridPane();
		Button but;
		int index = 1;
		dialog.add(new Label("player"), 0, 0);
		dialog.add(new Label("price"), 1, 0);
		dialog.add(new Label("action"), 2, 0);
		for (Transaction tr : transactions) {
			if (tr.isOutgoing()) {
				but = new Button("remove from market");
				but.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {

						Controller.getInstance().answerOffer(tr, false);
					}
				});
			} else {
				but = new Button("accept");
				but.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {

						Controller.getInstance().answerOffer(tr, true);
					}
				});
			}

			dialog.add(new Label(tr.getPlayer().getName()), 0, index);
			dialog.add(new Label(String.valueOf(tr.getPrice())), 1, index);
			dialog.add(but, 2, index);
			index++;
		}

		Stage dialogStage = new Stage();
		dialogStage.setResizable(false);
		dialogStage.setTitle("offers");
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

	@Override
	public void updateImage(Image image, int id) {
		if (!updateIsBlocked) {
			updateTableView();
		}
	}
}
