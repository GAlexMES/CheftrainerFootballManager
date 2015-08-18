package de.szut.dqi12.cheftrainer.client.serverCommunication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.SecretKey;

import de.szut.dqi12.cheftrainer.ConnectorLib.CipherFactory;
import de.szut.dqi12.cheftrainer.ConnectorLib.KeyGenerator;

/**
 * This class is the direct connection class to a java server. It receives and sends messages to the server.
 * @author Alexander Brennecke
 *
 */
public class ServerConnection implements Runnable {
	BufferedReader reader;
	PrintWriter writer;
	PublicKey rsaPublicKey;
	CipherFactory cipherFactory;
	
	/**
	 * Constructor
	 * @param socket
	 */
	public  ServerConnection(Socket socket) throws Exception{
		InputStreamReader streamReader = new InputStreamReader(
				socket.getInputStream());
		
		reader = new BufferedReader(streamReader); 

		writer = new PrintWriter(socket.getOutputStream());
	}
	
	public void sendMessage(String message) {
		try {
			writer.println(message);
			writer.flush();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public void run() {
		String message;
		BigInteger modulus = null;
		BigInteger exponent;
		int counter = 0;
		try {
			while ((message = reader.readLine()) != null) {
				System.out.println("empfangen: " + message);

				switch (counter) {
				case 1:
					modulus = new BigInteger(message.split(" ")[3]);
					break;
				case 2:
					exponent = new BigInteger(message.split(" ")[4]);
					PublicKey rsaPublicKey = KeyGenerator.generatePublicKey(modulus, exponent);
					sendSymetricKey(rsaPublicKey);
				}
				counter++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void sendSymetricKey(PublicKey rsaPublicKey){
		cipherFactory = new CipherFactory(rsaPublicKey, "RSA");
		try {
			SecretKey secKey = KeyGenerator.getRandomAESKey();
			String encodedKey = Base64.getEncoder().encodeToString(secKey.getEncoded());
			String rsaEncryptedEASKey = cipherFactory.encrypt(encodedKey);
			System.out.println("unverschlüsselt:" + encodedKey);
			System.out.println("verschlüsselt:" + rsaEncryptedEASKey);
			sendMessage(rsaEncryptedEASKey);
			cipherFactory.setKey(secKey);
			cipherFactory.setAlgorithm("AES");
			sendMessage("Hallo Server!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}