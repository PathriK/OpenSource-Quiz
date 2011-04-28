/* This script and many more are available free online at
The JavaScript Source :: http://javascript.internet.com
Created by: Neill Broderick :: http://www.bespoke-software-solutions.co.uk/downloads/downjs.php */

var mins;
var secs;

function cd() {
 	mins = 1 * m("2"); // change minutes here
 	secs = 0 + s(":01"); // change seconds here (always add an additional second to your total)
 	redo();
}

function m(obj) {
 	for(var i = 0; i < obj.length; i++) {
  		if(obj.substring(i, i + 1) == ":")
  		break;
 	}
 	return(obj.substring(0, i));
}

function s(obj) {
 	for(var i = 0; i < obj.length; i++) {
  		if(obj.substring(i, i + 1) == ":")
  		break;
 	}
 	return(obj.substring(i + 1, obj.length));
}

function dis(mins,secs) {
 	var disp;
 	if(mins <= 9) {
  		disp = " 0";
 	} else {
  		disp = " ";
 	}
 	disp += mins + ":";
 	if(secs <= 9) {
  		disp += "0" + secs;
 	} else {
  		disp += secs;
 	}
 	return(disp);
}

function redo() {
 	secs--;
 	if(secs == -1) {
  		secs = 59;
  		mins--;
 	}
 	document.getElementById("txt").value = dis(mins,secs); // setup additional displays here.
 	
 	if((mins == 0) && (secs == 0)) {
 		document.teamAnswers.submit();
  		window.alert("Uh oh, your time is up!\nDon't worry, your answers have been submitted!\nAfter you click OK, you should see a thank you page.");
 	} else {
 		cd = setTimeout("redo()", 1000);
 	}
}

function init() {
  cd();
}
window.onload = init;

window.onbeforeunload = function () {
   
}



