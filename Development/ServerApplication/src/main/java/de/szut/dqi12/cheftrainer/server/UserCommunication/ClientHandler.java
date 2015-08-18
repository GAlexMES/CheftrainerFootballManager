package de.szut.dqi12.cheftrainer.server.UserCommunication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.szut.dqi12.cheftrainer.ConnectorLib.CipherFactory;

public class ClientHandler implements Runnable {
	
	BufferedReader reader;
	PrintWriter writer;
	Socket socket;
	SecretKey aesSymetricKey;
	CipherFactory cipherFactory;
	
	public ClientHandler(Socket clientSocket, KeyPair rsaKeyPair) {
		try {
			cipherFactory = new CipherFactory(rsaKeyPair.getPrivate(), "RSA");
			socket = clientSocket;
			InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(isReader);
			
			writer.println(rsaKeyPair.getPublic());
			writer.flush();
		} catch(Exception ex) {ex.printStackTrace();}
	} 
	
	public void run() {
		String message;
		int counter = 0;
		String key ="";
		try {
			while ((message = reader.readLine()) != null) {
				System.out.println(message);
				if(counter<3){
					key+=message;
				}
				if(counter==2){
					System.out.println("verschlüsselt:"+key);
					key = cipherFactory.decrypt(key);
					System.out.println("unverschlüsselt:"+key);
					byte[] decodedKey = Base64.getDecoder().decode(key);
					aesSymetricKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
					cipherFactory.setKey(aesSymetricKey);
					cipherFactory.setAlgorithm("AES");
				}
				if(counter==3){
					cipherFactory.decrypt(message);
				}
				counter ++;
			}
		} catch(Exception ex) {ex.printStackTrace();}
	}
	
} 
