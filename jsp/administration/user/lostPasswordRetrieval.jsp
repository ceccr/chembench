<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<html:html>
<head>
<title>C-CHEMBENCH | Registration </title>
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
<script src="javascript/yahoo/yahoo.js"></script>
<script src="javascript/dom/dom.js"></script>
<script src="javascript/event/event.js"></script>
<script src="javascript/container/container_core.js"></script>
<script src="javascript/registerFormValidation.js"></script>

</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br/>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<span id="maincontent">
		<td height="557" colspan="2" valign="top"
			background="theme/img/backgrindex.jpg">
<table><tr><td class="ChangePSText">
<form action="retrievePassword.do" >
		<b><br/><br/>Did You Forget Your Password?</b>
		<br/><br/>
          &nbsp &nbsp   Your current username: <br/>
		 &nbsp &nbsp <input type="text" size="20" name="userName">
		 <br/><br/>
           &nbsp &nbsp Your email address: <br/>
		    &nbsp  &nbsp <input type="text" size="35" name="email"> &nbsp &nbsp<input type="submit" value="Submit" ></form>
     </td></tr></table>
        
     <!--  end of the fix. -->    
        
		</td>
		</span>
	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
