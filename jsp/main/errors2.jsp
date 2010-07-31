<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!-- struts2 styled error page -->

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<html>
<head>
    <title>CHEMBENCH | Error</title>
    
    <link href="theme/ss.css" rel="stylesheet" type="text/css" />
	<link href="theme/ajaxtabs.css" rel="stylesheet" type="text/css" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
	<link href="theme/links.css" rel="stylesheet" type="text/css" />
	<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
	<link rel="icon"  href="theme/img/mml.ico" type="image/ico" />
	<link rel="SHORTCUT ICON" href="theme/img/mml.ico" />
	
	<script language="javascript" src="javascript/script.js"></script>
</head>

<body bgcolor="#ffffff">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
	
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrmodelbuilders.jpg" style="background-repeat: no-repeat;"><span id="maincontent">
		
		<table width="557" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
				<p class="StandardTextDarkGrayParagraph"><b><br>Error: </b></p>
		<p class="StandardTextDarkGrayParagraph">
		<s:iterator value="errorMessages"><s:property /><br /><br /></s:iterator><br><br>
		To report a bug, or if you need help with Chembench, you can reach us at <a href="ceccr@email.unc.edu">ceccr@email.unc.edu</a>. <br />
		Include this error text in your email, along with a description of the problem.<br />
		Thanks, <br />
		The Chembench Team
		</p>
		</td>
          </tr>
        </table>
        

<br />

<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html>        
 
