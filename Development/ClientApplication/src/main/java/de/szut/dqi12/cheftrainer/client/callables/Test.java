package de.szut.dqi12.cheftrainer.client.callables;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;


public class Test extends CallableAbstract {
	
	
	public static CallableAbstract newInstance() {
		return new Test();
	}
	
	public void messageArrived(Message message) {
	}
	
}