<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
<title>C-CHEMBENCH | Cheminformatics Tools</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="/theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" src="javascript/script.js"> </script>

</head>
<body onload="setTabToHome();">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr><td height="180" colspan="5" valign="top"
			background="theme/img/backgrindex.jpg" STYLE="background-repeat: no-repeat;">
			<br />
		<p class="StandardTextDarkGrayParagraph"><b>Not Logged In</b></p>
		<br />
		<table width="465" border="0" cellspacing="0" cellpadding="0">
        <tr><td>
        <p class="StandardTextDarkGrayParagraph">
		You are seeing this page because you are currently not logged in, or your session has expired.
		<br /><br />
		If you need an account, you can make one from the <a href="home.do">Home</a> page. Creating an account is quick and free.
		<br /><br />
		If you already have an account, uou can log in from the <a href="home.do">Home</a> page as well.
		<br /><br />
		Thanks for using Chembench! If you encounter any problems, please contact us at ceccr@email.unc.edu.
		</p>
		</td></tr>
		</table>
	</td></tr>
</table>

<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html>
