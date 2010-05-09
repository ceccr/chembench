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
	<script language="JavaScript" src="javascript/sortableTable.js"></script>

	<script language="javascript">
		function loadModelsTab(newUrl){
			//When the user changes which page they're on in the Models tab
			//or changes the sorted element, run this function to update the tab's content
			
			//prepare the AJAX object
			var ajaxObject = GetXmlHttpObject();
			ajaxObject.onreadystatechange=function(){
				if(ajaxObject.readyState==4){
					hideLoading();
				  	document.getElementById("modelsDiv").innerHTML=ajaxObject.responseText;
				}
			}
			showLoading("LOADING. PLEASE WAIT.")
			
			//send request
			ajaxObject.open("GET",newUrl,true);
			ajaxObject.send(null);
			
			return true;
		}

		function loadYRandomTab(newUrl){
			//When the user changes which page they're on in the yRandom tab
			//or changes the sorted element, run this function to update the tab's content
			
			//prepare the AJAX object
			var ajaxObject = GetXmlHttpObject();
			ajaxObject.onreadystatechange=function(){
				if(ajaxObject.readyState==4){
				  	document.getElementById("yRandomDiv").innerHTML=ajaxObject.responseText;
				}
			}
			
			//send request
			ajaxObject.open("GET",newUrl,true);
			ajaxObject.send(null);
			
			return true;
		}		
		
		
	</script>
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