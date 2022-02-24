package com.zc.secondaryemail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.json.simple.JSONObject;

public class SecondayEmailClass {

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
	
	// Add secondary email to user
	public boolean AddSecondaryEmail(String user_id, String sec_email) throws ServletException {
		boolean status = true;
		init();
		PreparedStatement ps;
		try {
			
			con = dataSource.getConnection();
			String addSecQuery = "insert into useremail (user_id, user_email, email_status) value(?,?,?);";
			ps = con.prepareStatement(addSecQuery);
			ps.setString(1, user_id);
			ps.setString(2, sec_email);
			ps.setInt(3, 1);
			ps.executeUpdate();
			
			ps.close();
			con.close();
		} catch (SQLException e) {
			status = false;
			e.printStackTrace();
		}
		
		return status;
	}
	
	// Fetch user secondary email from database
	@SuppressWarnings("unchecked")
	public JSONObject GetSecEmail(String user_id) throws ServletException {
		init();
		JSONObject obj = new JSONObject();
		
		try {

			con = dataSource.getConnection();
			String getSecEmailQuery = "select user_email from usercredentials.useremail Where user_id = ? AND email_status = ? ;";
			
			PreparedStatement ps = con.prepareStatement(getSecEmailQuery);
			ps.setString(1, user_id);
			ps.setInt(2, 1);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String temp = "secemail";
				obj.put(rs.getString(1), temp);
			}
			
			ps.close();
			con.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return obj;
	}
	
	// Remove secondary email
	public boolean RemoveSecondaryEmail(String secEmail) throws ServletException {
		init();
		boolean status = true;
		
		try {

			con = dataSource.getConnection();
			String removeSecEmailQuery = "DELETE FROM useremail WHERE user_email = ?";
			PreparedStatement ps = con.prepareStatement(removeSecEmailQuery);
			ps.setString(1, secEmail);
			ps.execute();
			ps.close();
			con.close();
		} catch (SQLException e) {
			status = false;
			e.printStackTrace();
		}
		
		return status;
	}
	
	// Change secondary email to primary email
	public boolean SecondaryToPrimary(String user_id, String primaryEmail, String secondaryEmail) throws ServletException {
		
		System.out.println("Primary Email: " + primaryEmail);
		System.out.println("Secondary Email: " + secondaryEmail);
		
		boolean status = true;
		init();
		try {

			con = dataSource.getConnection();
			
			String dropEmailsQuery = "DELETE FROM useremail WHERE user_email = ?";
			PreparedStatement ps = con.prepareStatement(dropEmailsQuery);
			ps.setString(1, primaryEmail);
			ps.executeUpdate();
			
			ps.setString(1, secondaryEmail);
			ps.executeUpdate();
			
			String changeEmailQuery = "INSERT into usercredentials.useremail (user_id, user_email, email_status) value(?,?,?);";
			ps = con.prepareStatement(changeEmailQuery);
			ps.setString(1, user_id);
			ps.setString(2, secondaryEmail);
			ps.setInt(3, 0);
			ps.executeUpdate();
			
			ps.setString(1, user_id);
			ps.setString(2, primaryEmail);
			ps.setInt(3, 1);
			ps.executeUpdate();
			
			ps.close();
			con.close();
		} catch (SQLException e) {
			status = false;
			e.printStackTrace();
		}
		
		return status;
	}
	
}
