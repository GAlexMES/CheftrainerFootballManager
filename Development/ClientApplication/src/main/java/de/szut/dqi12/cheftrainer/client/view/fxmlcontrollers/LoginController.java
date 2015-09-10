package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.guicontrolling.AlertDialog;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIInitialator;
import de.szut.dqi12.cheftrainer.client.servercommunication.ServerConnection;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * Controller class for the Login dialog, which is defined in the Login.fxml
 * 
 * @author Alexander Brennecke
 *
 */
public class LoginController extends DialogController {

	// LINK TO FXML ELEMENTS ON GUI
	@FXML
	private TextField loginField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private TextField portField;
	@FXML
	private TextField ipField;
	@FXML
	private CheckBox showDetailsCheck;
	@FXML
	private AnchorPane serverDetailsPane;
	@FXML
	private AnchorPane buttonPane;
	@FXML
	private AnchorPane mainPane;

	// are used to show/hide the server details
	private double mainPaneMaxSize;
	private double buttonPane_YLayout;
	private double serverDetailsPane_YLayout;
	private double severDetailsPane_Height;

	// Used to close the registration controller.
	private RegistrationController registrationController;

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
	public void login() {
		TextField[] textFields = { loginField, passwordField, ipField,
				portField };
		List<String> errorList = checkInputs(textFields);
		if (errorList.size() == 0) {
			try {
				doLogin();
			} catch (IOException e) {
				showError("Login failed",
						"Something went wrong during your login",
						"Please check your server details!");
			}
		} else {
			String errorMessage = AlertDialog.WRONG_INPUTS;
			for (String s : errorList) {
				errorMessage += "\n " + s;
			}
			showError("Login failed", "Something went wrong during your login",
					errorMessage);
		}
	}

	private void doLogin() throws IOException {
		ClientProperties clientProps = new ClientProperties();
		clientProps.setPort(Integer.valueOf(portField.getText()));
		clientProps.setServerIP(ipField.getText());
		Session session = Controller.getInstance().getSession();
		Client serverCon;
		if (session != null) {
			serverCon = session.getClientSocket();
			if (!(serverCon.getServerIP().equals(ipField.getText()) && serverCon
					.getServerPort() == Integer.valueOf(portField.getText()))) {
				serverCon = ServerConnection
						.createServerConnection(clientProps);
			}
		} else {
			serverCon = ServerConnection.createServerConnection(clientProps);
		}

		Message loginMessage = new Message(
				ClientToServer_MessageIDs.USER_AUTHENTIFICATION);
		JSONObject loginInfo = new JSONObject();
		loginInfo.put("authentificationType", "login");
		loginInfo.put("username", loginField.getText());
		try {
			MessageDigest mg = MessageDigest.getInstance("MD5");
			byte[] passwordByte = passwordField.getText().getBytes("UTF-8");
			byte[] paswordHashByte = mg.digest(passwordByte);
			loginInfo.put("password", new String(paswordHashByte,
					StandardCharsets.UTF_8));
			loginMessage.setMessageContent(loginInfo);
			Thread.sleep(1500);
			serverCon.sendMessage(loginMessage);
			
			Session newSession = new Session();
			newSession.setClientSocket(serverCon);
			User user = new User();
			user.setFirstName(loginField.getText());
			newSession.setUser(user);
			Controller.getInstance().setSession(newSession);;
		} catch (NoSuchAlgorithmException e) {
			Alert alert = AlertDialog.createExceptionDialog(e);
			alert.showAndWait();
		} catch (UnsupportedEncodingException e) {
			Alert alert = AlertDialog.createExceptionDialog(e);
			alert.showAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Is called from the "cancle" button to close the aplication.
	 */
	@FXML
	public void endApplication() {
		stage.close();
	}

	/**
	 * is called, when the register button was pressed
	 */
	@FXML
	public void register() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class
					.getResource(GUIInitialator.FXML_RESOURCE
							+ "Registration.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setResizable(false);
			dialogStage.setTitle("Registration Dialog");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(stage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			registrationController = loader.getController();
			registrationController.setDialogStage(dialogStage);
			registrationController.setLoginController(this);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows a dialog, which says, that the registration was completed.
	 */
	public void showRegistrationDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(stage);
		alert.setTitle("registration Success");
		alert.setHeaderText("Your registration was completed!");
		alert.setContentText("We completed your registration. You can login now!");
		alert.showAndWait();
	}

	/**
	 * Is called to close the login dialog from a other thread.
	 */
	public void close() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				stage.close();
			}
		});
	}

	// GETTER AND SETTER
	public void setStage(Stage rStage) {
		this.stage = rStage;
	}

	public RegistrationController getRegistrationController() {
		return registrationController;
	}
}
