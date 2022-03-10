//$Id$
package com.zc.apihandlers.VerifyPasswordAPI;

import javax.servlet.ServletException;

import org.json.JSONException;
import org.json.JSONObject;

import com.zc.JWT.JsonWebToken;
import com.zc.apihandlers.apiInputValidation.APIInputValidationClass;
import com.zc.userdetails.UserDetailClass;

public class VerifyPasswordAPIHandler {
	
	public JSONObject VerifyPasswordAPI(String useremail, String password) {

		APIInputValidationClass inputValidationObj = new APIInputValidationClass();

		if (inputValidationObj.ValidateAPIInput(useremail))
			return new JSONObject().put("error", "Invalid useremail");

		if (inputValidationObj.ValidateAPIInput(password))
			return new JSONObject().put("error", "Invalid password");

		UserDetailClass udc = new UserDetailClass();
		
		try {
			if (!(udc.EmailValidate(useremail))) {
				return new JSONObject().put("error", "Account not exist: Try to Register");
			}

			if (!(udc.PasswordValidate(useremail, password)))
				return new JSONObject().put("error", "Invalid password");
			
			JSONObject mfaEnrollmentStatus = udc.UserMfaEnrollmentStatus(useremail);
			if (mfaEnrollmentStatus.has("error")) {
				return mfaEnrollmentStatus;
			}
			JsonWebToken jwt = new JsonWebToken();
			if ((mfaEnrollmentStatus.get("mfaEnrollmentStatus")).equals(0)) {
				return jwt.NonMfaUserIDToken(useremail, (int) mfaEnrollmentStatus.get("mfaEnrollmentStatus"));
			}
			if ((mfaEnrollmentStatus.get("mfaEnrollmentStatus")).equals(1)) {
				return jwt.mfaPendingCredToken(useremail, (int) mfaEnrollmentStatus.get("mfaEnrollmentStatus"));
			}
			return new JSONObject().put("error", "Internal Validation Error");

		} catch (JSONException | ServletException e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Intrenal Server Problem");
		}

	}
}
