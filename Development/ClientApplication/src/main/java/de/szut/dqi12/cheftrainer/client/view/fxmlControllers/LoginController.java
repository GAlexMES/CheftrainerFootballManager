package de.szut.dqi12.cheftrainer.client.view.fxmlControllers;

import de.szut.dqi12.cheftrainer.client.guiControlling.GUIController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 * Controller class for the Login dialog, which is defined in the Login.fxml
 * @author Alexander Brennecke
 *
 */
public class LoginController {

	//DEFINITIONS
	@FXML private TextField loginField;
	@FXML private PasswordField passwordField;
	@FXML private CheckBox showDetailsCheck;
	@FXML private AnchorPane serverDetailsPane;
	@FXML private AnchorPane buttonPane;
	@FXML private AnchorPane mainPane;
	
	private double mainPaneMaxSize;
	private double buttonPane_YLayout;
	private double serverDetailsPane_YLayout;
	private double severDetailsPane_Height;
	
	private Stage stage;
	
	/**
	 * initialized a few variables
	 */
	public void initialize(){
		mainPaneMaxSize = mainPane.getPrefHeight();
		buttonPane_YLayout = buttonPane.layoutYProperty().getValue();
		serverDetailsPane_YLayout=serverDetailsPane.layoutYProperty().get();
		severDetailsPane_Height = serverDetailsPane.getPrefHeight();
		
		buttonPane.layoutYProperty().set(serverDetailsPane_YLayout);
		mainPane.setPrefHeight(mainPaneMaxSize - severDetailsPane_Height );
		
		
		serverDetailsPane.visibleProperty().bind(showDetailsCheck.selectedProperty());
	}
	
	/**
	 * triggers the frame size, to display the additional server information
	 */
	@FXML
	public void triggerFrameSize(){
		if(serverDetailsPane.visibleProperty().getValue()){
			buttonPane.layoutYProperty().set(buttonPane_YLayout);
			mainPane.setMinHeight(mainPaneMaxSize);
		}
		else{
			buttonPane.layoutYProperty().set(serverDetailsPane_YLayout);
			mainPane.setPrefHeight(mainPaneMaxSize - severDetailsPane_Height );
		}
		stage.sizeToScene();
	}
	
	/**
	 * is called when the login button was pressed
	 */
	@FXML	
	public void loginButtonPressed(){
		if(login()){
			GUIController.getInstance().showMainApplication();
		}
	}
	
	/**
	 * Login Algorithmik muss ergänzt werden!!!
	 * @return
	 */
	private boolean login(){
		
		return true;
	}

	// GETTER AND SETTER
	public void setStage(Stage rStage) {
		this.stage = rStage;
	}
}
