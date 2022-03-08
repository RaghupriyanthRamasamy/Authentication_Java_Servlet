package com.zc.helper;

import java.util.ArrayList;

public class EmailMasking {
	public String MaskEmail (String source, int l) {
		final String regex = "(?<=.{"+l+"}).(?=[^@]*?@)";
		return source.replaceAll(regex, "*");
	}
	
	public ArrayList<String> MaskEmail(ArrayList<String> emails, int l){
		
		ArrayList<String> MaskedEmails = new ArrayList<String>();
		
		emails.forEach(email -> {
			MaskedEmails.add(MaskEmail(email, l));
		});
		return MaskedEmails;
	}
	
}
