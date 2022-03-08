package com.zc.apihandlers.mfaSignInStartAPI;

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

import com.zc.JWT.JsonWebToken;
import com.zc.tokenvalidation.TokenValidationClass;

@WebServlet("/api/v1/accounts/mfaSignIn:Start")
public class MFASignInStartAPIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public MFASignInStartAPIServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");

		final String mfaPendingCredential = request.getParameter("mfaPendingCredential");
		final String mfaEnrollemntId = request.getParameter("mfaEnrollemntId");

		JSONObject StartMfaEmailRequestInfo = new JSONObject();
		PrintWriter out = response.getWriter();

		try {
			JSONTokener tokener = new JSONTokener(new BufferedReader(new InputStreamReader(request.getInputStream())));
			StartMfaEmailRequestInfo = (JSONObject) new JSONObject(tokener).get("StartMfaEmailRequestInfo");
		} catch (Exception e) {
			out.println(new JSONObject().put("error", "Invalid StartMfaEmailRequestInfo"));
			return;
		}

		if (mfaPendingCredential.length() == 0 || mfaPendingCredential.trim().isEmpty() || "\"\"".equals(mfaPendingCredential)) {
			out.println(new JSONObject().put("error", "Invalid mfaPendingCredential"));
			return;
		}

		if (mfaEnrollemntId.length() == 0 || mfaEnrollemntId.trim().isEmpty() || "\"\"".equals(mfaEnrollemntId)) {
			out.println(new JSONObject().put("error", "Invalid mfaEnrollemntId"));
			return;
		}

		try {
			final int otpEmailId = (int) StartMfaEmailRequestInfo.get("emailId");

			if (otpEmailId <= 0) {
				out.println(new JSONObject().put("error", "Invalid StartMfaEmailRequestInfo: emailid can't be zero"));
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.println(new JSONObject().put("error", "Invalid StartMfaEmailRequestInfo: emailid required to send otp"));
			return;
		}

		JsonWebToken jwt = new JsonWebToken();
		JSONObject mfaPendingCredclaims = jwt.mfaPendingCredTokenDecoder(mfaPendingCredential);

		if (mfaPendingCredclaims.has("error")) {
			out.println(mfaPendingCredclaims);
			return;
		}

		if(!(Integer.parseInt(mfaEnrollemntId) == (int)mfaPendingCredclaims.get("mfaEnrollemntId"))) {
			out.println(new JSONObject().put("error", "Invalid mfaEnrollemntId"));
			return;
		}

		TokenValidationClass uvc = new TokenValidationClass();
		out.println(uvc.mfaSignInStart(mfaPendingCredclaims, (int) StartMfaEmailRequestInfo.get("emailId")));
	}

}
