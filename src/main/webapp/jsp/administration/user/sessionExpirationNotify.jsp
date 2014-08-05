<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<%@ page import="edu.unc.ceccr.global.Constants" %>

<html>
<head>
<title>CHEMBENCH | Session Expired</title>
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
    <script src="javascript/script.js"> </script>
<script src="javascript/miscellaneous.js"></script>
</head>
<body>
<div class="outer">

    <div class="includesHeader"><%@include file="/jsp/main/header.jsp" %></div>
    <div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp" %></div>


    <div class="benchBackground includes StandardTextDarkGrayParagraph homeLeft">
                <p>
                    You are seeing this page because you are currently not logged in, or your session has expired.
                    <br /><br />
                    If you need an account, you can make one from the <a href="home">Home</a> page. Creating an account is quick and free.
                    <br /><br />
                    If you already have an account, you can log in from the below form as well.
                    <br /><br />
                    Thanks for using Chembench! If you encounter any problems, please contact us at ceccr@email.unc.edu.
                </p>
                <p>You are currently logged in as: <b><s:property value="#session['user'].userName" /></b></p>
                <p>To continue working, please <a href="home"><font size="3" color="#ff3300">click here</font></a></p>
	            <p>To login as another user, please <a href="logout"><font size="3" color="#ff3300"><b>click here</b></font></a> to log out.</p>

             </div>
        <div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>
       </div>
</body>
</html>
