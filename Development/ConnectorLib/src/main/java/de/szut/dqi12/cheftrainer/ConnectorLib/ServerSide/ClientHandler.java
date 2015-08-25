package de.szut.dqi12.cheftrainer.connectorlib.serverside;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.Handshake_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messages.MessageController;

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
	private CipherFactory cipherFactory;
	private MessageController messageController;

	private boolean allowMessageSending = false;

	/**
	 * Constructor. Generates a cipherFactory and sends the RSA Public Key to the new client.
	 * @param clientSocket a new Socket to a new client
	 * @param rsaKeyPair the server KeyPair for RSA cipher
	 * @param serverProps received messages will be forwarded to that object
	 */
	public ClientHandler(Socket clientSocket, KeyPair rsaKeyPair,
			ServerProperties serverProps) {
		
		ClientToServer_MessageIDs cts_MessageIDs = new ClientToServer_MessageIDs();
		messageController = new MessageController(cts_MessageIDs.getIDs(),serverProps.getPathToCallableDir(),serverProps.getPackagePathToCallableDir());
		messageController.setRsaKeyPair(rsaKeyPair);

		try {
			cipherFactory = new CipherFactory(rsaKeyPair.getPrivate(), "RSA");
			socket = clientSocket;
			InputStreamReader isReader = new InputStreamReader(
					socket.getInputStream());
			writer = new PrintWriter(socket.getOutputStream());
			messageController.setWriter(writer);
			reader = new BufferedReader(isReader);
			
			Message rsaMessage = generateRSAMessage(rsaKeyPair.getPublic());
			
			messageController.sendMessage(rsaMessage);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Message generateRSAMessage(PublicKey publicKey) {
		Message rsaMessage = new Message(Handshake_MessageIDs.RSA_PUBLIC_KEY);
		String pKString = publicKey.toString();
		String[] pkStringSplitted = pKString.split("\n");
		BigInteger modulus  = new BigInteger(pkStringSplitted[1].split(" ")[3]);
		BigInteger exponent = new BigInteger(pkStringSplitted[2].split(" ")[4]);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("modulus", modulus.toString());
		jsonObject.put("exponent", exponent.toString());
		rsaMessage.setMessageContent(jsonObject.toString());
		return rsaMessage;
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
		try {
			while ((message = reader.readLine()) != null) {
				messageController.receiveMessage(message);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
