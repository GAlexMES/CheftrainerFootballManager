package de.szut.dqi12.cheftrainer.client.servercommunication;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.*;

/**
 * Detected an failed Connection
 */
public class ConnectionRefusedListener implements ConnectionDiedListener {

	private Controller controller;
	
	/**
	 * @param controller Controller, which needs to detect an failed Connection.
	 */
	public ConnectionRefusedListener(Controller controller){
		this.controller = controller;
	}

	@Override
	public void connectionDied() {
		controller.resetApplication();
	}
	
}
