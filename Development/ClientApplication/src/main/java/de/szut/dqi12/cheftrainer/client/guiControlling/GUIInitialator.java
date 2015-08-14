package de.szut.dqi12.cheftrainer.client.guiControlling;

import java.io.IOException;

import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.view.fxmlControllers.LoginController;
import de.szut.dqi12.cheftrainer.client.view.fxmlControllers.SideMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUIInitialator {

	private Stage rStage;
	private GridPane rLayout;
	private AnchorPane loginLayout;
	
	private SideMenuController controller;

	public static final String FXML_RESOURCE = "view/fxmlSources/";

	public GUIInitialator(Stage primaryStage) {
		this.rStage = primaryStage;
		this.rStage.setTitle("Cheftrainer Football Manager");
	}

	public void initLoginLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FXML_RESOURCE
					+ "Login.fxml"));
			loginLayout = (AnchorPane) loader.load();
			
			LoginController controller =  loader.getController();
			controller.setStage(rStage);
			
			Scene scene = new Scene(loginLayout);
			rStage.setScene(scene);
			rStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FXML_RESOURCE
					+ "RootFrame.fxml"));

			rLayout = (GridPane) loader.load();

			rStage.setMinWidth(600.0);
			Scene scene = new Scene(rLayout);
			rStage.setScene(scene);
			rStage.show();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showMenuLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FXML_RESOURCE
					+ "MenuLayout.fxml"));
			VBox menuLayout = (VBox) loader.load();

			rLayout.add(menuLayout, 0, 0);

			controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return rStage;
	}

	public GridPane getRootlayout() {
		return this.rLayout;
	}
	
	public SideMenuController getSideMenuController(){
		return this.controller;
	}

}
