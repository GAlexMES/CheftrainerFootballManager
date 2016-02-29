package de.szut.dqi12.cheftrainer.client.servercommunication;

import java.io.IOException;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.ClientProperties;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.ServerToClient_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;

/**
 * Can Connect an Server
 */
public class ServerConnection {
	private final static String PACKAGE_PATH = "de.szut.dqi12.cheftrainer.client.callables.CLASS";
	private final static String DIR_PATH = "de/szut/dqi12/cheftrainer/client/callables/";
	
	/**
	 * Creats an Connetion to an Server
	 * @param clientProps Properties of an Client
	 * @return An Client
	 * @throws IOException
	 */
	public static Client createServerConnection(ClientProperties clientProps ) throws IOException{
		ServerToClient_MessageIDs stc = new ServerToClient_MessageIDs();
		IDClass_Path_Mapper idMapper = new IDClass_Path_Mapper(stc,DIR_PATH, PACKAGE_PATH);
		clientProps.addClassPathMapper(idMapper);
		return new Client(clientProps);
	}
	
}
