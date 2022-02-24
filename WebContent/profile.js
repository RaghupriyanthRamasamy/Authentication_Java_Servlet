$(document).ready(function(){
			
	// intializing variables
	let i = 0;
	let j = 0;
	let sessionKeys;
	let activeSessionKeys;
	let primaryemail, firstname, lastname, gender, country;
	let xhrSS;
	let xhrCNS;

	// function to create session display container

	function createSessionElement(value) {
		let sessionContainer = document.getElementById("session-display");
				
		let type = document.createElement('div');
		// type.appendChild(document.createTextNode("Content will goes here"));
		type.setAttribute("class", "display_session-div");

		const input = document.createElement('input');
		i = i+1;
		let inputid = "input-ses_"+i;
		input.setAttribute("value", value);
		input.setAttribute("id", inputid);
		input.setAttribute("disabled", "");
		const button = document.createElement('button');
		button.textContent = "Terminate";
		let session_del_btn = "ses-del-btn_"+i;
		button.setAttribute("id", session_del_btn);
		type.appendChild(input);
		type.appendChild(button);
		sessionContainer.appendChild(type);
	}

	// Function to create secondary email container
	function createSecEmailContainer(value) {
		let SecEmailContainer = document.getElementById("secendory_email");
		let type = document.createElement('div');
		type.setAttribute("class", "display_email_div");
		const input = document.createElement('input');
		j = j+1;
		let inputid = "sec-email_"+j;
		input.setAttribute("value", value);
		input.setAttribute("id", inputid);
		input.setAttribute("disabled", "");

		const makeprimarybtn = document.createElement('button')
		let makeprybtnid = "make-pry_" + j;
		makeprimarybtn.innerHTML = '<i class="fas fa-chess-queen"></i>';
		makeprimarybtn.setAttribute("class", "makeprimary_icon");
		makeprimarybtn.setAttribute("id", makeprybtnid);

		const button = document.createElement('button');
		button.innerHTML = '<i class="fas fa-trash-alt"></i>';
		let session_del_btn = "sec-email-btn_"+j;
		button.setAttribute("id", session_del_btn);
		button.setAttribute("title", "Remove this email")

		type.appendChild(makeprimarybtn);
		type.appendChild(input);
		type.appendChild(button);
		SecEmailContainer.appendChild(type);
	}

	// get secondary email

	function getSecondaryemail() {
		$.ajax({
			type: 'POST',
			url: 'getsecondaryemail',
			async: false,
			success: function (result) {
				var sec_emailval = Object.keys(result);
				sec_emailval.forEach(element => {
					createSecEmailContainer(element);
				});
			}
		})
	}

	// Delete Terminated Session Container

	function deleteTerminatedContainer(containerValue) {
		for (var t = 1; t <= i; ++t){
			let sessionName = $("#input-ses_" + t).val();
			if (containerValue == sessionName) {
				$("#input-ses_"+t).unwrap()
				$("#ses-del-btn_"+t).remove();
				$("#input-ses_" + t).remove();
				sessionKeys.pop(sessionName);
			}
		}
	}

	// function to terminated current session if anyone terminated from other place
	
	function terminateLogout() {
		$.ajax({
			type: 'POST',
			url: 'terminatecurrentsession',
			success: function (result) {
				if ($.trim(result) == "true") {
					// console.log(xhrCNS);
					// console.log(xhrSS);
					xhrSS.abort();				// Abort Session Status ajax request
					xhrCNS.abort();				// Abort Check New Session ajax request
					location.reload(true);
				}
			}
		})
	}

	// function to check current Session Teminated or not

	function sessionStatus() {
		xhrSS = $.ajax({
					type: 'POST',
					url: 'sessionstatus',
					success: function (result) {
						if ($.trim(result) == "false") {
							terminateLogout();
						}
					}
				})
	}

	// Fetch all User Sessions from database through servlet and dynamically render into websites
	function sessionDetails() {
		$.ajax({
			type: 'POST',
			url: 'usersession',
			async: false,
			success: function(sessionResult) {
				if ($.trim(sessionResult) == false) {
					Console.log("No Session Initialized");
				} else {
					sessionKeys = Object.keys(sessionResult);
					sessionKeys.forEach(element => {
						createSessionElement(element);
					});
				}
			}
		})
	}

	// checking new session added, if added render it into template
	// Included with delete already terminated session
	function checkNewSession() {
		xhrCNS = $.ajax({
					type: 'POST',
					url: 'usersession',
					async: false,
					success: function(sessionResult) {
						if ($.trim(sessionResult) == "false") {
							Console.log("No Session Initialized");
						} else {
							activeSessionKeys = Object.keys(sessionResult);

							activeSessionKeys.forEach(element => {
								if (!(sessionKeys.includes(element))) {
									console.log("active session key ", element)
									sessionKeys.push(element);
									createSessionElement(element);
								}
							});

							sessionKeys.forEach(element => {
								if (!(activeSessionKeys.includes(element))) {
									console.log("session keys ", element);
									deleteTerminatedContainer(element);
								}
							})
						}
					}
				})
	}

	// Functio to set user data
	function settingUserProfile() {
		$('.user-name').html(firstname);
		$('#First_name').val(firstname);
		$('#Last_name').val(lastname);
		$('#user_gender').val(gender);
		$('#user_coutry').val(country);
	}

	// Fetch user profile details
	function profileDetails() {
		$.ajax({
			type: 'POST',
			url: 'userprofile',
			async: false,
			success: function (result) {
				$('.user-email').html(result.email);
				$('#primary_email').html(result.email);
				primaryemail = result.email;
				firstname = result.firstname;
				lastname = result.lastname;
				gender = result.gender;
				country = result.country;
				settingUserProfile();
			}
		})
	}
	
	// Enable Profile Data Fields to edit data, enable update button container and hide edit button
	function enableProfileFields() {
		$('#profile-edit').css('display', 'none');
		$('.savebtn').css('display', 'block');
		$('#First_name').prop("disabled", false);
		$('#Last_name').prop("disabled", false);
		$('#user_gender').prop("disabled", false);
		$('#user_coutry').prop("disabled", false);
	}

	// Disable Profile Data Fields, save button container and show edit button
	function disableProfileFields() {
		$('#profile-edit').css('display', 'block');
		$('.savebtn').css('display', 'none');
		$('#First_name').prop("disabled", true);
		$('#Last_name').prop("disabled", true);
		$('#user_gender').prop("disabled", true);
		$('#user_coutry').prop("disabled", true);
	}

	profileDetails(); // load user profile details

	getSecondaryemail(); // load user secondary email

	sessionDetails(); // load user session details

	// Enabling input and select option to edit user profile details

	$('#profile-edit').click(function(){
		enableProfileFields();
	})
			
	// Disable user profile fields on clicking cancel button
			
	$('#cancel').click(function () {
		disableProfileFields();
		settingUserProfile();
	})
	
	// Enable update button if anyone of user field (profile data) changed

	$('#First_name').change(function(){
		$('#update').prop("disabled", false);
	})
			
	$('#Last_name').change(function(){
		$('#update').prop("disabled", false);
	})

	$('#user_gender').change(function(){
		$('#update').prop("disabled", false);
	})

	$('#user_coutry').change(function(){
		$('#update').prop("disabled", false);
	})

	// enable and disable secondary input container
	$('#add_email_btn').click(function () {
		$('#popup-1').addClass("active");
	})
	$('.close-btn').click(function () {
		$('#popup-1').removeClass("active");
	})
	$('.close-btn-mp').click(function () {
		$('#popup-mp').removeClass("active");
	})
	$('.overlay').click(function () {
		$('#popup-1').removeClass("active");
	})
	$('.overlay-mp').click(function () {
		$('#popup-mp').removeClass("active");
	})

	// Tool tip visibility
	$('#primary_icon').mouseover(function () {
		$('#primary_tooltip').css("visibility", "visible");
	})
	$('#primary_icon').mouseout(function () {
		$('#primary_tooltip').css("visibility", "hidden");
	})
	$('.makeprimary_icon').mouseover(function () {
		$('#makeprimary_tooltip').css({ "visibility": "visible"});
	})
	$('.makeprimary_icon').mouseout(function () {
		$('#makeprimary_tooltip').css("visibility", "hidden");
	})

	// Add secondary email
	$('#add_sec_btn').click(function () {
		let secEmail = $('#sec_email_input').val();
		if (secEmail == null || secEmail == "") {
			$('#sec_error').html("Field cannot be empty");
		}
		else if (secEmail.endsWith("@gmail.com") == false) {
			$('#sec_error').html("Enter valid email id");
		}
		else {
			$.ajax({
				type: 'POST',
				url: 'emailvalidate',
				data: {useremail: secEmail},
				async: false,
				success: function (value) {
					if ($.trim(value) == "true") {
						$('#sec_error').html("Email ID Already Exist");
					}
					else if($.trim(value) == "false") {
						$.ajax({
							type: 'POST',
							url: 'secondaryemail',
							data: {secondaryEmail: secEmail},
							async: false,
							success: function (value1) {
								if ($.trim(value1) == "true") {
									$('#sec_email_input').val('');
									$('#popup-1').removeClass("active");
									createSecEmailContainer(secEmail);
								}
								else {
									console.log("sec email not added");
								}
							}
						})
					}
				} 

			})
		}
	})

	// on foucusing on secondary email input field error will turn off
	$('#sec_email_input').focus(function () {
		$('#sec_error').html("");
	})

	// Update user profile details
	$('#update').click(function(){
		var Firstname = $('#First_name').val();
		var Lastname = $('#Last_name').val();
		var Gender = $('#user_gender').val();
		var Country = $('#user_coutry').val();
		if ((Firstname == "" || Firstname == null) || (Lastname == "" || Lastname == null)) {
			alert("Profile fields cannot be empty");
		}
		else {
			$.ajax({
				type : 'POST',
				url: 'updateprofile',
				data: {firstname: Firstname, lastname: Lastname, gender: Gender, country: Country},
				async: false,
				success: function(value){
					if ($.trim(value) == "true") {
						
						firstname = Firstname;
						lastname = Lastname;
						gender = Gender;
						country = Country;

						$('#update').prop("disabled", true);
						$('#cancel').prop("disabled", true);

						$('#profile_alert-h3').removeClass('hide');
						$('#profile_alert-h3').addClass('show');

						disableProfileFields();
						settingUserProfile();

						setTimeout(() => {
							$('#profile_alert-h3').removeClass('show');
							$('#profile_alert-h3').addClass('hide');
						}, 1000);

					}
				}
			})
		}
	})
	
	// User choice to Terminate Session

	$(document).on("click", "button", function() {
		let sess_del_but = this.id;
		if (sess_del_but.includes("ses-del-btn_")) {
			let index = sess_del_but.split("_");
			let sessionName = $("#input-ses_"+index[1]).val();
			$.ajax({
				type: "POST",
				url: "terminatesession",
				data: {sessionname: sessionName},
				success: function (result) {
					if ($.trim(result) == "true") {
						$("#input-ses_"+index[1]).unwrap()
						$("#ses-del-btn_"+index[1]).remove();
						$("#input-ses_"+index[1]).remove();
						sessionKeys.pop(sessionName);
					}
					
					// $("#ses-del-btn_"+index[1]).closest('.display_session').remove();
					// $(this.id).parent().remove();
				}
			})
		}
	})

	// User choice to delete secondary email
	$(document).on("click", "button", function () {
		let email_del_but = this.id;
		if (email_del_but.includes('sec-email-btn_')) {
			let secemailindex = email_del_but.split("_");
			let secemailValue = $("#sec-email_" + secemailindex[1]).val();
			$.ajax({
				type: "POST",
				url: 'removesecondaryemail',
				data: {secondaryEmail : secemailValue},
				success: function (result) {
					if ($.trim(result) == "true") {
						$("#sec-email_"+secemailindex[1]).unwrap()
						$("#sec-email-btn_"+secemailindex[1]).remove();
						$("#sec-email_" + secemailindex[1]).remove();
						$("#make-pry_" + secemailindex[1]).remove();
					}
				}
			})
		}
	})

	// change secondary email to primary

	$(document).on("click", "button", function () {
		let make_pry_but = this.id;
		if (make_pry_but.includes("make-pry_")) {
			let secemailIndex = make_pry_but.split("_");
			let secEmailValue = $("#sec-email_" + secemailIndex[1]).val();
			$('#email_content').html(secEmailValue);
			$('#popup-mp').addClass("active");
			$('#make_primary').off().click(function () {
				$.ajax({
					type: "POST",
					url: 'secondarytoprimary',
					data: { secondaryvalue: secEmailValue, primaryvalue: primaryemail },
					success: function (result) {
						if ($.trim(result) == "true") {
							$("#sec-email_" + secemailIndex[1]).val(primaryemail);
							$('.user-email').html(secEmailValue);
							$('#primary_email').html(secEmailValue);
							primaryemail = secEmailValue;
							$('#popup-mp').removeClass("active");
						}
					}
				})
			})
			
		}
	})

	// Check current status and new session actvity
	function checkSession() {
		if (!($.active)) {
			sessionStatus();
			checkNewSession();
		}
	}
	setInterval(checkSession, 2000);

	// window.addEventListener('beforeunload', function (e) {
    // 	e.preventDefault();
    // 	e.returnValue = '';
	// });

})

	// adding session by user

	// $('#add-session').click(function(){
				
	// 	let session_name = $('#session_name').val();
	// 	let session_value = $('#session_value').val();
	// 	if((session_name == null || session_name == "") || (session_value == null || session_value == "") ){
	// 		alert("Session name or value cannot be empty");
	// 	}
	// 	else{
	// 		const addsession = session_name.toLowerCase();
	// 		if ((addsession == "useremail" ||addsession =="mailuser" || addsession.includes("email") || addsession.includes("user")) || addsession.length < 3){
	// 			alert("Session name invalid: Try with other session name");
	// 		}
	// 		else{
	// 			if(sessionKeys.includes(session_name)){
	// 				alert("Session already running: You cannot create session, delete this session and try again");
	// 			}
	// 			else {
	// 				$.ajax({
	// 					type : 'POST',
	// 					url : 'cookiegenerator',
	// 					data: {sessionName: session_name, sessionValue: session_value},
	// 					async: false,
	// 					success: function (result) {
	// 						console.log("Cookie added");
	// 						// location.reload(true);
	// 						createSessionElement(session_name);
	// 						$('#session_name').val("");
	// 						$('#session_value').val("");
	// 					}
	// 				})
	// 			}
	// 		}			
	// 	}
	// })