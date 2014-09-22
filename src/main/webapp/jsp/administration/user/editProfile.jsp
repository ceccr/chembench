<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page language="java" import="java.util.*"%>

<html>
<head>
<sx:head debug="false" cache="false" compressed="true" />

<title>CHEMBENCH | Edit Profile</title>

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
<script language="javascript" src="javascript/modeling.js"></script>
<script src="javascript/predictorFormValidation.js"></script>
<script language="javascript" src="javascript/editProfileAndSettings.js"></script>

</head>
<body onload="setTabToHome();">

  <div id="bodyDIV"></div>
  <!-- used for the "Please Wait..." box. Do not remove. -->
  <div class="outer">
    <div class="includesHeader"><%@include file="/jsp/main/header.jsp"%></div>
    <div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp"%></div>

    <div>
      <br />
    </div>
    <p class="StandardTextDarkGrayParagraph">
      <b><br /> Edit Profile</b>
    </p>
    <p align="justify" class="StandardTextDarkGrayParagraph">
      From this page, you may change your password, edit your user information, or select options to customize
      Chembench. <br /> <br />
      <br />
    </p>
    <p class="StandardTextDarkGrayParagraph">
      <font color='red'><s:iterator value="errorMessages">
          <s:property />
          <br />
        </s:iterator></font>
    </p>
    <br />

    <sx:tabbedpanel id="editProfileTabs">

      <sx:div href="/loadChangePasswordSection" label="Change Password" theme="ajax"
        loadingText="Loading password change form...">
      </sx:div>

      <sx:div href="/loadUpdateInfoSection" label="Update Info" theme="ajax" loadingText="Loading user information...">
      </sx:div>

      <sx:div href="/loadUserOptionsSection" label="Options" theme="ajax" loadingText="Loading options...">
      </sx:div>

    </sx:tabbedpanel>
    <br />

    <div class="includes"><%@include file="/jsp/main/footer.jsp"%></div>
  </div>
</body>
</html>