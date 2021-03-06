package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;

/**
 * This is the controller for the CommunitiesFrame.
 * 
 * @author Robin Bley, Alexander Brennecke
 *	@see /F0060/
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
	public void init(double width, double height) {
	}
	
	public CommunitiesController() {
		nameColumn = new TableColumn<Manager, String>();
		worthColumn = new TableColumn<Manager, String>();
		rangColumn = new TableColumn<Manager, String>();
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
	
	/**
	 * Is called, when the user click on an table entry. The selected {@link Community} will be set in the {@link Session}.
	 * @param manager the pressed {@link Manager} in the table.
	 * @see /F0061/
	 */
	private void communityPressed(Manager manager) {
		Session session = Controller.getInstance().getSession();
		session.setCurrentManager(manager);
		GUIController.getInstance().enableButtons();
	}

	/**
	 * Is called, when the enter community button was pressed. It shows a the
	 * EnterCommunityDialog.fxml
	 * @see /F0040/
	 */
	@FXML
	public void enterCommunity() {
		try {
			DialogUtils.showDialog("Spielrunde beitreten!",
					"EnterCommunityDialog.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Is called, when the enter community button was pressed. It shows a the
	 * CreateCommunityDialog.fxml
	 * @see /F0012/
	 */
	@FXML
	public void createCommunity() {
		try {
			DialogUtils.showDialog("Spielrunde Erstellen!",
					"CreateCommunityDialog.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void enterPressed() {
	}

	@Override
	public void messageArrived(Boolean flag) {
		
	}

	@Override
	public void initializationFinihed(Scene scene) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(double sizeDifferent) {
		// TODO Auto-generated method stub
		
	}
}