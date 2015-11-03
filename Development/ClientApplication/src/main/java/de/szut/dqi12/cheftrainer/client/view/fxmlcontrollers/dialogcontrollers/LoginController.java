package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.servercommunication.ConnectionRefusedListener;
import de.szut.dqi12.cheftrainer.client.servercommunication.ServerConnection;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.ControllerInterface;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.ControllerManager;
import de.szut.dqi12.cheftrainer.client.view.utils.AlertUtils;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;
import de.szut.dqi12.cheftrainer.connectorlib.cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.listeners.EnterPressedListener;

/**
 * Controller class for the Login dialog, which is defined in the Login.fxml
 * 
 * @author Alexander Brennecke
 *
 */
public class LoginController implements ControllerInterface {

	public static String ON_ACTION_KEY = "LoginMessageArrived";

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
	@FXML
	private Button loginButton;

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

		ObservableList<Node> childs = mainPane.getChildren();
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
		System.out.println("login presses");
		if (!loginButton.isDisabled()){
			System.out.println("button is not disabled");
			TextField[] textFields = { loginField, passwordField, ipField,
					portField };
			List<String> errorList = DialogUtils.checkInputs(textFields);
			if (errorList.size() == 0) {
				loginButton.setDisable(true);
				try {
					doLogin();
				} catch (IOException e) {
					AlertUtils.createSimpleDialog("Login failed",
							"Something went wrong during your login",
							"Please check your server details!",
							AlertType.ERROR);
				}
			} else {
				String errorMessage = AlertUtils.WRONG_INPUTS;
				for (String s : errorList) {
					errorMessage += "\n " + s;
				}
				AlertUtils.createSimpleDialog("Login failed",
						"Something went wrong during your login", errorMessage,
						AlertType.ERROR);
			}
		}
	}

	/**
	 * Is called, when all required input fields are filled. Creates a new
	 * server connection and sends a message with the required data for a login
	 * to the server. It also initializes a few parameters.
	 * 
	 * @throws IOException
	 */
	private void doLogin() throws IOException {
		Client serverCon = createServerCon();
		Message loginMessage = new Message(
				ClientToServer_MessageIDs.USER_AUTHENTIFICATION);
		JSONObject loginInfo = new JSONObject();
		loginInfo.put("authentificationType", "login");
		loginInfo.put("username", loginField.getText());
		try {
			String passwordMD5 = CipherFactory.getMD5(passwordField.getText());
			loginInfo.put("password", passwordMD5);
			loginMessage.setMessageContent(loginInfo);
			Thread.sleep(1500);
			serverCon.sendMessage(loginMessage);

			Session newSession = new Session();
			newSession.setClientSocket(serverCon);
			User user = new User();
			user.setUserName(loginField.getText());
			newSession.setUser(user);
			Controller.getInstance().setSession(newSession);
			;
		} catch (NoSuchAlgorithmException e) {
			Alert alert = AlertUtils.createExceptionDialog(e);
			alert.showAndWait();
		} catch (UnsupportedEncodingException e) {
			Alert alert = AlertUtils.createExceptionDialog(e);
			alert.showAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new Client with the given parameters in the input fields.
	 * 
	 * @return
	 * @throws IOException
	 */
	private Client createServerCon() throws IOException {
		ClientProperties clientProps = new ClientProperties();
		clientProps.setPort(Integer.valueOf(portField.getText()));
		clientProps.setServerIP(ipField.getText());
		clientProps.addConnectionDiedListener(new ConnectionRefusedListener(
				Controller.getInstance()));
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
		return serverCon;
	}

	/**
	 * Is called from the "cancle" button to close the aplication.
	 */
	@FXML
	public void endApplication() {
		stage.close();
	}

	/**
	 * Is called, when the register button was pressed
	 */
	@FXML
	public void register() {
		try {
			FXMLLoader loader = new FXMLLoader();
			ClassLoader classLoader = getClass().getClassLoader();
			URL fxmlFile = classLoader
					.getResource("sourcesFXML/Registration.fxml");
			loader.setLocation(fxmlFile);
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
		alert.setTitle("registration success");
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

	@Override
	public void init() {
		// NOT USED HERE
	}

	@Override
	public void enterPressed() {
		login();
	}

	@Override
	public void messageArrived() {
		loginButton.setDisable(false);
		System.out.println("button is now enabled");
	}
}
