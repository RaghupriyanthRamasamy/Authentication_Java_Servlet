package com.zc.sessionservlet;

import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.json.JSONObject;

public class UserSessionClass {
	
	private DataSource dataSource;
	
	public UserSessionClass() {
		try {
			Context initContext  = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			dataSource = (DataSource)envContext.lookup("jdbc/usercredentialsDB");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	// Fetching user active sessions from database
	public JSONObject UserActiveSessions(String user_id) throws ServletException {
		JSONObject obj = new JSONObject();
	
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("select session from usercredentials.usersession Where user_id = ? ;");
		) {
			ps.setString(1, user_id);
			try (ResultSet rs = ps.executeQuery()){
				while(rs.next()) {
					obj.put(rs.getString(1), "session");
				}
			}
		}
		catch (Exception e) {
			obj.put("error", "Internal error");
			e.printStackTrace();
		}
		return obj;
	}
	
	// Delete user session value from database
	public boolean TerminateUserSession(String sessionValue) throws ServletException {
		
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM usersession WHERE session = ? ;");
		) {
			ps.setString(1, sessionValue);
			ps.executeUpdate();
			return true;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// Checking user session status
	public boolean UserSesionStatus(String sessionValue) throws ServletException {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM usersession WHERE session = ? ;");
		) {
			ps.setString(1, sessionValue);
			try (ResultSet rs = ps.executeQuery()){
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
