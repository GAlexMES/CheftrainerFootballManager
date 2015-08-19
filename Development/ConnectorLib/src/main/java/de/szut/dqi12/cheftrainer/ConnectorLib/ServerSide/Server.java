package de.szut.dqi12.cheftrainer.ConnectorLib.ServerSide;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class Server {

	private final int PORT = 5000;
	private KeyPair keyPair;
	private ServerInterface serverInterface;
	private ArrayList<Thread> clientList = new ArrayList<Thread>();
	private ArrayList<ClientHandler> clientHandlerList = new ArrayList<ClientHandler>();
	

	public Server(ServerInterface serverInterface) {
		this.serverInterface = serverInterface;
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			keyPair = kpg.genKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	public void run() {
		try {
			ServerSocket serverSock = new ServerSocket(PORT);
			while (true) {
				Socket clientSocket = serverSock.accept();
				newClient(clientSocket);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void newClient(Socket clientSocket){
		ClientHandler tempClientHandler = new ClientHandler(clientSocket, keyPair,
				serverInterface);
		clientHandlerList.add(tempClientHandler);
		Thread t = new Thread(tempClientHandler);
		clientList.add(t);
		serverInterface.updateClientHandlerList(clientHandlerList);
		t.run();

	}

}