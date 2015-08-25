package de.szut.dqi12.cheftrainer.connectorlib.messages;

public abstract class CallableAbstract {

	public void messageArrived(Message message) {
	}
	
	
	public static CallableAbstract newInstance() {
		return null;
	}
}
