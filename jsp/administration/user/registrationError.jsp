<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<html:html>
<head>
<title>C-CHEMBENCH | User Registration</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<script src="javascript/script.js"></script>

</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br />
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">

	<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrlibrary.jpg">
		<p class="StandardTextDarkGray">&nbsp;</p>
		<p class="StandardTextDarkGray"><font size=4 color=red><b><!-- ERROR: Email didn't get sent. --><b/></font><br/>
		</p>
		<p class="StandardTextDarkGrayParagraph">
		<%if((String)session.getAttribute("error1")!=null){%>
		<%=(String)session.getAttribute("error1")%><%}%><br/><br/>
      	<%if((String)session.getAttribute("error2")!=null){%>
		<%=(String)session.getAttribute("error2")%><%}%><br/><br/>
      	<%if((String)session.getAttribute("error3")!=null){%>
		<%=(String)session.getAttribute("error3")%><%}%><br/><br/><br/><br/><br/><br/><br/>
      	
      	<form action="home.do">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      	<input type="submit" value="Return to Home Page" /></form>
      	<form action="gotoregister.do">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      	<input type="submit" value="Back to Registration" /></form>
		
        </p>
		<p class="StandardTextBlack">&nbsp;</p>
		</td>
		</span>
	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
