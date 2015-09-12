package de.szut.dqi12.cheftrainer.client.view.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.guicontrolling.AlertDialog;
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
	
	
	/**
	 * Checks the user inputs when the user inputs and change the border color
	 * of empty/wrong input fields
	 * 
	 * @return
	 */
	public static List<String> checkInputs(TextField[] inputFields) {
		List<String> retval = new ArrayList<>();
		for (TextField tf : inputFields) {
			if (tf.getText() == null || tf.getText().isEmpty()) {
				tf.setStyle("-fx-text-box-border: red;");
				retval.add(tf.getId().substring(0, tf.getId().length() - 5));
			} else {
				tf.setStyle("-fx-text-box-border: green;");
			}
		}
		return retval;
	}
	
	/**
	 * Shows a error alert with the given parameters. Can also be called from a
	 * other thread.
	 * 
	 * @param title
	 *            of the dialog
	 * @param header
	 *            of the dialog
	 * @param content
	 *            of the dialog
	 */
	public static void showError(String title, String header, String content) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Alert alert = AlertDialog.createSimpleDialog(title, header,
						content, AlertType.ERROR);
				alert.showAndWait();
			}
		});

	}
}
