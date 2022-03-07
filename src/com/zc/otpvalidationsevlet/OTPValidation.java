package com.zc.otpvalidationsevlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import com.zc.userdetails.UserDetailClass;

@WebServlet(name = "otpvalidation", urlPatterns = { "/otpvalidation" })
public class OTPValidation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public OTPValidation() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		String userEmail = request.getParameter("useremail");
		String sessionInfo = request.getParameter("sessionInfo");
		String otp = request.getParameter("otp");
		PrintWriter out = response.getWriter();

		JSONObject result = new JSONObject();
		UserDetailClass udc = new UserDetailClass();
		result = udc.userOTPValidation(userEmail, otp, sessionInfo);

		out.println(result);
	}
}
