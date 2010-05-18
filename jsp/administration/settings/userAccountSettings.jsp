<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>

<html:html>
<head>
<title>CHEMBENCH | Account Settings</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" 	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<script type="text/javascript" src="javascript/neon.js"></script>
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css"/>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<script src="javascript/script.js"></script>
<script language="javascript">
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
</script>
</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br/>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td height="557" colspan="5" valign="top" background="theme/img/backgrindex.jpg">

<form action="changePassword.do">
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
          <tr><td height="60"></td></tr>

          
          <tr><td width="200" height="30" valign="middle" align="right" class="ChangePSText" ><b>Account Settings<br>Change Your Password</b></td>
           
           <td></td><td width="300" ></td>
          </tr>
          <tr><td height="20"></td></tr>

     <tr>
              <td width="218" align="left" ><input type="hidden" name="userName" size="27"  value="<bean:write name="user" property="userName" />"></td><td width="300" ></td>
    </tr>
<tr>
       <td width="200" height="30" valign="middle" align="right" class="ChangePSText" >Current Password &nbsp&nbsp;</td>
       <td width="218" align="left" ><input type="password" name="oldPs" size="27"></td><td width="300" ></td>
    </tr>

<tr>
       <td width="200" height="30" valign="middle" align="right" class="ChangePSText" >New Password &nbsp&nbsp;</td>
       <td width="218" align="left" ><input type="password" name="newPs" id="newPs" size="27"></td><td width="300" ></td>
    </tr>

<tr>
       <td width="200" height="30" valign="middle" align="right"  class="ChangePSText">Confirm Password &nbsp&nbsp;</td>
       <td width="218" align="left" ><input type="password" id="rePs" size="27"></td><td width="300" align="left"><div id="error1"></div></td>
    </tr>
<tr>
       <td width="200" height="30" valign="middle" align="right" ></td>
       <td width="218" align="left" ><input type="reset" onclick="return confirm('Are you sure to reset all the fields?')"/>
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp;
<input type="submit" value="Submit" onclick="return valid();" /></td><td width="300" ></td>
    </tr>



   </table>  </form>

		</td>
			</tr>


	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
