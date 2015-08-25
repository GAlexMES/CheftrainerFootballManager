package de.szut.dqi12.cheftrainer.connectorlib.serverside;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * This Server class creates a new server socket.
 * @author Alexander Brennecke
 *
 */
public class Server {

	private KeyPair keyPair;
	private ArrayList<Thread> clientList = new ArrayList<Thread>();
	private ArrayList<ClientHandler> clientHandlerList = new ArrayList<ClientHandler>();
	private ServerProperties serverProps;

	/**
	 * Constructor. Generates a new RSA KeyPair.
	 * @param serverInterface
	 */
	public Server(ServerProperties serverProps) {
		this.serverProps = serverProps;
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			keyPair = kpg.genKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Is used to start the Server Thread. Will receive new connection to unlisted clients and registers them.
	 */
	public void run() {
		try {
			ServerSocket serverSock = new ServerSocket(serverProps.getPort());
			while (true) {
				Socket clientSocket = serverSock.accept();
				newClient(clientSocket);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Is called, when a new Client wants to connect to the server. generate a new ClientHandler and Thread for that client and updates the ServerInterface.
	 * @param clientSocket
	 */
	private void newClient(Socket clientSocket){
		ClientHandler tempClientHandler = new ClientHandler(clientSocket, keyPair,
				serverProps);
		clientHandlerList.add(tempClientHandler);
		Thread t = new Thread(tempClientHandler);
		clientList.add(t);
		serverProps.getServInterface().updateClientHandlerList(clientHandlerList);
		t.run();
	}

}