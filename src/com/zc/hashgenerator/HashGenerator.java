package com.zc.hashgenerator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.json.JSONObject;

public class HashGenerator {

	String algorithm = "SHA-256";
	
	// To create hash password by creating different salt value
	public JSONObject generateHash(String data) throws NoSuchAlgorithmException {
		JSONObject jobj = new JSONObject();
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();
		byte[] salt = createSalt();
		digest.update(salt);
		byte[] hash = digest.digest(data.getBytes());
		String hashvalue = bytesToStringHex(hash);
		jobj.put("salt", salt);
		jobj.put("hashvalue", hashvalue);
		return jobj;
	}

	// To generate hash password by passing user salt
	public String generateHash(String data, byte[] salt) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();
		digest.update(salt);
		byte[] hash = digest.digest(data.getBytes());
		String hashvalue = bytesToStringHex(hash);
		return hashvalue;
	}
	
	public static byte[] createSalt() {
		byte[] bytes = new byte[20];
		SecureRandom random = new SecureRandom();
		random.nextBytes(bytes);
		return bytes;
	}
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToStringHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; ++j) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
}
