package de.szut.dqi12.cheftrainer.server.UserCommunication;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CipherFactory {
    
	   private Key key = null;
	   private String verfahren = null;
	 
	   public CipherFactory(Key k, String verfahren) throws Exception {
	      this.key = k;
	      this.verfahren = verfahren;
	   }
	 
	   public OutputStream encryptOutputStream(OutputStream os) throws Exception {
	       
	      // eigentliche Nachricht mit RSA verschluesseln
	      Cipher cipher = Cipher.getInstance(verfahren);
	      cipher.init(Cipher.ENCRYPT_MODE, key);
	      os = new CipherOutputStream(os, cipher);
	       
	      return os;
	   }
	 
	   public InputStream decryptInputStream(InputStream is) throws Exception {
	       
	      // Daten mit AES entschluesseln
	      Cipher cipher = Cipher.getInstance(verfahren);
	      cipher.init(Cipher.DECRYPT_MODE, key);
	      is = new CipherInputStream(is, cipher);
	 
	      return is;
	   }
	 
	   public String encrypt(String text) throws Exception {
	       
	      // Verschluesseln
	      Cipher cipher = Cipher.getInstance(verfahren);
	      cipher.init(Cipher.ENCRYPT_MODE, key);
	      byte[] encrypted = cipher.doFinal(text.getBytes());
	 
	      // bytes zu Base64-String konvertieren
	      BASE64Encoder myEncoder = new BASE64Encoder();
	      String geheim = myEncoder.encode(encrypted);
	       
	      return geheim;
	   }
	 
	   public String decrypt(String geheim) throws Exception {
	       
	      // BASE64 String zu Byte-Array
	      BASE64Decoder myDecoder = new BASE64Decoder();
	      byte[] crypted = myDecoder.decodeBuffer(geheim);      
	        
	      // entschluesseln
	      Cipher cipher = Cipher.getInstance(verfahren);
	      cipher.init(Cipher.DECRYPT_MODE, key);
	      byte[] cipherData = cipher.doFinal(crypted);
	      return new String(cipherData);
	   }
	    
	    
	   //++++++++++++++++++++++++++++++
	   // Getter und Setter
	   //++++++++++++++++++++++++++++++
	    
	   public Key getKey() {
	      return key;
	   }
	 
	   public void setKey(Key key) {
	      this.key = key;
	   }
	 
	   public String getVerfahren() {
	      return verfahren;
	   }
	 
	   public void setVerfahren(String verfahren) {
	      this.verfahren = verfahren;
	   }
	}