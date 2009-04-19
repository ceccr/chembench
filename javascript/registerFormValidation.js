
function submitForm1(button, textarea)
{
	if(!validate1(button.form))	
	{return false;}
	else{
	button.disabled=true;
	button.form.submit();
	textarea.innerHTML="<font size=2 color= #8B7765 face=arial>Your registration information is being submitted, please wait</font>";
	return true;
	}
}


function validate1(form) 
{
   var messageDiv1 = document.getElementById("messageDiv1");
   messageDiv1.innerHTML = "";	
   var messageDiv2 = document.getElementById("messageDiv2");
   messageDiv2.innerHTML = "";	
   var messageDiv4 = document.getElementById("messageDiv4");
   messageDiv4.innerHTML = "";	
   var messageDiv5 = document.getElementById("messageDiv5");
   messageDiv5.innerHTML = "";
   
    var messageDiv6 = document.getElementById("messageDiv6");
   messageDiv6.innerHTML = "";
    var messageDiv7 = document.getElementById("messageDiv7");
   messageDiv7.innerHTML = "";
   
   var messageDiv8 = document.getElementById("messageDiv8");
   messageDiv8.innerHTML = "";
   
   var messageDiv9 = document.getElementById("messageDiv9");
   messageDiv9.innerHTML = "";	
   
   var messageDiv12 = document.getElementById("messageDiv12");
   messageDiv12.innerHTML = "";	
   var messageDiv11 = document.getElementById("messageDiv11");
   messageDiv11.innerHTML = "";	
   var messageDiv13 = document.getElementById("messageDiv13");
   messageDiv13.innerHTML = "";
   
    var valid=true;
   
    for(i in form.elements) 
    {
		 var elem = form.elements[i];
		 if(elem!=null) 
		 { 
			 if(elem.name=="firstName"&&elem.value=="") 
			 {            
			 	messageDiv1.innerHTML+="<font size=1 color=red face=arial>Please enter your first name.</font>"
			 	 valid=false;
			 }
			 if(elem.name=="lastName"&&elem.value=="") 
			 {            
			 	messageDiv2.innerHTML+="<font size=1 color=red face=arial>Please enter your last name.</font>"
			 	 valid=false;
			 }
			 if(elem.name=="nameOfOrg"&&elem.value.length<2) 
			 {            
			 	messageDiv4.innerHTML+="<font size=1 color=red face=arial>Please enter the name of your organization.</font>"
			 	 valid=false;
			 }
			 if(elem.name=="position"&&elem.value.length<2) 
			 {            
			 	messageDiv5.innerHTML+="<font size=1 color=red face=arial>Please enter the title of your position.</font>"
			 	 valid=false;
			 }
			 if(elem.name=="address"&&elem.value.length<2) 
			 {            
			 	messageDiv6.innerHTML+="<font size=1 color=red face=arial>Please enter your street address.</font>"
			 	 valid=false;
			 }
			 if(elem.name=="city"&&elem.value.length<2) 
			 {            
			 	messageDiv7.innerHTML+="<font size=1 color=red face=arial>Please enter the name of your city.</font>"
			 	 valid=false;
			 }
			 if(elem.name=="state"&&elem.value.length<2) 
			 {            
			 	messageDiv8.innerHTML+="<font size=1 color=red face=arial>Please enter your state.</font>"
			 	 valid=false;
			 }
			 if(elem.name=="zipCode"&&elem.value.length<5)
			 {  
			    messageDiv9.innerHTML+="<font size=1 color=red face=arial>Please enter a valid ZIP code.</font>"
			 	 valid=false;
			 }
			 if(elem.name=="phone"&&elem.value.length<5) 
			 {            
			 	messageDiv11.innerHTML+="<font size=1 color=red face=arial>Please enter a valid telephone number.</font>"
			 	 valid=false;
			 }
			 if(elem.name=="email"&&(elem.value.length<6||elem.value.indexOf("@")<1||elem.value.indexOf(".")<0)) 
			 {            
			 	messageDiv12.innerHTML+="<font size=1 color=red face=arial>Please enter a valid email address.</font>"
			 	 valid=false;
			 }
			 if(elem.name=="userName"&&elem.value.length<4) 
			 {            
			 	messageDiv13.innerHTML+="<font size=1 color=red face=arial>Please choose a longer user name.</font>"
			 	 valid=false;
			 }
       
                }
          }
	return valid;
}