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
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.MarketPlayer;

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

	public TransferMarketController() {
		data = FXCollections.observableArrayList();

	}

	public void init() {
		addListener();
	}

	public void addAll(ArrayList<MarketPlayer> players) {
		for (MarketPlayer player : players) {
			data.add(player);
		}
		nameCol.setCellValueFactory(data -> data.getValue().getPlayerName());
		pointsCol.setCellValueFactory(data -> data.getValue().getPoints());
		werthCol.setCellValueFactory(data -> data.getValue().getPrice());
		marketTable.setItems(data);
	}

	public void reloadTable(ArrayList<MarketPlayer> players) {
		data.clear();
		addAll(players);
	}

	public void addRow(MarketPlayer player) {
		data.add(player);
		nameCol.setCellValueFactory(data -> data.getValue().getPlayerName());
		pointsCol.setCellValueFactory(data -> data.getValue().getPoints());
		werthCol.setCellValueFactory(data -> data.getValue().getPrice());
		marketTable.setItems(data);
	}

	//DATEN IN DER FUNKTION BEFUELLEN
	public void addPlayer(){
		//
		//Get Players
		//
		GridPane dialog = new GridPane();
		Button but;
		
		int i = 0;
		For(Player player : players){
			but = new Button("put on market");
			but.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					//
					//AUF DEN MARKT SETZTEN
					//
					
					GridPane dlog = new GridPane();
					dlog.add(new Label()player.getName()), 1, 0);
					TextField field = new TextField();
					field.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
							public void handle( KeyEvent t ) {
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
							
							//
							//Spieler auf den Markt setzten!!!!!!!
							//
							textfield.getText()
							//
							
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

	//DATEN MIT RICHTIGEM INHALT BEFUELLEN
	public void showOffers() {
		//
		// Get Offers
		//
		GridPane dialog = new GridPane();
		
		for(Offer offer : offers){
			dialog.add(new Label(offer.getPlayerName()), 0, 1);
			Button but = new Button("remove from market");
			but.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					
					offers.remove(offer);
					
				}
			});
			dialog.add(but, 1, 0);
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
					System.out.println(rowData.getPrice().get());

					GridPane dialog = new GridPane();
					Stage dialogStage = new Stage();
					dialog.add(new Label("Name of the Player "), 0, 0);
					dialog.add(new Label(rowData.getPlayerName().get()), 0, 1);
					dialog.add(new Label("Actual Points "), 1, 0);
					dialog.add(new Label(rowData.getPoints().get()), 1, 1);
					dialog.add(new Label("Werth "), 2, 0);
					dialog.add(new Label(rowData.getPrice().get()), 2, 1);
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

							System.out.println(field.getText());
							//
							// Berechnungen ob Der Preis passt
							// Gebot wird abgegeben

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
