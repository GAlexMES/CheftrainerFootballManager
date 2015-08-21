package de.szut.dqi12.cheftrainer.ConnectorLib.messages;

import java.util.HashMap;

import org.json.JSONObject;

public class MessageController {

	private HashMap<String, CallableInterface> callableMap = new HashMap<String, CallableInterface>();

	private final static String JSON_IDENTIFIER_ID = "m_ID";

	private final static String JSON_IDENTIFIER_CONTENT = "m_CONTENT";

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

	}

	public void registerCallable(String messageID, CallableInterface call) {
		callableMap.put(messageID, call);
	}

	private void handleMessage(Message message) throws MessageException {
		String messageID = message.getMessageID();
		callableMap.get(messageID).messageArrived(message);
	}

	private Message parseJSON(String jsonString) throws MessageException {
		JSONObject jsonObj = new JSONObject(jsonString);
		String messageID = jsonObj.getString(JSON_IDENTIFIER_ID);
		String jsonMessage = jsonObj.getString("JSON_IDENTIFIER_CONTENT");
		JSONObject jsonContent = new JSONObject(jsonMessage);
		Message tempMessage = new Message(messageID, jsonContent);
		return tempMessage;
	}

}
