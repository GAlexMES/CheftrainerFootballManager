package de.szut.dqi12.cheftrainer.client.view.utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;

/**
 * This class provides a few method for Dialogs
 * 
 * @author Alexander Brennecke
 *
 */
public class DialogUtils {

	/**
	 * Opens new windows and loads the given fxmmlFile.
	 * 
	 * @param dialogTitle
	 *            the title of the dialog
	 * @param fxmlFile
	 *            the fxml source of the dialog (should be *.fxml)
	 */
	public static void showDialog(String dialogTitle, String fileName)
			throws Exception {
		// if (GUIController.getInstance().getCurrentContentLoader() != null) {
		// throw new Exception(
		// "There is already a dialog opened. Please close it first!");
		// }
		FXMLLoader dialogLoader = new FXMLLoader();
		ClassLoader classLoader = DialogUtils.class.getClassLoader();
		URL fxmlFile = classLoader.getResource("dialogFXML/" + fileName);

		dialogLoader.setLocation(fxmlFile);
		AnchorPane dialog;
		try {
			dialog = (AnchorPane) dialogLoader.load();
			Stage dialogStage = new Stage();
			Image icon = GUIController.getInstance().getGUIInitialator().getIcon();
			dialogStage.getIcons().add(icon);
			dialogStage.setResizable(false);
			dialogStage.setTitle(dialogTitle);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			Scene scene = new Scene(dialog);
			dialogStage.setScene(scene);
			GUIController.getInstance().setCurrentDialogStage(dialogStage);
			dialogStage.showAndWait();
			GUIController.getInstance().setCurrentDialogStage(null);
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

	public static void addOnClickListener(List<Node> nodes,
			EventHandler<KeyEvent> eventHandlet) {
		for (Node n : nodes) {
				n.setOnKeyPressed(eventHandlet);
		}
	}
}
