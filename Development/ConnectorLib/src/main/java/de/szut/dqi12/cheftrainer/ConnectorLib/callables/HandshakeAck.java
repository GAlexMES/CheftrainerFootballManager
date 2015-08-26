package de.szut.dqi12.cheftrainer.connectorlib.callables;

import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class HandshakeAck extends CallableAbstract {
	public void messageArrived(Message message) {
		mesController.setCompletedHandshake(true);
	}

	public static CallableAbstract newInstance() {
		return new HandshakeAck();
	}
}
