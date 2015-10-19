package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.servercommunication.ConnectionRefusedListener;
import de.szut.dqi12.cheftrainer.client.servercommunication.ServerConnection;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;
import de.szut.dqi12.cheftrainer.connectorlib.cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * Controller for the registration dialog
 * 
 * @author Alexander Brennecke
 *
 */
public class RegistrationController {

	private Stage dialogStage;

	// LINK TO JAVAFX GUI ELEMENTS
	@FXML
	private AnchorPane serverDetailsPane;
	@FXML
	private AnchorPane buttonPane;
	@FXML
	private AnchorPane registrationPane;
	@FXML
	private CheckBox showDetailsCheck;
	@FXML
	private TextField vornameField;
	@FXML
	private TextField nachnameField;
	@FXML
	private TextField mailField;
	@FXML
	private TextField loginField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private PasswordField passwordConfirmationField;
	@FXML
	private TextField portField;
	@FXML
	private TextField ipField;

	// are used to show/hide the server details
	private double mainPaneMaxSize;
	private double buttonPane_YLayout;
	private double serverDetailsPane_YLayout;
	private double severDetailsPane_Height;

	// are used for the communication with the login dialog and the server
	private LoginController loginController;
	private Client serverCon;

	/**
	 * initialized a few variables
	 */
	public void initialize() {
		mainPaneMaxSize = registrationPane.getPrefHeight();
		buttonPane_YLayout = buttonPane.layoutYProperty().getValue();
		serverDetailsPane_YLayout = serverDetailsPane.layoutYProperty().get() + 200;
		severDetailsPane_Height = serverDetailsPane.getPrefHeight();

		buttonPane.layoutYProperty().set(serverDetailsPane_YLayout);
		registrationPane.setPrefHeight(mainPaneMaxSize
				- severDetailsPane_Height);

		serverDetailsPane.visibleProperty().bind(
				showDetailsCheck.selectedProperty());
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * triggers the frame size, to display the additional server information
	 */
	@FXML
	public void triggerFrameSize() {
		if (serverDetailsPane.visibleProperty().getValue()) {
			buttonPane.layoutYProperty().set(buttonPane_YLayout);
			registrationPane.setMinHeight(mainPaneMaxSize);
			dialogStage.setMaxHeight(serverDetailsPane_YLayout + 1000);

		} else {
			buttonPane.layoutYProperty().set(serverDetailsPane_YLayout);
			registrationPane.setPrefHeight(mainPaneMaxSize
					- severDetailsPane_Height);
			dialogStage.setMaxHeight(serverDetailsPane_YLayout + 100);
		}
		dialogStage.sizeToScene();
	}

	/**
	 * Is called from the "register" button. Creates a message with the user
	 * entries and sends it to the server, if the entries are complete and
	 * correct.
	 * 
	 * @throws IOException
	 */
	@FXML
	public void register() {
		List<String> errorList = checkInputs();
		if (errorList.size() == 0) {
			try {
				createServerConnection();
				Thread.sleep(800);
				sendRegistrationMessage();
			} catch (IOException e1) {
				AlertUtils.createSimpleDialog("Registration failed",
						"Something went wrong during your registration",
						"Please check your server details!", AlertType.ERROR);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			String errorMessage = AlertUtils.WRONG_INPUTS;
			for (String s : errorList) {
				errorMessage += "\n " + s;
			}
			AlertUtils.createSimpleDialog("Registration failed",
					"Something went wrong during your registration",
					errorMessage, AlertType.ERROR);
		}
	}

	/**
	 * Checks the user inputs when the user inputs and change the border color
	 * of empty/wrong input fields
	 * 
	 * @return
	 */
	private List<String> checkInputs() {
		TextField[] inputFields = { vornameField, nachnameField, mailField,
				loginField, portField, ipField, passwordField,
				passwordConfirmationField };

		List<String> retval = DialogUtils.checkInputs(inputFields);

		if (!(passwordField.getText().equals(passwordConfirmationField
				.getText()))) {
			passwordField.setText("");
			passwordConfirmationField.setText("");
			passwordField.setStyle("-fx-text-box-border: red;");
			passwordConfirmationField.setStyle("-fx-text-box-border: red;");
			retval.add("Passwords are not the same");
		}
		return retval;
	}

	/**
	 * Creates the registration message and sends it to the server.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void sendRegistrationMessage() throws UnsupportedEncodingException {
		Message registrationMessage = new Message(
				ClientToServer_MessageIDs.USER_AUTHENTIFICATION);

		JSONObject registrationInfo = new JSONObject();
		registrationInfo.put("authentificationType", "register");
		registrationInfo.put("vorname", vornameField.getText());
		registrationInfo.put("nachname", nachnameField.getText());
		registrationInfo.put("mail", mailField.getText());
		registrationInfo.put("login", loginField.getText());

		try {
			String passwordMD5 = CipherFactory.getMD5(passwordField.getText());
			registrationInfo.put("password", passwordMD5);
			registrationMessage.setMessageContent(registrationInfo);
			serverCon.sendMessage(registrationMessage);
		} catch (NoSuchAlgorithmException e) {
			Alert alert = AlertUtils.createExceptionDialog(e);
			alert.showAndWait();
		}

	}

	/**
	 * Creates a new server connection to the IP and Port, standing in the input
	 * fields.
	 */
	private void createServerConnection() throws IOException {
		ClientProperties clientProps = new ClientProperties();
		clientProps.setPort(Integer.valueOf(portField.getText()));
		clientProps.setServerIP(ipField.getText());
		clientProps.addConnectionDiedListener(new ConnectionRefusedListener(Controller.getInstance()));
		try {
			serverCon = ServerConnection.createServerConnection(clientProps);
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Is called from the "cancle" button. Closes the dialog.
	 */
	@FXML
	public void cancle() {
		dialogStage.close();
	}

	/**
	 * Is used to close the dialog from a other thread. Sets the server
	 * connection to the login controller.
	 */
	public void closeDialog() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Session newSession = new Session();
				newSession.setClientSocket(serverCon);
				User user = new User();
				user.setFirstName(loginField.getText());
				newSession.setUser(user);
				loginController.showRegistrationDialog();
				dialogStage.close();
			}
		});
	}

	// GETTER AND SETTER
	public void setLoginController(LoginController loginController) {
		this.loginController = loginController;
	}

}
