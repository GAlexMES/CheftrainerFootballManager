package de.szut.dqi12.cheftrainer.server.UserCommunication;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class Server {

	private final int PORT = 5000;

	public void run() {
		try {
			ServerSocket serverSock = new ServerSocket(PORT);
			while(true) {
				Socket clientSocket = serverSock.accept();
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}