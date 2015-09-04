package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONObject;

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
import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.guicontrolling.AlertDialog;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIInitialator;
import de.szut.dqi12.cheftrainer.client.servercommunication.ServerConnection;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

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

	private double mainPaneMaxSize;
	private double buttonPane_YLayout;
	private double serverDetailsPane_YLayout;
	private double severDetailsPane_Height;

	private ServerConnection serverConnection;

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
		ClientProperties clientProps = new ClientProperties();
		clientProps.setPort(Integer.valueOf(portField.getText()));
		clientProps.setServerIP(ipField.getText());
		ServerConnection serverCon = new ServerConnection(clientProps);
		Message loginMessage = new Message(ClientToServer_MessageIDs.USER_LOGIN);
		JSONObject loginInfo = new JSONObject();
		loginInfo.put("username", loginField.getText());
		try {
			MessageDigest mg = MessageDigest.getInstance("MD5");
			byte[] passwordByte = passwordField.getText().getBytes("UTF-8");
			byte[] paswordHashByte = mg.digest(passwordByte);
			loginInfo.put("password", new String(paswordHashByte,StandardCharsets.UTF_8));
			loginMessage.setMessageContent(loginInfo);
			Thread.sleep(800);
			serverCon.sendMessage(loginMessage);
		} catch (NoSuchAlgorithmException e) {
			Alert alert = AlertDialog.createExceptionDialog(e);
			alert.showAndWait();
		}
		catch (UnsupportedEncodingException e) {
			Alert alert = AlertDialog.createExceptionDialog(e);
			alert.showAndWait();
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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

	public RegistrationController getRegistrationController() {
		return registrationController;
	}

	public void showRegistrationDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(stage);
		alert.setTitle("registration Success");
		alert.setHeaderText("Your registration was completed!");
		alert.setContentText("We completed your registration. You can login now!");

		alert.showAndWait();
	}

	public void close() {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	stage.close();
            }
        });		
	}

}
