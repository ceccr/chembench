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

<body>
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
		<span class="Errors"><b><!-- errors go here..? --></b></span> 
		<span class="StandardTextDarkGray">
		
		<table width="924">
		<tr>
			<td class="TableRowText01">Job Name</td>
			<td class="TableRowText01">Date Created</td>
			<td class="TableRowText01">SD File</td>
			<td class="TableRowText01">ACT File</td>
			<td class="TableRowText01">Modeling Method</td>
			<td class="TableRowText01">Descriptor Generation Method</td>
		</tr>
		<tr>
			<td class="TableRowText02"><s:property value="selectedPredictor.name" /></td>
			<td class="TableRowText02"><s:property value="selectedPredictor.dateCreated" /></td>
			<td class="TableRowText02"><span style="width:100"><s:property value="selectedPredictor.sdFileName" /></span></td>
			<td class="TableRowText02"><span style="width:100"><s:property value="selectedPredictor.actFileName" /></span></td>
			<td class="TableRowText02"><s:property value="selectedPredictor.modelMethodDisplay" /></td>
			<td class="TableRowText02"><s:property value="selectedPredictor.descriptorGenerationDisplay" /></td>
		</tr>
		</table>

	<!-- End Header Info -->
	
	<!-- Info For Each Model -->	
		<br />
		<p class="StandardTextDarkGray"><b><u>Modeling Results</u></b></p>
		<s:if test="models.size==0">
			<s:if test="selectedPredictor.activityType=='CONTINUOUS'">
				<br/><b class="StandardTextDarkGray">No models that passed your r<sup>2</sup> and q<sup>2</sup> cutoffs were generated.</b><br/><br/>
			</s:if>
			<s:else>
				<b class="StandardTextDarkGray">No models were generated.</b><br/><br/>
			</s:else>
		</s:if>
		<s:else>
		<p style="background:white;width:924;">
			<b>Of the <s:property value="selectedPredictor.numTotalModels" /> models generated, 
			<s:property value="selectedPredictor.numTrainModels" /> passed the training set criteria and 
			<s:property value="selectedPredictor.numTestModels" /> passed both training and test set criteria. 
			<br />
			<s:if test="selectedPredictor.numTestModels>=10">
               The top 10 models are displayed below.</b></p>
			<br />
			</s:if>
			<s:elseif test="selectedPredictor.numTestModels<10">
				The top <s:property value="selectedPredictor.numTestModels" />
				models are displayed below.</b></p><br />
			</s:elseif>
		</s:else>	
	
	<table width="100%" align="center">
	<s:if test="dataType=='CONTINUOUS'">
	<tr>
		<td class="TableRowText01">nnn</td>
		<td class="TableRowText01">q<sup>2</sup></td>
		<td class="TableRowText01">n</td>
		<td class="TableRowText01">r</td>
		<td class="TableRowText01">r<sup>2</sup></td>
		<td class="TableRowText01">R<sub>01</sub><sup>2</sup></td>
		<td class="TableRowText01">R<sub>02</sub><sup>2</sup></td>
		<td class="TableRowText01">k1</td>
		<td class="TableRowText01">k2</td>
	</tr>
	
	<s:iterator value="models" status="modelsStatus">
		<s:if test="#modelsStatus.index<10">
		<tr>
		<td class="TableRowText02"><s:property value="nnn" /></td>
		<td class="TableRowText02"><s:property value="q_squared" /></td>
		<td class="TableRowText02"><s:property value="n" /></td>
		<td class="TableRowText02"><s:property value="r" /></td>
		<td class="TableRowText02"><s:property value="r_squared" /></td>
		<td class="TableRowText02"><s:property value="r01_squared" /></td>
		<td class="TableRowText02"><s:property value="r02_squared" /></td>
		<td class="TableRowText02"><s:property value="k1" /></td>
		<td class="TableRowText02"><s:property value="k2" /></td>
		</tr> 
		</s:if>
	</s:iterator>
	</s:if>
	<s:elseif test="dataType=='CATEGORY'">
	<tr>
		<td class="TableRowText01">nnn</td>
		<td class="TableRowText01">Training Accuracy</td>
		<td class="TableRowText01">Normalized Training Accuracy</td>
		<td class="TableRowText01">Test Accuracy</td>
		<td class="TableRowText01">Normalized Test Accuracy</td>
	</tr>
	
	<s:iterator value="models" status="modelsStatus">
		<s:if test="#modelsStatus.index<10">
		<tr>
			<td class="TableRowText02"><s:property value="nnn" /></td>
			<td class="TableRowText02"><s:property value="trainingAcc" /></td>
			<td class="TableRowText02"><s:property value="normalizedTrainingAcc" /></td>
			<td class="TableRowText02"><s:property value="testAcc" /></td>
			<td class="TableRowText02"><s:property value="normalizedTestAcc" /></td>
		</tr>
		</s:if>
	</s:iterator>

	</s:elseif>
	</table>
	<!-- End Info For Each Model -->	
	
	
	<!-- Info For Each y-Random Model -->	
	<br />
	<p class="StandardTextDarkGray"><b><u>Y-Randomization Results</u></b></p>
	<p style="background:white;width:100%;">
		<b>Y-randomization</b> is a statistical QSAR model validation technique where the performance of the model 
		built with original data is compared to that of models built for multiple artificial datasets with
		randomly shuffled activities. Ideally, there will be no models with high values(> 0.6) of both 
		q<sup>2</sup> (training set) and R<sup>2 </sup>(test set) found.
		<br/>For your data, <font color="red"><s:property value="selectedPredictor.numyTotalModels" /> </font> 
		models for randomized datasets were built and <font color="red">
		<s:property value="selectedPredictor.numyTestModels" /> </font>models were found to have high 
		prediction accuracy. <br />
		<s:if test="selectedPredictor.numyTestModels>=10">
             The top 10 models are displayed below.</b></p><br />
		</s:if>
	</p>

	<s:if test="selectedPredictor.numyTestModels>0">
	<table width="100%" align="center">
	<s:if test="dataType=='CONTINUOUS'">
	<tr>
		<td class="TableRowText01">nnn</td>
		<td class="TableRowText01">q<sup>2</sup></td>
		<td class="TableRowText01">n</td>
		<td class="TableRowText01">r</td>
		<td class="TableRowText01">r<sup>2</sup></td>
		<td class="TableRowText01">R<sub>01</sub><sup>2</sup></td>
		<td class="TableRowText01">R<sub>02</sub><sup>2</sup></td>
		<td class="TableRowText01">k1</td>
		<td class="TableRowText01">k2</td>
	</tr>
	
	<s:iterator value="randomModels" status="randomModelsStatus">
		<s:if test="#randomModelsStatus.index<10">
		<tr>
		<td class="TableRowText02"><s:property value="nnn" /></td>
		<td class="TableRowText02"><s:property value="q_squared" /></td>
		<td class="TableRowText02"><s:property value="n" /></td>
		<td class="TableRowText02"><s:property value="r" /></td>
		<td class="TableRowText02"><s:property value="r_squared" /></td>
		<td class="TableRowText02"><s:property value="r01_squared" /></td>
		<td class="TableRowText02"><s:property value="r02_squared" /></td>
		<td class="TableRowText02"><s:property value="k1" /></td>
		<td class="TableRowText02"><s:property value="k2" /></td>
		</tr> 
		</s:if>
	</s:iterator>
	</s:if>
	<s:elseif test="dataType=='CATEGORY'">
	<tr>
		<td class="TableRowText01">nnn</td>
		<td class="TableRowText01">Training Accuracy</td>
		<td class="TableRowText01">Normalized Training Accuracy</td>
		<td class="TableRowText01">Test Accuracy</td>
		<td class="TableRowText01">Normalized Test Accuracy</td>
	</tr>
	
	<s:iterator value="randomModels" status="randomModelsStatus">
		<s:if test="#randomModelsStatus.index<10">
		<tr>
			<td class="TableRowText02"><s:property value="nnn" /></td>
			<td class="TableRowText02"><s:property value="trainingAcc" /></td>
			<td class="TableRowText02"><s:property value="normalizedTrainingAcc" /></td>
			<td class="TableRowText02"><s:property value="testAcc" /></td>
			<td class="TableRowText02"><s:property value="normalizedTestAcc" /></td>
		</tr>
		</s:if>
	</s:iterator>

	</s:elseif>
	</table>
	</s:if>
	<!-- End Info For Each y-Random Model -->	
	
	
	<!-- External Validation Compound Predictions -->
	<p class="StandardTextDarkGray"><b><u>External Validation Results</u></b>
	<br></br>
	<table width="100%" align="center">
		<!--DWLayoutTable-->
		<tr>
		<td class="TableRowText01">Compound ID</td>
		<td class="TableRowText01">Structure</td>
		<td class="TableRowText01">Observed Value</td>
		<td class="TableRowText01">Predicted Value</td>
		<td class="TableRowText01">Residual</td>
		<td class="TableRowText01"># of Models</td>
		</tr>
		
	<s:iterator value="externalValValues" status="extValStatus">
		<tr>
			<td class="TableRowText02"><s:property value="compoundId" /></td>
			<td class="TableRowText02">
			<a href="#" onclick="window.open('compound3D?project=<s:property value='selectedPredictor.name' />&projectType=modeling&compoundId=<s:property value='compoundId' />&user=<s:property value='user.userName' />&datasetID=<s:property value='selectedPredictor.datasetId' />', '<% new java.util.Date().getTime(); %>','width=350, height=350'); return false;">
			<img src="/imageServlet?projectType=modeling&user=<s:property value='user.userName' />&project=<s:property value='selectedPredictor.name' />&compoundId=<s:property value='compoundId' />&datasetID=<s:property value='selectedPredictor.datasetId' />" border="0" height="150" onmouseover='enlargeImage(this);' onmouseout='shrinkImage(this)'/>
			</a>
			</td>
			<td class="TableRowText02"><s:property value="actualValue" /></td>
			<td class="TableRowText02">
			<s:if test="numModels>=2">
				<s:property value="predictedValue" /> &#177; <s:property value="standDev" />
			</s:if>
			<s:elseif test="numModels==1">
				<s:property value="predictedValue" />
			</s:elseif>
			</td>
			<td class="TableRowText02">
				<s:property value="residuals[#extValStatus.index]" />
			</td>
			<td class="TableRowText02"><s:property value="numModels" /></td>
		</tr>
	</s:iterator>
	
	</table>
	<br />
	<!-- End External Validation Compound Predictions -->
	
	<!-- External Validation Chart -->
	<s:if test="models.size!=0">
	<s:if test="dataType=='CONTINUOUS'">
		<p class="StandardTextDarkGray"><b><u>External Validation Chart</u></b>
		<s:url id="externalChartLink" value="/externalValidationChart.do" includeParams="none">
			<s:param name="user" value="user.userName" />
			<s:param name="project" value="selectedPredictor.name" />
		</s:url>
		
		<!-- old way: have it open in a new window <s:a href="%{externalChartLink}" target="_blank"><u>Chart View</u></s:a> -->
		<!-- new way: ajax it onto the page, woots! -->
		<br />
		<sx:div id="extValidationChart" href="%{externalChartLink}" theme="ajax">
		</sx:div>
		</p>
	</s:if>
	</s:if>
	<!-- End External Validation Chart -->
	
	
	<tr>
<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>