//$Id$
package com.zc.apihandlers.mfaSignInFinalizeAPI;

import org.json.JSONObject;

import com.zc.JWT.JsonWebToken;
import com.zc.apihandlers.apiInputValidation.APIInputValidationClass;
import com.zc.tokenvalidation.TokenValidationClass;

public class MFASignInFinalizeAPIHandler {
	
	public JSONObject MFASignInFinalizeAPI(String mfaPendingCredential, JSONObject emailVerificationInfo) {
		
		APIInputValidationClass inputValidationObj = new APIInputValidationClass();
		
		if (inputValidationObj.ValidateAPIInput(mfaPendingCredential))
			return new JSONObject().put("error", "Invalid mfaPendingCredential");
		
		String session_info, code;
		
		try {
			session_info = emailVerificationInfo.getString("sessionInfo");
			code = emailVerificationInfo.getString("code");
		} catch (Exception e) {
			return new JSONObject().put("error", "Required sessionInfo or Code credentials");
		}
		if (inputValidationObj.ValidateAPIInput(session_info)) {
			return new JSONObject().put("error", "Invalid sessionInfo");
		}
		if (inputValidationObj.ValidateAPIInput(code) || code.length() != 6) {
			return new JSONObject().put("error", "Invalid otp code");
		}
		
		JsonWebToken jwt = new JsonWebToken();
		JSONObject mfaPendingCredclaims = jwt.mfaPendingCredTokenDecoder(mfaPendingCredential);

		if (mfaPendingCredclaims.has("error")) {
			return mfaPendingCredclaims;
		}
		
		TokenValidationClass tvc = new TokenValidationClass();
		return tvc.otpSessionValidation(mfaPendingCredclaims, code, session_info);
	}
	
}
