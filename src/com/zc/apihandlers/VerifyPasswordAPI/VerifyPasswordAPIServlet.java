package com.zc.apihandlers.VerifyPasswordAPI;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

import com.zc.loginservlet.UserDetailClass;

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
		
		if(useremail.length() == 0 || useremail.trim() == "" || useremail == null || "\"\"".equals(useremail)) {
			HashMap <String, String> usernameErrorObj = new HashMap <String, String>();
			usernameErrorObj.put("error", "Invalid useremail");
			JSONObject usernameErrorJObj = new JSONObject(usernameErrorObj);
			out.println(usernameErrorJObj);
			return;
		}
		UserDetailClass udc = new UserDetailClass();
		if(udc.PasswordValidate(useremail, password)) {
			JsonWebToken jwt = new JsonWebToken();
			out.println(jwt.JWTPartialIDToken(useremail));
		}else {
			HashMap <String, String> userErrorObj = new HashMap <String, String>();
			userErrorObj.put("error", "Invalid email or password");
			JSONObject usernameErrorJObj = new JSONObject(userErrorObj);
			out.println(usernameErrorJObj);
		}
	}
}
