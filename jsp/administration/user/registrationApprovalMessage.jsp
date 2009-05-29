<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<jsp:useBean class="edu.unc.ceccr.persistence.User" id="userInfo" 	scope="session"></jsp:useBean>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<html:html>
<head>
<title>C-CHEMBENCH | User Added</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css"/>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<script src="javascript/script.js"></script>
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
		<td height="557" colspan="5" valign="top"		background="theme/img/backgrlibrary.jpg">
		<p class="StandardTextDarkGray">&nbsp;</p>
		<p class="StandardTextDarkGray">
		</p>
		<p class="TextDarkGray" sytle="background:white;"><font size=4><bean:write name="userInfo" property="firstName"/>,</font><br/><br/>
		&nbsp&nbsp&nbsp&nbsp&nbsp;Thank you for your interest in <a href="login.do">CECCR</a>. 
		
		<% if(Constants.ACCEPTANCE.equalsIgnoreCase("manual")){%>
		Your application has been submitted.<br/><br/>
		&nbsp&nbsp&nbsp&nbsp&nbsp;An email will be sent to <font color="#FF3300">
		<bean:write name="userInfo" property="email"/></font> with your password or information about any <br/>
            &nbsp&nbsp&nbsp&nbsp&nbsp;problems that are encountered. If you have any question, please send email to: <a href="mailto:register@ceccr.ibiblio.org">
            <font color="#FF3300">register@ceccr.ibiblio.org</font></a><br/><br/><br/><br/>
            <%}%>
            
            <% if(Constants.ACCEPTANCE.equalsIgnoreCase("automatic")){%>Your application has been approved.<br/><br/>
		&nbsp&nbsp&nbsp&nbsp&nbsp;An email has been sent to <font color="#FF3300"><bean:write name="userInfo" property="email"/></font> with your password  <br/>
            &nbsp&nbsp&nbsp&nbsp&nbsp;Please check your email and log in to <b>CECCR</b>.<br/><br/><br/><br/>
            
            <%}%>
            
	     &nbsp&nbsp&nbsp&nbsp&nbsp; Thank you.<br/><br/>
		
		&nbsp&nbsp&nbsp&nbsp&nbsp;The C-Chembench Team.
		<br/>
		<br/></p>
				
		<p class="StandardTextBlack">&nbsp;</p>
		</td>
		</span>
	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
