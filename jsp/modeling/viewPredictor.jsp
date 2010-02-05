<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
    <title>C-CHEMBENCH | View Predictor</title>
    
    <link href="theme/ss.css" rel="stylesheet" type="text/css" />
	<link href="theme/ajaxtabs.css" rel="stylesheet" type="text/css" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
	<link href="theme/links.css" rel="stylesheet" type="text/css" />
	<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
	<link rel="icon"  href="theme/img/mml.ico" type="image/ico" />
	<link rel="SHORTCUT ICON" href="theme/img/mml.ico" />
	
	<script language="javascript" src="javascript/script.js"></script>

</head>

<body onload="setTabToMyBench();">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td>
<table width="924" border="0" align="center" cellpadding="0"	cellspacing="0">
	<%@include file="/jsp/main/header.jsp" %>
	<%@include file="/jsp/main/centralNavigationBar.jsp" %>
	</td>
		</span>
	</tr>
	
	<!-- Header Info -->
	<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top">
		<p class="StandardTextDarkGray">&nbsp;</p>
		<span class="StandardTextDarkGray"></span>
		
		<table width="924">
		<tr>
			<td class="TableRowText01">Job Name</td>
			<td class="TableRowText01">Date Created</td>
			<td class="TableRowText01">Dataset</td>
			<td class="TableRowText01">Modeling Method</td>
			<td class="TableRowText01">Descriptor Generation Method</td>
		</tr>
		<tr>
			<td class="TableRowText02"><s:property value="selectedPredictor.name" /></td>
			<td class="TableRowText02"><s:property value="selectedPredictor.dateCreated" /></td>
			
			<a href="viewDatasetFlash.do?fileName=<s:property value="selectedPredictor.datasetDisplay" />&isPublic=<s:if test="datasetUserName=='_all'">true</s:if><s:else>false</s:else>">
						
			<td class="TableRowText02"><span style="width:100"><s:property value="selectedPredictor.datasetDisplay" /></span></td>
			<td class="TableRowText02"><s:property value="selectedPredictor.modelMethodDisplay" /></td>
			<td class="TableRowText02"><s:property value="selectedPredictor.descriptorGenerationDisplay" /></td>
		</tr>
		</table>
		
		<!-- Show description if it is a public predictor -->
		<s:if test="datasetUserName=='_all'">
		<table width="500">
		<tr><td>
		<p class="StandardTextDarkGrayParagraph">
		<s:property value="selectedPredictor.description"/>
		</p>
		</td></tr></table>
		</s:if>
		<!-- End description -->

		<!-- load tabs -->
		<a name="tabs"></a> 
		<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
		<sx:tabbedpanel id="viewPredictionTabs" >
	
			<s:url id="externalValidationLink" value="/viewPredictorExternalValidationSection" includeParams="none">
				<s:param name="id" value='selectedPredictor.predictorId' />
			</s:url>
	    	<sx:div href="%{externalValidationLink}" id="externalValidationDiv" label="External Validation" theme="ajax" loadingText="Loading external validation..." showLoadingText="true">
			</sx:div>
			
			<s:url id="modelsLink" value="/viewPredictorExternalValidationSection" includeParams="none">
				<s:param name="id" value='selectedPredictor.predictorId' />
			</s:url>
	    	<sx:div href="%{modelsLink}" id="modelsDiv" label="Models" theme="ajax" loadingText="Loading models..." showLoadingText="true">
			</sx:div>
			
			<s:url id="yRandomLink" value="/viewPredictorExternalValidationSection" includeParams="none">
				<s:param name="id" value='selectedPredictor.predictorId' />
			</s:url>
	    	<sx:div href="%{yRandomLink}" id="predictionValuesDiv" label="Prediction Values" theme="ajax" loadingText="Loading predictions..." showLoadingText="true">
			</sx:div>
			
			<s:url id="warningsLink" value="/viewPredictorWarningsSection" includeParams="none">
				<s:param name="id" value='selectedPredictor.predictorId' />
			</s:url>
			<sx:div href="%{warningsLink}" label="Warnings" theme="ajax" loadingText="Loading warnings..." showLoadingText="true">
			</sx:div>
			
	   	</sx:tabbedpanel>
		<!-- end load tabs -->

	<!-- End Header Info -->
	
	<tr>
<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>