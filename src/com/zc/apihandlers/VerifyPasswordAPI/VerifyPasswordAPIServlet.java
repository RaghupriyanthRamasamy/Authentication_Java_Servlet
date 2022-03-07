package com.zc.apihandlers.VerifyPasswordAPI;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import com.zc.userdetails.UserDetailClass;
import com.zc.profile.ProfileDetailsClass;
import com.zc.JWT.JsonWebToken;;

@WebServlet("/api/v1/accounts/verifypassword")
public class VerifyPasswordAPIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public VerifyPasswordAPIServlet() {super();}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
		final String useremail = request.getParameter("useremail"); 
		final String password = request.getParameter("password");
		
		PrintWriter out = response.getWriter();
		
		if(useremail.length() == 0 || useremail.trim().isEmpty() || "\"\"".equals(useremail)) {
			out.println(new JSONObject().put("error", "Invalid useremail"));
			return;
		}
		
		if(password.length() == 0 || password.trim().isEmpty() || "\"\"".equals(password)) {
			out.println(new JSONObject().put("error", "Invalid password"));
			return;
		}
		
		UserDetailClass udc = new UserDetailClass();
		
		if (!(udc.EmailValidate(useremail))) {
			out.println(new JSONObject().put("error", "Account not exist: Try to Register"));
			return;
		}
		
		if(udc.PasswordValidate(useremail, password)) {
			ProfileDetailsClass pdc = new ProfileDetailsClass();
			JSONObject mfaEnrollmentStatus = pdc.UserMfaEnrollmentStatus(useremail);
			if (mfaEnrollmentStatus.has("error")) {
				out.println(mfaEnrollmentStatus);
				return;
			}
			JsonWebToken jwt = new JsonWebToken();
			if ((mfaEnrollmentStatus.get("mfaEnrollmentStatus")).equals(0)) {
				out.println(jwt.NonMfaUserIDToken(useremail, (int)mfaEnrollmentStatus.get("mfaEnrollmentStatus")));
				return;
			}
			if ((mfaEnrollmentStatus.get("mfaEnrollmentStatus")).equals(1)) {
				out.println(jwt.mfaPendingCredToken(useremail, (int)mfaEnrollmentStatus.get("mfaEnrollmentStatus")));
				return;
			}
			out.println(new JSONObject().put("error", "Internal Validation Error"));

//			out.println(jwt.JWTPartialIDToken(useremail));
		}else {
			out.println(new JSONObject().put("error", "Invalid password"));
		}
	}
}
