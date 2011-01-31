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
	<link href="theme/standard.css" rel="stylesheet" type="text/css" />
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
		
		<table width="924" align="center"><tr><td>
			<div class="StandardTextDarkGray"><br />
			<b>Predictor Name: </b><s:property value="selectedPredictor.name" /><br />
			<b>Dataset: </b><a href="viewDataset?id=<s:property value="selectedPredictor.datasetId" />"><s:property value="selectedPredictor.datasetDisplay" /></a><br />
			<b>Date Created: </b><s:date name="selectedPredictor.createdDateTime" format="yyyy-MM-dd HH:mm" /><br />
			<b>Modeling Method: </b><s:property value="selectedPredictor.modelMethod" /><br />
			<b>Descriptor Generation Method: </b><s:property value="selectedPredictor.descriptorGeneration" /><br />
			<br />			
			</div>
		
			<s:if test="editable=='YES'">
				<s:form action="updatePredictor" enctype="multipart/form-data" theme="simple">
				<div class="StandardTextDarkGray"><b>Description: </b></div><s:textarea id="predictorDescription" name="predictorDescription" align="left" style="height: 50px; width: 50%" /></div><br />
				<div class="StandardTextDarkGray"><b>Paper Reference: </b></div><s:textarea id="predictorReference" name="predictorReference" align="left" style="height: 50px; width: 50%" /></div><br />
				<input type="button" name="userAction" id="userAction" onclick="this.form.submit()" value="Save Changes" />
				<s:hidden id="predictorId" name="predictorId" />
				</s:form>
			</s:if>
			<s:else>
				<div class="StandardTextDarkGray"><br />
				<b>Description: </b><s:property value="predictor.description" /><br />
				<b>Paper Reference: </b><s:property value="predictor.paperReference" /><br />
				<s:if test="selectedPredictor.userName!='all-users'||user.isAdmin=='YES'">
					<!-- display edit link -->
					<a href="viewPredictor?id=<s:property value="predictorId" />&editable=YES">Edit description and reference</a><br />
				</s:if>
				</div>
			</s:else>
		</td></tr></table>
		
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
		<script type="text/javascript">
		   dojo.event.topic.subscribe('/modelingTabSelect', function(tab, tabContainer) {
		      //alert("Tab "+ tab.widgetId + " was selected");
		      sortables_init_delay();
		   });
		</script>
		<!-- end script -->
		
		<sx:tabbedpanel id="viewPredictionTabs" afterSelectTabNotifyTopics="/modelingTabSelect">
	
			<s:if test="selectedPredictor.modelMethod=='KNN'">
				
				<s:url id="externalValidationLink" value="/viewPredictorExternalValidationSection" includeParams="none">
					<s:param name="id" value='selectedPredictor.predictorId' />
				</s:url>
		    	<sx:div href="%{externalValidationLink}" id="externalValidationDiv" label="External Validation" theme="ajax" loadingText="Loading external validation..." executeScripts="true" preload="false" showLoadingText="true">
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
			</s:if>
			<s:elseif test="selectedPredictor.modelMethod=='KNN-GA' || selectedPredictor.modelMethod=='KNN-SA'">
			
				<s:url id="externalValidationLink" value="/viewKnnPlusPredictorExternalValidationSection" includeParams="none">
					<s:param name="id" value='selectedPredictor.predictorId' />
				</s:url>
		    	<sx:div href="%{externalValidationLink}" id="externalValidationDiv" label="External Validation" theme="ajax" loadingText="Loading external validation..." preload="false" executeScripts="true" showLoadingText="true">
				</sx:div>
				
				<s:url id="modelsLink" value="/viewKnnPlusPredictorModelsSection" includeParams="none">
					<s:param name="id" value='selectedPredictor.predictorId' />
				</s:url>
		    	<sx:div href="%{modelsLink}" id="modelsDiv" label="Models" theme="ajax" loadingText="Loading models..." executeScripts="true" showLoadingText="true">
				</sx:div>
			
				<s:url id="yRandomLink" value="/viewPredictorYRandomSection" includeParams="none">
					<s:param name="id" value='selectedPredictor.predictorId' />
				</s:url>
		    	<sx:div href="%{yRandomLink}" id="yRandomDiv" label="Y-Randomization" theme="ajax" loadingText="Loading Y-Randomization Models..." executeScripts="true" showLoadingText="true">
				</sx:div>
			</s:elseif>
			
			<s:elseif test="selectedPredictor.modelMethod=='RANDOMFOREST'">
				<s:url id="externalValidationLink" value="/viewRandomForestExternalValidationSection" includeParams="none">
					<s:param name="id" value='selectedPredictor.predictorId' />
				</s:url>
		    	<sx:div href="%{externalValidationLink}" id="externalValidationDiv" label="External Validation" theme="ajax" loadingText="Loading external validation..." preload="false" executeScripts="true" showLoadingText="true">
				</sx:div>
				
				<!-- For now, we don't display groves. If someday we want to, uncomment this.
				<s:url id="grovesLink" value="/viewRandomForestPredictorGrovesSection" includeParams="none">
					<s:param name="id" value='selectedPredictor.predictorId' />
				</s:url>
		    	<sx:div href="%{grovesLink}" id="grovesDiv" label="Forests" theme="ajax" loadingText="Loading forests..." executeScripts="true" showLoadingText="true">
				</sx:div>
				
				<s:url id="grovesYRandomLink" value="/viewRandomForestPredictorYRandomGrovesSection" includeParams="none">
					<s:param name="id" value='selectedPredictor.predictorId' />
				</s:url>
		    	<sx:div href="%{grovesYRandomLink}" id="randomGrovesDiv" label="Y-Randomized Forests" theme="ajax" loadingText="Loading forests..." executeScripts="true" showLoadingText="true">
				</sx:div>
				-->
				<s:url id="treesLink" value="/viewRandomForestPredictorTreesSection" includeParams="none">
					<s:param name="id" value='selectedPredictor.predictorId' />
				</s:url>
		    	<sx:div href="%{treesLink}" id="treesDiv" label="Trees" theme="ajax" loadingText="Loading trees..." executeScripts="true" showLoadingText="true">
				</sx:div>
				
				<s:url id="treesYRandomLink" value="/viewRandomForestPredictorYRandomTreesSection" includeParams="none">
					<s:param name="id" value='selectedPredictor.predictorId' />
				</s:url>
		    	<sx:div href="%{treesYRandomLink}" id="randomTreesDiv" label="Y-Randomized Trees" theme="ajax" loadingText="Loading trees..." executeScripts="true" showLoadingText="true">
				</sx:div>
			</s:elseif>
			
			<s:elseif test="selectedPredictor.modelMethod=='SVM'">
			
				<s:url id="externalValidationLink" value="/viewSvmExternalValidationSection" includeParams="none">
					<s:param name="id" value='selectedPredictor.predictorId' />
				</s:url>
		    	<sx:div href="%{externalValidationLink}" id="externalValidationDiv" label="External Validation" theme="ajax" loadingText="Loading external validation..." executeScripts="true" preload="false" showLoadingText="true">
				</sx:div>
				
				<s:url id="modelsLink" value="/viewSvmModelsSection" includeParams="none">
					<s:param name="id" value='selectedPredictor.predictorId' />
				</s:url>
		    	<sx:div href="%{modelsLink}" id="modelsLinkDiv" label="Models" theme="ajax" loadingText="Loading models..." executeScripts="true" preload="false" showLoadingText="true">
				</sx:div>
				
				<s:url id="yRandomLink" value="/viewSvmYRandomSection" includeParams="none">
					<s:param name="id" value='selectedPredictor.predictorId' />
				</s:url>
		    	<sx:div href="%{yRandomLink}" id="yRandomDiv" label="Y-Randomization" theme="ajax" loadingText="Loading Y-Randomization Models..." executeScripts="true" showLoadingText="true">
				</sx:div>
				
			</s:elseif>
			
			<!-- All modeling methods should display their parameters at the end. -->
			<s:url id="parametersLink" value="/viewPredictorParametersSection" includeParams="none">
				<s:param name="id" value='selectedPredictor.predictorId' />
			</s:url>
			<sx:div href="%{parametersLink}" label="Modeling Parameters" theme="ajax" loadingText="Loading parameters..." showLoadingText="true">
			</sx:div>
			
		<!-- Considered having a "warnings" page that will cover any problems with
			predictors. Nixed the idea, though.
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