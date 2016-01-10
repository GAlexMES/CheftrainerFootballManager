package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.client.images.ImageController;
import de.szut.dqi12.cheftrainer.client.images.ImageUpdate;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Market;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.MarketPlayer;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.PlayerLabel;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

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
		ArrayList<Player> players = (ArrayList<Player>) Controller.getInstance().getSession().getCurrentManager().getPlayers();

		ScrollPane sp = new ScrollPane();
		VBox content = new VBox();
		sp.setContent(content);

		ImageController iController = new ImageController(this);
		for (Player player : players) {

			PlayerLabel l = new PlayerLabel();
			l.setPlayerId(player.getID());
			l.setPosition(player.getPosition());
			l.setImage(iController.getPicture(player));
			player.setLabel(l);
			content.getChildren().add(l);

			l.setOnMouseClicked(new EventHandler<Event>() {

				@Override
				public void handle(Event event) {

					GridPane dlog = new GridPane();
					dlog.add(new Label("Player:"), 1, 0);
					dlog.add(new Label("Price:"), 2, 0);
					dlog.add(new Label(player.getName()), 1, 1);
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
					dlog.add(field, 2, 1);
					Button butt = new Button("send");
					dlog.add(butt, 3, 1);
					Stage stage = new Stage();
					Scene s1 = new Scene(dlog);
					stage.setTitle("offer");
					stage.setScene(s1);
					stage.initModality(Modality.WINDOW_MODAL);
					butt.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							Controller.getInstance().setPlayeronMarket(player, Integer.valueOf(field.getText()));
							stage.close();
						}
					});
					stage.showAndWait();

				}
			});

		}

		Stage dialogStage = new Stage();
		// dialogStage.setResizable(false);
		dialogStage.setTitle("your players");
		dialogStage.initModality(Modality.WINDOW_MODAL);
		Scene scene = new Scene(sp);
		dialogStage.setScene(scene);
		dialogStage.showAndWait();

	}

	/**
	 * Is called when the Button "show offers" is called. Opens a dialog which
	 * shows all offers possibility's for every player.
	 */
	@FXML
	public void showOffers() {
		Session session = Controller.getInstance().getSession();
		Community con = session.getCurrentCommunity();
		List<Transaction> transactions = con.getMarket().getTransactions();
		GridPane dialog = new GridPane();
		Button but;
		int index = 1;
		dialog.add(new Label("player"), 0, 0);
		dialog.add(new Label("price"), 1, 0);
		dialog.add(new Label("action"), 2, 0);
		for (Transaction tr : transactions) {
			tr.takeInformation(session);
			if (tr.isOutgoing()) {
				but = new Button("remove offer");
				but.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {

						sendAnswerOffer(tr, false, true);
					}
				});
			} else {
				but = new Button("accept offer");
				but.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {

						sendAnswerOffer(tr, true, true);
					}
				});
			}
			// TODO: gebot ablehnen k?nnen
			// else if(tr.isOutgoing()) {
			// but = new Button("denie offer");
			// but.setOnAction(new EventHandler<ActionEvent>() {
			//
			// @Override
			// public void handle(ActionEvent event) {
			//
			// //sendAnswerOffer(tr, true, true);
			// }
			// });
			// }
			dialog.add(new Label(tr.getPlayer().getName()), 0, index);
			dialog.add(new Label(String.valueOf(tr.getOfferedPrice())), 1, index);
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

	private void sendAnswerOffer(Transaction tr, boolean accept, boolean remove) {
		JSONObject transactionJSON = new JSONObject();
		transactionJSON.put(MIDs.TRANSACTION, tr.toJSON());
		transactionJSON.put(MIDs.ACCEPT, accept);
		transactionJSON.put(MIDs.REMOVE, remove);

		JSONObject messageContent = new JSONObject();
		messageContent.put(MIDs.TYPE, MIDs.TRANSACTION);
		messageContent.put(MIDs.INFORMATION, transactionJSON);

		Message message = new Message(ClientToServer_MessageIDs.TRANSFER_MARKET);
		message.setMessageContent(messageContent);

		Controller.getInstance().sendMessageToServer(message);
	}

	@Override
	public void enterPressed() {

	}

	@Override
	public void updateImage(Image image, int id) {
		if (!updateIsBlocked) {
			updateTableView();
		}
	}

	@Override
	public void messageArrived(Boolean flag) {
		// TODO Auto-generated method stub
		
	}
}
