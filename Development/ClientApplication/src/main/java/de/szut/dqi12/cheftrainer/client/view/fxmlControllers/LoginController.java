package de.szut.dqi12.cheftrainer.client.view.fxmlControllers;

import de.szut.dqi12.cheftrainer.client.guiControlling.GUIController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {

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
	public void initialize(){
		mainPaneMaxSize = mainPane.getPrefHeight();
		buttonPane_YLayout = buttonPane.layoutYProperty().getValue();
		serverDetailsPane_YLayout=serverDetailsPane.layoutYProperty().get();
		severDetailsPane_Height = serverDetailsPane.getPrefHeight();
		
		buttonPane.layoutYProperty().set(serverDetailsPane_YLayout);
		mainPane.setPrefHeight(mainPaneMaxSize - severDetailsPane_Height );
		
		
		serverDetailsPane.visibleProperty().bind(showDetailsCheck.selectedProperty());
	}
	
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
	
	@FXML	
	public void loginButtonPressed(){
		if(login()){
			GUIController.getInstance().showMainApplikation();
		}
	}
	
	/**
	 * Login Algorithmik muss ergänzt werden!!!
	 * @return
	 */
	private boolean login(){
		
		return true;
	}

	public void setStage(Stage rStage) {
		this.stage = rStage;
	}
}
