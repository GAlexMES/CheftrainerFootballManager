package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIInitialator;
import de.szut.dqi12.cheftrainer.client.servercommunication.ServerConnection;

/**
 * Controller class for the Login dialog, which is defined in the Login.fxml
 * 
 * @author Alexander Brennecke
 *
 */
public class LoginController {

	// DEFINITIONS
	@FXML
	private TextField loginField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private CheckBox showDetailsCheck;
	@FXML
	private AnchorPane serverDetailsPane;
	@FXML
	private AnchorPane buttonPane;
	@FXML
	private AnchorPane mainPane;

	private double mainPaneMaxSize;
	private double buttonPane_YLayout;
	private double serverDetailsPane_YLayout;
	private double severDetailsPane_Height;
	
	private ServerConnection serverConnection;

	private Stage stage;

	/**
	 * initialized a few variables
	 */
	public void initialize() {
		mainPaneMaxSize = mainPane.getPrefHeight();
		buttonPane_YLayout = buttonPane.layoutYProperty().getValue();
		serverDetailsPane_YLayout = serverDetailsPane.layoutYProperty().get();
		severDetailsPane_Height = serverDetailsPane.getPrefHeight();

		buttonPane.layoutYProperty().set(serverDetailsPane_YLayout);
		mainPane.setPrefHeight(mainPaneMaxSize - severDetailsPane_Height);

		serverDetailsPane.visibleProperty().bind(
				showDetailsCheck.selectedProperty());
	}

	/**
	 * triggers the frame size, to display the additional server information
	 */
	@FXML
	public void triggerFrameSize() {
		if (serverDetailsPane.visibleProperty().getValue()) {
			buttonPane.layoutYProperty().set(buttonPane_YLayout);
			mainPane.setMinHeight(mainPaneMaxSize);
		} else {
			buttonPane.layoutYProperty().set(serverDetailsPane_YLayout);
			mainPane.setPrefHeight(mainPaneMaxSize - severDetailsPane_Height);
		}
		stage.sizeToScene();
	}

	/**
	 * is called when the login button was pressed
	 */
	@FXML
	public void loginButtonPressed() {
		if (login()) {
			GUIController.getInstance().showMainApplication();
		}
	}

	/**
	 * is called, when the register button was pressed
	 */
	@FXML
	public void register() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(GUIInitialator.FXML_RESOURCE
					+ "Registration.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setResizable(false);
			dialogStage.setTitle("Registration Dialog");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(stage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			RegistrationController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Login Algorithmik muss ergänzt werden!!!
	 * 
	 * @return
	 */
	private boolean login() {

		return true;
	}

	// GETTER AND SETTER
	public void setStage(Stage rStage) {
		this.stage = rStage;
	}

	public ServerConnection getServerConnection() {
		return serverConnection;
	}

	public void setServerConnection(ServerConnection serverConnection) {
		this.serverConnection = serverConnection;
	}
	
}
