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

	@SuppressWarnings("unused")
	public void run() {
		String message;

		int counter = 0;
		try {
			while ((message = reader.readLine()) != null) {
				if (counter < 3) {
					handshakek(message, counter);
					counter++;
				}
				else if (cipherFactory != null) {
					String encodedMessage = cipherFactory.decrypt(message);
					conInterface.receiveMessage(encodedMessage);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		try {
			String encryptedMessage = cipherFactory.encrypt(message);
			writer.println(encryptedMessage);
			writer.flush();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

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
			sendSymetricKey(rsaPublicKey);
		}
	}

	private void sendSymetricKey(PublicKey rsaPublicKey) {
		cipherFactory = new CipherFactory(rsaPublicKey, "RSA");
		try {
			SecretKey secKey = KeyGenerator.getRandomAESKey();
			String encodedKey = Base64.getEncoder().encodeToString(
					secKey.getEncoded());
			sendMessage(encodedKey);
			cipherFactory.setKey(secKey);
			cipherFactory.setAlgorithm("AES");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}