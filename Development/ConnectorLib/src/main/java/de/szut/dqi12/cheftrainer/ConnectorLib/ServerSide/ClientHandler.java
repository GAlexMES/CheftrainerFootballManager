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

public class ClientHandler implements Runnable {

	private BufferedReader reader;
	private PrintWriter writer;
	private Socket socket;
	private SecretKey aesSymetricKey;
	private CipherFactory cipherFactory;
	private ServerInterface servInterface;

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

	public void sendMessage(String message) {
		String encryptedMessage;
		try {
			encryptedMessage = cipherFactory.encrypt(message);
			writer.println(encryptedMessage);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

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

	private void handshake(String key) throws Exception {

		String aesKey = cipherFactory.decrypt(key);

		byte[] decodedKey = Base64.getDecoder().decode(aesKey);
		aesSymetricKey = new SecretKeySpec(decodedKey, 0, decodedKey.length,
				"AES");

		cipherFactory.setKey(aesSymetricKey);
		cipherFactory.setAlgorithm("AES");
	}

}
