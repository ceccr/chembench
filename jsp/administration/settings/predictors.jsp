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
            Make Predictor public:<br />
            <form action="makePredictorPublicAction">
                <table width="680" border="0">
                    <tr><td>Predictor name:</td><td><s:textfield name="predictorName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td>User name:</td><td><s:textfield name="userName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td>Predictor type:</td><td><s:textfield name="predictorType" value="Toxicity" size="43" theme="simple" /></td></tr>
                    <tr><td></td><td><input type="submit" value="Change" /></td></tr>
                </table>
            </form>
        </div>

        <div class="StandardTextDarkGrayParagraph" style="border:#000 solid 1px;">
            Delete Public Dataset:<br />
            <form action="deletePublicDataseAction">
                <table width="680" border="0">
                    <tr><td>Dataset ID:</td><td><s:textfield name="datasetName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td>User name:</td><td><s:textfield name="userName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td></td><td><input type="submit" value="Delete" /></td></tr>
                </table>
            </form>
        </div>

        <div class="StandardTextDarkGrayParagraph" style="border:#000 solid 1px;">
            Delete Public Predictor:<br />
            <form action="deletePublicPredictorAction">
                <table width="680" border="0">
                    <tr><td>Predictor ID:</td><td><s:textfield name="predictorName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td>User name:</td><td><s:textfield name="userName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td></td><td><input type="submit" value="Delete" /></td></tr>
                </table>
            </form>
        </div>

        <div class="StandardTextDarkGrayParagraph" style="border:#000 solid 1px;">
            Delete Public Prediction:<br />
            <form action="deletePublicPredictionAction">
                <table width="680" border="0">
                    <tr><td>Prediction ID:</td><td><s:textfield name="predictionName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td>User name:</td><td><s:textfield name="userName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td></td><td><input type="submit" value="Delete" /></td></tr>
                </table>
            </form>
        </div>

        <div class="StandardTextDarkGrayParagraph">
            <a href="#" onclick="window.open('/emailToAll','emailToAll','width=1000,height=700')">Send email to all users</a> (opens in a new window)
            <br />
            <a href="#" id="sendToAll">Send email to selected users</a>
            <br />
            <div id="sendEmail" style="display: none;background-color: silver;border: 1px;">
                <form action="emailSelectedUsers">
                    <b>Emails need to have HTML markup in them or they will look silly.</b><br /><br />
                    <table width="480" border="0">
                        <tr><td width="35" height="18">From:</td><td>ceccr@email.unc.edu</td></tr>
                        <tr><td width="60" height="18">Send to:</td><td><s:textarea value="" theme="simple" id="sendTo" name="sendTo"/></td></tr>
                        <tr><td width="35" height="18">Subject:</td><td><s:textfield name="emailSubject" value="" size="43" theme="simple" /></td></tr>
                        <tr><td height="160" colspan="2"><s:textarea name="emailMessage" value="" rows="10" cols="45" theme="simple" /></td></tr>
                        <tr><td width="35" height="18"></td><td><input type="submit" onclick="return checkContent()" value="Send" /></td></tr>
                    </table>
                    <br />
                </form>
            </div>
        </div>
        <br /><br />

    </div>
    <div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>
</div>
</body>
</html>