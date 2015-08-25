package de.szut.dqi12.cheftrainer.server;

import junit.framework.TestCase;
import de.szut.dqi12.cheftrainer.connectorlib.messages.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.MessageController;
import de.szut.dqi12.cheftrainer.server.callables.CallableController;

public class MessageTest extends TestCase {

	public void testMessage() {
		ClientToServer_MessageIDs messageIDs = new ClientToServer_MessageIDs();
		MessageController megController = new MessageController();
		CallableController cc = new CallableController(messageIDs,megController);
	}
}
