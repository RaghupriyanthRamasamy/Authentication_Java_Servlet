package com.zc.sendemailotp;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;

import com.zc.credentialsVault.EmailCredentialsVault;
import com.zc.emailutility.EmailUtility;
import com.zc.loginservlet.UserDetailClass;
import com.zc.mfacredentials.OTPCodeGenerator;
import com.zc.mfacredentials.SessionInfoGenerator;

import org.json.simple.JSONObject;

public class SendEmailOTP {

	@SuppressWarnings({ "static-access", "unchecked" })
	public JSONObject sendEmailOTP(String useremail) throws ServletException, AddressException, MessagingException {

		JSONObject userMfa = new JSONObject();

		UserDetailClass udc = new UserDetailClass();
		EmailCredentialsVault ecVault = new EmailCredentialsVault();
		EmailUtility emailutility = new EmailUtility();

		String user_id = udc.GetUserId(useremail);
		String username = udc.GetUserName(user_id);

		if (user_id != null && username != null) {
			OTPCodeGenerator ocg = new OTPCodeGenerator();
			SessionInfoGenerator sig = new SessionInfoGenerator();
			String otp = ocg.generateOTP();
			String sessionInfo = sig.generateSessionInfo(20);

			try {
				emailutility.sendEmail(ecVault.get_host(), ecVault.get_port(), ecVault.get_senderEmail(),
						ecVault.get_senderEmailPass(), useremail, otp, username);
				System.out.println("Email Send Succesfull");

				if (udc.setUserOTP(user_id, otp, sessionInfo)) {
					userMfa.put("mfapassed", true);
					userMfa.put("otpSessionInfo", sessionInfo);
					userMfa.put("ServerError", false);
				} else
					userMfa.put("ServerError", true);
			} catch (Exception e) {
				System.out.println("Email Send failed");
				userMfa.put("ServerError", true);
				System.out.println(e);
				System.out.println("sendEmailOTP " + e.getMessage());
			}
		} else
			userMfa.put("ServerError", true);

		return userMfa;
	}
}
