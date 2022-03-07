package com.zc.secondaryemail;

import java.io.IOException;
import java.io.PrintWriter;

import com.zc.userdetails.UserDetailClass;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SecondaryEmail
 */
@WebServlet("/secondaryemail")
public class SecondaryEmail extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public SecondaryEmail() {}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String secondaryEmail = request.getParameter("secondaryEmail");
		PrintWriter out = response.getWriter();
		
		Cookie[] cookies = request.getCookies();
		
		UserDetailClass UDC = new UserDetailClass();
		String user_id = UDC.GetUserId(cookies);
		
		SecondayEmailClass SEC = new SecondayEmailClass();
		
		boolean status = SEC.AddSecondaryEmail(user_id, secondaryEmail);
		if(status) {
			out.println("true");
		}
		else {
			out.println("false");
		}
	}
}
