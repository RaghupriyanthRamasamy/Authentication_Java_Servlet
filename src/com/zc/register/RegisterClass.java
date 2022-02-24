package com.zc.register;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.json.simple.JSONObject;

import com.zc.hashgenerator.HashGenerator;

public class RegisterClass {

	private DataSource dataSource;
	private Connection con;
	
	public void init() throws ServletException {
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
		boolean status = true;
		init();
		JSONObject jobj = new JSONObject();
		HashGenerator hg = new HashGenerator();
		
		try {
			
	    	String userId = UserIdGenerator();
			
			jobj = hg.generateHash(password);
			String Password = (String) jobj.get("hashvalue");
			byte[] salt = (byte[]) jobj.get("salt");
			
			con = dataSource.getConnection();
			
			String registerQuery = "insert into usercredentials.userdetail(user_id, first_name, last_name, password, bytesalt) value(?,?,?,?,?)";
			
			PreparedStatement ps = con.prepareStatement(registerQuery);
			
			ps.setString(1, userId);
			ps.setString(2, firstname);
			ps.setString(3, lastname);
			ps.setString(4, Password);
			ps.setBytes(5, salt);
			ps.executeUpdate();
			
			String registerEmailQuery = "insert into useremail(user_id, user_email, email_status) value(?,?,?);";
			ps = con.prepareStatement(registerEmailQuery);
			ps.setString(1, userId);
			ps.setString(2, primaryemail);
			ps.setInt(3, 0);
			ps.executeUpdate();
			
		} catch (NoSuchAlgorithmException | SQLException e) {
			status = false;
			e.printStackTrace();
		}
		
		return status;
	}
	
}
