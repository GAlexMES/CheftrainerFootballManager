package de.szut.dqi12.cheftrainer.client;


import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

	
	

	public static void main(String[] args) {
		launch(args);
	}
	
	

	@Override
	public void start(Stage primaryStage) {
		Initialator initialator = new Initialator(primaryStage);
	}

	
}