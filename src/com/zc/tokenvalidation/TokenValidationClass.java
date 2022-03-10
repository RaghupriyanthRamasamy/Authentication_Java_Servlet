package com.zc.tokenvalidation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.zc.sendemailotp.SendEmailOTP;
import com.zc.userdetails.UserDetailClass;
import com.zc.JWT.JsonWebToken;

public class TokenValidationClass {
	
	private DataSource dataSource;
	private Connection con;
	boolean emailIdStatus = false;
	
	public void init() throws ServletException {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/usercredentialsDB");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject mfaSignInStart(JSONObject tokenClaims, int otpEmailId) {
		try {
			if (tokenClaims.get("email_verified").equals(true)) {
				if (tokenClaims.get("mfa_status").equals("mfa enrolled") && tokenClaims.get("auth_status").equals("mfa not verified")) {
					
					JSONArray emailIds = (JSONArray) tokenClaims.get("emailIds");
					
					emailIds.forEach(emailid -> {
						if ((int)emailid == otpEmailId) {
							emailIdStatus = true;
							return;
						}
					});
					
					if (emailIdStatus) {
						UserDetailClass udc = new UserDetailClass();
						String otpEmail= udc.GetUserEmail(otpEmailId);
						SendEmailOTP sendOTP = new SendEmailOTP();
						try {
							JSONObject mfaEmailSessionInfo = sendOTP.sendEmailOTP(otpEmail);
							if (mfaEmailSessionInfo.get("ServerError").equals(false)) {
								return new JSONObject().put("mfaEmailSessionInfo", new JSONObject().put("mfa_Sessioninfo", mfaEmailSessionInfo.get("otpSessionInfo")));
							}
							return new JSONObject().put("error", "server problem, otp not send");
						} catch (AddressException e) {
							e.printStackTrace();
							return new JSONObject().put("error", "server problem, otp not send");
						} catch (ServletException e) {
							e.printStackTrace();
							return new JSONObject().put("error", "server problem, otp not send");
						} catch (MessagingException e) {
							e.printStackTrace();
							return new JSONObject().put("error", "server problem, otp not send");
						}
					} else {
						return new JSONObject().put("error", "email provided is not valid");
					}
				} else {
					return new JSONObject().put("error", "Invalid mfa request, First get authenticated in first factor");
				}
			} else {
				return new JSONObject().put("error", "Invalid Email ID");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Server problem");
		}catch(Exception e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Server problem");
		}
	}
	
	public JSONObject otpSessionValidation(JSONObject tokenClaims, String otpCode, String sessionInfo) {
		try {
			init();
			con = dataSource.getConnection();
			UserDetailClass udc = new UserDetailClass();
			String user_id, userEmail;
			try {
				userEmail = udc.GetUserEmail((int)tokenClaims.get("emailId"));
				user_id = udc.GetUserId(userEmail);
				if(user_id.isEmpty())
					return new JSONObject().put("error", "Invalid Signin Request");
			} catch (Exception e) {
				e.printStackTrace();
				return new JSONObject().put("error", "Internal Server Problem");
			}
			String otpValidationQuery = "SELECT * from usermfa WHERE user_id = ? and otp = ? and otp_session_info = ? and auth_info = ?";
			PreparedStatement ps = con.prepareStatement(otpValidationQuery);
			ps.setString(1, user_id);
			ps.setString(2, otpCode);
			ps.setString(3, sessionInfo);
			ps.setString(4, "notSignin");
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				try {
					String removeOtpSessionQuery = "DELETE FROM usermfa WHERE user_id =? and otp_session_info = ? and otp = ?";
					ps = con.prepareStatement(removeOtpSessionQuery);
					ps.setString(1, user_id);
					ps.setString(2, sessionInfo);
					ps.setString(3, otpCode);
					ps.executeUpdate();
					ps.close();
					con.close();
					JsonWebToken jwt = new JsonWebToken();
					return jwt.MfaEnrolledUserIDToken(userEmail);
				} catch (Exception e) {
					e.printStackTrace();
					return new JSONObject().put("error", "server problem");
				}
			}
			ps.close();
			con.close();
			return new JSONObject().put("error", "Invalid otp");
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("error", "server problem");
		}
	}
}
