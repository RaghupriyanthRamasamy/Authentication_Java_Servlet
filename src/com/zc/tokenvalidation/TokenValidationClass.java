package com.zc.tokenvalidation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zc.database.Database;
import com.zc.sendemailotp.SendEmailOTP;
import com.zc.userdetails.UserDetailClass;
import com.zc.JWT.JsonWebToken;

public class TokenValidationClass {
	
	boolean emailIdStatus = false;
	
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
		try (
			Connection con = Database.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * from usermfa WHERE user_id = ? and otp = ? and otp_session_info = ? and auth_info = ?");
		) {
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
			ps.setString(1, user_id);
			ps.setString(2, otpCode);
			ps.setString(3, sessionInfo);
			ps.setString(4, "notSignin");
			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM usermfa WHERE user_id =? and otp_session_info = ? and otp = ?");) {
						ps2.setString(1, user_id);
						ps2.setString(2, sessionInfo);
						ps2.setString(3, otpCode);
						ps2.executeUpdate();
						JsonWebToken jwt = new JsonWebToken();
						return jwt.MfaEnrolledUserIDToken(userEmail);
					} catch (Exception e) {
						e.printStackTrace();
						return new JSONObject().put("error", "server problem");
					}
				}
				return new JSONObject().put("error", "Invalid otp");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("error", "server problem");
		}
	}
}
