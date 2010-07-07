<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
    <title>CHEMBENCH | View Predictor</title>
    
    <link href="theme/ss.css" rel="stylesheet" type="text/css" />
	<link href="theme/ajaxtabs.css" rel="stylesheet" type="text/css" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
	<link href="theme/links.css" rel="stylesheet" type="text/css" />
	<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
	<link rel="icon"  href="theme/img/mml.ico" type="image/ico" />
	<link rel="SHORTCUT ICON" href="theme/img/mml.ico" />
	
	<script language="JavaScript" src="javascript/script.js"></script>
	<script language="JavaScript" src="javascript/sortableTable-delay.js"></script>
	
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
			<td class="TableRowText02"><a href="viewDataset?id=<s:property value="selectedPredictor.datasetId" />"><s:property value="selectedPredictor.datasetDisplay" /></a></td>
			<td class="TableRowText02"><s:property value="selectedPredictor.modelMethod" /></td>
			<td class="TableRowText02"><s:property value="selectedPredictor.descriptorGeneration" /></td>
		</tr>
		</table>
		
		<s:if test="selectedPredictor.userName=='_all'">
		<br />
		<p class="StandardTextDarkGrayParagraph">
		<b>Predictor Description:</b> <s:property value="selectedPredictor.description"/>
		</p>
		<p class="StandardTextDarkGrayParagraph">For this and the other public predictors, there is no detailed information on the external set or models available for display.
		</p>
		</s:if>
		<!-- End description -->
	
		<br /><br />
		<!-- load tabs -->
		<a name="tabs"></a> 
		<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
		<sx:tabbedpanel id="viewPredictionTabs" >
	
			<s:if test="selectedPredictor.modelMethod=='KNN'">
			
			<s:url id="externalValidationLink" value="/viewPredictorExternalValidationSection" includeParams="none">
				<s:param name="id" value='selectedPredictor.predictorId' />
			</s:url>
	    	<sx:div href="%{externalValidationLink}" id="externalValidationDiv" label="External Validation" theme="ajax" loadingText="Loading external validation..." executeScripts="true" showLoadingText="true">
			</sx:div>
			
			<s:url id="modelsLink" value="/viewPredictorModelsSection" includeParams="none">
				<s:param name="id" value='selectedPredictor.predictorId' />
			</s:url>
	    	<sx:div href="%{modelsLink}" id="modelsDiv" label="Models" theme="ajax" loadingText="Loading models..." executeScripts="true" showLoadingText="true">
			</sx:div>
			
			</s:if>
			<s:elseif test="selectedPredictor.modelMethod=='KNN-GA' || selectedPredictor.modelMethod=='KNN-SA'">
			
			<s:url id="externalValidationLink" value="/viewKnnPlusPredictorExternalValidationSection" includeParams="none">
				<s:param name="id" value='selectedPredictor.predictorId' />
			</s:url>
	    	<sx:div href="%{externalValidationLink}" id="externalValidationDiv" label="External Validation" theme="ajax" loadingText="Loading external validation..." executeScripts="true" showLoadingText="true">
			</sx:div>
			
			<s:url id="modelsLink" value="/viewKnnPlusPredictorModelsSection" includeParams="none">
				<s:param name="id" value='selectedPredictor.predictorId' />
			</s:url>
	    	<sx:div href="%{modelsLink}" id="modelsDiv" label="Models" theme="ajax" loadingText="Loading models..." executeScripts="true" showLoadingText="true">
			</sx:div>
			
			</s:elseif>
			
			<s:url id="yRandomLink" value="/viewPredictorYRandomSection" includeParams="none">
				<s:param name="id" value='selectedPredictor.predictorId' />
			</s:url>
	    	<sx:div href="%{yRandomLink}" id="yRandomDiv" label="Y-Randomization" theme="ajax" loadingText="Loading Y-Randomization Models..." executeScripts="true" showLoadingText="true">
			</sx:div>
		
		<!--
			<s:url id="warningsLink" value="/viewPredictorWarningsSection" includeParams="none">
				<s:param name="id" value='selectedPredictor.predictorId' />
			</s:url>
			<sx:div href="%{warningsLink}" label="Warnings" theme="ajax" loadingText="Loading warnings..." showLoadingText="true">
			</sx:div>
		-->	
			
	   	</sx:tabbedpanel>
		<!-- end load tabs -->

	<!-- End Header Info -->
	
	<tr>
<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>