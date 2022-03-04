package com.zc.loginservlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	boolean emailStatus = false;
	boolean passStatus = false;

	private DataSource dataSource;
	private Connection con;

	public void init() throws ServletException {
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
		init();
		try {
			con = dataSource.getConnection();
			String emailCheckQuery = "SELECT * from useremail WHERE user_email = ?";

			PreparedStatement ps = con.prepareStatement(emailCheckQuery);
			ps.setString(1, useremail);
			ResultSet rs = ps.executeQuery();
			emailStatus = rs.next();

			ps.close();
			con.close();
		} catch (Exception e) {
			System.out.println("EmailValidate" + e.getMessage());
		}
		return emailStatus;
	}

	// validate password entered by user is correct or not
	public boolean PasswordValidate(String useremail, String password) throws ServletException {
		init();
		try {

			con = dataSource.getConnection();
			String getUserIDQuery = "SELECT user_id from useremail WHERE user_email = ?";
			PreparedStatement ps = con.prepareStatement(getUserIDQuery);
			ps.setString(1, useremail);
			ResultSet rs = ps.executeQuery();

			String user_id = null;
			if (rs.next()) {
				user_id = rs.getString(1);
			}else {
				return false;
			}

			String getByteQuery = "SELECT bytesalt from userdetail WHERE user_id = ?";
			String hashpass = null;

			ps = con.prepareStatement(getByteQuery);
			ps.setString(1, user_id);
			rs = ps.executeQuery();

			if (rs.next()) {
				byte[] salt = rs.getBytes(1);
				HashGenerator hg = new HashGenerator();
				hashpass = hg.generateHash(password, salt);
			}

			String validate = "SELECT * from userdetail WHERE user_id = ? and password = ?";

			ps = con.prepareStatement(validate);
			ps.setString(1, user_id);
			ps.setString(2, hashpass);

			rs = ps.executeQuery();
			passStatus = rs.next();

			ps.close();
			con.close();
		} catch (Exception e) {
			System.out.println("PasswordValidate" + e.getMessage());
		}
		return passStatus;
	}

	// ADD new session into database
	public boolean AddSession(String sessionId, String email) throws ServletException {
		boolean sessionStatus = true;
		init();
		try {

			con = dataSource.getConnection();
			String id = null;

			String getUserIDQuery = "SELECT user_id from useremail WHERE user_email = ?";
			PreparedStatement ps = con.prepareStatement(getUserIDQuery);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				id = rs.getString(1);
			}

			String setSession = "insert into usersession (user_id,session) values(?,?)";
			ps = con.prepareStatement(setSession);
			ps.setString(1, id);
			ps.setString(2, sessionId);
			ps.executeUpdate();
			ps.close();
			con.close();
		} catch (Exception e) {
			sessionStatus = false;
			System.out.println("In Add Session Method " + e.getMessage());
		}
		return sessionStatus;
	}

	// Fetch user id from database using session id from cookie
	public String GetUserId(Cookie[] cookies) throws ServletException {
		init();
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
		try {
			con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement(getid);
			ps.setString(1, sessionValue);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				user_id = rs.getString(1);
			}
			ps.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user_id;
	}

	// Fetch user id using user email
	public String GetUserId(String useremail) throws ServletException {
		init();
		String user_id = null;
		try {
			con = dataSource.getConnection();
			String getUserIDQuery = "SELECT user_id from useremail WHERE user_email = ?";

			PreparedStatement ps = con.prepareStatement(getUserIDQuery);
			ps.setString(1, useremail);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				user_id = rs.getString(1);
			}
			ps.close();
			con.close();
		} catch (Exception e) {
			System.out.println("Get User ID: " + e.getMessage());
		}
		return user_id;
	}

	// Method to get username of a user
	public String GetUserName(String user_id) throws ServletException {
		init();
		String username = null;
		try {
			con = dataSource.getConnection();
			String getUsername = "SELECT first_name, last_name from userdetail where user_id = ?";
			PreparedStatement ps = con.prepareStatement(getUsername);
			ps.setString(1, user_id);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				username = rs.getString(1);
				username += " ";
				username += rs.getString(2);
			}
			ps.close();
			con.close();
		} catch (Exception e) {
			System.out.println("Get User Name " + e.getMessage());
		}
		return username;
	}

	public boolean setUserOTP(String user_id, String otp, String session_info) throws ServletException {
		init();
		try {
			con = dataSource.getConnection();
			String setUserOTPQuery = "insert into usermfa(user_id, otp, otp_session_info, auth_info) value(?,?,?,?)";
			PreparedStatement ps = con.prepareStatement(setUserOTPQuery);

			ps.setString(1, user_id);
			ps.setString(2, otp);
			ps.setString(3, session_info);
			ps.setString(4, "notSignin");
			ps.executeUpdate();
			ps.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public JSONObject userOTPValidation(String useremail, String otp, String sessionInfo) throws ServletException {

		init();
		JSONObject otpStatus = new JSONObject();
		String user_id = GetUserId(useremail);

		try {
			con = dataSource.getConnection();
			String otpValidationQuery = "SELECT * from usermfa WHERE user_id = ? and otp = ? and otp_session_info = ? and auth_info = ?";

			PreparedStatement ps = con.prepareStatement(otpValidationQuery);
			ps.setString(1, user_id);
			ps.setString(2, otp);
			ps.setString(3, sessionInfo);
			ps.setString(4, "notSignin");

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				SessionInfoGenerator sig = new SessionInfoGenerator();
				String authenticationInfo = sig.generateSessionInfo(25);
				try {
					String setAuthInfoAuery = "update usermfa set auth_info = ? WHERE user_id = ? and otp_session_info = ?";
					ps = con.prepareStatement(setAuthInfoAuery);
					ps.setString(1, authenticationInfo);
					ps.setString(2, user_id);
					ps.setString(3, sessionInfo);
					ps.executeUpdate();
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
			ps.close();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
			otpStatus.put("serverError", true);
		}

		return otpStatus;
	}

	public boolean loginAuthValidation(String email, String authInfo) throws ServletException {
		init();
		boolean status = false;
		String user_id = GetUserId(email);
		try {
			con = dataSource.getConnection();
			String loginAuthQuery = "SELECT * from usermfa WHERE user_id = ? and auth_info = ?";
			PreparedStatement ps = con.prepareStatement(loginAuthQuery);
			ps.setString(1, user_id);
			ps.setString(2, authInfo);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				status = true;
			}
			ps.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	
	public boolean removeAuthInfo(String email, String authInfo) throws ServletException {
		init();
		try {
			con = dataSource.getConnection();
			String removeAuthQuery = "DELETE FROM usermfa WHERE user_id =? and auth_info = ?";
			PreparedStatement ps = con.prepareStatement(removeAuthQuery);
			ps.setString(1, GetUserId(email));
			ps.setString(2, authInfo);
			ps.executeUpdate();
			ps.close();
			con.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public JSONObject GetUserMFAEnrollmentID(String email) {
		try {
			init();
			con = dataSource.getConnection();
			String userMfaEnrollmentQuery = "SELECT mfaId FROM usermultifactor WHERE user_id =?";
			PreparedStatement ps = con.prepareStatement(userMfaEnrollmentQuery);
			ps.setString(1, GetUserId(email));
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new JSONObject().put("userMfaId", rs.getInt(1));
			}
			ps.close();
			con.close();
			return new JSONObject().put("error", "User not enrolled in multi-factor");
		} catch (ServletException e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Internal Server Error");
		} catch (SQLException e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Internal Server Error");
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Internal Server Error");
		}
	}
	
	public ArrayList<String> GetUserEmails(String email) {
		try {
			init();
			con = dataSource.getConnection();
			String getUserEmailsQuery = "select user_email from useremail Where user_id = ?";
			PreparedStatement ps = con.prepareStatement(getUserEmailsQuery);
			ps.setString(1, GetUserId(email));
			ResultSet rs = ps.executeQuery();
			
			ArrayList<String> useremails =  new ArrayList<String>();
			while(rs.next()) {
				useremails.add(rs.getString(1));
			}
			if(useremails.isEmpty()) {
				System.out.println("Something wrong while fetching user emails in GetUserEmails method lookinto it");
				return new ArrayList<String>();
			}
			ps.close();
			con.close();
			return useremails;
		} catch (ServletException e) {
			e.printStackTrace();
			return new ArrayList<String>();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<String>();
//			return new JSONObject().put("error", "Internal Server Error");
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