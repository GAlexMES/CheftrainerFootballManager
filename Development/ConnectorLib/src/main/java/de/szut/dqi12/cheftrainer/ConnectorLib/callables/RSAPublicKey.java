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

public class RSAPublicKey extends CallableAbstract {
	
	BigInteger modulus;
	CipherFactory cipherFactory;
	
	public void messageArrived(Message message) {
		JSONObject jsonObject = new JSONObject(message.getMessageContent());
		PublicKey rsaKey = readRSAKey(jsonObject);
		SecretKey secKey = sendSymmetricKey(rsaKey);
		mesController.setAESKey(secKey);
	}

	public static CallableAbstract newInstance() {
		return new RSAPublicKey();
	}

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
	
	private Message generateAESKeyMessage(String encryptedKey){
		Message aesMessage = new Message(Handshake_MessageIDs.AES_KEY);
		aesMessage.setMessageContent(encryptedKey);
		return aesMessage;
	}
}
