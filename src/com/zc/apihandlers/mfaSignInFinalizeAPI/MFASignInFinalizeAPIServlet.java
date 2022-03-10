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

import com.zc.apihandlers.mfaSignInFinalizeAPI.MFASignInFinalizeAPIHandler;

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
		
		final String mfaPendingCredential = request.getParameter("mfaPendingCredential");
		
		try {
			JSONTokener tokener = new JSONTokener(new BufferedReader(new InputStreamReader(request.getInputStream())));
			emailVerificationInfo = (JSONObject) new JSONObject(tokener).get("emailVerificationInfo");
		} catch (Exception e) {
			out.println(new JSONObject().put("error", "Invalid emailVerificationInfo"));
			return;
		}
		
		MFASignInFinalizeAPIHandler mfaSignInFinalizeObj = new MFASignInFinalizeAPIHandler();
		out.println(mfaSignInFinalizeObj.MFASignInFinalizeAPI(mfaPendingCredential, emailVerificationInfo));		
	}
}
