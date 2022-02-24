package com.zc.emailutility;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtility {
	public static void sendEmail(String host, String port, final String userName, final String password,
			String toAddress, final String otp, final String username) throws AddressException, MessagingException {
		
		String subject = "OTP for localhost sign-in";
		String message = "<h1>Hi %s !</h1>";
		message += "<p color: rgb(156, 0, 156)>Use the following one-time password (OTP) to sign in to your localhost account.</p>";
		message += "This OTP will be valid for 15 minutes";
		message += "<h2>%s</h2>";
		message += "If you didn't initiate this action or if you think you received this email by mistake, please contact";
		message += "<a href='support@localhost.com'>support@localhost.com</a>";
		message += "<p>Kind Regards,</p><p>Localhost Testing Team</p>";
		
		message = String.format(message, username, otp);
		
		// sets SMTP server properties
		Properties properties = new Properties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		// creates a new session with an authenticator
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};

		Session session = Session.getInstance(properties, auth);

		// creates a new e-mail message
		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(userName));
		InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setContent(message, "text/html");

		// sends the e-mail
		Transport.send(msg);
	}
}
