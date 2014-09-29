<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<html>
<head>
<sx:head debug="false" cache="false" compressed="true" />

<title>Chembench | Edit Profile</title>
<%@ include file="/jsp/main/head.jsp"%>
<script src="javascript/Chembench.js"></script>
<script src="javascript/editProfileAndSettings.js"></script>

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