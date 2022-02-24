$(document).ready(function () {
  let email, sessionInfo;
  $("#next").click(function () {
    email = $("#userName").val();
    if ($("#userName").val() == null || $("#userName").val() == "") {
      $("#result1").html("Please Enter your Username").css("color", "red");
      $("#userName").css("border", "2px solid #ef476f");
    } else {
      $.ajax({
        type: "POST",
        data: { useremail: email },
        url: "emailvalidate",
        async: false,
        success: function (result) {
          if ($.trim(result) == "true") {
            $("#username").toggleClass("hidden");
            $("#password").toggleClass("show");
            $("#pass_select").addClass("animate_sel");
            $("#pass1").addClass("animate_pass");
          }
          if ($.trim(result) == "false") {
            $("#result1")
              .html(
                "This Email ID cannot be found. Please use a different Email ID or <span style='padding-left: 5px;'><a href='register'> sign up</a></span> for a new account."
              )
              .css("color", "red");
            $("#userName").css("border", "2px solid red");
            let email = document.getElementById("userName").value;
            sessionStorage.setItem("email", email);
          }
        },
      });
    }
  });

  $("#userName").focus(function () {
    $("#result1").html("");
    $("#userName").css("border", "");
  });

  const passwordResult = message => {
    $("#user_container").removeClass("hidden");
    $("#user_container").addClass("show");
    $("#loader_container").removeClass("show");
    $("#loader_container").addClass("hidden");
    $("#pass-result").html(message).css("color", "red");
    $("#pass1").css("border", "2px solid #d90429");
  };

  $("#btnsubmit").click(function () {
    const password = $("#pass1").val();
    if ($.trim(password) == null || $.trim(password) == "") {
      passwordResult("Please Enter your password");
    } else {
      $("#user_container").addClass("hidden");
      $("#loader_container").removeClass("hidden");
      $("#loader_container").addClass("show");
      console.log("pasword else");
      $.ajax({
        type: "POST",
        data: { Password: password, useremail: email },
        url: "passwordvalidate",
        async: false,
        success: function (value) {
          console.log(value);
          if (value.ServerError === true) {
            passwordResult(
              "Problem in our end We will rectify it soon. Kindly try after sometime"
            );
          } else if (value.mfapassed === false) {
            passwordResult("Incorrect password. Please try again.");
          } else {
            sessionInfo = value.otpSessionInfo;
            $("#loader_container").removeClass("show");
            $("#loader_container").addClass("hidden");
            $("#user_container").addClass("hidden");
            $("#otp_container").removeClass("hidden");
            $("#otp_container").addClass("show");
            console.log("Inside Pass else");
          }
        },
      });
    }
  });

  $("#pass1").focus(function () {
    $("#pass-result").html("");
    $("pass1").css("border", "2px solid #1f52f9");
  });

  const otpResult = message => {
    $("#otp_error").html(message);
  };

  const loginCall = (email, authCredential) => {
    $.ajax({
      type: "POST",
      data: { email, authCredential },
      url: "loginservlet",
      async: false,
      success: function (value) {
        console.log("Inside login call success", value);
        if (value.status === true) {
          console.log("inside logincall if");
          window.location.href =
            "http://localhost:8080/Authentication/profile";
        } else if (value.status === false) {
          otpResult(
            "Problem in our end We will rectify it soon. Kindly try after sometime"
          );
        }
      },
    });
  };

  $("#verify_otp").click(() => {
    const otp = $("#otp-input").val();
    if ($.trim(otp) == null || $.trim(otp) === "") otpResult("Enter your OTP");
    else if ($.trim(otp).length != 6) otpResult("Invalid OTP");
    else if (otp.match(/^[0-9]+$/) == null) otpResult("Invalid OTP");
    else {
      $.ajax({
        type: "POST",
        data: { useremail: email, otp: otp, sessionInfo: sessionInfo },
        url: "otpvalidation",
        async: false,
        success: function (value) {
          console.log("In verify otp", value);
          if (value.serverError === true)
            otpResult(
              "Problem in our end We will rectify it soon. Kindly try after sometime"
            );
          else if (value.validOTP === true)
            loginCall(value.email, value.authCredential);
          else otpResult("Invalid OTP");
        },
      });
    }
  });

  $("#otp-input").focus(() => $("#otp_error").html(""));

  $("#cancel_otp").click(() =>
    window.location.replace("http://localhost:8080/Authentication/login")
  );

  $("resend_otp").click(() =>
    alert("This option is not enabled. Refresh this window")
  );
});

document.getElementById("eye").onclick = show;

function show() {
  // confirm = document.getElementById('pass2');
  image = document.getElementById("eye");
  var password = document.getElementById("pass1");
  if (password.type === "password") {
    password.type = "text";
    // confirm.type = "text";
    image.setAttribute("src", "eyeshow.png");
  } else if (password.type === "text") {
    password.type = "password";
    // confirm.type = "password";
    image.setAttribute("src", "eyehide.png");
  }
}

// window.location.replace(
//   "http://localhost:8080/Authentication/profile"
// );
// $("#form").submit();
