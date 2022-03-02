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

import org.json.JSONException;
import org.json.JSONObject;
import com.zc.sendemailotp.SendEmailOTP;
import com.zc.loginservlet.UserDetailClass;
import com.zc.JWT.JsonWebToken;

public class TokenValidationClass {
	
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
	
	public JSONObject mfaSignInStart(JSONObject tokenClaims, String email, String mfaPendingCredential) {
		System.out.println(tokenClaims);
		System.out.println(email);
		System.out.println("Email verified: " + tokenClaims.get("email_verified"));
		try {
			if (tokenClaims.get("email_verified").equals(true) && tokenClaims.get("email").equals(email)) {
				if (tokenClaims.get("mfa_verfication").equals("partially verified first factor")) {
					if (mfaPendingCredential.equals("Successfully passed first factor")) {
						SendEmailOTP sendOTP = new SendEmailOTP();
						try {
							JSONObject mfaEmailSessionInfo = sendOTP.sendEmailOTP(email);
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
						return new JSONObject().put("error", "First get authenticated in first factor");
					}
				} else {
					return new JSONObject().put("error", "Invalid mfa request");
				}
			} else {
				return new JSONObject().put("error", "Invalid Email ID");
			}
		} catch (JSONException e) {
			return new JSONObject().put("error", "Server problem");
		}catch(Exception e) {
			return new JSONObject().put("error", "Server problem");
		}
	}
	
	public JSONObject otpSessionValidation(String email, String otpCode, String sessionInfo) throws ServletException {
		try {
			init();
			con = dataSource.getConnection();
			UserDetailClass udc = new UserDetailClass();
			String user_id = udc.GetUserId(email);
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
					ps.setString(1, udc.GetUserId(email));
					ps.setString(2, sessionInfo);
					ps.setString(3, otpCode);
					ps.executeUpdate();
					ps.close();
					con.close();
					JsonWebToken jwt = new JsonWebToken();
					return jwt.IDToken(email);
				} catch (Exception e) {
					System.out.println("Exception throwed at removeOtpSessionQuery: "+e);
					return new JSONObject().put("error", "server problem");
				}
			}
			ps.close();
			con.close();
			return new JSONObject().put("error", "Invalid otp");
		} catch (Exception e) {
			return new JSONObject().put("error", "server problem");
		}
	}
}
