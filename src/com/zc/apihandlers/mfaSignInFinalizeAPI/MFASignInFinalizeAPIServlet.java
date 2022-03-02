package com.zc.apihandlers.mfaSignInFinalizeAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONTokener;
import com.zc.tokenvalidation.TokenValidationClass;

@WebServlet("/api/v1/accounts/mfaSignIn:finalize")
public class MFASignInFinalizeAPIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public MFASignInFinalizeAPIServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JSONObject emailVerificationInfo = new JSONObject();
		
		final String email = request.getParameter("email");
		
		if (email.length() == 0 || email.trim().isEmpty() || "\"\"".equals(email)) {
			out.println(new JSONObject().put("error", "Invalid email"));
			return;
		}
		
		try {
			JSONTokener tokener = new JSONTokener(new BufferedReader(new InputStreamReader(request.getInputStream())));
			emailVerificationInfo = (JSONObject) new JSONObject(tokener).get("emailVerificationInfo");
		} catch (Exception e) {
			out.println(new JSONObject().put("error", "Invalid emailVerificationInfo"));
			return;
		}
		
		String session_info, code;
		
		try {
			session_info = emailVerificationInfo.getString("sessionInfo");
			code = emailVerificationInfo.getString("code");
		} catch (Exception e) {
			out.println(new JSONObject().put("error", "Invalid sessionInfo or Code credentials"));
			return;
		}
		if (session_info.length() == 0 || session_info.trim().isEmpty() || "\"\"".equals(session_info)) {
			out.println(new JSONObject().put("error", "Invalid sessionInfo"));
			return;
		}
		if (code.length() == 0 || code.trim().isEmpty() || "\"\"".equals(code) || code.length() != 6) {
			out.println(new JSONObject().put("error", "Invalid otp code"));
			return;
		}
		
		TokenValidationClass tvc = new TokenValidationClass();
		out.println(tvc.otpSessionValidation(email, code, session_info));
		
	}

}
