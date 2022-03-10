package com.zc.apihandlers.VerifyPasswordAPI;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.zc.apihandlers.VerifyPasswordAPI.VerifyPasswordAPIHandler;

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
		
		VerifyPasswordAPIHandler verifyPasswordObj = new VerifyPasswordAPIHandler();
		PrintWriter out = response.getWriter();
		out.println(verifyPasswordObj.VerifyPasswordAPI(useremail, password));
	}
}
