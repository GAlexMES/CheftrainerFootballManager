package de.szut.dqi12.cheftrainer.client;

import java.io.IOException;

import de.szut.dqi12.cheftrainer.client.view.fxmlControllers.SideMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Initialator {
	
	private Stage rStage;
	private BorderPane rLayout;
	
	private final String FXML_RESOURCE = "view/fxmlSources/";
	
	public Initialator(Stage primaryStage){
		this.rStage = primaryStage;
		this.rStage.setTitle("Cheftrainer Football Manager");
		initRootLayout();
		showMenuLayout();
	}
	
	private void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class
					.getResource(FXML_RESOURCE+"RootFrame.fxml"));
			rLayout = (BorderPane) loader.load();

			Scene scene = new Scene(rLayout);
			rStage.setScene(scene);

			rStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void showMenuLayout(){
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(FXML_RESOURCE+"MenuLayout.fxml"));
			VBox menuLayout = (VBox) loader.load();
			rLayout.setLeft(menuLayout);
			SideMenuController controller = loader.getController();
			controller.setMainApp(this);
		}
		catch(IOException e){
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
