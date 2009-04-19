<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib prefix="c" uri="/application/jstl/core"%>
<%@ page isELIgnored="false" %>

<%@ page import="edu.unc.ceccr.global.Constants" %>
<html:html>
<head>
<title>C-CHEMBENCH | Files submited successfully!</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<script src="javascript/script.js"></script>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
</head>
<body>
<table width="924px" border="0" align="center" cellpadding="0"	cellspacing="0">
	<%@include file="/jsp/main/header.jsp" %>
<%@include file="/jsp/main/centralNavigationBar.jsp" %>
		</td>
		</span>
	</tr>
		<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrlibrary.jpg">
		<p class="StandardTextDarkGray">&nbsp;</p>
		<p class="StandardTextDarkGray"><strong>Error</strong>		</p>
		<p class="StandardTextDarkGrayParagraph">Generation of visualization files process stopped due to error:</p>
		&nbsp;&nbsp;<font size=3 color='blue'><b><u><%=request.getAttribute("message")%></u></b></font><br/><br/><br/><br/>
		 </td>
		</span>
	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
