package de.szut.dqi12.cheftrainer.connectorlib.serverside;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableController;
import de.szut.dqi12.cheftrainer.connectorlib.cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.Handshake_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.HandshakeMapperCreator;
import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;
import de.szut.dqi12.cheftrainer.connectorlib.messages.MessageController;

/**
 * The ClientHandler class is the direct connection to the clients. The server
 * class has a ClientHandler object for every open connection. This class
 * creates the handshake with the client, stores the symmetric key for AES
 * cipher and encrypts/decrypts the sent/received messages.
 * 
 * @author Alexander Brennecke
 *
 */
public class ClientHandler implements Runnable {

	private BufferedReader reader;
	private PrintWriter writer;
	private Socket socket;
	private MessageController messageController;
	
	private Server server;

	/**
	 * Constructor. Calls methods to create a message controller and a server connection
	 * 
	 * @param clientSocket
	 *            a new Socket to a new client
	 * @param rsaKeyPair
	 *            the server KeyPair for RSA cipher
	 * @param serverProps
	 *            received messages will be forwarded to that object
	 * @param server 
	 */
	public ClientHandler(Socket clientSocket, KeyPair rsaKeyPair,
			ServerProperties serverProps, Server server) {

		this.server = server;
		createMessageController(serverProps, rsaKeyPair);
		createClientConnection(clientSocket, rsaKeyPair);
		
	}
	
	/**
	 * Creates a Connection to the Client, initializes a new InputStream and print writer
	 * Sends the welcome message, including the RSA public key
	 * @param clientSocket
	 * @param rsaKeyPair
	 */
	private void createClientConnection(Socket clientSocket, KeyPair rsaKeyPair){
		try {
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
	
	/**
	 * Creates new instances for the required callables.
	 * Creates a new message controller.
	 * @param serverProps
	 * @param rsaKeyPair
	 */
	private void createMessageController(ServerProperties serverProps, KeyPair rsaKeyPair){
		List<IDClass_Path_Mapper> idMappers = new ArrayList<IDClass_Path_Mapper>();
		idMappers.addAll(serverProps.getIDMappers());
		idMappers
				.add(HandshakeMapperCreator.getIDClassPathMapperForHandshake());

		messageController = new MessageController(idMappers);
		messageController.setRsaKeyPair(rsaKeyPair);
	}

	/**
	 * Generates the RSAMessage, which includes the modulus and exponent of the
	 * RSA Public Key
	 * 
	 * @param publicKey
	 *            the RSA Public key, which should be send to the client.
	 * @return
	 */
	private Message generateRSAMessage(PublicKey publicKey) {
		Message rsaMessage = new Message(Handshake_MessageIDs.RSA_PUBLIC_KEY);
		String pKString = publicKey.toString();
		String[] pkStringSplitted = pKString.split("\n");
		BigInteger modulus = new BigInteger(pkStringSplitted[1].split(" ")[3]);
		BigInteger exponent = new BigInteger(pkStringSplitted[2].split(" ")[4]);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("modulus", modulus.toString());
		jsonObject.put("exponent", exponent.toString());
		rsaMessage.setMessageContent(jsonObject.toString());
		return rsaMessage;
	}

	/**
	 * Is used to send a message. The handshake must be completed otherwise
	 * there will nothing happen.
	 * 
	 * @param message
	 *            the decrypted message that should be send to the client
	 */
	public void sendMessage(Message message) {
		messageController.sendMessage(message);
	}

	/**
	 * Starts the thread for the connection. Receives the messages, which were
	 * sent by the client.
	 */
	public void run() {
		String message;
		try {
			while ((message = reader.readLine()) != null) {
				messageController.receiveMessage(message);
			}
		} catch (Exception ex) {
			if (ex.getClass() == SocketException.class) {
				System.out.println("connection lost!");
				closeConnection();
			} else {
				ex.printStackTrace();
			}
		} finally {
			closeConnection();
		}
	}
	
	private void closeConnection(){
		server.removeClient(this, Thread.currentThread());
	}

}
