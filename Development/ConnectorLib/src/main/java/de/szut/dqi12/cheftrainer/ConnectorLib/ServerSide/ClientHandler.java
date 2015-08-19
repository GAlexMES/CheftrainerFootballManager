package de.szut.dqi12.cheftrainer.ConnectorLib.ServerSide;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.szut.dqi12.cheftrainer.ConnectorLib.Cipher.CipherFactory;

/**
 * The ClientHandler class is the direct connection to the clients. The server class has a ClientHandler object for every open connection.
 * This class creates the handshake with the client, stores the symmetric key for AES cipher and encrypts/decrypts the sent/received messages.
 * @author Alexander Brennecke
 *
 */
public class ClientHandler implements Runnable {

	private BufferedReader reader;
	private PrintWriter writer;
	private Socket socket;
	private SecretKey aesSymetricKey;
	private CipherFactory cipherFactory;
	private ServerInterface servInterface;

	private boolean allowMessageSending = false;

	/**
	 * Constructor. Generates a cipherFactory and sends the RSA Public Key to the new client.
	 * @param clientSocket a new Socket to a new client
	 * @param rsaKeyPair the server KeyPair for RSA cipher
	 * @param servInterface received messages will be forwarded to that object
	 */
	public ClientHandler(Socket clientSocket, KeyPair rsaKeyPair,
			ServerInterface servInterface) {
		this.servInterface = servInterface;

		try {
			cipherFactory = new CipherFactory(rsaKeyPair.getPrivate(), "RSA");
			socket = clientSocket;
			InputStreamReader isReader = new InputStreamReader(
					socket.getInputStream());
			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(isReader);
			writer.println(rsaKeyPair.getPublic());
			writer.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Is used to send a message. The handshake must be completed otherwise there will nothing happen.
	 * @param message the decrypted message that should be send to the client
	 */
	public void sendMessage(String message) {
		if (allowMessageSending) {
			String encryptedMessage;
			try {
				encryptedMessage = cipherFactory.encrypt(message);
				writer.println(encryptedMessage);
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Starts the thread for the connection. Receives the messages, which were sent by the client.
	 */
	public void run() {
		String message;
		int counter = 0;
		String key = "";
		try {
			while ((message = reader.readLine()) != null) {
				if (counter < 3) {
					key += message;
					if (counter == 2) {
						handshake(key);
					}
					counter++;
				} else {
					String decryptedMessage = cipherFactory.decrypt(message);
					servInterface.receiveMessage(decryptedMessage);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Creates the handshake with the new client. Builds a decrypts the AES key, which was sent by the client and configures the cipherFactory for further AES cipher. 
	 * @param key the aes key, which is encrypted with the public rsa key.
	 */
	private void handshake(String key) throws Exception {

		String aesKey = cipherFactory.decrypt(key);

		byte[] decodedKey = Base64.getDecoder().decode(aesKey);
		aesSymetricKey = new SecretKeySpec(decodedKey, 0, decodedKey.length,
				"AES");

		cipherFactory.setKey(aesSymetricKey);
		cipherFactory.setAlgorithm("AES");

		allowMessageSending = true;

	}

}
