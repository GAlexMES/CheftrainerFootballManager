package de.szut.dqi12.cheftrainer.connectorlib.callables;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.connectorlib.cipher.KeyGenerator;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.Handshake_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;


/**
 * Is used to receive a RSAPublicKEy, generate a symmetric AESKey and send it back.
 * @author Alexander Brennecke
 *
 */
public class RSAPublicKey extends CallableAbstract {
	
	BigInteger modulus;
	CipherFactory cipherFactory;
	
	
	/**
	 * Is called from the MessageController when a Message with the ID RSAPublicKey arrives.
	 */
	public void messageArrived(Message message) {
		JSONObject jsonObject = new JSONObject(message.getMessageContent());
		PublicKey rsaKey = readRSAKey(jsonObject);
		SecretKey secKey = sendSymmetricKey(rsaKey);
		mesController.setAESKey(secKey);
	}

	/**
	 * Is used by the classloader to map this class to the ID
	 * 
	 * @return a new RSAPublicKey instance
	 */
	public static CallableAbstract newInstance() {
		return new RSAPublicKey();
	}

	/**
	 * Tries to make a new PublicKey out of the incoming JSONObject.
	 * @param message the JSONObject, that contains the modulus and exponent.
	 * @return a new PublicKey object, which was generated with the exponent and modulus, which were received
	 */
	private PublicKey readRSAKey(JSONObject message){
		BigInteger modulus = new BigInteger(message.getString("modulus"));
		BigInteger exponent = new BigInteger(message.getString("exponent"));
		
		PublicKey rsaPublicKey = null;
		try {
			rsaPublicKey = KeyGenerator.generatePublicKey(modulus,exponent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsaPublicKey;
		
	}
	
	/**
	 * Generates a symmetric key for AES cipher. Encrypts the symmetric key with given RSA Key and sent the encrypted symmetric key back to the server.
	 * @param rsaPublicKey
	 */
	private SecretKey sendSymmetricKey(PublicKey rsaPublicKey) {
		cipherFactory = new CipherFactory(rsaPublicKey, "RSA");
		SecretKey secKey = null;
		try {
			secKey = KeyGenerator.getRandomAESKey();
			String encodedKey = Base64.getEncoder().encodeToString(
					secKey.getEncoded());
			String encryptedKey = cipherFactory.encrypt(encodedKey);
			mesController.sendMessage(generateAESKeyMessage(encryptedKey));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return secKey;
	}
	
	/**
	 * Generates the Message object, which includes the AES Key
	 * @param encryptedKey the encrypted AESKey
	 * @return the new Message object, which includes the AES Key
	 */
	private Message generateAESKeyMessage(String encryptedKey){
		Message aesMessage = new Message(Handshake_MessageIDs.AES_KEY);
		aesMessage.setMessageContent(encryptedKey);
		return aesMessage;
	}
}
