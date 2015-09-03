package de.szut.dqi12.cheftrainer.connectorlib.messages;

import java.io.PrintWriter;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableController;
import de.szut.dqi12.cheftrainer.connectorlib.cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.connectorlib.logging.LoggingMessages;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.Handshake_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ClientHandler;

/**
 * The MessageController Class is important for sending and receiving Message
 * objects. It could be used from both, client and server. It says the ID <->
 * Class Mapper to map the IDs to the correct classes and forwards new message
 * to the correct callable.
 * 
 * @author Alexander Brennecke
 *
 */
public class MessageController {

	private HashMap<String, CallableAbstract> callableMap;
	private final static String JSON_IDENTIFIER_ID = "m_ID";
	private final static String JSON_IDENTIFIER_CONTENT = "m_CONTENT";
	private CipherFactory cipherFactory;
	private boolean completedHandshake = false;
	private KeyPair rsaKeyPair;
	private PrintWriter writer;
	private String[] handshakeIDs = { Handshake_MessageIDs.AES_KEY,
			Handshake_MessageIDs.HANDSHAKE_ACK,
			Handshake_MessageIDs.RSA_PUBLIC_KEY };

	private final static Logger LOGGER = Logger
			.getLogger(MessageController.class);

	/**
	 * Constructor. Tries to generate a ID<->Class Map for each element in the
	 * idMappers list. After that it sets the messageController of each instance
	 * to itself.
	 * 
	 * @param idMappers
	 */
	public MessageController(List<IDClass_Path_Mapper> idMappers) {

		callableMap = new HashMap<String, CallableAbstract>();

		idMappers.forEach(element -> mapIDsToCallableAbstract(element));

		Iterator<?> it = callableMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			((CallableAbstract) pair.getValue()).setMessageController(this);
		}
	}

	/**
	 * Creates a ID<->CallableAbstract Map for the given idClassPathMapper.
	 * 
	 * @param idClassPathMapper
	 *            the IDClass_Path_Mapper, which should be mapped.
	 */
	private void mapIDsToCallableAbstract(IDClass_Path_Mapper idClassPathMapper) {

		List<String> idList = idClassPathMapper.getMesIDabs().getIDs();
		HashMap<String, CallableAbstract> callableIDMap = CallableController
				.getInstancesForIDs(idList, idClassPathMapper.getPathToDir(),
						idClassPathMapper.getPackagePathToDir());
		callableMap.putAll(callableIDMap);
	}

	/**
	 * Is called from the Client/ServerHandler, when a new message comes through
	 * the InputStream. Tries to transform the String to a Message and handles
	 * it.
	 * 
	 * @param jsonString
	 *            the String, that should be parsed.
	 */
	public void receiveMessage(String jsonString) {
		try {
			Message message = parseJSON(jsonString);
			handleMessage(message);
		} catch (MessageException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Is called to send a message. Creates a new JSONObject with the given
	 * message object and a String out of the JSONObject.
	 * 
	 * @param message
	 *            the Message, that should be send.
	 */
	public void sendMessage(Message message) {
		JSONObject tempJsonObj = new JSONObject();

		tempJsonObj.put(JSON_IDENTIFIER_ID, message.getMessageID());

		String encryptedMessage = message.getMessageContent();
		if (completedHandshake) {
			try {
				encryptedMessage = cipherFactory.encrypt(encryptedMessage);
				tempJsonObj.put(JSON_IDENTIFIER_CONTENT, encryptedMessage);

				writer.println(tempJsonObj.toString());
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (Arrays.asList(handshakeIDs).contains(message.getMessageID())) {
			tempJsonObj.put(JSON_IDENTIFIER_CONTENT, encryptedMessage);

			writer.println(tempJsonObj.toString());
			writer.flush();
		}

	}

	/**
	 * Calls the massageArrived() function of the Callable, which belongs to the
	 * ID of the message.
	 * 
	 * @param message
	 *            the received message.
	 * @throws MessageException
	 */
	private void handleMessage(Message message) throws MessageException {
		String messageID = message.getMessageID();
		LOGGER.info(LoggingMessages.NEW_MESSAGE + messageID);
		callableMap.get(messageID).messageArrived(message);
	}

	/**
	 * Tries to parse the incoming JSON String and maps it to a new Message
	 * Object.
	 * 
	 * @param jsonString
	 *            the String, that should be parsed.
	 * @return a new Message object, to which the incoming String was mapped.
	 * @throws MessageException
	 */
	private Message parseJSON(String jsonString) throws MessageException {
		JSONObject jsonObj = new JSONObject(jsonString);
		String messageID = jsonObj.getString(JSON_IDENTIFIER_ID);
		String jsonMessage = jsonObj.getString(JSON_IDENTIFIER_CONTENT);
		String content = "";

		if (completedHandshake) {
			try {
				content = cipherFactory.decrypt(jsonMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			content = jsonMessage;
		}
		Message tempMessage = new Message(messageID, content);
		return tempMessage;
	}

	// GETTER AND SETTER

	public KeyPair getRsaKeyPair() {
		return rsaKeyPair;
	}

	public void setRsaKeyPair(KeyPair rsaKeyPair) {
		this.rsaKeyPair = rsaKeyPair;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public void setCompletedHandshake(boolean completedHandshake) {
		this.completedHandshake = completedHandshake;
		LOGGER.info(LoggingMessages.HANDSHAKE_COMPLETED);
	}

	public void setAESKey(SecretKey aesKey) {
		cipherFactory = new CipherFactory(aesKey, "AES");
	}

}
