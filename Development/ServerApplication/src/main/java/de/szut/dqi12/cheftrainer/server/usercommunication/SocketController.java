package de.szut.dqi12.cheftrainer.server.usercommunication;

import java.util.HashMap;
import java.util.Map;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.Server;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ServerProperties;

/**
 * This Class uses the ConnectorLib to create a new server.
 * 
 * @author Alexander Brennecke
 *
 */
public class SocketController {

	private Server server;

	private Map<String, Session> activeSessions;

	/**
	 * This method creates a new server and starts it.
	 * 
	 * @throws Exception
	 */
	public SocketController(ServerProperties serverProps) throws Exception {
		activeSessions = new HashMap<>();
		try {
			server = new Server(serverProps);
		} catch (Exception e) {
			throw e;
		}
		server.run();
	}

	public void addSession(Session s) {
		activeSessions.put(s.getUser().getUserName(), s);
	}

	public Session getSession(String userName) {
		return activeSessions.get(userName);

	}
}
