package com.zc.sessionservlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TerminateCurrentSession
 */
@WebServlet("/terminatecurrentsession")
public class TerminateCurrentSession extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public TerminateCurrentSession() {}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if((cookie.getName()).compareTo("_Session_ID") == 0) {
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
		
//		response.sendRedirect("login");
		
		out.println("true");
	}

}
