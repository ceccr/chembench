<!DOCTYPE html>

<!-- struts2 styled error page -->

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<html>
<head>
    <title>CHEMBENCH | Error</title>

	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link href="theme/ccbStyle.css" rel="stylesheet" type="text/css">
    <link href="theme/ccbStyleNavBar.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="theme/screen.css" type="text/css" media="screen, projection">
    <link rel="stylesheet" href="theme/print.css" type="text/css" media="print">
    <link href="theme/standard.css" rel="stylesheet" type="text/css">
    <link href="theme/links.css" rel="stylesheet" type="text/css">
    <link href="theme/dynamicTab.css" rel="stylesheet" type="text/css">
    <link rel="icon" href="/theme/img/mml.ico" type="image/ico">
    <link rel="SHORTCUT ICON" href="/theme/img/mml.ico">
    <link href="theme/customStylesheet.css" rel="stylesheet" type="text/css">
	
	<script language="javascript" src="javascript/script.js"></script>
</head>

<body bgcolor="#ffffff">
<div class="outer">

    <div class="includesHeader"><%@include file="/jsp/main/header.jsp" %></div>
    <div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp" %></div>


    <p class="StandardTextDarkGrayParagraph"><b><br>Error: </b></p>
		<p class="StandardTextDarkGrayParagraph">
		<s:iterator value="errorStrings"><s:property /><br /><br /></s:iterator><br><br>
		To report a bug, or if you need help with Chembench, you can reach us at <a href="ceccr@email.unc.edu">ceccr@email.unc.edu</a>. <br />
		Include this error text in your email, along with a description of the problem.<br />
		Thanks, <br />
		The Chembench Team
		</p>
        

<br />

    <div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>
    </div>
</body>
</html>        
 
