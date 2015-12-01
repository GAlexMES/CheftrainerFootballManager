package de.szut.dqi12.cheftrainer.client.servercommunication;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.*;

public class ConnectionRefusedListener implements ConnectionDiedListener {

	private Controller controller;
	
	public ConnectionRefusedListener(Controller controller){
		this.controller = controller;
	}

	@Override
	public void connectionDied() {
		controller.resetApplication();
	}
	
}
