function showPanel()
{

  if(document.getElementById("updateDiv").style.display=='inline')
{
document.getElementById("updateDiv").style.display='none';}
else{document.getElementById("updateDiv").style.display='inline';}

}
function showPanel2()
{

  if(document.getElementById("updateDiv2").style.display=='inline')
{
document.getElementById("updateDiv2").style.display='none';}
else{document.getElementById("updateDiv2").style.display='inline';}

}
// a new function has been defined for the change password div area.
function showPanel3()
{

  if(document.getElementById("updateDiv3").style.display=='inline')
{
document.getElementById("updateDiv3").style.display='none';}
else{document.getElementById("updateDiv3").style.display='inline';}

}
function showPanelDocs()
{

if(document.getElementById("docsDiv").style.display=='inline')
{
document.getElementById("docsDiv").style.display='none';}
else{document.getElementById("docsDiv").style.display='inline';}

}

// a new function has been defined for the manage users area.
function showPanel4()
{

  if(document.getElementById("updateDiv4").style.display=='inline')
{
document.getElementById("updateDiv4").style.display='none';}
else{document.getElementById("updateDiv4").style.display='inline';}

}

function confirmation2()
{
if(
window.confirm("Are you sure to delete this notification?"))
{return true;}else{return false;}
}

function checkName()
{
  if(document.getElementById("name").value=="")
{ window.alert("The software name is needed!");
  return false;}
else{return true;}
}
function valid()
{
  var error=document.getElementById("error1");
  error.innerHTML="";
  var p1=document.getElementById("newPs");
  var p2=document.getElementById("rePs");
  if(p1.value!=p2.value)
   {error.innerHTML+="<font size=2 color=red face=arial>The passwords do not match.</font>"
			 	p1.focus(); p1.value="";p2.value="";
			 	return false; }
   else{return true;}
  }