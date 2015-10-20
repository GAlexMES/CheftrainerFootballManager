package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;
import de.szut.dqi12.cheftrainer.client.view.utils.UpdateUtils;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.ManagerTeam;

/**
 * This is the controller for the CommunitiesFrame.
 * 
 * @author Robin Bley, Alexander Brennecke
 *
 */
public class CommunitiesController implements ControllerInterface{

	@FXML
	private TableView<ManagerTeam> communitiesTable;
	@FXML
	private TableColumn<ManagerTeam, String> nameColumn;
	@FXML
	private TableColumn<ManagerTeam, String> worthColumn;
	@FXML
	private TableColumn<ManagerTeam, String> rangColumn;
	private ObservableList<ManagerTeam> data;

	
	@Override
	public void init() {
		ManagerTeam t = new ManagerTeam("",0D,"");
		List<ManagerTeam> teamList = new ArrayList<>();
		teamList.add(t);
		initTable();
	}
	
	public CommunitiesController() {
		nameColumn = new TableColumn<ManagerTeam, String>();
		worthColumn = new TableColumn<ManagerTeam, String>();
		rangColumn = new TableColumn<ManagerTeam, String>();
		data = FXCollections.observableArrayList();
		UpdateUtils.getCommunityUpdate();
	}

	
	/**
	 * This method adds a row into the table of the CommunitiesFrame
	 * @param communityName	The name of the community
	 * @param wertDesTeams The worth of the team
	 * @param rang The rang of the User in this community
	 */
	public void addRow(String communityName, double wertDesTeams, int rang) {
		data.add(new ManagerTeam(communityName, wertDesTeams, String
				.valueOf(rang)));
	}
	
	/**
	 * This method reloads the table of the communityFrame
	 * @param teams List of all ManagerTeams
	 */
	public void reloadTable(List<ManagerTeam> teams) {
		List<ManagerTeam> currentData = new ArrayList<>();
		
		for (int i = 0; data.size() > i; i++) {
			currentData.add(data.get(i));
		}

		data.removeAll(currentData);

		for (int i = 0; teams.size() > i; i++) {
			data.add(teams.get(i));
		}
	}

	/**
	 * Initialization of gui-components.
	 * This method have to be called before this object be used.
	 */
	public void initTable() {

		// for (int i = 0; teams.size() > i; i++) {
		// data.add(teams.get(i));
		// }

		nameColumn.setCellValueFactory(data -> data.getValue()
				.getCommunityName());
		rangColumn.setCellValueFactory(data -> data.getValue()
				.getPlazierung());
		worthColumn.setCellValueFactory(data -> data.getValue()
				.getWertDesTeams());

		communitiesTable.setItems(data);
		this.addListener();;
	}

	/**
	 * This method adds every row of the table one listener
	 */
	public void addListener() {
		communitiesTable.setRowFactory(tv -> {
			TableRow<ManagerTeam> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					ManagerTeam rowData = row.getItem();
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