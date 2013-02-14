<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<html>
<head>
<title>CHEMBENCH | New User Registration</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css"/>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<script src="javascript/script.js"></script>
</head>

<body>
<div class="outer">

    <div class="includesHeader"><%@include file="/jsp/main/header.jsp" %></div>
    <div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp" %></div>
    <div class="homeLeft topMarginBench">
		<span id="maincontent">
		
		<p class="StandardTextDarkGray">&nbsp;</p>
		<p class="StandardTextDarkGray">
		</p>
		<p class="TextDarkGray" sytle="background:white;"><br /><s:property value="firstName" />,<br/><br/>
		Thank you for your interest in Chembench. 
		
		<s:property value="outputMessage" />
		<br /><br />
		While you wait, you might like to read the <a href="help-overview">overview of Chembench</a>.
		<br /><br />
		Enjoy,<br/>
		The Chembench Team.
		<br/>
		<br/></p>
		<p class="StandardTextBlack">&nbsp;</p>
		</span>
    </div>
    <div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>

    </div>
</body>
</html>
