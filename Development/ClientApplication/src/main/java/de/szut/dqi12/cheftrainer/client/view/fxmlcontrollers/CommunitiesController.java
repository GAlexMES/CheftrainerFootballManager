package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;
import de.szut.dqi12.cheftrainer.client.view.utils.UpdateUtils;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;

/**
 * This is the controller for the CommunitiesFrame.
 * 
 * @author Robin Bley, Alexander Brennecke
 *
 */
public class CommunitiesController implements ControllerInterface{

	@FXML
	private TableView<Manager> communitiesTable;
	@FXML
	private TableColumn<Manager, String> nameColumn;
	@FXML
	private TableColumn<Manager, String> worthColumn;
	@FXML
	private TableColumn<Manager, String> rangColumn;
	
	
	@Override
	public void init() {
		
	}
	
	public CommunitiesController() {
		nameColumn = new TableColumn<Manager, String>();
		worthColumn = new TableColumn<Manager, String>();
		rangColumn = new TableColumn<Manager, String>();
		
		UpdateUtils.getCommunityUpdate();
	}

	
	/**
	 * Initialization of gui-components.
	 * This method have to be called before this object be used.
	 */
	@FXML
	public void initialize() {
		communitiesTable.setItems(Controller.getInstance().getSession().getManagerObservable());
		
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().getCommunityNameProperty());
		worthColumn.setCellValueFactory(cellData -> cellData.getValue().getTeamWorthProperty());
		rangColumn.setCellValueFactory(cellData -> cellData.getValue().getRangProperty());
    	
		communitiesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> communityPressed(newValue));
	}
	
	private void communityPressed(Manager manager) {
		Session session = Controller.getInstance().getSession();
		session.setCurrentManager(manager);
		GUIController.getInstance().enableButtons();
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

	@Override
	public void enterPressed() {
	}

	@Override
	public void messageArrived() {
		// TODO Auto-generated method stub
		
	}
}