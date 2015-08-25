package de.szut.dqi12.cheftrainer.connectorlib.messages;

import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableController;
import de.szut.dqi12.cheftrainer.connectorlib.cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.Handshake_MessageIDs;

public class MessageController {

	private HashMap<String, CallableAbstract> callableMap;
	private final static String JSON_IDENTIFIER_ID = "m_ID";
	private final static String JSON_IDENTIFIER_CONTENT = "m_CONTENT";
	private CipherFactory cipherFactory;
	private boolean completedHandshake = false;
	private KeyPair rsaKeyPair;
	private PrintWriter writer;

	public MessageController(List<String> fieldList, URL pathToCallableDir,
			String packagePathToCallableDir) {
		Handshake_MessageIDs hm = new Handshake_MessageIDs();
		callableMap = new HashMap<String, CallableAbstract>();
		List<String> idList = hm.getIDs();

		URL pathToHandshakeDir = null;
		try {
			pathToHandshakeDir = CallableController.class.getResource(".")
					.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		String pathToHandshakePackage = CallableController.class.getName();

		HashMap<String, CallableAbstract> callableHandshakeMap = CallableController
				.getInstancesForIDs(idList, pathToHandshakeDir,
						pathToHandshakePackage);
		HashMap<String, CallableAbstract> callabelOtherMap = CallableController
				.getInstancesForIDs(fieldList, pathToCallableDir,
						packagePathToCallableDir);

		callableMap.putAll(callableHandshakeMap);
		callableMap.putAll(callabelOtherMap);

		Iterator it = callableMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			((CallableAbstract) pair.getValue()).setMessageController(this);
		}
	}

	public void receiveMessage(String jsonString) {
		try {
			Message message = parseJSON(jsonString);
			handleMessage(message);
		} catch (MessageException e) {
			e.printStackTrace();
		}

	}

	public void sendMessage(Message message) {
		JSONObject tempJsonObj = new JSONObject();

		tempJsonObj.put(JSON_IDENTIFIER_ID, message.getMessageID());
		tempJsonObj.put(JSON_IDENTIFIER_CONTENT, message.getMessageContent());

		writer.println(tempJsonObj.toString());
		writer.flush();
		
	}

	public void registerCallable(String messageID, CallableAbstract call) {
		callableMap.put(messageID, call);
	}

	private void handleMessage(Message message) throws MessageException {
		String messageID = message.getMessageID();
		callableMap.get(messageID).messageArrived(message);
	}

	private Message parseJSON(String jsonString) throws MessageException {
		JSONObject jsonObj = new JSONObject(jsonString);
		String messageID = jsonObj.getString(JSON_IDENTIFIER_ID);
		String jsonMessage = jsonObj.getString(JSON_IDENTIFIER_CONTENT);
		String content = "";
		if(completedHandshake){
			try {
				content = cipherFactory.decrypt(jsonMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			content = jsonMessage;
		}
		Message tempMessage = new Message(messageID, content);
		return tempMessage;
	}

	public KeyPair getRsaKeyPair() {
		return rsaKeyPair;
	}

	public void setRsaKeyPair(KeyPair rsaKeyPair) {
		this.rsaKeyPair = rsaKeyPair;
	}

	public void setWriter(PrintWriter writer){
		this.writer = writer;
	}
	
}
