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
	
	public CipherFactory (Key key){
		this.key=key;
	}
	
	public String encrypt(String text) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrypted = cipher.doFinal(text.getBytes());

		BASE64Encoder myEncoder = new BASE64Encoder();
		String geheim = myEncoder.encode(encrypted);

		return geheim;
	}

	public String decrypt(String geheim) throws Exception {
		BASE64Decoder myDecoder = new BASE64Decoder();
		byte[] crypted = myDecoder.decodeBuffer(geheim);

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] cipherData = cipher.doFinal(crypted);
		return new String(cipherData);
	}


	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

}