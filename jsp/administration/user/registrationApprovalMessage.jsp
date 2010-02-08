<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<html>
<head>
<title>C-CHEMBENCH | New User Registration</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css"/>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<script src="javascript/script.js"></script>
</head>

<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br />
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top" background="theme/img/backgrlibrary.jpg">
		
		<table width="465" border="0" cellspacing="0" cellpadding="0"><tr><td width="465">
		
		<p class="StandardTextDarkGray">&nbsp;</p>
		<p class="StandardTextDarkGray">
		</p>
		<p class="TextDarkGray" sytle="background:white;"><br /><s:property value="firstName" />,<br/><br/>
		Thank you for your interest in Chembench. 
		
		<s:property value="outputMessage" />
		<br /><br />
		While you wait, you might like to read the <a href="/help-overview#justRegistered">overview of Chembench</a>.
		<br /><br />
		Enjoy,<br/>
		The C-Chembench Team.
		<br/>
		<br/></p>
			
        </td></tr></table>     
				
				
		<p class="StandardTextBlack">&nbsp;</p>
		</td>
		</span>
	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html>
