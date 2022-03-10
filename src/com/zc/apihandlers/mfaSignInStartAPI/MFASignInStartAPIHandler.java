//$Id$
package com.zc.apihandlers.mfaSignInStartAPI;

import org.json.JSONObject;

import com.zc.JWT.JsonWebToken;
import com.zc.apihandlers.apiInputValidation.APIInputValidationClass;
import com.zc.tokenvalidation.TokenValidationClass;

public class MFASignInStartAPIHandler {

	public JSONObject MFASignInStartAPI (String mfaPendingCredential, String mfaEnrollemntId, JSONObject StartMfaEmailRequestInfo) {
		
		APIInputValidationClass inputValidationObj = new APIInputValidationClass();
		
		if (inputValidationObj.ValidateAPIInput(mfaPendingCredential))
			return new JSONObject().put("error", "Invalid mfaPendingCredential");
		
		if (inputValidationObj.ValidateAPIInput(mfaEnrollemntId))
			return new JSONObject().put("error", "Invalid mfaEnrollemntId");
		
		try {
			final int otpEmailId = (int) StartMfaEmailRequestInfo.get("emailId");

			if (otpEmailId <= 0) {
				return new JSONObject().put("error", "Invalid StartMfaEmailRequestInfo: Invalid emailid");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject().put("error", "Invalid StartMfaEmailRequestInfo: emailid required to send otp");
		}

		JsonWebToken jwt = new JsonWebToken();
		JSONObject mfaPendingCredclaims = jwt.mfaPendingCredTokenDecoder(mfaPendingCredential);

		if (mfaPendingCredclaims.has("error")) {
			return mfaPendingCredclaims ;
		}

		if(!(Integer.parseInt(mfaEnrollemntId) == (int)mfaPendingCredclaims.get("mfaEnrollemntId"))) {
			return new JSONObject().put("error", "Invalid mfaEnrollemntId");
		}

		TokenValidationClass uvc = new TokenValidationClass();
		return uvc.mfaSignInStart(mfaPendingCredclaims, (int) StartMfaEmailRequestInfo.get("emailId"));
	}
}
