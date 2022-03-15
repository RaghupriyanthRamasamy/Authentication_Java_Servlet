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

import org.json.JSONObject;

public class SecondayEmailClass {

	private DataSource dataSource;
	
	public SecondayEmailClass() {
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
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("insert into useremail (user_id, user_email, email_status) value(?,?,?);");
		) {
			ps.setString(1, user_id);
			ps.setString(2, sec_email);
			ps.setInt(3, 1);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// Fetch user secondary email from database
	public JSONObject GetSecEmail(String user_id) throws ServletException {
		JSONObject obj = new JSONObject();
		
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("select user_email from usercredentials.useremail Where user_id = ? AND email_status = ? ;");
		) {
			ps.setString(1, user_id);
			ps.setInt(2, 1);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
				obj.put(rs.getString(1), "secemail");
			
		} catch (SQLException e) {
			obj.put("error", "Internal error");
			e.printStackTrace();
		}
		
		return obj;
	}
	
	// Remove secondary email
	public boolean RemoveSecondaryEmail(String secEmail) throws ServletException {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM useremail WHERE user_email = ?");
		) {
			ps.setString(1, secEmail);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// Change secondary email to primary email
	public boolean SecondaryToPrimary(String user_id, String primaryEmail, String secondaryEmail) throws ServletException {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE useremail SET email_Status = ? WHERE user_id = ? AND user_email = ?;");
		) {
			ps.setInt(1, 0);
			ps.setString(2, user_id);
			ps.setString(3, secondaryEmail);
			ps.executeUpdate();
			
			ps.setInt(1, 1);
			ps.setString(2, user_id);
			ps.setString(3, primaryEmail);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}

//try (
//		Connection con = dataSource.getConnection();
//		PreparedStatement ps = con.prepareStatement("DELETE FROM useremail WHERE user_email = ?");
//	) {
//		ps.setString(1, primaryEmail);
//		ps.executeUpdate();
//		
//		ps.setString(1, secondaryEmail);
//		ps.executeUpdate();
//		
//		try (PreparedStatement ps2 = con.prepareStatement("INSERT into usercredentials.useremail (user_id, user_email, email_status) value(?,?,?);");){
//			ps2.setString(1, user_id);
//			ps2.setString(2, secondaryEmail);
//			ps2.setInt(3, 0);
//			ps2.executeUpdate();
//			
//			ps2.setString(1, user_id);
//			ps2.setString(2, primaryEmail);
//			ps2.setInt(3, 1);
//			ps2.executeUpdate();
////			return true;
//		}