<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
    <title>C-CHEMBENCH | View Dataset</title>
      
    <link href="theme/ss.css" rel="stylesheet" type="text/css" />
	<link href="theme/ajaxtabs.css" rel="stylesheet" type="text/css" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="theme/standard.css" rel="stylesheet" type="text/css" />
	<link href="theme/links.css" rel="stylesheet" type="text/css" />
	<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
	<link rel="icon"  href="theme/img/mml.ico" type="image/ico" />
	<link rel="SHORTCUT ICON" href="theme/img/mml.ico" />
	
	<script language="javascript" src="javascript/script.js" />
	<script language="javascript">
	
	</script>
</head>

<body onload="setTabToMyBench();">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<%@include file="/jsp/main/header.jsp" %>
	<%@include file="/jsp/main/centralNavigationBar.jsp" %>
	</td>
		</span>
	</tr>
	
	<!-- Header Info -->
	<tr>
	<span id="maincontent">
	<td height="557" colspan="5" valign="top">
	
	<span class="Errors"><b><!-- errors go here..? --></b></span> 
	<span class="StandardTextDarkGray">

	<br />
	<table width="924" align="center">
		<tr>
			<td class="TableRowText01">Dataset Name</td>
			<td class="TableRowText01">Number of Compounds</td>
			<td class="TableRowText01">Date Created</td>
			<td class="TableRowText01">Description</td>
			<td class="TableRowText01">Paper Reference</td>
		</tr>
		<tr>
			<td class="TableRowText02"><s:property value="dataset.fileName" /></td>
			<td class="TableRowText02"><s:property value="dataset.numCompound" /></td>
			<td class="TableRowText02"><s:property value="dataset.createdTime" /></td>
			<td class="TableRowText02"><s:property value="dataset.description" /></td>
			<td class="TableRowText02"><s:property value="dataset.paperReference" /></td>
		</tr>
	</table>
	<!-- End Header Info -->

	<!-- Page description -->	
	<br />
	<s:if test="dataset.datasetType=='PREDICTION'">
	<p class="StandardTextDarkGray" width="550">The compounds in your dataset are below.  </p>
	</s:if>
	<s:elseif test="dataset.datasetType=='MODELING'">
	<p class="StandardTextDarkGray" width="550">The compounds in your dataset are below, with the activity values you supplied.
	 The compounds in the external set are shown at the bottom of the page.</p>
	</s:elseif>
	<!-- End page description -->
	
	
	<sx:tabbedpanel id="viewDatasetTabs" >
	
    	<sx:div href="/loadViewDatasetCompoundsSection" label="Compounds" theme="ajax" loadingText="Loading compounds...">
		</sx:div>
		
		<s:if test="dataset.datasetType=='MODELING'">
		<sx:div href="/loadViewDatasetExternalCompoundsSection" label="Update Info" theme="ajax" loadingText="Loading external compounds...">
		</sx:div>
		</s:if>
		
		<sx:div href="/loadViewDatasetVisualizationSection" label="Heatmap" theme="ajax" loadingText="Loading heatmap...">
		</sx:div>
		
		<sx:div href="/loadViewDatasetWarningsSection" label="Warnings" theme="ajax" loadingText="Loading heatmap...">
		</sx:div>
		
   	</sx:tabbedpanel>
	
	
	
<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>