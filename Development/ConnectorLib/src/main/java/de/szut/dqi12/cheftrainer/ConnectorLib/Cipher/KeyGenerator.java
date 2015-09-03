package de.szut.dqi12.cheftrainer.connectorlib.cipher;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.SecretKey;

/**
 * Generates Keys
 * @author Alexander Brennecke
 *
 */
public class KeyGenerator {
	
	
	/**
	 * Generates a public RSA Key with the given exponent and modulus
	 * @param modulus for the RSA Key
	 * @param exponent for the RSA Key
	 * @return a Public RSA Key
	 * @throws Exception
	 */
	public static PublicKey generatePublicKey(BigInteger modulus,
			BigInteger exponent) throws Exception{
		RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		PublicKey pub = factory.generatePublic(spec);
		return pub;
	}
	
	/**
	 * Generates a new AES Key with a length of 128 bit .
	 * @return a new SecretKey for AES cipher
	 */
	public static SecretKey getRandomAESKey() throws Exception{
		javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("AES");
		keyGen.init(128); // for example
		SecretKey secretKey = keyGen.generateKey();
		return secretKey;
	}

}
