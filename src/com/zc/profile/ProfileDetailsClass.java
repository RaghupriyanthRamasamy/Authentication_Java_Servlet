package com.zc.profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;

import com.zc.database.Database;
import org.json.JSONObject;

public class ProfileDetailsClass {
	
	public JSONObject UserDetails(String user_id) throws ServletException {
		JSONObject udobj = new JSONObject();
		
		try (
			Connection con = Database.getConnection();
			PreparedStatement ps = con.prepareStatement("select * from userdetail where user_id = ?");
		) {
			ps.setString(1, user_id);
			try (ResultSet rs = ps.executeQuery();){
				if(rs.next()) {
					udobj.put("firstname", rs.getString(2));
					udobj.put("lastname", rs.getString(3));
					udobj.put("gender", rs.getString(6));
					udobj.put("country", rs.getString(7));
				}
			}
			
			try (
				PreparedStatement ps2 = con.prepareStatement("SELECT user_email from useremail where user_id = ? AND email_status = ?")
			){
				ps2.setString(1, user_id);
				ps2.setInt(2, 0);
				try (ResultSet rs2 = ps2.executeQuery();){
					if(rs2.next()) {
						udobj.put("email", rs2.getString(1));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Server error");
		}
		return udobj;
	}
	
	public boolean UpdateProfile(String firstname, String lastname, String gender, String country, String user_id) throws ServletException {
		try (
			Connection con = Database.getConnection();
			PreparedStatement ps = con.prepareStatement("update userdetail set first_name = ?,last_name = ?,gender = ?,country = ? where user_id =?");
			) {
			ps.setString(1, firstname);
			ps.setString(2, lastname);
			ps.setString(3, gender);
			ps.setString(4, country);
			ps.setString(5, user_id);
			ps.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
