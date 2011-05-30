<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<html>
<head>
<title>Email to All Users</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet"
	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css" />

<script src="javascript/script.js"></script>
<script type="text/javascript">
function checkContent()
{
if(document.getElementById("content").value=="")
{return (window.confirm("Send emails without content?"));}
else{return true;}
}
</script>
</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br />
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">

	<tr><td>
	<form action="emailAllUsers">
	<b>Emails need to have HTML markup in them or they will look silly.</b><br /><br />
	<table width="480" height="480" border="0">
		<tr><td width="35" height="18">From:</td><td>ceccr@email.unc.edu</td><tr>
		<tr><td width="60" height="18">Send to:</td><td><s:radio name="sendTo" id="sendTo" value="sendTo" list="#{'JUSTME':'Just Me (for testing)','ALLUSERS':'All users<br />'}" theme="simple" /></td><tr>		
		<tr><td width="35" height="18">Subject:</td><td><s:textfield name="emailSubject" value="" size="43" theme="simple" /></td><tr>
		<tr><td height="160" colspan="2">&nbsp;<s:textarea name="emailMessage" value="" rows="10" cols="45" theme="simple" /></td><tr>
		<tr><td width="35" height="18"></td><td><input type="submit" onclick="return checkContent()" value="Send" /></td><tr>
	</table>
	<br />
	</form>
      
    </td></tr>
</table>
</body>
</html>
