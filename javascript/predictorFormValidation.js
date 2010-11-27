function validateForm(){
	
	//check that the job name is not the same as any of the user's existing
	//job names, model names, predictor names, or dataset names
	
	//check that there are no spaces in the job name.
	
	var rejectName = false;
	var errorstring;
	var jobname = document.getElementById("jobName").value;
	if(jobname.length == 0){
		rejectName = true;
		errorstring="Please enter a name for this job.";
	}
	if(rejectName){
		window.alert(errorstring);
		return false; 	
	}
	else{
		return true;
	}
}

function submitForm3(button)
{
	if(validateForm()){
		button.disabled=true;
		button.form.submit();
		document.getElementById("textarea").innerHTML="<i>Your workflow is being submitted, please wait.</i>";
		return true;
	}
	else{
		return false;
	}
}


function validate1(form) 
{
   var messageDiv1 = document.getElementById("messageDiv1");
   messageDiv1.innerHTML = "";	
   var messageDiv2 = document.getElementById("messageDiv2");
   messageDiv2.innerHTML = "";	
   
    for(i in form.elements) 
    {
		 var elem = form.elements[i];
		 if(elem!=null) 
		 { 
			 if(elem.name=="jobName"&&elem.value=="") 
			 {            
			 	messageDiv1.innerHTML+="<font size=1 color=red face=arial>Please give your prediction an identical name.</font>"
			 	elem.focus();
			 	return false; 
			 }

			 if(elem.name=="cutOff")
			 {
			  if(elem.value==""){  messageDiv2.innerHTML+="<font size=1 color=red face=arial>The CutOff  is a required field.</font>";elem.focus();return false;  }
			   else
			      { var f=parseFloat(elem.value);
			        if(isNaN(elem.value)){  messageDiv2.innerHTML+="<font size=1 color=red face=arial>The similarity cutoff value should be a number.</font>";elem.focus();return false;  }
			        if(f<0 ||f>5){  messageDiv2.innerHTML+="<font size=1 color=red face=arial>The similarity cutoff value should be between 0 and 5.</font> ";elem.focus();return false;  }
			      } 
			  }
			
       }
   }
	return true;
}