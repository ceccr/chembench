<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
<title>CHEMBENCH | Administration</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="/theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>

<script src="javascript/script.js"></script>
<script src="javascript/admin.js"></script>

</head>
<body onload="setTabToHome();">

	<!-- headers -->
	<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
	<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
	<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>

	<!--  page content -->
	<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
   		<tr><td>
 <div class="StandardTextDarkGrayParagraph">
	Build Date: <s:property value="buildDate" /><br />
	<br />
	Documentation: <br />
	<a href="/documentation/C-Chembench Developer Guide.docx">Chembench Developer's Guide</a><br />
	<a href="/documentation/Database Design.docx">Database Design</a><br />
	<a href="/documentation/ceccr design notes.txt">Notes</a><br />
	<a href="/documentation/Install Guide.txt">Install Guide</a><br />
	<br /><br />
</div> 

<div class="StandardTextDarkGrayParagraph">
	<a href="#" onclick="window.open('/emailAll','emailAll','width=1000,height=700')">Send email to all users</a> (opens in a new window)
	<br />
</div> 
<br /><br />

<table>
	<tr>
		<th class="TableRowText01">User Name</th>
		<th class="TableRowText01">First Name</th>
		<th class="TableRowText01">Last Name</th>
		<th class="TableRowText01">Organization</th>
		<th class="TableRowText01">Country</th>
		<th class="TableRowText01">email</th>
		<th class="TableRowText01">Can Download Descriptors</th>
		<th class="TableRowText01">Administrator</th>
		<th class="TableRowText01">Delete</th>
	</tr>
	<s:iterator value="users">
		<tr>
		<td class="TableRowText02"><s:property value="userName" /></td>
		<td class="TableRowText02"><s:property value="firstName" /></td>
		<td class="TableRowText02"><s:property value="lastName" /></td>
		<td class="TableRowText02"><s:property value="orgName" /></td>
		<td class="TableRowText02"><s:property value="country" /></td>
		<td class="TableRowText02"><a href="mailto:<s:property value="email" />"><s:property value="email" /></a></td>
		<td class="TableRowText02"><input type="checkbox" onclick="loadUrl('/changeUserDescriptorDownloadStatus?userToChange=<s:property value="userName" />')" <s:if test="canDownloadDescriptors=='YES'">checked</s:if> /></td>
		<td class="TableRowText02"><input type="checkbox" onclick="loadUrl('/changeUserAdminStatus?userToChange=<s:property value="userName" />')" <s:if test="isAdmin=='YES'">checked</s:if>  <s:if test="userName==user.userName">disabled="true"</s:if> /></td>
		<td class="TableRowText02"><a onclick="return confirmDelete('user')" href="/deleteUser?userToDelete=<s:property value="userName" />">delete</a></td>
		</tr>
	</s:iterator>
</table> 		

    	</td></tr>
   </table>
	<%@include file ="/jsp/main/footer.jsp" %>
	</body>
	</html>