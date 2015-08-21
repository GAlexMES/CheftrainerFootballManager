package de.szut.dqi12.cheftrainer.ConnectorLib.messages;

import org.json.JSONObject;

public class Message {
	
	private String messageID;
	private JSONObject messageContent;
	
	
	public Message(String messageID, JSONObject jsonContent) {
		this.messageID = messageID;
		this.messageContent = jsonContent;
	}


	public String getMessageID() {
		return messageID;
	}


	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}


	public JSONObject getMessageContent() {
		return messageContent;
	}


	public void setMessageContent(JSONObject messageContent) {
		this.messageContent = messageContent;
	}
	
	
}
