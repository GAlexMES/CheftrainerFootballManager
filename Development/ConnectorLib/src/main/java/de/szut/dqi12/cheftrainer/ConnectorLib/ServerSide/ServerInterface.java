package de.szut.dqi12.cheftrainer.connectorlib.serverside;

import java.util.ArrayList;

/**
 * Interface for the user of the lib.
 * The rceiveMessage function will be called from the socket, when a new message arrived. An object of the class, which implements this interface must be set to the Client class to provide that function.
 * updateClientHandlerList will be called when a new client registers to the socket
 * sendMessage and createServer are placeholder to create a new Server object or tosend messages to this server object.
 * @author Alexander Brennecke
 *
 */
public interface ServerInterface {
	
	public void updateClientHandlerList(ArrayList<ClientHandler> clientHandlerList);
	public void  receiveMessage(String message);
	public void  sendMessage(String message);
	public void createServer();
}
