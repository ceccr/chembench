<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="edu.unc.ceccr.global.Constants" %>
<jsp:useBean id="user" class="edu.unc.ceccr.persistence.User" scope="session"/>
<html:html>
<head>
<title>CHEMBENCH | Virtual Pubchem</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<script src="javascript/script.js"></script>
</head>
<body onload="setTabToCeccrBase(); selection()">
<table width="749" border="0" align="center" cellpadding="0" cellspacing="0">
<%@include file="/jsp/main/header.jsp" %>
<%@include file="/jsp/main/centralNavigationBar.jsp" %></td>
		</span>
	</tr>
      <tr><span id="maincontent">
        <td height="600" colspan="5" valign="top" background="theme/img/backgrvpubchem.jpg">
      

 <table width="100%" border="0">
<tr><td height="70">
       <p class="StandardTextDarkGrayParagraph" >
		<b>CECCR Base</b> is a tool to search through PubChem data and view it in new ways. Additionally, CECCR Base will store predicted properties from all models validated by the Laboratory for Molecular Modeling at UNC-CH. 
		</p>
          <p class="StandardTextBlack">&nbsp;</p>
       </td></tr>
<tr><td height="500">

<%@include file="/jsp/ceccrBase/ceccrBase.jsp" %>

</td></tr>



 </td>
      </span></tr>
	<tr><td height="80" colspan="10"></td></tr>

<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>