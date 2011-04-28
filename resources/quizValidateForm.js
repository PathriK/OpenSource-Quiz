function validateQuizRegistrationForm()
{
	var status = true;

	var teamName = document.getElementById("teamName").value;
	var activationCode = document.getElementById("activationCode").value;

	var userName_1 = document.getElementById("userName_1").value;
	var usn_1 = document.getElementById("usn_1").value;
	var email_1 = document.getElementById("email_1").value;
	var mobile_1 = document.getElementById("mobile_1").value;

	var userName_2 = document.getElementById("userName_2").value;
	var usn_2 = document.getElementById("usn_2").value;
	var email_2 = document.getElementById("email_2").value;
	var mobile_2 = document.getElementById("mobile_2").value;

	var msg = "Oops, we have some problems...\n\n";

	if(teamName.length &lt; 4)
	{
		msg += "Team name must be at least 3 characters long.\n";
		status = false;
	}

	if(activationCode.search(/\w{8}/))
	{
		msg += "Activation code must be exactly 8 characters long and can contain only alphanumeric characters.\n";
		status = false;
	}

	if(userName_1.length &lt; 4)
	{
		msg += "Member 1's name must be at least 3 characters long.\n";
		status = false;
	}

	if(usn_1.search(/1[Jj][Ss]0\d[Ii][Ss]\d{3}/))
	{
		msg += "Member 1's USN is not in the proper format.\n";
		status = false;
	}

	if(email_1.search(/.+@.+\..+/))
	{
		msg += "Member 1's email is not in the proper format.\n";
		status = false;
	}

	if(mobile_1.search(/\d{10}/))
	{
		msg += "Member 1's mobile is not in the proper format.\n";
		status = false;
	}

	if(userName_2.length &lt; 4)
	{
		msg += "Member 2's name must be at least 3 characters long.\n";
		status = false;
	}

	if(usn_2.search(/1[Jj][Ss]0\d[Ii][Ss]\d{3}/))
	{
		msg += "Member 2's USN is not in the proper format.\n";
		status = false;
	}

	if(email_2.search(/.+@.+\..+/))
	{
		msg += "Member 2's email is not in the proper format.\n";
		status = false;
	}

	if(mobile_2.search(/\d{10}/))
	{
		msg += "Member 2's mobile is not in the proper format.\n";
		status = false;
	}

	if(usn_1 == usn_2 &amp;&amp; usn_1.length == 0 &amp;&amp; usn_2.length == 0)
	{
		msg += "Both member 1 and member 2 cannot have the same USN!\n";
	}

	if(usn_1 == usn_2 &amp;&amp; usn_1.length == 0 &amp;&amp; usn_2.length == 0)
	{
		msg += "Both member 1 and member 2 cannot have the same USN!\n";
	}				


	if(status == false)
	{
		alert(msg + "\n\nPlease correct the above problems and try again!");
	}

	return status;
}
