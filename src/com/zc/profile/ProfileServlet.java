package com.zc.profile;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONObject;
import com.zc.loginservlet.UserDetailClass;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ProfileServlet
 */
@WebServlet("/userprofile")
public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public ProfileServlet() {}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
		Cookie[] cookies = request.getCookies();
		
		UserDetailClass UDC = new UserDetailClass();
		
		String user_id = UDC.GetUserId(cookies);
		
		PrintWriter out = response.getWriter();
		
		ProfileDetailsClass pd = new ProfileDetailsClass();
		
		if(user_id != null) {
			JSONObject obj= pd.UserDetails(user_id);
			if(obj != null)
				out.println(obj);
		}
	}
}
