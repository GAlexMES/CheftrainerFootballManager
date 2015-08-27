package de.szut.dqi12.cheftrainer.connectorlib.callables;

import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * The class is used to confirm the handshake.
 * @author Alexander Brennecke
 *
 */
public class HandshakeAck extends CallableAbstract {
	
	/**
	 * Is called from the MessageController when a Message with the ID HANDSHACKE_ACK arrives.
	 * Sets the completeHandshake boolean to true, which allows free messaging.
	 */
	public void messageArrived(Message message) {
		mesController.setCompletedHandshake(true);
	}

	/**
	 * Is used by the classloader to map this class to the ID
	 * 
	 * @return a new HandshakeAck instance
	 */
	public static CallableAbstract newInstance() {
		return new HandshakeAck();
	}
}
