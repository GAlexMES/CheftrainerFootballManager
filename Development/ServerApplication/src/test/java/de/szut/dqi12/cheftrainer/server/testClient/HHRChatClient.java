package de.szut.dqi12.cheftrainer.server.testClient;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class HHRChatClient {

	JTextArea eingehend;
	JTextField ausgehend;
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;

	public static void main(String[] args) {
		HHRChatClient client = new HHRChatClient();
		client.los();
	}

	public void los() {

		// GUI erstellen

		JFrame frame = new JFrame("Lächerlich einfacher Chat-Client");
		JPanel hauptPanel = new JPanel();

		eingehend = new JTextArea(15, 20);
		eingehend.setLineWrap(true);
		eingehend.setWrapStyleWord(true);
		eingehend.setEditable(false);

		JScrollPane fScroller = new JScrollPane(eingehend);
		fScroller
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		fScroller
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		ausgehend = new JTextField(20);

		JButton sendenButton = new JButton("Senden");
		sendenButton.addActionListener(new SendenButtonListener());

		hauptPanel.add(fScroller);
		hauptPanel.add(ausgehend);
		hauptPanel.add(sendenButton);

		netzwerkEinrichten();
		Thread readerThread = new Thread(new EingehendReader());
		readerThread.start();

		frame.getContentPane().add(BorderLayout.CENTER, hauptPanel);
		frame.setSize(400, 500);
		frame.setVisible(true);

	} // los schließen

	private void netzwerkEinrichten() {
		try {
			sock = new Socket("127.0.0.1", 5000);
			InputStreamReader streamReader = new InputStreamReader(
					sock.getInputStream());
			reader = new BufferedReader(streamReader);

			writer = new PrintWriter(sock.getOutputStream());

			System.out.println("Netzwerkverbindung steht");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	} // netzwerkEinrichten schließen

	public class SendenButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			try {
				writer.println(ausgehend.getText());
				writer.flush();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			ausgehend.setText("");
			ausgehend.requestFocus();
		}
	} // innere Klasse SendenButtonListener schließen

	public class EingehendReader implements Runnable {
		@SuppressWarnings("unused")
		public void run() {
			String nachricht;
			BigInteger modulus = null;
			BigInteger exponent;
			int counter = 0;
			try {

				while ((nachricht = reader.readLine()) != null) {
					System.out.println("gelesen: " + nachricht);
					eingehend.append(nachricht + "\n");
					
					switch(counter){
					case 1: modulus = new BigInteger(nachricht.split(" ")[3]);
							break;
					case 2: exponent = new BigInteger(nachricht.split(" ")[4]);
							
					}
					counter++;
				} // Ende der while-Schleife
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} // run schließen
	} // innere Klasse EingehendReader schließen
} // äußere Klasse schließen