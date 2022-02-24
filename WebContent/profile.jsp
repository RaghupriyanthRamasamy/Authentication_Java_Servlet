<%@page import="com.zc.accessvariables.AccessVariables"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
	<title>Profile</title>
    <link rel="stylesheet" href="./profile.css">

	<script src="https://kit.fontawesome.com/bed725bf97.js" crossorigin="anonymous"></script>
	<script type="text/javascript" src="jquery/jquery-3.6.0.js"></script>
	<script  src="jquery/jquery-3.3.1.min.js"></script>
	<script  src="jquery/jquery-migrate-1.4.1.min.js"></script>
	<script  src="jquery/jquery-ui-1.10.3.custom.min.js"></script>

</head>
<body>
	
	<%
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("pragma", "no-cache");
		response.setHeader("Expires", "0");
		
		/* String email = AccessVariables.getEmail();
		if(email == null){
			response.sendRedirect("login.jsp");
		} */
		
		try{
			
			int flag = 0;
			
			Cookie[] cookies = request.getCookies();
			for(Cookie cookie : cookies){
				if((cookie.getName()).compareTo("_Session_ID") == 0){
					flag =1;
				}
			}
			
			if(flag == 0)
				response.sendRedirect("login");
			
		}
		catch(Exception e){
			response.sendRedirect("login");
		}
		
	%>
	
	<!-- update success alert -->

	<div  class="profile_alert" style="z-index: 200;">
		<h3 id="profile_alert-h3" class="hide">Profile updated successfully</h3>
	</div>

	<!-- navigation bar -->

	<nav>
		<form style="margin: 0;" action="logout" method="post" >
			<button type="submit" id="signout">Sign Out</button>
		</form>
		<div class="nav-div">
			<div class="profile_pic-container">
                <!-- <input class="profile-pic"  type="file" name="" id=""> -->
            </div>
		</div>
	</nav>

	<!-- user profile code -->

	<div class="user-details">
		<div class="user_details-inside">

			<button class="user-edit" id="profile-edit">
				<span>Edit</span>
			</button>

			<div class="user-profile">
				<div class="user-name"></div>
				<div class="user-email"></div>
			</div>

			<div>
				<form method="post">
					<div>
						<div class="alignfields">
							<label for="First_name">First Name</label><br>
							<input type="text" name="firstname" id="First_name" disabled>
						</div>
						<div class="alignfields">
							<label for="Last_name">Last Name</label><br>
							<input type="text" name="lastname" id="Last_name" disabled>
						</div>
					</div>

					<div>
						<div class="alignfields">
							<label for="user_gender">Gender</label><br>
							<select name="gender" id="user_gender" disabled>
								<option value=""></option>
								<option value="Male">Male</option>
								<option value="Female">Female</option>
								<option value="Non Binary">Non binary</option>
							</select>
						</div>

						<div class="alignfields">
							<label for="user_coutry">Country</label><br>
							<select name="country" id="user_coutry" disabled>
								<option value="India">India</option>
								<option value="California">California</option>
								<option value="Australia">Australia</option>
							</select>
						</div>
					</div>

					<div style="display: none;" class="savebtn">

						<button type="button" id="update" disabled>
							<span>Update</span>
						</button>

						<button type="button" id="cancel">
							<span>Cancel</span>
						</button>

					</div>
				</form>
			</div>
		</div>
	</div>
	
	<!-- email container -->
	<div class="Sec_email">

		<div class="Add_email">
			<h2>Your Email Address</h2>
			<div class="primary_email">
				<span id="primary_tooltip">Your primary email</span>
				<h3><span id="primary_email"></span> <span style="color: Tomato;" id="primary_icon">   <i class="fas fa-key"></i></span></h3>
				
			</div>
			<div class="secendory_email" id="secendory_email">
				<span id="makeprimary_tooltip">Make this email to primary</span>
				<span id="removeemail_tooltip">Remove this email</span>
			</div>

			<div class="popup" id="popup-1">
				<div class="overlay"></div>
					<div class="content">
						<div class="close-btn">&times;</div>
						<h1>Add Email Address</h1>
						<hr>
						<h3>You will receive OTP to verify the email</h3>
						<div id="add_secondary_email">
							<label for="sec_email_input">Enter your email Address</label>
							<input type="email" name="sec_email" id="sec_email_input" autofocus>
							<div>
								<span style="color: red;" id="sec_error"></span>
							</div>
							<div>
								<button type="button" id="add_sec_btn" title="Add entered email"><i class="fas fa-user-plus"></i></button>
							</div>
						</div>
					</div>
			</div>

			<div class="popup-mp" id="popup-mp">
				<div class="overlay-mp"></div>
					<div class="content-mp">
						<div class="popup-head">
							<span class="close-btn-mp">&times;</span>
							<span >Make this email to primary</span>
						</div>
						<hr>
						<div style="margin: 20px;">
							<span>This will make <strong><span id="email_content"></span></strong> as your primary email address.</span>
						</div>
						
						<div>
							<button type="button" id="make_primary">Make primary</button>
						</div>
					</div>
			</div>

			<div>
				<center><button id="add_email_btn" style="display: block;" >+Add Email Address</button></center>
			</div>
		</div>

		

	</div>
		
	<!-- session display part -->
	<div>
		<!-- <div class="active_session_text"> -->
			<h2 id="active-h2" style="text-align: center;">Active Sessions</h2>
			<center><i class="fas fa-angle-double-down down_icon"></i></center>
		<!-- </div> -->
		
		<div class="display_session" id="session-display">

		</div>
	</div>
    <script src="./profile.js"></script>
</body>
</html>

<!-- <div class="display_session-div">
				<input type="text" name="" id="" value="ffjhgk456587fdb5678" style="display: none;">
				<p class="device">Windows</p><br>
				<p class="ip">197:67:89:09:01</p><br>
				<p>12:13 pm</p><br>
				<p><span>Salem</span>,<span>India </span></p><br>
				<button>terminate</button>
</div> -->