package com.zc.randomsessionid;

import java.security.SecureRandom;

public class RandomSessionIdGenerator {

	public String RandomSessionId() {
		int n = 30;
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "@#$%^&*-+./" + "abcdefghijklmnopqrstuvxyz";
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
