<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <title>Chembench | Password Reset</title>
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

</head>
<body>
<!--  Navigation Bars  -->
<div class="outer">

  <div class="includesHeader">
    <%@ include file="/jsp/main/header.jsp" %>
  </div>

  <!--  main content -->
  <div class="StandardTextDarkGrayParagraph">
    Your password has been reset. <br /> An email containing the password has been sent to <font color="red"><s:property
      value="email" /></font>.<br /> When the email arrives, you'll want to return to <a href="home">Home page</a> and
    log in.<br /> You may change your password from the 'edit profile' page when you are logged in.<br />
    <br />
  </div>
  <div class="includes">
    <%@ include file="/jsp/main/footer.jsp" %>
  </div>

</div>
</body>
</html>



