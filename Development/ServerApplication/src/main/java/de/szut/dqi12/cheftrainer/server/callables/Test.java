package de.szut.dqi12.cheftrainer.server.callables;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class Test extends CallableAbstract {
	
	public void messageArrived(Message message) {
		System.out.println(message.getMessageContent());
	}

	public static CallableAbstract newInstance() {
		return new Test();
	}
}
