package de.szut.dqi12.cheftrainer.connectorlib.clientside;

/**
 * Interface for the user of the lib.
 * The rceiveMessage function will be called from the socket, when a new message arrived. An object of the class, which implements this interface must be set to the Client class to provide that function.
 * The sendMessage and createClient functions are not necessary for this lib, but it would be good to create a new Client Object into the createClient function. And to use the sendMessage function to send a Message to the server.
 * @author Alexander Brennecke
 *
 */
public interface ClientInterface {
	public void  receiveMessage(String message);
	public void  sendMessage(String message);
	public void createClient();;
}
