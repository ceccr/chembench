<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>

<%@ page import="edu.unc.ceccr.global.Constants" %>
<jsp:useBean id="user" class="edu.unc.ceccr.persistence.User" scope="session"/>
<jsp:useBean id="allkNNValues" class="java.util.ArrayList" scope="session"/>
<jsp:useBean id="allExternalValues" class="java.util.ArrayList" scope="session"/>

<html:html>
<head>
<title>C-CHEMBENCH | C-ChemBench Model Builders</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css">td img {display: block;}body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	background-image: url(theme/img/euroBackgr.jpg);
}
</style>
<script src="javascript/script.js"></script>
<link href="theme/euro.css" rel="stylesheet" type="text/css" />
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css"/>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<script language="javascript">
function valid()
{
  var error=document.getElementById("error1");
  error.innerHTML="";
  var p1=document.getElementById("newPs");
  var p2=document.getElementById("rePs");
  if(p1.value!=p2.value)
   {error.innerHTML+="<font size=2 color=red face=arial>The passwords are not identical.</font>"
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
   <tr><form action="changePassword.do">
   <td height="25" colspan="17" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
     <tr><td height="80"></td></tr>
     <tr>
       <td width="200" height="40" valign="middle" align="right" class="ChangePSText">User Name &nbsp&nbsp;</td>
       <td width="218" align="left" ><input type="text" name="userName" size="27" ></td><td width="300" ></td>
    </tr>
<tr>
       <td width="200" height="40" valign="middle" align="right" class="ChangePSText" >Password &nbsp&nbsp;</td>
       <td width="218" align="left" ><input type="text" name="oldPs" size="27"></td><td width="300" ></td>
    </tr>

<tr>
       <td width="200" height="40" valign="middle" align="right" class="ChangePSText" >New Password &nbsp&nbsp;</td>
       <td width="218" align="left" ><input type="text" name="newPs" id="newPs" size="27"></td><td width="300" ></td>
    </tr>

<tr>
       <td width="200" height="40" valign="middle" align="right"  class="ChangePSText">Re-Password &nbsp&nbsp;</td>
       <td width="218" align="left" ><input type="text" id="rePs" size="27"></td><td width="300" align="left"><div id="error1"></div></td>
    </tr>
<tr>
       <td width="200" height="40" valign="middle" align="right" ></td>
       <td width="218" align="left" ><input type="reset" onclick="return confirm('Are you sure to reset all the fields?')"/>
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp;
<input type="submit" value="Submit" onclick="return valid();" /></td><td width="300" ></td>
    </tr>



   </table>  
 </td>
  </form></tr>
  <tr>
     <td colspan="14" class="StandardText"><p>The third (and the last) row begins here. . It has a fixed height and it is not expandable. It is a cell with a background image.  </p>
     <p>&nbsp;</p></td>   <td><img src="theme/img/spacer.gif" width="1" height="92" border="0" alt="" /></td>
  </tr>
  <tr>
   <td colspan="12"><img name="footer_left" src="theme/img/footer_left.jpg" width="690" height="62" border="0" id="footer_left" alt="" /></td>
   <td colspan="4"><img name="unc_logo" src="theme/img/unc_logo.jpg" width="72" height="62" border="0" id="unc_logo" alt="" /></td>
   <td><img src="theme/img/spacer.gif" width="1" height="62" border="0" alt="" /></td>
  </tr>
  <tr>
   <td colspan="7"><img name="footer_blank_bar" src="theme/img/footer_blank_bar.jpg" width="520" height="19" border="0" id="footer_blank_bar" alt="" /></td>
   <td><img name="button_about" src="theme/img/button_about.jpg" width="45" height="19" border="0" id="button_about" alt="" /></td>
   <td><img name="button_tos" src="theme/img/button_tos.jpg" width="30" height="19" border="0" id="button_tos" alt="" /></td>
   <td colspan="2"><img name="button_privacy" src="theme/img/button_privacy.jpg" width="86" height="19" border="0" id="button_privacy" alt="" /></td>
   <td colspan="5"><img name="button_helpdesk" src="theme/img/button_helpdesk.jpg" width="81" height="19" border="0" id="button_helpdesk" alt="" /></td>
   <td><img src="theme/img/spacer.gif" width="1" height="19" border="0" alt="" /></td>
  </tr>
  <tr>
   <td colspan="16"><img name="footer02" src="theme/img/footer02.jpg" width="762" height="41" border="0" id="footer02" alt="" /></td>
   <td><img src="theme/img/spacer.gif" width="1" height="41" border="0" alt="" /></td>
  </tr>
</table>
</body>
</html:html>
