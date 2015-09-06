package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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

import de.szut.dqi12.cheftrainer.client.guicontrolling.AlertDialog;
import de.szut.dqi12.cheftrainer.client.servercommunication.ServerConnection;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;
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
	
	// Error message for wrong Inputs
	private final String WRONG_INPUTS = "Please check ypur input for the following parameters: ";
	
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
	private ServerConnection serverCon;

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
	 * Is called from the "register" button. Creates a message with the user entries and sends it to the server, if the entries are complete and correct.
	 */
	@FXML
	public void register() {
		List<String> errorList = checkInputs();
		if (errorList.size()==0) {
			createServerConnection();
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				sendRegistrationMessage();
			} catch (UnsupportedEncodingException e) {
			}
		}
		else{
			String errorMessage = WRONG_INPUTS;
			for(String s : errorList){
				errorMessage += "\n "+s;
			}
			showError("Registration failed", "Something went wrong during your registration", errorMessage);
		}
	}
	
	
	/**
	 * Shows a error alert with the given parameters. Can also be called from a other thread.
	 * @param title of the dialog
	 * @param header of the dialog
	 * @param content of the dialog
	 */
	public void showError(String title, String header, String content){
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	Alert alert = AlertDialog.createSimpleDialog(title, header, content, AlertType.ERROR);
            	alert.showAndWait();
            }
        });
		
	}

	/**
	 * Checks the user inputs when the user inputs and change the border color of empty/wrong input fields
	 * @return
	 */
	private List<String> checkInputs(){
		TextField[] inputFields = {vornameField,nachnameField,mailField,loginField,portField,ipField,passwordField,passwordConfirmationField};
		List<String> retval = new ArrayList<>();
		for(TextField tf:inputFields){
			if(tf.getText()==null || tf.getText().isEmpty()){
				tf.setStyle("-fx-text-box-border: red;");
				retval.add(tf.getId().substring(0,tf.getId().length()-5));
			}
			else{
				tf.setStyle("-fx-text-box-border: green;");
			}
		}
		
		if(!(passwordField.getText().equals(passwordConfirmationField.getText()))){
			passwordField.setText("");
			passwordConfirmationField.setText("");
			passwordField.setStyle("-fx-text-box-border: red;");
			passwordConfirmationField.setStyle("-fx-text-box-border: red;");
		}
		return retval;
	}

	/**
	 * Creates the registration message and sends it to the server.
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
		
		// Creeates a MD5 hash of the password.
		MessageDigest mg;
		try {
			mg = MessageDigest.getInstance("MD5");
			byte[] passwordByte = passwordField.getText().getBytes("UTF-8");
			byte[] paswordHashByte = mg.digest(passwordByte);
			registrationInfo.put("password", new String(paswordHashByte, StandardCharsets.UTF_8));
			registrationMessage.setMessageContent(registrationInfo);
			serverCon.sendMessage(registrationMessage);
		} catch (NoSuchAlgorithmException e) {
			Alert alert = AlertDialog.createExceptionDialog(e);
			alert.showAndWait();
		}
		
	}

	/**
	 * Creates a new server connection to the IP and Port, standing in the input fields.
	 */
	private void createServerConnection() {
		ClientProperties clientProps = new ClientProperties();
		clientProps.setPort(Integer.valueOf(portField.getText()));
		clientProps.setServerIP(ipField.getText());
		serverCon = new ServerConnection(clientProps);
	}

	/**
	 * Is called from the "cancle" button. Closes the dialog.
	 */
	@FXML
	public void cancle(){
		dialogStage.close();
	}
	
	/**
	 * Is used to close the dialog from a other thread. Sets the server connection to the login controller.
	 */
	public void closeDialog() {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	loginController.setServerConnection(serverCon);
            	loginController.showRegistrationDialog();
            	dialogStage.close();
            }
        });
	}
	
	// GETTER AND SETTER
	public void setLoginController(LoginController loginController) {
		this.loginController=loginController;
	}

}