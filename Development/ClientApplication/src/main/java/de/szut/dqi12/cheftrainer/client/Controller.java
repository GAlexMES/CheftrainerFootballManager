package de.szut.dqi12.cheftrainer.client;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;

/**
 * This class is the Controller class for the whole application.
 * @author Alexander Brennecke
 *
 */
public class Controller {

	private static Controller instance =null;
	
	private Session currentSession;
	
	private GUIController guiController;
	
	/**
	 * For singleton patter.
	 * @return
	 */
	public static Controller getInstance(){
		if(instance==null){
			instance = new Controller();
		}
		return instance;
	}
	
	/**
	 * Creates the GUI with the GUIController.
	 * @param primaryStage needs the primary stage of the application.
	 */
	public void startApplication(Stage primaryStage) {
		guiController = GUIController.getInstance(primaryStage);
		guiController.showLogin();		
	}
	
	/**
	 * Closes all dialogs and shows the Login Dialog.
	 * @param evt
	 */
	public void resetApplication(ActionEvent evt){
		currentSession=null;
		guiController.resetApplication();
	}
	
	//GETTER AND SETTER
	public Session getSession() {
		return currentSession;
	}
	public void setSession(Session session) {
		this.currentSession = session;
		session.getClientSocket().getServerHandler().getMessageController().setSession(session);
	}

}
