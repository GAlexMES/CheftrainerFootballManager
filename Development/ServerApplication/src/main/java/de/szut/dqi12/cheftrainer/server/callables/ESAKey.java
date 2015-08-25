package de.szut.dqi12.cheftrainer.server.callables;

import de.szut.dqi12.cheftrainer.connectorlib.messages.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;


public class ESAKey extends CallableAbstract{
	
	

	@Override
	public void messageArrived(Message message) {
		
	}

	public static CallableAbstract newInstance() {
		return new ESAKey();
	}
	

}
