<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
<title>CHEMBENCH | Reset Password </title>
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css"/>
<link href="theme/ss.css" rel="stylesheet" type="text/css"></link>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/yahoo_ui/menu.css" rel="stylesheet" type="text/css"></link>
<link href="theme/yahoo_ui/text.css" rel="stylesheet" type="text/css"></link>
<link href="theme/yahoo_ui/container/container.css" rel="stylesheet"	type="text/css"></link>
<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>

<script src="javascript/script.js"></script>

</head>
<body>
<!-- Navigation bar -->
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br/>

<!-- Main page -->
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<span id="maincontent">
		<td height="557" colspan="2" valign="top" background="theme/img/backgrindex.jpg">
			<table>
				<tr>
					<td class="ChangePSText">
						<form action="resetPassword" ><br/>
							<b>Reset Your Password</b><br/><br/>
	          				Your username: <br/>
	          				<s:textfield name="userName" size="20" />
			 				<br/><br/>
	           				Your email address: <br/>
	         				<s:textfield name="email" size="35" />
			    			<br /><br />
			    			<input type="submit" value="Submit" >
			    			<br /><br />
			    			<s:property value="errorMessage" />
		    			</form>
     				</td>
     			</tr>
     		</table>
		</td>
		</span>
	</tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>
</html>
