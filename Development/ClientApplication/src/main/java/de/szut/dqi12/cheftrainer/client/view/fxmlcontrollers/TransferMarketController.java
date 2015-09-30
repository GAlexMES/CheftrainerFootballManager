package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.MarketPlayer;

public class TransferMarketController {
//	@FXML
//	GridPane market;
	@FXML
	TableView<MarketPlayer> marketTable;
	@FXML
	private TableColumn<MarketPlayer, String> nameCol;
	@FXML
	private TableColumn<MarketPlayer, String> pointsCol;
	@FXML
	private TableColumn<MarketPlayer, String> priceCol;
	private ObservableList<MarketPlayer> data;
	
	public TransferMarketController(){
		data = FXCollections.observableArrayList();
		
	}
	public void init(){
		addListener();
	}
	
	public void addAll(ArrayList<MarketPlayer> players){
		for(MarketPlayer player : players){
			data.add(player);
		}
		nameCol.setCellValueFactory(data -> data.getValue().getPlayerName());
		pointsCol.setCellValueFactory(data -> data.getValue().getPoints());
		priceCol.setCellValueFactory(data -> data.getValue().getPrice());
		marketTable.setItems(data);
	}
	
	public void reloadTable(ArrayList<MarketPlayer> players){
		data.clear();
		addAll(players);
	}
	
	public void addRow(MarketPlayer player){
		data.add(player);
		nameCol.setCellValueFactory(data -> data.getValue().getPlayerName());
		pointsCol.setCellValueFactory(data -> data.getValue().getPoints());
		priceCol.setCellValueFactory(data -> data.getValue().getPrice());
		marketTable.setItems(data);
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
					Label l;
					dialog.add(new Label(" Name of the Player "), 0, 0);
					dialog.add(new Label(rowData.getPlayerName().get()),0,1);
					dialog.add(new Label(" Actual Points "), 1, 0);
					dialog.add(new Label(rowData.getPoints().get()),1,1);
					dialog.add(new Label(" Price "), 2, 0);
					dialog.add(new Label(rowData.getPrice().get()),2,1);
					javafx.scene.control.Button but = new javafx.scene.control.Button("buy");
					but.setOnAction(new EventHandler<ActionEvent>() {
			            @Override
			            public void handle(ActionEvent event) {
			                dialogStage.close();
			                
			                
			                //SPIELER WIRD GEKAUFT
			                
			                
			                
			                
			                
			            }
			        });
					dialog.add(but, 3, 1);
					dialogStage.setResizable(false);
					dialogStage.setTitle("SPIELER");
					dialogStage.initModality(Modality.WINDOW_MODAL);
					Scene scene = new Scene(dialog);
					
					
					dialogStage.setScene(scene);
//					GUIController.getInstance().setCurrentDialogStage(dialogStage);
					dialogStage.showAndWait();

					// DO SOMETHING

				}
			});
			return row;
		});
	}

}
