package de.szut.dqi12.cheftrainer.client;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;

public class Controller {

	private static Controller instance =null;
	
	private Session currentSession;
	
	private GUIController guiController;
	
	public static Controller getInstance(){
		if(instance==null){
			instance = new Controller();
		}
		return instance;
	}
	public void startApplication(Stage primaryStage) {
		guiController = GUIController.getInstance(primaryStage);
		guiController.showLogin();		
	}
	
	public void resetApplication(ActionEvent evt){
		currentSession=null;
		guiController.resetApplication();
	}
	
	public Session getSession() {
		return currentSession;
	}
	public void setSession(Session session) {
		this.currentSession = session;
		session.getClientSocket().getServerHandler().getMessageController().setSession(session);
	}

}
