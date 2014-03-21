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
    <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/smoothness/jquery-ui.min.css" />

    <script src="javascript/script.js"></script>
    <script src="javascript/admin.js"></script>
    <script language="JavaScript" src="javascript/sortableTable.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>

    <script type="text/javascript">
        function checkContent() {
            if (document.getElementById("content").value == "") {
                return (window.confirm("Send emails without content?"));
            } else {
                return true;
            }
        }

        $(document).ready(function() {
            $("#tabs").tabs();
        });
    </script>
</head>
<body onload="setTabToHome();">

<!-- headers -->
<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
<div class="outer">
    <div class="includesHeader"><%@include file="/jsp/main/header.jsp" %></div>
    <div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp" %></div>

    <!--  page content -->
    <div class="border StandardTextDarkGrayParagraph benchAlign bottomMargin">
        <script>
        </script>

        <div id="tabs">
            <ul>
                <li>
                    <a href="#message">Messages</a>
                </li>
                <li>
                    <a href="adminPredictors">Datasets &amp; Predictors</a>
                </li>
                <li>
                    <a href="adminUsers">Users</a>
                </li>
                <li>
                    <a href="adminJobs">Jobs</a>
                </li>
            </ul>

            <div id="message" class="StandardTextDarkGrayParagraph">
                <b class="StandardTextDarkGrayParagraph2">
                Add new global message:
                </b>
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
    </div>
    <div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>
</div>

</body>
</html>

