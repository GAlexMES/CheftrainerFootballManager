package de.szut.dqi12.cheftrainer.client.view.utils;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIInitialator;

public class DialogUtils {

	public static void showDialog(String dialogTitle, String fxmlFile){
		FXMLLoader dialogLoader= new FXMLLoader();
		dialogLoader.setLocation(MainApp.class
				.getResource(GUIInitialator.FXML_RESOURCE
						+ "dialogs/"+ fxmlFile));
		AnchorPane dialog;
		try {
			dialog = (AnchorPane) dialogLoader.load();
			Stage dialogStage = new Stage();
			dialogStage.setResizable(false);
			dialogStage.setTitle(dialogTitle);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			Scene scene = new Scene(dialog);
			dialogStage.setScene(scene);

			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
