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

import com.zc.apihandlers.mfaSignInStartAPI.MFASignInStartAPIHandler;

@WebServlet("/api/v1/accounts/mfaSignIn:Start")
public class MFASignInStartAPIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public MFASignInStartAPIServlet() {super();}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");

		final String mfaPendingCredential = request.getParameter("mfaPendingCredential");
		final String mfaEnrollemntId = request.getParameter("mfaEnrollemntId");

		PrintWriter out = response.getWriter();
		JSONObject StartMfaEmailRequestInfo = new JSONObject();

		try {
			JSONTokener tokener = new JSONTokener(new BufferedReader(new InputStreamReader(request.getInputStream())));
			StartMfaEmailRequestInfo = (JSONObject) new JSONObject(tokener).get("StartMfaEmailRequestInfo");
		} catch (Exception e) {
			out.println(new JSONObject().put("error", "Invalid StartMfaEmailRequestInfo"));
			return;
		}

		MFASignInStartAPIHandler mfaSignInStartobj = new MFASignInStartAPIHandler();
		out.println(mfaSignInStartobj.MFASignInStartAPI(mfaPendingCredential, mfaEnrollemntId, StartMfaEmailRequestInfo));
	}
}
