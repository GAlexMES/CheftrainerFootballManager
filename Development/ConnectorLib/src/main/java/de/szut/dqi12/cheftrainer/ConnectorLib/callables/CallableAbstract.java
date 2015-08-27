package de.szut.dqi12.cheftrainer.connectorlib.callables;

import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messages.MessageController;

/**
 * Abstract Class, which should be extended from all Callables.
 * @author Alexander Brennecke
 *
 */
public abstract class CallableAbstract {

	MessageController mesController;

	/**
	 * Is called from the MessageController whenever a new Message with arrives.
	 * @param message
	 */
	public void messageArrived(Message message) {
	}

	/**
	 * Must be overriden by every class, extends from this class. Is called by the CallableController, which creates a new instance with this function.
	 * @return
	 */
	public static CallableAbstract newInstance() {
		return null;
	}

	/**
	 * Is called from the MessageController to set itself to the class.
	 * @param mesController
	 */
	public void setMessageController(MessageController mesController) {
		this.mesController = mesController;
	}
}
