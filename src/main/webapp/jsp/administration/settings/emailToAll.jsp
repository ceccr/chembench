<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>


<html>
<head>
  <title>Email to All Users</title>
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

  <script src="javascript/chembench.js"></script>
  <script type="text/javascript">
    function checkContent() {
      if (document.getElementById("content").value == "") {
        return (window.confirm("Send emails without content?"));
      } else {
        return true;
      }
    }
  </script>
</head>
<body>

<div class="outer">
  <div class="includesHeader">
    <%@include file="/jsp/main/header.jsp" %>
  </div>
  <div class="includesNavbar">
    <%@include file="/jsp/main/centralNavigationBar.jsp" %>
  </div>

  <div>
    <br />
  </div>
  <form action="emailAllUsers">
    <b>Emails need to have HTML markup in them or they will look silly.</b><br />
    <br />
    <table width="480" height="480" border="0">
      <tr>
        <td width="35" height="18">From:</td>
        <td>ceccr@email.unc.edu</td>
      <tr>
      <tr>
        <td width="60" height="18">Send to:</td>
        <td><s:radio name="sendTo" id="sendTo" value="sendTo"
                     list="#{'JUSTME':'Just Me (for testing)','ALLUSERS':'All users<br />'}" theme="simple" /></td>
      <tr>
      <tr>
        <td width="35" height="18">Subject:</td>
        <td><s:textfield name="emailSubject" value="" size="43" theme="simple" /></td>
      <tr>
      <tr>
        <td height="160" colspan="2">&nbsp;<s:textarea name="emailMessage" value="" rows="10" cols="45"
                                                       theme="simple" /></td>
      <tr>
      <tr>
        <td width="35" height="18"></td>
        <td><input type="submit" onclick="return checkContent()" value="Send" /></td>
      <tr>
    </table>
    <br />
  </form>

  <div class="includes">
    <%@include file="/jsp/main/footer.jsp" %>
  </div>
</div>
</body>
</html>