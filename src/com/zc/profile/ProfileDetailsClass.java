package com.zc.profile;

import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.json.simple.JSONObject;

public class ProfileDetailsClass {
	
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
	
	@SuppressWarnings("unchecked")
	public JSONObject UserDetails(String user_id) throws ServletException {
		init();
		JSONObject udobj = new JSONObject();
		
		try {
			
			con = dataSource.getConnection();
			
			String profileSelectQuery = "select * from userdetail where user_id = ?";
			PreparedStatement ps = con.prepareStatement(profileSelectQuery);
			ps.setString(1, user_id);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				udobj.put("firstname", rs.getString(2));
				udobj.put("lastname", rs.getString(3));
				udobj.put("gender", rs.getString(6));
				udobj.put("country", rs.getString(7));
			}
			
			String emailGetQuery = "SELECT user_email from useremail where user_id = ? AND email_status = ?";
			ps = con.prepareStatement(emailGetQuery);
			ps.setString(1, user_id);
			ps.setInt(2, 0);
			rs = ps.executeQuery();
			if(rs.next()) {
				udobj.put("email", rs.getString(1));
			}
			
			ps.close();
			con.close();
		} catch (Exception e) {
			System.out.println("In userdetails "+ e.getMessage());
		}
		return udobj;
	}
	
	public boolean UpdateProfile(String firstname, String lastname, String gender, String country, String user_id) throws ServletException {
		init();
		boolean status = true;
		try {

			con = dataSource.getConnection();
			String profileUpdateQuery = "update userdetail set first_name = ?,last_name = ?,gender = ?,country = ? where user_id =?";
			
			PreparedStatement ps = con.prepareStatement(profileUpdateQuery);
			ps.setString(1, firstname);
			ps.setString(2, lastname);
			ps.setString(3, gender);
			ps.setString(4, country);
			ps.setString(5, user_id);
			ps.executeUpdate();
			ps.close();
			con.close();
		} catch (Exception e) {
			status = false;
			System.out.println("In update profile method: "+ e.getMessage());
		}
		
		return status;
	}
	
}
