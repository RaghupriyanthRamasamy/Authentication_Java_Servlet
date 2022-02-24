package com.zc.secondaryemail;

import java.io.IOException;
import java.io.PrintWriter;

import com.zc.loginservlet.UserDetailClass;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SecondaryToPrimary
 */
@WebServlet("/secondarytoprimary")
public class SecondaryToPrimary extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public SecondaryToPrimary() {}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String secondaryEmail = request.getParameter("secondaryvalue");
		String primaryEmail = request.getParameter("primaryvalue");
		
		Cookie[] cookies = request.getCookies();
		
		UserDetailClass UDC = new UserDetailClass();
		String user_id = UDC.GetUserId(cookies);
		
		PrintWriter out = response.getWriter();
		SecondayEmailClass SEC = new SecondayEmailClass();
		
		if(SEC.SecondaryToPrimary(user_id, primaryEmail, secondaryEmail)) {
			out.println("true");
		}
		else {
			out.println("false");
		}
	}

}
