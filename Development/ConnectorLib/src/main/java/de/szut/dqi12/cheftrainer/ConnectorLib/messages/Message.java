package de.szut.dqi12.cheftrainer.connectorlib.messages;

/**
 * A Message object contains and ID and the Content. The ID should be one of the IDs, defined in an class extends MessageIDAbstract.
 * @author Alexander Brennecke
 *
 */
public class Message {
	
	private String messageID;
	private String messageContent;
	
	public Message(String messageID){
		this.messageID=messageID;
	}
	
	public Message(String messageID, String content) {
		this.messageID = messageID;
		this.messageContent = content;
	}


	public String getMessageID() {
		return messageID;
	}


	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}


	public String getMessageContent() {
		return messageContent;
	}


	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	
	
}
