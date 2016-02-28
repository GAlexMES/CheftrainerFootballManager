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

/**
 * This class provides constants for Alerts and a few method to simply create Alerts.
 * @author Alexander Brennecke
 *
 */
public class AlertUtils {
public static final String WRONG_INPUTS = "Bitte überprüfe die folgenden Eingaben: ";
	
	public static final String LOGIN_WRONG_USER = "Der eingegebene Nzuername existiert nicht. Bitte überprüfe deine Eingaben oder erstelle einen Account.";
	public static final String LOGIN_WRONG_PASSWORD = "Dein Nutzername oder dein passwort ist falsch. Bitte überprüfe deine Eingaben!";

	public static final String COMMUNITY_CREATION_TITLE = "Erstelle eine neue Spielrunde";
	public static final String COMMUNITY_CREATION_WORKED_HEAD = "Deine Spielrunde wurde erstellt!";
	public static final String COMMUNITY_CREATION_WORKED_NOT_HEAD = "Deine Spielrunde konnte nicht erstellt werden!";
	public static final String COMMUNITY_CREATION_WORKED_MESSAGE = "Gebe den Namen und das Passwort der Spielrunde deinen Freunden um mit ihnen zu Spielen!";
	public static final String COMMUNITY_CREATION_WORKED_NOT_MESSAGE = "Der Spielrundenname existiert bereits. Versuche einen anderen.";
	
	public static final String COMMUNITY_ENTER_TITLE = "Tritt einer Spielrunde bei.";
	public static final String COMMUNITY_ENTER_WORKED_HEAD = "Du bist einer Spielrunde beigetreten!";
	public static final String COMMUNITY_ENTER_WORKED_NOT_HEAD = "Etwas hat nicht funktioniert. Du konntest der Spielrunde nicht beitreten!";
	public static final String COMMUNITY_ENTER_WORKED_MESSAGE = "Du bist der Spielrunde beigetreten und wir haben einen neuen Manager und ein neues Team für dich erstellt. Viel Glück!";
	public static final String COMMUNITY_ENTER_WRONG_AUTHENTIFICATION = "Der Spielrunden Name existiert nicht oder das Passwort ist falsch.";
	public static final String COMMUNITY_ENTER_ALREADY_EXIST = "Du spielst bereits in dieser Spielrunde. Du kannst nur ein Team pro Spielrunde haben";
	
	public static final String USER_CREATION_EMAIL_USERNAME = "Deine E-Mail oder dein Nutzername existieren bereits.";
	public static final String USER_CREATION_USERNAME = "Dein Nutzername ist bereits in verwendung. Wähle einen anderen."; 
	public static final String USER_CREATION_EMAIL = "Deine E-Mail existiert bereits. Hast du bereits einen Account?"; 
	
	public static final String LOGIN_ERROR = "Anmeldung fehlgeschlagen!";
	public static final String LOGIN_ERROR_DETAILS ="Wärend deiner Anmeldung trat ein Problem auf. Versuche es erneut.";
	
	public static final String USER_REGISTRATION_ERROR = "Während deiner Registrierung ist etwas schief gelaufen. Versuche es erneut.";
	public static final String FORMATION_SAVED = "Deine neue Aufstellung wurde gespeichert!";
	public static final String FORMATION_NOT_SAVED = "Die neue Aufstellung konnte nicht gespeichert werden! Versuche es erneut!";
	
	public static final String REGISTRATION = "Registrierung!";
	public static final String REGISTRATION_SUCCESS="Registrierung erfolgreich!";
	public static final String REGISTRATION_SUCCESS_DETAILS =" Deine Registrierung war erfolgreich. Du kannst dich nun anmelden.";
	
	public static final String WRONG_PASSWORD  ="Bitte gebe gleiche Passwörter ein!";
	public static final String CHECK_SERVER ="Bitte überprüfe die Server Einstellungen!";
	
	public static final String COMMUNITY_ENTRY_ERROR = "Etwas hat nicht funktioniert. Versuchen sie es erneut.";
	public static final String COMMUNITY_CREATION_ERROR ="Bei der Erstellung der Spielrunde hat etwas nicht funktioniert. Bitte versuche es erneut.";
	
	public static final String UNKNOWN_ERROR = "Es ist ein Problem aufgetreten. Versuche es erneut.";
	public static final String ERROR = "Fehler!";
	
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
	
	/**
	 * Shows a Alert with the given parameters.
	 */
	private static Alert showAlert(String title, String header,
			String message, AlertType type) {
		Alert alert = new Alert(type);
		alert.setContentText(message);
		alert.setTitle(title);
		alert.setHeaderText(header);
		return alert;
	}

	/**
	 * Creates a Dialog, which displays the given exception.
	 * @param e the Exception, that should be displayed
	 * @return the created Alert dialog
	 */
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
