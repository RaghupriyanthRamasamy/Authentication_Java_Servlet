package com.zc.sessionservlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;

import org.json.JSONObject;

import com.zc.database.Database;

public class UserSessionClass {
	
	// Fetching user active sessions from database
	public JSONObject UserActiveSessions(String user_id) throws ServletException {
		JSONObject obj = new JSONObject();
	
		try (
			Connection con = Database.getConnection();
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
			Connection con = Database.getConnection();
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
			Connection con = Database.getConnection();
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
