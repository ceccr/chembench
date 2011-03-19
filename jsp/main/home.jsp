<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<%@ page import="edu.unc.ceccr.utilities.ActiveUser" %>
<% ActiveUser au= new ActiveUser();%>
<%@ page import="edu.unc.ceccr.utilities.Utility" %>
<%@ page import="edu.unc.ceccr.persistence.User"%>
<% Utility u=new Utility();%>

<html:html>
<head>
<title>CHEMBENCH | Home </title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="/theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" src="javascript/script.js"> </script>
 
</head>
<body onload="setTabToHome();">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br />
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td width="407" height="665" align="left" valign="top"><p class="ccbHomeHeadings">
      Chembench is currently down for maintenance. Service will return on March 20th at 10 PM EST. Thank you for your patience.<br />
        <br />
        <img src="/theme/ccbTheme/images/ccbHorizontalRule.jpg" width="407" height="6" /></p>
      
</table>

	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
