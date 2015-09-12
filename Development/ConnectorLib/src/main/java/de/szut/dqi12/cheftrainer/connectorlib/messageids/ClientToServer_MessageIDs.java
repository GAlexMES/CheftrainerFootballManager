package de.szut.dqi12.cheftrainer.connectorlib.messageids;

/**
 * IDs from Messages, which are sent by the client and received by the server.
 * @author Alexander Brennecke
 *
 */
public class ClientToServer_MessageIDs extends MessageIDAbstract{
	
	public static String USER_AUTHENTIFICATION ="UserAuthentification";
	public static String COMMUNITY_AUTHENTIFICATION = "CommunityAuthentification";
	public static String REQUEST_UPDATE = "UpdateRequest";
}
