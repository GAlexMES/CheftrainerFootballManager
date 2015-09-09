package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;
import java.util.List;

import de.szut.dqi12.cheftrainer.client.guicontrolling.AlertDialog;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public abstract class DialogController {

	/**
	 * Checks the user inputs when the user inputs and change the border color
	 * of empty/wrong input fields
	 * 
	 * @return
	 */
	protected List<String> checkInputs(TextField[] inputFields) {
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
	public void showError(String title, String header, String content) {
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
