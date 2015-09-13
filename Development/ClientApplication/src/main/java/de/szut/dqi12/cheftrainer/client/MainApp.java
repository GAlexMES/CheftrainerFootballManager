package de.szut.dqi12.cheftrainer.client;


import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This class is called when the software was started. It defines an GUIController instance and opens the login dialog.
 * @author Alexander Brennecke
 *
 */
public class MainApp extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		Controller controller = Controller.getInstance();
		controller.startApplication(primaryStage);
	}

	
}