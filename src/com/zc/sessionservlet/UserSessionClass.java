package com.zc.sessionservlet;

import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.json.simple.JSONObject;

public class UserSessionClass {
	
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
	
	// Fetching user active sessions from database
	@SuppressWarnings("unchecked")
	public JSONObject UserActiveSessions(String user_id) throws ServletException {
		init();
		JSONObject obj = new JSONObject();
	
		try {
			con = dataSource.getConnection();
			String sessionQuery = "select session from usercredentials.usersession Where user_id = ? ;";
			
			PreparedStatement ps = con.prepareStatement(sessionQuery);
			ps.setString(1, user_id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String temp = "session";
				obj.put(rs.getString(1), temp);
			}
			ps.close();
			con.close();
		} 
		catch (Exception e) {
			System.out.println("In user active session method: "+ e.getMessage());
		}
		
		return obj;
	}
	
	// Delete user session value from database
	public boolean TerminateUserSession(String sessionValue) throws ServletException {
		init();
		boolean teminationStatus = true;
		
		try {

			con = dataSource.getConnection();
			String sessionDelete = "DELETE FROM usersession WHERE session = ? ;";
			PreparedStatement ps = con.prepareStatement(sessionDelete);
			ps.setString(1, sessionValue);
			ps.execute();
			
			ps.close();
			con.close();
		} 
		catch (Exception e) {
			teminationStatus = false;
			System.out.println("In Terminate User Session Method "+ e.getMessage());
		}
		return teminationStatus;
	}
	
	// Checking user session status
	public boolean UserSesionStatus(String sessionValue) throws ServletException {
		init();
		boolean sessionStatus = false;
		try {
			
			con = dataSource.getConnection();
			String sessionStatusQuery = "SELECT * FROM usersession WHERE session = ? ;";
			PreparedStatement ps = con.prepareStatement(sessionStatusQuery);
			ps.setString(1, sessionValue);
			ResultSet rs = ps.executeQuery();
			sessionStatus = rs.next();
			
			ps.close();
			con.close();
		} catch (Exception e) {
			System.out.println("In User Session Status " + e.getMessage());
		}
		return sessionStatus;
	}
}
