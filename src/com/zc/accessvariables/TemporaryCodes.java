package com.zc.accessvariables;
import java.security.SecureRandom;

public class TemporaryCodes {
	static long theRandomNum = (long) (Math.random()*Math.pow(20,20));
	
	
	
	public static void main(String[] args) {
		int n = 30;
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "@#$%^&*-+./" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(n);
		SecureRandom random = new SecureRandom();
		
//		for (int i = 0; i < n; i++) {
//			int index = (int) (AlphaNumericString.length() * Math.random());
//			sb.append(AlphaNumericString.charAt(index));
//			
//		}
		for (int i = 0; i < n; i++) {
	        // 0-62 (exclusive), random returns 0-61
	        int rndCharAt = random.nextInt(AlphaNumericString.length());
	        char rndChar = AlphaNumericString.charAt(rndCharAt);

	        sb.append(rndChar);
	    }
		System.out.println(sb.toString());
	}
	
}