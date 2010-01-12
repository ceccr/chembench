<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
 
    <title>C-CHEMBENCH | Edit Profile</title>
    
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
<body>

<!-- CHANGE PASSWORD -->

<div id="changePassword">
<s:form action="changePassword" enctype="multipart/form-data" theme="simple">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
   <tr>
   <td height="25" colspan="17" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
     <tr><td height="80"></td></tr>
     <tr><td width="200" height="40" valign="middle" align="right" class="ChangePSText">User Name</td>
     <td width="218" align="left" ><s:textfield name="newUserName" size="27" /></td><td width="300"></td></tr>
    
	<tr><td width="200" height="40" valign="middle" align="right" class="ChangePSText" >Password</td>
    <td width="218" align="left" ><s:textfield name="oldPassword" size="27" /></td><td width="300" ></td></tr>

	<tr><td width="200" height="40" valign="middle" align="right" class="ChangePSText" >New Password</td>
    <td width="218" align="left" ><s:textfield name="newPassword" id="confirmNewPassword" size="27" /></td><td width="300" ></td></tr>

	<tr><td width="200" height="40" valign="middle" align="right"  class="ChangePSText">Confirm New Password</td>
    <td width="218" align="left" ><s:textfield name="confirmNewPassword" id="confirmNewPassword" size="27" /></td><td width="300" align="left"><div id="passwordMatchin"></div></td></tr>

<tr><td width="200" height="40" valign="middle" align="right" ></td>
<td width="218" align="right" ><input type="submit" value="Submit" onclick="return validateMatchingPasswords()"/></td><td width="300" ></td>
</tr>

</td></tr></table>
</s:form>
</div>

<!-- END CHANGE PASSWORD -->

<!-- UPDATE USER INFO -->

<div id="updateUserInfo">
<table width="465" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
		<p align="justify" class="StandardTextDarkGrayParagraph"><br />
		<br/><br/>This is the information collected when you first registered for C-Chembench. 
		Update any fields you need to, then click "Submit". An asterisk (*) indicates required fields.<br/>
        </td>
          </tr>
        </table>       

       <s:form action="updateUserInfo" enctype="multipart/form-data" theme="simple">
	
        <table border="0" align="center" width="680">
  
<!-- error message (if any) -->
<tr height="20"><td colspan="2" class="StandardTextDarkGray"><div class="StandardTextDarkGray"><font color="red"><br />
<s:iterator value="errorMessages"><s:property /><br /></s:iterator></font></div></td>
<td width="250" align="left"></td></tr>
  
<!-- user information form -->
<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">First Name *</td>
<td width="250"><s:textfield name="firstName" size="30"/></td><td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">Last Name *</td>
<td width="250"><s:textfield name="lastName" size="30"/></td><td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">Type of Organization *</td>
<td width="250"><s:select name="organizationType" list="#{'Academia':'Academia','Government':'Government','Industry':'Industry','Nonprofit':'Nonprofit','Other':'Other'}" />
</td><td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">Name of Organization *</td>
<td width="250"><s:textfield name="organizationName" size="30"/></td><td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">Position in Organization *</td>
<td width="250"><s:textfield name="organizationPosition" size="30"/></td><td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">Address</td>
<td width="250"><s:textfield name="address" size="30"/></td><td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">City *</td>
<td width="250"><s:textfield name="city" size="30"/></td><td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">State/Province</td>
<td width="250"><s:textfield name="stateOrProvince" size="30"/></td><td width="250" align="left"></td></tr>
	
<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">Zip Code</td>
<td width="250"><s:textfield name="zipCode" size="30"/></td><td width="250" align="left"></td></tr>
	
<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">Country *</td>
<td width="250"><s:textfield name="country" size="30"/></td><td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">Phone Number</td>
<td width="250"><s:textfield name="phoneNumber" size="30"/></td><td width="250" align="left"></td></tr>

<tr height="6"><td width="180" class="StandardTextDarkGray"></td><td width="250"><div class="StandardTextDarkGray"><i><small><br />Please use your organization email account.
Your password will be sent to this email address when you register.</small></i></div></td><td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">Email *</td>
<td width="250"><s:textfield name="email" size="30"/></td><td width="250" align="left"></td></tr>

<!-- 
<tr height="20"><td align="right" width="180" class="StandardTextDarkGray">Work Bench</td>
<td width="250"><s:radio name="workBench" list="#{'cchem':'C-CHEM','ctox':'C-TOX'}" /></td>
 -->
 <!-- The idea of having a separate workbench for tox people and for chem people may come back someday. Removed it for now. -->
<s:hidden name="workbench" value="cchem" /> 

<tr height="20"><td align="right" width="180"></td>
<td width="250">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
<input type="button" name="userAction" id="userAction" onclick="this.form.submit()" value="Submit" />
</td>
</tr>
</table>
</s:form>
</div>

<!-- END UPDATE USER INFO -->

<!-- USER OPTIONS -->

<s:form action="changeUserOptions" enctype="multipart/form-data" theme="simple">
<table border="0" align="center" width="680">
 
<tr>
	<td width="100%" height="24" align="left" colspan="2">
	<p class="StandardTextDarkGrayParagraph">
	<i>Chembench provides sample datasets and predictors for you to experiment with. <br />If you choose
	to hide them, they will no longer appear on the My Bench, Modeling, and Prediction pages.</i>
	</p> 
	</td>
</tr>  

<tr>
	<td>
	<div class="StandardTextDarkGrayParagraph"><b>Show Public Datasets:</b></div></td>
	<td align="left" valign="top">
	<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="showPublicDatasets" value="showPublicDatasets" list="#{'NONE':'None','SOME':'Some','ALL':'all'}" /></div>
	</td>
</tr>

<tr>
	<td>
	<div class="StandardTextDarkGrayParagraph"><b>Show Public Predictors:</b></div></td>
	<td align="left" valign="top">
	<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="showPublicPredictors" value="showPublicPredictors" list="#{'NONE':'None','ALL':'all'}" /></div>
	</td>
</tr>	

<tr>
	<td></td>
	<td class="" valign="top"><input type="button" name="userAction" id="userAction" onclick="this.form.submit()" value="Submit" /> 
	<span id="textarea"></span> <br /></td>
</tr>

</table>
</s:form>

<!-- END USER SETTINGS -->

<!-- ADMIN SETTINGS -->
<s:if test="userIsAdmin">
<a href="/admin">Go to the Administrator Settings page.</a>
</s:if>

<!-- END ADMIN SETTINGS -->

</body>
