<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>

<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<jsp:useBean class="edu.unc.ceccr.persistence.User" id="user" 	scope="session"></jsp:useBean>

<html:html>
<head>
<title>C-CHEMBENCH | Library Design</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" 	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<script src="javascript/script.js"></script>
</head>
<body>
<table width="749" border="0" align="center" cellpadding="0"	cellspacing="0">
<%@include file="/jsp/main/header.jsp" %>
<%@include file="/jsp/main/centralNavigationBar.jsp" %>
</td>
		</span>
	</tr>
	<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrlibrary.jpg">
<table width="465" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
		<p align="justify" class="StandardTextDarkGrayParagraph"><br />
		
		<b>Coming Soon: C-ChemBench Library Design</b><br/><br/>
		
		'CECCR Library Design' provides a comprehensive set of tools that can be used to enumerate virtual libraries, computationally profile virtual compound sets, and
		select subsets of compounds for <strong>synthesis and/or screening</strong>. <br/><br/>CECCR Library Design uses a multiobjective optimization algorithm to
		select <strong>compound subsets</strong> or <strong>plate subsets</strong> by optimizing selected objectives such as diversity, similarity to
		leads, fit to QSAR and other activity models as well as ADMET properties.</p><br/><br/>
		
		
		<p class="StandardTextDarkGray" ><a href="application.do"><font color="blue"><u>Download Applications</u></font></p>
		
				</td>
          </tr>
        </table>  
		
		
		</td>
		</span>
	</tr>
	
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
