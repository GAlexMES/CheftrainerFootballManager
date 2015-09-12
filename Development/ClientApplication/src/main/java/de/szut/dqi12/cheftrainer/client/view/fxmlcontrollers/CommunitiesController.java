package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIInitialator;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Team;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class CommunitiesController {

	@FXML
	private TableView<Team> communitiesFrame;

	@FXML
	private TableColumn<Team, String> communityNameColumn;
	@FXML
	private TableColumn<Team, String> wertDesTeamsColumn;
	@FXML
	private TableColumn<Team, String> plazierungColumn;
	private ObservableList<Team> data;
	
	
	FXMLLoader dialogLoader;

	public CommunitiesController() {
		dialogLoader = new FXMLLoader();
		
		communityNameColumn = new TableColumn<Team, String>();
		wertDesTeamsColumn = new TableColumn<Team, String>();
		plazierungColumn = new TableColumn<Team, String>();
		data = FXCollections.observableArrayList();
		getCommunityUpdate();
	}

	private void getCommunityUpdate() {
		JSONObject messageContent = new JSONObject();
		messageContent.put("update", "CommunityList");
		Message updateMessage = new Message(
				ClientToServer_MessageIDs.REQUEST_UPDATE);
		updateMessage.setMessageContent(messageContent);
		Controller.getInstance().getSession().getClientSocket()
				.sendMessage(updateMessage);
	}

	public void addRow(String communityName, double wertDesTeams, int rang) {
		data.add(new Team(communityName, String.valueOf(wertDesTeams), String
				.valueOf(rang)));

	}

	public void reloadTable(ArrayList<Team> teams) {
		for (int i = 0; teams.size() > i; i++) {
			data.remove(teams.get(i));
		}

		for (int i = 0; teams.size() > i; i++) {
			data.add(teams.get(i));
		}
	}
	
	public void initTable(List<Team> teams) {
		
		for (int i = 0; teams.size() > i; i++) {
			data.add(teams.get(i));
		}

		communityNameColumn.setCellValueFactory(data -> data.getValue()
				.getCommunityName());
		plazierungColumn.setCellValueFactory(data -> data.getValue()
				.getPlazierung());
		wertDesTeamsColumn.setCellValueFactory(data -> data.getValue()
				.getWertDesTeams());
		
		communitiesFrame.setItems(data);
		this.blabla();
		}

		public void blabla() {
			communitiesFrame.setRowFactory( tv -> {
			    TableRow<Team> row = new TableRow<>();
			    row.setOnMouseClicked(event -> {
			        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
			            Team rowData = row.getItem();
			            System.out.println(rowData.getCommunityName().get());
			            //DO SOMETHING
			            
			            
//			            Stage dialogStage = new Stage();
//			            dialogStage.initModality(Modality.WINDOW_MODAL);
//			            dialogStage.setScene(new Scene(VBoxBuilder.create().
//			                children(new Text(rowData.getCommunityName().get()), new Text(rowData.getPlazierung().get()), new Text(rowData.getWertDesTeams().get())).
//			                alignment(Pos.CENTER).padding(new Insets(5)).build()));
//			            dialogStage.show();
			            
			            
			        }
			    });
			    return row ;
			});
		}
		
		@FXML
		public void enterCommunity(){
			DialogUtils.showDialog("Enter Community!", "EnterCommunityDialog.fxml");
		}
		
		@FXML
		public void createCommunity(){
			DialogUtils.showDialog("Create Community!", "CreateCommunityDialog.fxml");
		}
}