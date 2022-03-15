package com.zc.userdetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.zc.hashgenerator.HashGenerator;
import com.zc.mfacredentials.SessionInfoGenerator;

public class UserDetailClass {

	public static final String[] HEADERS_TO_TRY = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" };

	private DataSource dataSource;

	public UserDetailClass() {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/usercredentialsDB");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	// Validate user email present in database or not
	public boolean EmailValidate(String useremail) throws ServletException {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * from useremail WHERE user_email = ?");
		) {
			ps.setString(1, useremail);
			try (ResultSet rs = ps.executeQuery();){
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String GetHashedPassword(String userId, String password) {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT bytesalt from userdetail WHERE user_id = ?");
		){
			ps.setString(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					byte[] salt = rs.getBytes(1);
					HashGenerator hg = new HashGenerator();
					return hg.generateHash(password, salt);
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// validate password entered by user is correct or not
	public boolean PasswordValidate(String useremail, String password) throws ServletException {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * from userdetail WHERE user_id = ? and password = ?");
		) {
			String user_id = GetUserId(useremail);
			if (user_id == null)
				return false;
			
			String hashpass = GetHashedPassword(user_id, password);
	
			if (hashpass == null)
				return false;
			
			ps.setString(1, user_id);
			ps.setString(2, hashpass);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// ADD new session into database
	public boolean AddSession(String sessionId, String email) throws ServletException {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("insert into usersession (user_id,session) values(?,?)");
		) {
			ps.setString(1, GetUserId(email));
			ps.setString(2, sessionId);
			ps.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Fetch user id from database using session id from cookie
	public String GetUserId(Cookie[] cookies) throws ServletException {
		String sessionValue = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ((cookie.getName()).compareTo("_Session_ID") == 0) {
					sessionValue = cookie.getValue();
				}
			}
		}

		String getid = "SELECT user_id from usersession WHERE session = ?";
		String user_id = null;
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement(getid);
		) {
			ps.setString(1, sessionValue);
			try (ResultSet rs = ps.executeQuery()){
				if (rs.next())
					user_id = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user_id;
	}

	// Fetch user id using user email
	public String GetUserId(String useremail) throws ServletException {
		String user_id = null;
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT user_id from useremail WHERE user_email = ?");
		) {
			ps.setString(1, useremail);
			try (ResultSet rs = ps.executeQuery();){
				if (rs.next()) 
					user_id = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user_id;
	}

	// Method to get username of a user
	public String GetUserName(String user_id) throws ServletException {
		String username = null;
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT first_name, last_name from userdetail where user_id = ?");
		) {
			ps.setString(1, user_id);
			try (ResultSet rs = ps.executeQuery();){
				if (rs.next()) {
					username = rs.getString(1);
					username += " ";
					username += rs.getString(2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return username;
		}
		return username;
	}

	public boolean setUserOTP(String user_id, String otp, String session_info) throws ServletException {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("insert into usermfa(user_id, otp, otp_session_info, auth_info) value(?,?,?,?)");
		) {
			ps.setString(1, user_id);
			ps.setString(2, otp);
			ps.setString(3, session_info);
			ps.setString(4, "notSignin");
			ps.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public JSONObject userOTPValidation(String useremail, String otp, String sessionInfo) throws ServletException {

		JSONObject otpStatus = new JSONObject();
		String user_id = GetUserId(useremail);

		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * from usermfa WHERE user_id = ? and otp = ? and otp_session_info = ? and auth_info = ?");
			){
			ps.setString(1, user_id);
			ps.setString(2, otp);
			ps.setString(3, sessionInfo);
			ps.setString(4, "notSignin");

			try (ResultSet rs = ps.executeQuery();){
				if (rs.next()) {
					SessionInfoGenerator sig = new SessionInfoGenerator();
					String authenticationInfo = sig.generateSessionInfo(25);
					try (PreparedStatement ps2 = con.prepareStatement("update usermfa set auth_info = ? WHERE user_id = ? and otp_session_info = ?")) {
						ps2.setString(1, authenticationInfo);
						ps2.setString(2, user_id);
						ps2.setString(3, sessionInfo);
						ps2.executeUpdate();
						otpStatus.put("validOTP", true);
						otpStatus.put("email", useremail);
						otpStatus.put("authCredential", authenticationInfo);
						otpStatus.put("serverError", false);
					} catch (Exception e) {
						otpStatus.put("serverError", true);
						e.printStackTrace();
					}
				} else {
					otpStatus.put("validOTP", false);
					otpStatus.put("serverError", false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			otpStatus.put("serverError", true);
		}
		return otpStatus;
	}

	public boolean loginAuthValidation(String email, String authInfo) throws ServletException {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * from usermfa WHERE user_id = ? and auth_info = ?");
		) {
			ps.setString(1, GetUserId(email));
			ps.setString(2, authInfo);
			try (ResultSet rs = ps.executeQuery()){
				if (rs.next())
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public boolean removeAuthInfo(String email, String authInfo) throws ServletException {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM usermfa WHERE user_id =? and auth_info = ?");
		) {
			ps.setString(1, GetUserId(email));
			ps.setString(2, authInfo);
			ps.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public JSONObject GetUserMFAEnrollmentID(String email) {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT mfaId FROM usermultifactor WHERE user_id =?");
		) {
			ps.setString(1, GetUserId(email));
			try (ResultSet rs = ps.executeQuery();){
				if (rs.next())
					return new JSONObject().put("userMfaId", rs.getInt(1));
			}
			return new JSONObject().put("error", "User not enrolled in multi-factor");
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Internal Server Error");
		}
	}
	
	public ArrayList<String> GetUserEmails(String email) {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("select user_email from useremail Where user_id = ?");
		) {
			ps.setString(1, GetUserId(email));
			try (ResultSet rs = ps.executeQuery()){
				ArrayList<String> useremails =  new ArrayList<String>();
				while(rs.next()) {
					useremails.add(rs.getString(1));
				}
				if(useremails.isEmpty()) {
					System.out.println("Something wrong while fetching user emails in GetUserEmails method look into it");
					return new ArrayList<String>();
				}
				return useremails;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	public String GetUserEmail(int email_id) {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("select user_email from useremail Where auto_email_id = ?");
		) {
			ps.setInt(1, email_id);
			try (ResultSet rs = ps.executeQuery();){
				if(rs.next())
					return rs.getString(1);
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int GetUserEmailID (String email) {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("select auto_email_id from useremail Where user_email = ?");
		) {
			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next())
					return rs.getInt(1);
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public ArrayList<Integer> GetUserEmailID(ArrayList<String> emails){
		try {
			
			ArrayList<Integer> emailIds = new ArrayList<Integer>();
			emails.forEach(email -> {
				emailIds.add(GetUserEmailID(email));
			});
			
			return emailIds;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Integer>();
		}
	}
	
	public JSONObject UserMfaEnrollmentStatus(String email) {
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT mfaEnrolled FROM userdetail WHERE user_id = ?");
		) {
			String userId = GetUserId(email);
			ps.setString(1, userId);
			try(ResultSet rs = ps.executeQuery();) {
				if(rs.next())
					return new JSONObject().put("mfaEnrollmentStatus", rs.getInt(1));
			}
			return new JSONObject().put("error", "User Not Exsist");
		} catch (Exception e) {
			return new JSONObject().put("error", "Internal Server Error");
		}
	}
	
	// Finding User IP address from request

	public String UserIP(HttpServletRequest request) {
		for (String header : HEADERS_TO_TRY) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}

}

//String getbyte = "select bytesalt from usercredentials.userdetail where ? IN(email, secemail1, secemail2, secemail3);";