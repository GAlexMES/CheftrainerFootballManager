package de.szut.dqi12.cheftrainer.connectorlib.messages;

/**
 * The MessageException should be used, when something failed during the parsing process of an incoming message.
 * @author Alexander Brennecke
 *
 */
public class MessageException extends Exception {

	public MessageException(){
		super();
	}
	
	public MessageException(String message){
		super(message);
	}
	
	public MessageException(String message, Throwable cause){
		super(message, cause);
	}
	
	public MessageException(Throwable cause){
		super(cause);
	}
}
