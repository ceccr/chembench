function enableEdit()
{
  document.getElementById("Edit").disabled=false;
}
function enableSW()
{
  document.getElementById("userAction").disabled=false;
}

function enableEdit()
{
  document.getElementById("Edit").disabled=false;
}
function enableSW()
{
  document.getElementById("userAction").disabled=false;
}

function confirmation()
{
   if(confirm('Are you sure to deny this user?'))
   {window.location.href="deny.do";}
}
 
 function agree()
{
 if(confirm('Approve the account for this user?'))
   {window.location.href="agree.do";}
}
