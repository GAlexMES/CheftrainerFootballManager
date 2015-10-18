package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.MarketPlayer;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Transaction;

public class TransferMarketController {
	// @FXML
	// GridPane market;
	@FXML
	TableView<MarketPlayer> marketTable;
	@FXML
	private TableColumn<MarketPlayer, String> nameCol;
	@FXML
	private TableColumn<MarketPlayer, String> pointsCol;
	@FXML
	private TableColumn<MarketPlayer, String> werthCol;
	private ObservableList<MarketPlayer> data;
	private ArrayList<Player> players;
	private ArrayList<Transaction> transactions;

	public TransferMarketController() {
		Community com = Controller
				.getInstance()
				.getSession()
				.getCommunityMap()
				.get(Controller.getInstance().getSession()
						.getCurrentCommunity());
		data = FXCollections.observableArrayList();
		players = (ArrayList<Player>) com.getMarket().getPlayers();
		transactions = (ArrayList<Transaction>) com.getManagers()
				.get(Controller.getInstance().getSession().getCurrentManager())
				.getTransactions();
	}

	public void init() {
		addListener();
		addAll();
	}

	public void addAll() {
		for (Player p : players) {
			data.add(new MarketPlayer(p.getName(),
					String.valueOf(p.getPoints()), String.valueOf(p.getWorth())));
		}
		nameCol.setCellValueFactory(data -> data.getValue().getPlayerName());
		pointsCol.setCellValueFactory(data -> data.getValue().getPoints());
		werthCol.setCellValueFactory(data -> data.getValue().getWerth());
		marketTable.setItems(data);
	}

	public void reloadTable(ArrayList<Player> players) {
		data.clear();
		this.players = players;
		addAll();
	}

	public void addRow(MarketPlayer player) {
		data.add(player);
		nameCol.setCellValueFactory(data -> data.getValue().getPlayerName());
		pointsCol.setCellValueFactory(data -> data.getValue().getPoints());
		werthCol.setCellValueFactory(data -> data.getValue().getWerth());
		marketTable.setItems(data);
	}

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
					field.addEventFilter(KeyEvent.KEY_TYPED,
							new EventHandler<KeyEvent>() {
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

							Controller.getInstance().setPlayeronMarket(player,
									Integer.valueOf(field.getText()));

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

	public void showOffers() {
		
		GridPane dialog = new GridPane();
		

		Button but;
		int index = 1;
		dialog.add(new Label("player"), 0, 0);
		dialog.add(new Label("price"), 1, 0);
		dialog.add(new Label("action"), 2, 0);
		for (Transaction tr : transactions) {
			if(tr.isOutgoing()){
				but = new Button("remove from market");
				but.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {

						Controller.getInstance().answerOffer(tr, false);
					}
				});
			}else{
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

	public void addListener() {
		marketTable.setRowFactory(tv -> {
			TableRow<MarketPlayer> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					MarketPlayer rowData = row.getItem();
					System.out.println(rowData.getWerth().get());

					GridPane dialog = new GridPane();
					Stage dialogStage = new Stage();
					
					//INDEXE MUESSTEN MOEGLICHERWEISE UEBERARBEITET WERDEN
					
					dialog.add(new Label("Name of the Player "), 0, 0);
					dialog.add(new Label(rowData.getPlayerName().get()), 0, 1);
					dialog.add(new Label("Actual Points "), 1, 0);
					dialog.add(new Label(rowData.getPoints().get()), 1, 1);
					dialog.add(new Label("Werth "), 2, 0);
					dialog.add(new Label(rowData.getWerth().get()), 2, 1);
					TextField field = new TextField();
					field.addEventFilter(KeyEvent.KEY_TYPED,
							new EventHandler<KeyEvent>() {
								public void handle(KeyEvent t) {
									char ar[] = t.getCharacter().toCharArray();
									char ch = ar[t.getCharacter().toCharArray().length - 1];
									if (!(ch >= '0' && ch <= '9')) {
										t.consume();
									}
								}
							});
					Button but = new Button("offer");
					but.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							dialogStage.close();
							Player currentPlayer;
							if (Integer.valueOf(field.getText()) >= Integer
									.valueOf(rowData.getWerth().toString())) {
								for (Player player : players) {
									if (player.getName().equals(
											rowData.getPlayerName().toString())) {
										currentPlayer = player;
										Controller.getInstance()
												.sendOffer(
														currentPlayer,
														Integer.valueOf(field
																.getText()));
										break;
									}
								}
							} else {
								// Hinweis anzeigen: Falsches angebot
							}
							//
						}
					});
					dialog.add(field, 3, 1);
					dialog.add(but, 4, 1);
					dialogStage.setResizable(false);
					dialogStage.setTitle("SPIELER");
					dialogStage.initModality(Modality.WINDOW_MODAL);
					Scene scene = new Scene(dialog);

					dialogStage.setScene(scene);
					dialogStage.showAndWait();
				}
			});
			return row;
		});
	}
}
