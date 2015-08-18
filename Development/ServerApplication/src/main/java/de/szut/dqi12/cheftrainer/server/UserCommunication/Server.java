package de.szut.dqi12.cheftrainer.server.UserCommunication;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class Server {

	private final int PORT = 5000;
	private KeyPair keyPair;
	private CipherFactory rsaCipherFactory;
	
	public Server(){
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			keyPair = kpg.genKeyPair();
			try {
				rsaCipherFactory = new CipherFactory(keyPair.getPrivate(), "RSA");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}

	public void run() {
		try {
			ServerSocket serverSock = new ServerSocket(PORT);
			while(true) {
				Socket clientSocket = serverSock.accept();
				Thread t = new Thread(new ClientHandler(clientSocket, keyPair.getPublic()));
				t.start();
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}