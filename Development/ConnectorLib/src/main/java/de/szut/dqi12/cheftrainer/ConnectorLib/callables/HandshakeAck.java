package de.szut.dqi12.cheftrainer.connectorlib.callables;

import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class HandshakeAck extends CallableAbstract {
	public void messageArrived(Message message) {
		System.out.println(message.getMessageContent());
	}

	public static CallableAbstract newInstance() {
		return new HandshakeAck();
	}
}
