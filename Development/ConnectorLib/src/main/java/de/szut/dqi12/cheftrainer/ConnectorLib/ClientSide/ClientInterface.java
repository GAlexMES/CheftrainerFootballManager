package de.szut.dqi12.cheftrainer.ConnectorLib.ClientSide;

public interface ClientInterface {
	public void  receiveMessage(String message);
	public void  sendMessage(String message);
	public void createClient();
}
