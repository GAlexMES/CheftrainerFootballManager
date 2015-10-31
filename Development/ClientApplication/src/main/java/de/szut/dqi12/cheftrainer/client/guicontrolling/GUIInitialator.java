package de.szut.dqi12.cheftrainer.client.guicontrolling;

import java.io.IOException;
import java.net.URL;

import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.LoginController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.SideMenuController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The GUI Initialator replaces, generates and renders the components of the
 * GUI.
 * 
 * @author Alexander Brennecke
 *
 */
public class GUIInitialator {

	// DEFINITION
	private Stage rStage;
	private Stage mainApplicationStage;
	private Stage loginDialogStage;
	private GridPane rLayout;
	private AnchorPane loginLayout;

	private SideMenuController controller;
	private LoginController loginController;

	private FXMLLoader currentFXMLLoader;

	private ClassLoader classLoader;
	private URL fxmlFile;

	/**
	 * Constructor to define this class
	 * 
	 * @param primaryStage
	 */
	public GUIInitialator(Stage primaryStage) {
		this.rStage = primaryStage;
		this.rStage.setTitle("Cheftrainer Football Manager");
		this.rStage.setMinWidth(500);
		classLoader = getClass().getClassLoader();
		currentFXMLLoader = new FXMLLoader();
	}

	/**
	 * Is called to close the application.
	 */
	public void closeMainApplication() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (mainApplicationStage != null) {
					mainApplicationStage.close();
				}
			}
		});
	}

	/**
	 * Is called to close the application.
	 */
	public void closeLoginDialog() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				loginDialogStage.close();
			}
		});
	}

	/**
	 * Generates the Login Layout and displays it.
	 */
	public void initLoginLayout() {
		try {
			// new FXMLLoader with Login.fxml as source
			currentFXMLLoader = new FXMLLoader();
			fxmlFile = classLoader.getResource("sourcesFXML/Login.fxml");

			currentFXMLLoader.setLocation(fxmlFile);
			loginLayout = (AnchorPane) currentFXMLLoader.load();

			loginDialogStage = new Stage();
			loginDialogStage.initOwner(rStage);
			loginDialogStage.initModality(Modality.WINDOW_MODAL);
			Scene scene = new Scene(loginLayout);
			loginDialogStage.setScene(scene);

			// definition of the login controller
			loginController = currentFXMLLoader.getController();
			loginController.setStage(loginDialogStage);

			loginDialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates the Root Layout/Frame of the main application and displays it
	 * on the screen
	 */
	public void initRootLayout() {
		try {
			// new FXMLLoader with RooFrame.fxml as source
			currentFXMLLoader = new FXMLLoader();
			fxmlFile = classLoader.getResource("rootFXML/RootFrame.fxml");
			currentFXMLLoader.setLocation(fxmlFile);
			rLayout = (GridPane) currentFXMLLoader.load();

			// displays the RootFrame.fxml on screen
			mainApplicationStage = new Stage();
			mainApplicationStage.setMinWidth(600.0);
			Scene scene = new Scene(rLayout);
			mainApplicationStage.setScene(scene);
			mainApplicationStage.show();

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
			FXMLLoader menuLoader = new FXMLLoader();
			fxmlFile = classLoader.getResource("rootFXML/MenuLayout.fxml");

			menuLoader.setLocation(fxmlFile);
			VBox menuLayout = (VBox) menuLoader.load();
			rLayout.add(menuLayout, 0, 0);

			// defines the SideMenuController
			controller = menuLoader.getController();
			controller.setGUIInitialator(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// GETTER AND SETTER
	public Stage getPrimaryStage() {
		return rStage;
	}

	public GridPane getRootlayout() {
		return this.rLayout;
	}

	public SideMenuController getSideMenuController() {
		return this.controller;
	}

	public LoginController getLoginController() {
		return loginController;
	}

	public FXMLLoader getCurrentFXMLLoader() {
		return currentFXMLLoader;
	}
}
