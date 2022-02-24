$(document).ready(function(){
   	var email;
   	$('#email').blur(function(){
   		email = $('#email').val();
   		if(email == null || email == ""){
   			$('#emailerror').html("Please Enter your Email ID").css("color","red");
   			$('#email').css("border", "2px solid red");
   		}
   		else if(email.endsWith("@gmail.com") == false){
   			$('#emailerror').html("Entered Email ID Not Valid").css("color","red");
   			$(':input[type="submit"]').prop('disabled', true);
   		}
   		else {
   			$.ajax({
   				type: 'POST',
   				data: {useremail: email},
   				url: 'emailvalidate',
   				async: false,
   				success: function(result){
   					if($.trim(result) == "true"){
   						$(':input[type="submit"]').prop('disabled', true);
   						$('#emailerror').html("Entered Email ID already exist").css("color","red");
                        $('#emailerror').toggleClass("res");
                        
   					}
   					if($.trim(result) == "false"){
   						$('#emailerror').html("");
   						$(':input[type="submit"]').prop('disabled', false);
   					}
   				}
   			})
   		};
   	});
   	
   	$('#email').focus(function(){
   		$('#emailerror').html("");
   		$('#email').css("border", "");
   	})
   	
    var emailvalue = sessionStorage.getItem("email");
	if (emailvalue != "" & emailvalue != null) {
		if (emailvalue.endsWith("@gmail.com")) {
			document.getElementById("email").value = emailvalue;
        	console.log(sessionStorage.getItem("email"));
		}	
    }
});