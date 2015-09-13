package de.szut.dqi12.cheftrainer.client.view.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class AlertUtils {
public static final String WRONG_INPUTS = "Please check ypur input for the following parameters: ";
	
	public static final String LOGIN_WRONG_USER = "Your username does not exist in our database. Please check your inputs or create a account.";
	public static final String LOGIN_WRONG_PASSWORD = "Your password is wrong, or u use the wrong username. Pleas check your inputs!";

	public static final String COMMUNITY_CREATION_TITLE = "Create a new community";
	public static final String COMMUNITY_CREATION_WORKED_HEAD = "We created your community!";
	public static final String COMMUNITY_CREATION_WORKED_NOT_HEAD = "We could not create your community!";
	public static final String COMMUNITY_CREATION_WORKED_MESSAGE = "Give the community name and password to friends to play with them!";
	public static final String COMMUNITY_CREATION_WORKED_NOT_MESSAGE = "Maybe your community name is already in use. Try a different one.";
	
	public static final String COMMUNITY_ENTER_TITLE = "Enter a existing community";
	public static final String COMMUNITY_ENTER_WORKED_HEAD = "You joined the community!";
	public static final String COMMUNITY_ENTER_WORKED_NOT_HEAD = "Something went wrong. You could not join the community!";
	public static final String COMMUNITY_ENTER_WORKED_MESSAGE = "You joind the community and we created a new manager and team for you. Good luck!";
	public static final String COMMUNITY_ENTER_WRONG_AUTHENTIFICATION = "Your combination of community name and password does not exist.";
	public static final String COMMUNITY_ENTER_ALREADY_EXIST = "You already play in this community. You can only have one team per community.";
	
	
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
	public static void createSimpleDialog(String title, String header, String content, AlertType type) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Alert alert = showAlert(title, header,
						content, type);
				alert.showAndWait();
			}
		});

	}
	
	
	private static Alert showAlert(String title, String header,
			String message, AlertType type) {
		Alert alert = new Alert(type);
		alert.setContentText(message);
		alert.setTitle(title);
		alert.setHeaderText(header);
		return alert;
	}

	public static Alert createExceptionDialog(Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		return alert;
	}
}
