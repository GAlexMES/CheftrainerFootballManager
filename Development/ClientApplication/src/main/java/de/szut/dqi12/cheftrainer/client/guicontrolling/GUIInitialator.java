package de.szut.dqi12.cheftrainer.client.guicontrolling;

import java.io.IOException;
import java.net.URL;

import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.LoginController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.SideMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * The GUI Initialator replaces, generates and renders the components of the GUI.
 * @author Alexander Brennecke
 *
 */
public class GUIInitialator {

	
	// DEFINITION
	private Stage rStage;
	private GridPane rLayout;
	private AnchorPane loginLayout;
	
	private SideMenuController controller;
	private LoginController loginController;

	public static final String FXML_RESOURCE = "view/fxmlsources/";

	
	/**
	 * Constructor to define this class
	 * @param primaryStage
	 */
	public GUIInitialator(Stage primaryStage) {
		this.rStage = primaryStage;
		this.rStage.setTitle("Cheftrainer Football Manager");
	}

	
	/**
	 * Generates the Login Layout and displays it.
	 */
	public void initLoginLayout() {
		try {
			// new FXMLLoader with Login.fxml as source
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(new URL(MainApp.class.getResource(".")+FXML_RESOURCE
					+ "Login.fxml"));
			loginLayout = (AnchorPane) loader.load();
			
			Stage dialogStage = new Stage();
			dialogStage.initOwner(rStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			Scene scene = new Scene(loginLayout);
			dialogStage.setScene(scene);
			
			//definition of the login controller
			loginController =  loader.getController();
			loginController.setStage(dialogStage);
			

			dialogStage.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Generates the Root Layout/Frame of the main application and displays it on the screen
	 */
	public void initRootLayout() {
		try {
			// new FXMLLoader with RooFrame.fxml as source
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FXML_RESOURCE
					+ "RootFrame.fxml"));
			rLayout = (GridPane) loader.load();

			// displays the RootFrame.fxml on screen
			rStage.setMinWidth(600.0);
			Scene scene = new Scene(rLayout);
			rStage.setScene(scene);
			rStage.show();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Puts the side menu onto the RootFrame.fxml
	 */
	public void showMenuLayout() {
		try {
			// new FXMLLoader with ManuLayout.fxml as source
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FXML_RESOURCE
					+ "MenuLayout.fxml"));
			VBox menuLayout = (VBox) loader.load();
			rLayout.add(menuLayout, 0, 0);

			// defines the SideMenuController
			controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//GETTER AND SETTER
	public Stage getPrimaryStage() {
		return rStage;
	}

	public GridPane getRootlayout() {
		return this.rLayout;
	}
	
	public SideMenuController getSideMenuController(){
		return this.controller;
	}

	public LoginController getLoginController(){
		return loginController;
	}
}