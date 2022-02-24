package com.zc.mfacredentials;

import java.security.SecureRandom;

public class SessionInfoGenerator {
	public String generateSessionInfo(int n) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(n);
		
		SecureRandom random = new SecureRandom();
		
		for (int i = 0; i < n; i++) {
	        int rndCharAt = random.nextInt(AlphaNumericString.length());
	        char rndChar = AlphaNumericString.charAt(rndCharAt);
	        sb.append(rndChar);
	    }
		return sb.toString();
	}
}
