package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerManager;
import de.szut.dqi12.cheftrainer.client.listeners.EnterPressedListener;
import de.szut.dqi12.cheftrainer.client.servercommunication.ConnectionRefusedListener;
import de.szut.dqi12.cheftrainer.client.servercommunication.ServerConnection;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;
import de.szut.dqi12.cheftrainer.connectorlib.cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.MIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messagetemplates.UserAuthenticationMessage;

/**
 * Controller for the registration dialog
 * 
 * @author Alexander Brennecke
 *
 */
public class RegistrationController implements ControllerInterface {

	public static String ON_ACTION_KEY = "RegistrationMessageArrived";

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
	@FXML
	private Button registrationButton;

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
		registrationPane.setPrefHeight(mainPaneMaxSize - severDetailsPane_Height);

		serverDetailsPane.visibleProperty().bind(showDetailsCheck.selectedProperty());

		ObservableList<Node> childs = registrationPane.getChildren();
		DialogUtils.addOnClickListener(childs, new EnterPressedListener(this));

		ControllerManager.getInstance().registerController(this, ON_ACTION_KEY);
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
			registrationPane.setPrefHeight(mainPaneMaxSize - severDetailsPane_Height);
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
		if (!registrationButton.isDisabled() && errorList.size() == 0) {
			registrationButton.setDisable(true);
			try {
				createServerConnection();
				Thread.sleep(800);
				sendRegistrationMessage();
			} catch (IOException e1) {
				AlertUtils.createSimpleDialog("Registration failed", "Something went wrong during your registration", "Please check your server details!", AlertType.ERROR);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			String errorMessage = AlertUtils.WRONG_INPUTS;
			for (String s : errorList) {
				errorMessage += "\n " + s;
			}
			AlertUtils.createSimpleDialog("Registration failed", "Something went wrong during your registration", errorMessage, AlertType.ERROR);
		}
	}

	/**
	 * Checks the user inputs when the user inputs and change the border color
	 * of empty/wrong input fields
	 * 
	 * @return
	 */
	private List<String> checkInputs() {
		TextField[] inputFields = { vornameField, nachnameField, mailField, loginField, portField, ipField, passwordField, passwordConfirmationField };

		List<String> retval = DialogUtils.checkInputs(inputFields);

		if (!(passwordField.getText().equals(passwordConfirmationField.getText()))) {
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

		User user = new User();
		user.seteMail(mailField.getText());
		user.setFirstName(vornameField.getText());
		user.setLastName(nachnameField.getText());
		user.setUserName(loginField.getText());
		user.setPassword(passwordField.getText());

		UserAuthenticationMessage uaMessage = new UserAuthenticationMessage();
		uaMessage.setUser(user);
		uaMessage.setAuthentificationType(MIDs.REGISTRATION);
		serverCon.sendMessage(uaMessage);

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

	@Override
	public void init(double width, double height) {
		// NOTHING TO DO HERE
	}

	@Override
	public void enterPressed() {
		register();
	}

	// GETTER AND SETTER
	public void setLoginController(LoginController loginController) {
		this.loginController = loginController;
	}

	@Override
	public void messageArrived(Boolean flag) {
		registrationButton.setDisable(false);
	}
	
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
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
