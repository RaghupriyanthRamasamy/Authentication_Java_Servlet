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

		final String idToken = request.getParameter("idToken");
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

		if (idToken.length() == 0 || idToken.trim().isEmpty() || "\"\"".equals(idToken)) {
			out.println(new JSONObject().put("error", "Invalid idToken"));
			return;
		}
		if (mfaPendingCredential.length() == 0 || mfaPendingCredential.trim().isEmpty() || "\"\"".equals(mfaPendingCredential)) {
			out.println(new JSONObject().put("error", "Invalid mfaPendingCredential"));
			return;
		}

		if (!("01".equals(mfaEnrollemntId)) || mfaEnrollemntId.length() == 0 || mfaEnrollemntId.trim().isEmpty() || "\"\"".equals(mfaEnrollemntId)) {
			out.println(new JSONObject().put("error", "Invalid mfaEnrollemntId"));
			return;
		}

		try {
			final String useremail = (String) StartMfaEmailRequestInfo.get("email");

			if (useremail.length() == 0 || useremail.trim().isEmpty() || "\"\"".equals(useremail)) {
				out.println(new JSONObject().put("error", "Invalid StartMfaEmailRequestInfo: email required"));
				return;
			}
		} catch (Exception e) {
			out.println(new JSONObject().put("error", "Invalid StartMfaEmailRequestInfo: email required"));
			return;
		}

		JsonWebToken jwt = new JsonWebToken();
		JSONObject decodedIDTokenclaims = jwt.JWTDecoder(idToken);

		if (decodedIDTokenclaims.has("error")) {
			out.println(decodedIDTokenclaims);
			return;
		}

		TokenValidationClass uvc = new TokenValidationClass();
		out.println(uvc.mfaSignInStart(decodedIDTokenclaims, (String) StartMfaEmailRequestInfo.get("email"), mfaPendingCredential));
	}

}
