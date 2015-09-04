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
	
	private final String WRONG_INPUTS = "Please check ypur input for the following parameters: ";
	
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

	private double mainPaneMaxSize;
	private double buttonPane_YLayout;
	private double serverDetailsPane_YLayout;
	private double severDetailsPane_Height;
	
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

	@FXML
	public void register() {
		List<String> errorList = checkInputs();
		if (errorList.size()==0) {
			createServerConnection();
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
	
	
	public void showError(String title, String header, String content){
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	Alert alert = new Alert(AlertType.ERROR);
        		alert.setContentText(content);
        		alert.setTitle(title);
        		alert.setHeaderText(header);
        		alert.showAndWait();
            }
        });
		
	}
	
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

	private void sendRegistrationMessage() throws UnsupportedEncodingException {
		Message registrationMessage = new Message(
				ClientToServer_MessageIDs.USER_REGISTRATION);

		JSONObject registrationInfo = new JSONObject();
		registrationInfo.put("vorname", vornameField.getText());
		registrationInfo.put("nachname", nachnameField.getText());
		registrationInfo.put("mail", mailField.getText());
		registrationInfo.put("login", loginField.getText());
		
		
		MessageDigest mg;
		try {
			mg = MessageDigest.getInstance("MD5");
			byte[] passwordByte = passwordField.getText().getBytes("UTF-8");
			byte[] paswordHashByte = mg.digest(passwordByte);
			registrationInfo.put("password", new String(paswordHashByte, StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		registrationMessage.setMessageContent(registrationInfo);
		serverCon.sendMessage(registrationMessage);
	}

	private void createServerConnection() {
		ClientProperties clientProps = new ClientProperties();
		clientProps.setPort(Integer.valueOf(portField.getText()));
		clientProps.setServerIP(ipField.getText());
		serverCon = new ServerConnection(clientProps);
	}

	@FXML
	public void cancle(){
		dialogStage.close();
	}
	
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

	public void setLoginController(LoginController loginController) {
		this.loginController=loginController;
	}

}