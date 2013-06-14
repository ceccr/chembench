<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<%@ page language="java" import="java.util.*" %>

<html>
<head>
    <sx:head debug="false" cache="false" compressed="true" />
    <title>CHEMBENCH | Administration</title>
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

    <script src="javascript/script.js"></script>
    <script src="javascript/admin.js"></script>
    <script language="JavaScript" src="javascript/sortableTable.js"></script>
    <script language="javascript" src="javascript/jquery-1.6.4.min.js"></script>

    <script type="text/javascript">
        function checkContent()
        {
            if(document.getElementById("content").value=="")
            {return (window.confirm("Send emails without content?"));}
            else{return true;}
        }
    </script>
</head>
<body onload="setTabToHome();">

<!-- headers -->
<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
<div class="outer">
    <div class="includesHeader"><%@include file="/jsp/main/header.jsp" %></div>
    <div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp" %></div>

    <div><br /></div>
    <!--  page content -->
    <div class="border StandardTextDarkGrayParagraph benchAlign bottomMargin">


        <div id="horizontalmenu">
            <div class="adminlink"><a href="admin">Messages</a></div>
            <div class="adminlink"><a href="adminPredictors">Predictors</a></div>
            <div class="adminlink"><a href="adminUsers">Users</a></div>
            <div class="adminlink"><a href="adminJobs">Jobs</a></div>
        </div>

        <div class="StandardTextDarkGrayParagraph" style="border:#000 solid 1px;">
            Add new global message:<br />
            <form action="globalNotifAdd">
                <table width="680" border="0">
                    <tr><td>Start Date:</td><td><s:textfield name="startDate" value="" size="43" theme="simple" /></td></tr>
                    <tr><td>End Date:</td><td><s:textfield name="endDate" value="" size="43" theme="simple" /></td></tr>
                    <tr><td>Message:</td><td><s:textfield name="message" value="" size="43" theme="simple" /></td></tr>
                    <tr><td></td><td><input type="submit" value="Add" /></td></tr>
                </table>
            </form>
        </div>


    </div>
    <div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>
</div>

</body>
</html>