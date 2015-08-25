package de.szut.dqi12.cheftrainer.connectorlib.callables;

import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messages.MessageController;

public abstract class CallableAbstract {

	MessageController mesController;

	public void messageArrived(Message message) {
	}

	public static CallableAbstract newInstance() {
		return null;
	}

	public void setMessageController(MessageController mesController) {
		this.mesController = mesController;
	}
}
