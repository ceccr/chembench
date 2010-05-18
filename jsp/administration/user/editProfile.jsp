<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
 
    <title>CHEMBENCH | Edit Profile</title>
    
    <link href="theme/ss.css" rel="stylesheet" type="text/css" />
	<link href="theme/ajaxtabs.css" rel="stylesheet" type="text/css" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
	<link href="theme/links.css" rel="stylesheet" type="text/css" />
	<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
	<link rel="icon"  href="theme/img/mml.ico" type="image/ico" />
	<link rel="SHORTCUT ICON" href="theme/img/mml.ico" />
	
	<script language="javascript" src="javascript/script.js"></script>
	<script language="javascript" src="javascript/modeling.js"></script>
	<script src="javascript/predictorFormValidation.js"></script>
	<script language="javascript" src="javascript/editProfileAndSettings.js"></script>
	
</head>
<body onload="setTabToHome();">

<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>

<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td colspan="5" valign="top"  background="theme/img/backgrmodelbuilders.jpg">
      <table width="465" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td><p class="StandardTextDarkGrayParagraph"><b><br />
            Edit Profile</b></p>
              <p align="justify" class="StandardTextDarkGrayParagraph">
              From this page, you may change your password, edit your user information, or select options to customize Chembench. <br />
              <br /><br />
			   <!-- ADMIN SETTINGS LINK -->
			   <s:if test="userIsAdmin">
			   You are currently logged in with administrative rights. You may <a href="/admin.do">go to the Administrator Settings page.</a>
			   </s:if>
			   <!-- END ADMIN SETTINGS LINK -->
            </p></td>
        </tr>
        <tr><td>
        <p class="StandardTextDarkGrayParagraph">
        <font color='red'><s:iterator value="errorMessages">
        <s:property /><br />
        </s:iterator></font>
        </p><br />
        </td></tr>
      </table>
   </td>
  </tr>
  <tr><td colspan="2">
		
	<table width="915" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
   	<sx:tabbedpanel id="editProfileTabs" >
   	
    	<sx:div href="/loadChangePasswordSection" label="Change Password" theme="ajax" loadingText="Loading password change form...">
		</sx:div>
		
		<sx:div href="/loadUpdateInfoSection" label="Update Info" theme="ajax" loadingText="Loading user information...">
		</sx:div>
		
		<sx:div href="/loadUserOptionsSection" label="Options" theme="ajax" loadingText="Loading options...">
		</sx:div>
		
   	</sx:tabbedpanel>
   	</td></tr></table>
   	</td></tr>
   	</tbody>
   </table>
<br />

<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html>