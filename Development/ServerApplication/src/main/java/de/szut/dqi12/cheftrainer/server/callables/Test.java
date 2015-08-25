package de.szut.dqi12.cheftrainer.server.callables;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.szut.dqi12.cheftrainer.connectorlib.callables.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.cipher.CipherFactory;
import de.szut.dqi12.cheftrainer.connectorlib.messageids.Handshake_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.Message;

public class Test extends CallableAbstract {
	
	
	public static CallableAbstract newInstance() {
		return new Test();
	}
	
	public void messageArrived(Message message) {
	}
	
}