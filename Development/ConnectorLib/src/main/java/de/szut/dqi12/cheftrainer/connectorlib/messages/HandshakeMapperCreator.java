package de.szut.dqi12.cheftrainer.connectorlib.messages;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableController;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.Handshake_MessageIDs;

public class HandshakeMapperCreator {
	
	/**
	 * 
	 * @return a new IDClass_Path_Mapper for the Handshake Message IDs
	 */
	public static IDClass_Path_Mapper getIDClassPathMapperForHandshake() {

		Handshake_MessageIDs hm_messageIDs = new Handshake_MessageIDs();
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
		
		return new IDClass_Path_Mapper(hm_messageIDs, pathToHandshakeDir, pathToHandshakePackage);
	}

}
