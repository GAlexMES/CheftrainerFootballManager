package de.szut.dqi12.cheftrainer.ConnectorLib.ServerSide;

import java.util.ArrayList;

public interface ServerInterface {
	
	public void updateClientHandlerList(ArrayList<ClientHandler> clientHandlerList);
	public void  receiveMessage(String message);
	public void  sendMessage(String message);
	public void createServer();
}
