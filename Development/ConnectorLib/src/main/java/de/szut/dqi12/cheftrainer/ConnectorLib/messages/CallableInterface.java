package de.szut.dqi12.cheftrainer.ConnectorLib.messages;

public interface CallableInterface {

	public void messageArrived(Message message);
	public CallableInterface newInstance();
}
