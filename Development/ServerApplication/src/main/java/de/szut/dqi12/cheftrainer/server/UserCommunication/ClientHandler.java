package de.szut.dqi12.cheftrainer.server.UserCommunication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class ClientHandler implements Runnable {
	
	BufferedReader reader;
	PrintWriter writer;
	Socket socket;
	
	public ClientHandler(Socket clientSocket) {
		try {
			socket = clientSocket;
			InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(isReader);
			
			KeyPairGenerator kpg = KeyPairGenerator.getInstance( "RSA" );
			kpg.initialize(1024);
			KeyPair keyPair = kpg.genKeyPair();
			
			writer.println(keyPair.getPublic());
			writer.flush();
		} catch(Exception ex) {ex.printStackTrace();}
	} 
	
	public void run() {
		String nachricht;
		try {
			while ((nachricht = reader.readLine()) != null) {
				System.out.println("gelesen: " + nachricht);
			}
		} catch(Exception ex) {ex.printStackTrace();}
	}
} 
