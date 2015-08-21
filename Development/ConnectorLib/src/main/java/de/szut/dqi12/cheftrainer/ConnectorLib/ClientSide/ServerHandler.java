package de.szut.dqi12.cheftrainer.ConnectorLib.ClientSide;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.SecretKey;

import de.szut.dqi12.cheftrainer.ConnectorLib.Cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.ConnectorLib.Cipher.KeyGenerator;


/**
 * This class is the direct connection class to a java server. It receives and
 * sends messages to the server.
 * 
 * @author Alexander Brennecke
 *
 */
public class ServerHandler implements Runnable {
	private BufferedReader reader;
	private PrintWriter writer;
	private CipherFactory cipherFactory;
	private ClientInterface conInterface;

	private BigInteger modulus = null;
	private BigInteger exponent = null;

	private boolean allowMessageSending = false;

	/**
	 * Constructor
	 * 
	 * @param socket
	 */
	public ServerHandler(Socket socket, ClientInterface conInterface)
			throws Exception {
		this.conInterface = conInterface;
		InputStreamReader streamReader = new InputStreamReader(
				socket.getInputStream());

		reader = new BufferedReader(streamReader);

		writer = new PrintWriter(socket.getOutputStream());
	}

	
	/**
	 * This method runs the client thread. It also receives every message, which will be sent to the client from the server.
	 */
	@SuppressWarnings("unused")
	public void run() {
		String message;

		int counter = 0;
		try {
			while ((message = reader.readLine()) != null) {
				if (counter < 3) {
					handshakek(message, counter);
					counter++;
				} else if (cipherFactory != null) {
					String encodedMessage = cipherFactory.decrypt(message);
					conInterface.receiveMessage(encodedMessage);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * This method will sent the given message, if the handshake was already completed.
	 * @param message the message, that should be sended to the server
	 */
	public void sendMessage(String message) {
		if (allowMessageSending) {
			try {
				String encryptedMessage = cipherFactory.encrypt(message);
				writer.println(encryptedMessage);
				writer.flush();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	
	/**
	 * Is used to parse the Server RSA Public Key and to call the sendSymetricKey function.
	 * @param message modulus or exponent of the RSA Public Key in X.509 Format
	 * @param counter 1 = modulus message is the modulus; 2 = message is the exponent and key is complete
	 */
	private void handshakek(String message, int counter) throws Exception {
		switch (counter) {
		case 1:
			modulus = new BigInteger(message.split(" ")[3]);
			break;
		case 2:
			exponent = new BigInteger(message.split(" ")[4]);
			PublicKey rsaPublicKey = KeyGenerator.generatePublicKey(modulus,
					exponent);
			modulus = null;
			exponent = null;
			sendSymmetricKey(rsaPublicKey);
		}
	}

	/**
	 * Generates a symmetric key for AES cipher. Encrypts the symmetric key with given RSA Key and sent the encrypted symmetric key back to the server.
	 * @param rsaPublicKey
	 */
	private void sendSymmetricKey(PublicKey rsaPublicKey) {
		cipherFactory = new CipherFactory(rsaPublicKey, "RSA");
		try {
			SecretKey secKey = KeyGenerator.getRandomAESKey();
			String encodedKey = Base64.getEncoder().encodeToString(
					secKey.getEncoded());

			String encryptedKey = cipherFactory.encrypt(encodedKey);
			writer.println(encryptedKey);
			writer.flush();
			cipherFactory.setKey(secKey);
			cipherFactory.setAlgorithm("AES");
			allowMessageSending = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}