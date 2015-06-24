package de.szut.dqi12.cheftrainer.client;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Initialator {
	
	private Stage rStage;
	private BorderPane rLayout;
	
	public Initialator(Stage primaryStage){
		this.rStage = primaryStage;
		this.rStage.setTitle("Cheftrainer Football Manager");
		initRootLayout();
	}
	
	private void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class
					.getResource("view/fxmlSources/RootFrame.fxml"));
			rLayout = (BorderPane) loader.load();

			Scene scene = new Scene(rLayout);
			rStage.setScene(scene);

			rStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public Stage getPrimaryStage() {
		return rStage;
	}
	
	public BorderPane getRootlayout(){
		return this.rLayout;
	}

}
