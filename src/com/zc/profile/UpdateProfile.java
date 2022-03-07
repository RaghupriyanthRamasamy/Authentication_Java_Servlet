package com.zc.profile;

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
 * Servlet implementation class UpdateProfile
 */
@WebServlet(description = "User Profile Update( If User edit his/her Profile, Update code Provided Here )", urlPatterns = { "/updateprofile" })
public class UpdateProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public UpdateProfile() {}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String gender = request.getParameter("gender");
		String country = request.getParameter("country");
		
		PrintWriter out = response.getWriter();
		boolean status = false;
		
		Cookie[] cookies = request.getCookies();
		
		UserDetailClass UDC = new UserDetailClass();
		
		String user_id = UDC.GetUserId(cookies);
		
		ProfileDetailsClass pd = new ProfileDetailsClass();
		if(user_id != null) {
			status = pd.UpdateProfile(firstname, lastname, gender, country, user_id);
		}
		
		if(status)
			out.println("true");
		else
			out.println("false");
	}

}
