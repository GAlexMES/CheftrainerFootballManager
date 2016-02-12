package de.szut.dqi12.cheftrainer.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * This class is the Controller class for the whole application.
 * 
 * @author Alexander Brennecke
 *
 */
public class Controller {

	private static Controller instance = null;

	private Session currentSession;

	private GUIController guiController;
	
	private Stage mainStage;

	/**
	 * For singleton patter.
	 * 
	 * @return  An Instance of this class
	 */
	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}

	/**
	 * Creates the GUI with the GUIController.
	 * 
	 * @param primaryStage needs the primary stage of the application.
	 */
	public void startApplication(Stage primaryStage) {
		guiController = GUIController.getInstance(primaryStage);
		mainStage = primaryStage;
		guiController.showLogin();
	}

	/**
	 * Closes all dialogs and shows the Login Dialog.
	 * 
	 * @param evt
	 */
	public void resetApplication(ActionEvent evt) {
		resetApplication();
	}

	/**
	 * Closes all dialogs and shows the Login Dialog.
	 * 
	 * @param evt
	 */
	public void resetApplication() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				currentSession = null;
				guiController.resetApplication();
			}
		});
	}

	public void sendMessageToServer(Message message) {
		currentSession.getClientSocket().sendMessage(message);
	}
	
	public Session getSession() {
		return currentSession;
	}

	public void setSession(Session session) {
		this.currentSession = session;
		session.getClientSocket().getServerHandler().getMessageController().setSession(session);
	}
	
	public Stage getMainStage(){
		return mainStage;
	}
}
