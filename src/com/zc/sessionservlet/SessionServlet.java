package com.zc.sessionservlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.zc.loginservlet.UserDetailClass;

/**
 * Servlet implementation class SessionServlet
 */
@WebServlet("/usersession")
public class SessionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public SessionServlet() {}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		Cookie[] cookies = request.getCookies();
		
		UserDetailClass UDC = new UserDetailClass();
		String user_id = UDC.GetUserId(cookies);
		
		UserSessionClass usc = new UserSessionClass();
		JSONObject sobj = new JSONObject();
		sobj = usc.UserActiveSessions(user_id);
		
		if(sobj != null) {
			out.println(sobj);
		}
		else
			out.println("false");
	}

}
