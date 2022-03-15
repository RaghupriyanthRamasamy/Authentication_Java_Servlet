package com.zc.register;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.json.JSONObject;

import com.zc.hashgenerator.HashGenerator;

public class RegisterClass {

	private DataSource dataSource;
	
	public RegisterClass() {
		try {
			Context initContext  = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			dataSource = (DataSource)envContext.lookup("jdbc/usercredentialsDB");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	public String UserIdGenerator() {
		int n = 10;
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder(n);
		String number = "0123456789";
		for (int i = 0; i < n; i++) {
	        int rndCharAt = random.nextInt(number.length());
	        char rndChar = number.charAt(rndCharAt);
	        sb.append(rndChar);
	    }
		
		String userId = sb.toString();
		return userId;
	}
	
	public boolean UserRegister(String firstname, String lastname, String primaryemail, String password) throws ServletException {
		JSONObject jobj = new JSONObject();
		HashGenerator hg = new HashGenerator();
		
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("insert into usercredentials.userdetail(user_id, first_name, last_name, password, bytesalt) value(?,?,?,?,?)");
			) {
			
	    	String userId = UserIdGenerator();
			
			jobj = hg.generateHash(password);
			String Password = (String) jobj.get("hashvalue");
			byte[] salt = (byte[]) jobj.get("salt");
			
			ps.setString(1, userId);
			ps.setString(2, firstname);
			ps.setString(3, lastname);
			ps.setString(4, Password);
			ps.setBytes(5, salt);
			ps.executeUpdate();
			
			try (PreparedStatement ps2 = con.prepareStatement("insert into useremail(user_id, user_email, email_status) value(?,?,?);");){
				ps2.setString(1, userId);
				ps2.setString(2, primaryemail);
				ps2.setInt(3, 0);
				ps2.executeUpdate();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
}
