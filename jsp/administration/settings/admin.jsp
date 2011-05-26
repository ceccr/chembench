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

</head>
<body onload="setTabToHome();">

	<!-- headers -->
	<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
	<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
	<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>

	<!--  page content -->
	<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
   		<tr><td>
   		
   		<!--  tabs -->
   		<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
    	<sx:tabbedpanel id="adminTabbedPanel">
    	
	    	<sx:div id="GENERAL" href="/loadGeneralAdminSection" label="General" theme="ajax">
			</sx:div>
			
			<sx:div id="USERS" href="/loadUsersAdminSection" label="Users" theme="ajax">
			</sx:div>

			<sx:div id="JOBS" href="/loadJobsAdminSection" label="Jobs" theme="ajax" >
			</sx:div>
			
    	</sx:tabbedpanel>
    	</td></tr></table>
    	</td></tr>
    </table>
	<%@include file ="/jsp/main/footer.jsp" %>
	</body>
	</html>