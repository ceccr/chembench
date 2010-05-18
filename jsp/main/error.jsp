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
<title>CHEMBENCH | Error</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<script src="javascript/script.js"></script>
</head>

<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
		<tr>
		<span id="maincontent">
		<td height="557" valign="top"
			background="theme/img/backgrlibrary.jpg">
		<p class="StandardTextDarkGray">&nbsp;</p>
		<c:if test="${hasPredictions eq ''}">
		<p class="StandardTextDarkGray"><strong>ERROR</strong>		</p>
		<p class="StandardTextDarkGrayParagraph">An error has occured.</p>
		</c:if>
		  
		<p class="StandardTextBlack">&nbsp;<br/><br/><br/><br/>
          <html:errors />
          
          <logic:equal name="user" property="userName" value="">

            <a href="login.do"><font size=4 color='red'><b><u>BACK</u></b></font></a></p>
		</logic:equal>
		
		<logic:present name="validationMsg">
		
		${validationMsg} <br/><br/><br/>
		
		<a href="modelbuilders.do"><font size=3 color='blue'><b><u>BACK TO MODELBUILDING</u></b></font></a>&nbsp;&nbsp;&nbsp;<a href="manageFile.do"><font size=3 color='blue'><b><u>BACK TO DATASET</u></b></font></a></p><br/><br/><br/><br/>
        <p class="StandardTextDarkGray">
         <a href="http://www.edusoft-lc.com/molconn/manuals/350/chapsix.html" target="_blank"><u>SD & ACT file format reference</u></a></p>
		</logic:present>
		
		
        <c:if test="${hasPredictions ne ''}">
        
		${hasPredictions} <br/><br/><br/>
		</c:if>
            </td>
		</span>
	</tr>
</table>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
