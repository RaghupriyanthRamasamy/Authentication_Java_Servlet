package com.zc.mfacredentials;

public class OTPCodeGenerator {
	public String generateOTP() {
		int randomPin   =(int) (Math.random()*900000)+100000;
		String otp  = String.valueOf(randomPin);
		return otp;
	}
}
