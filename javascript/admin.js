


function autoInfoShow(){
if(document.getElementById("automatic").disabled){document.getElementById("info").innerHTML="The current setting is <u>Automatic </u>Acceptance";}
else{document.getElementById("info").innerHTML="The current setting is <u>Manual</u> Acceptance";}}
function autoInfoOut(){document.getElementById("info").innerHTML="";}

function manualInfoShow(){if(document.getElementById("manual").disabled){document.getElementById("info").innerHTML="The current setting is <u>Manual</u> Acceptance";}
else{document.getElementById("info").innerHTML="The current setting is <u>Automatic</u> Acceptance";}}
function manualInfoOut(){document.getElementById("info").innerHTML="";}

function newuserShow(){document.getElementById("info").innerHTML="Check the registered but not approved new users.";}
function newuserOut(){document.getElementById("info").innerHTML="";}

function viewuserShow(){document.getElementById("info").innerHTML="View current approved user's basic information.";}
function viewuserOut(){document.getElementById("info").innerHTML="";}

function addmoreShow(){document.getElementById("info").innerHTML="Add more administrator tasks here.";}
function addmoreOut(){document.getElementById("info").innerHTML="";}


function setAcceptance(set){ 
if(set=="automatic")
{document.getElementById("automatic").disabled="true";}
else{document.getElementById("manual").disabled="true";}
}


function switchSetting(thisOne)
{
var option;
if(thisOne.value=="automatic")
{ option="automatic";}
else{ option="manual";}
if( confirm("Change new user acceptance to "+option+"?"))
 { window.location.href="updateAdminSettings.do?userAcceptance="+thisOne.value+"";}

}
