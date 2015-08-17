package de.szut.dqi12.cheftrainer.server.UserCommunication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class ClientHandler implements Runnable {
	
	BufferedReader reader;
	PrintWriter writer;
	Socket socket;
	
	public ClientHandler(Socket clientSocket, Key publicKey) {
		try {
			socket = clientSocket;
			InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(isReader);
			
			writer.println(publicKey);
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
