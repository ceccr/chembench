<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<jsp:useBean class="edu.unc.ceccr.persistence.User" id="user" 	scope="session"></jsp:useBean>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<html:html>
<head>
<title>CHEMBENCH | library design</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" 	type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css"/>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<script src="javascript/script.js"></script>
<script src="javascript/miscellaneous.js"></script>
<script>
  function login() {
	window.alert("Please login.");
}
</script>
</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br />
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<span id="maincontent">
		<td height="500" colspan="5" background="theme/img/backgrlibrary.jpg"><div style="margin-left:100px; height:300px">
            <table style="border-style:dotted dashed;border-color:#ffff33;border-width:2px; width:550px; height:100px">
<tr><td>
		<div class="TextDarkGray">You are currently logged in as:<b> <bean:write name="user" property="userName" /></b> </div></td></tr>
<tr><td>

		<div class="TextDarkGray">To continue working, please <a href="home.do"><font size="3" color="#ff3300">click here</font></a></div></td></tr>
	<tr><td>
	<div class="TextDarkGray">To login as another user, please <a href="logout.do"><font size="3" color="#ff3300"><b>click here</b></font></a> to close this session. </div>
            </td></tr></table></div>
		</td>
		</span>
	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
