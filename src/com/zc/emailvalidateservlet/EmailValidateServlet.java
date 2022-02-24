package com.zc.emailvalidateservlet;

import java.io.IOException;
import java.io.PrintWriter;
import com.zc.loginservlet.UserDetailClass;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class EmailValidateServlet
 */
@WebServlet("/emailvalidate")
public class EmailValidateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public EmailValidateServlet() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String useremail = request.getParameter("useremail");
		PrintWriter out = response.getWriter();
		
		boolean Status = false;
		UserDetailClass evc = new UserDetailClass();
		
		Status = evc.EmailValidate(useremail);
		
		if(Status)
			out.println("true");
		else
			out.println("false");
	}

}
