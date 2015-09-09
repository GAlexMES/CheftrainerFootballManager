package de.szut.dqi12.cheftrainer.connectorlib.callables;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.szut.dqi12.cheftrainer.connectorlib.cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.Handshake_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

/**
 * This class handles messages with the ID "AESKey". It saves the received
 * AESKey and sends an Handshacke_ACK message.
 * 
 * @author Alexander Brennecke
 *
 */
public class AESKey extends CallableAbstract {

	CipherFactory cipherFactory;

	/**
	 * Is used by the classloader to map this class to the ID
	 * 
	 * @return a new AESKey instance
	 */
	public static CallableAbstract newInstance() {
		return new AESKey();
	}

	/**
	 * Is called, when a new message with the ID AESKey arrived at the
	 * MessageController.
	 */
	public void messageArrived(Message message) {
		cipherFactory = new CipherFactory(mesController.getRsaKeyPair()
				.getPrivate(), "RSA");
		String key = message.getMessageContent();
		try {
			SecretKey aesKey = readAESKey(key);
			mesController.setAESKey(aesKey);
			mesController.setCompletedHandshake(true);
			sendAck();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * generates a new Message with the ID HANDSHACKE_ACK and sends it through
	 * the MessageController
	 */
	private void sendAck() {
		Message handshakeAck = new Message(Handshake_MessageIDs.HANDSHAKE_ACK,
				"ACK!");
		mesController.sendMessage(handshakeAck);
	}

	/**
	 * Creates the handshake with the new client. Builds a decrypts the AES key,
	 * which was sent by the client and configures the cipherFactory for further
	 * AES cipher.
	 * 
	 * @param key
	 *            the AES key, which is encrypted with the public rsa key.
	 */
	private SecretKey readAESKey(String key) throws Exception {
		String aesKey = cipherFactory.decrypt(key);
		byte[] decodedKey = Base64.getDecoder().decode(aesKey);

		SecretKey aesSymetricKey = new SecretKeySpec(decodedKey, 0,
				decodedKey.length, "AES");
		return aesSymetricKey;
	}
}
