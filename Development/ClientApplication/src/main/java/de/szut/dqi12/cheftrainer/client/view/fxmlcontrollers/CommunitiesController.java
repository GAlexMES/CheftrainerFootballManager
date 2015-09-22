package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;
import de.szut.dqi12.cheftrainer.client.view.utils.UpdateUtils;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Team;

/**
 * This is the controller for the CommunitiesFrame.
 * 
 * @author Robin Bley, Alexander Brennecke
 *
 */
public class CommunitiesController {

	@FXML
	private TableView<Team> communitiesTable;
	@FXML
	private TableColumn<Team, String> communityNameColumn;
	@FXML
	private TableColumn<Team, String> wertDesTeamsColumn;
	@FXML
	private TableColumn<Team, String> plazierungColumn;
	private ObservableList<Team> data;

	public CommunitiesController() {
		communityNameColumn = new TableColumn<Team, String>();
		wertDesTeamsColumn = new TableColumn<Team, String>();
		plazierungColumn = new TableColumn<Team, String>();
		data = FXCollections.observableArrayList();
		UpdateUtils.getCommunityUpdate();
	}

	public void addRow(String communityName, double wertDesTeams, int rang) {
		data.add(new Team(communityName, String.valueOf(wertDesTeams), String
				.valueOf(rang)));
	}

	public void reloadTable(List<Team> teams) {
		List<Team> currentData = new ArrayList<>();

		for (int i = 0; data.size() > i; i++) {
			currentData.add(data.get(i));
		}

		data.removeAll(currentData);

		for (int i = 0; teams.size() > i; i++) {
			data.add(teams.get(i));
		}
	}

	public void initTable() {

		// for (int i = 0; teams.size() > i; i++) {
		// data.add(teams.get(i));
		// }

		communityNameColumn.setCellValueFactory(data -> data.getValue()
				.getCommunityName());
		plazierungColumn.setCellValueFactory(data -> data.getValue()
				.getPlazierung());
		wertDesTeamsColumn.setCellValueFactory(data -> data.getValue()
				.getWertDesTeams());

		communitiesTable.setItems(data);
		this.addListener();;
	}

	/**
	 * Diese Funktion wird aufgerufen, wenn auf eine Reihe der Tabelle ein
	 * Doppelklick ausgeuebt wird.
	 */
	public void addListener() {
		communitiesTable.setRowFactory(tv -> {
			TableRow<Team> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Team rowData = row.getItem();
					System.out.println(rowData.getCommunityName().get());

					// DO SOMETHING

					// Stage dialogStage = new Stage();
					// dialogStage.initModality(Modality.WINDOW_MODAL);
					// dialogStage.setScene(new Scene(VBoxBuilder.create().
					// children(new Text(rowData.getCommunityName().get()), new
					// Text(rowData.getPlazierung().get()), new
					// Text(rowData.getWertDesTeams().get())).
					// alignment(Pos.CENTER).padding(new Insets(5)).build()));
					// dialogStage.show();

				}
			});
			return row;
		});
	}

	/**
	 * Is called, when the enter community button was pressed. It shows a the
	 * EnterCommunityDialog.fxml
	 */
	@FXML
	public void enterCommunity() {
		try {
			DialogUtils.showDialog("Enter Community!",
					"EnterCommunityDialog.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Is called, when the enter community button was pressed. It shows a the
	 * CreateCommunityDialog.fxml
	 */
	@FXML
	public void createCommunity() {
		try {
			DialogUtils.showDialog("Create Community!",
					"CreateCommunityDialog.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}