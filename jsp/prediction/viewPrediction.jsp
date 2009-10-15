<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
    <title>C-CHEMBENCH | View Prediction</title>
      
    <link href="theme/ss.css" rel="stylesheet" type="text/css" />
	<link href="theme/ajaxtabs.css" rel="stylesheet" type="text/css" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="theme/standard.css" rel="stylesheet" type="text/css" />
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
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<%@include file="/jsp/main/header.jsp" %>
	<%@include file="/jsp/main/centralNavigationBar.jsp" %>
	</td>
		</span>
	</tr>
	
	<!-- Predictions -->
		<table width="924" align="center">
			<tr>
				<td class="TableRowText01">Prediction Name</td>
				<td class="TableRowText01">Date Created</td>
				<td class="TableRowText01">Predictor Used</td>
				<td class="TableRowText01">Dataset Predicted</td>
				<td class="TableRowText01">Similarity Cutoff</td>
			</tr>
			<tr>
				<td class="TableRowText02"><s:property value="selectedPrediction.jobName" /></td>
				<td class="TableRowText02"><s:property value="selectedPrediction.dateCreated" /></td>
				<td class="TableRowText02"><s:property value="selectedPrediction.predictorName" /></td>
				<td class="TableRowText02"><s:property value="selectedPrediction.datasetDisplay" /></td>
				<td class="TableRowText02"><s:property value="selectedPrediction.similarityCutoff" /></td>
			</tr>
		</table>
		<br />
		
		<table width="924" align="center">
			<tr><td>
			<table>
				<tr>
				<!-- header for left side table -->
				<td>Compound ID</td>
				<td>Structure</td>
				</tr>
				<!-- body for left side table -->
				<s:iterator value="compounds" status="compoundsStatus">
					<tr>
						<td><s:property /></td>
						<td>
							<a href="#" onclick="window.open('compound3D?compoundId=<s:property />&project=<s:property value="prediction.jobName" />&projectType=predictor&user=<s:property value="user.userName" />&datasetID=<s:property value="prediction.datasetId" />, '<% new java.util.Date().getTime(); %>','width=350, height=350');">
							<img src="/imageServlet?user=<s:property value="user.userName" />&projectType=predictor&compoundId=<bean:write name='predictionOutput' property='compoundName' />&project=<s:property value="prediction.jobName" />&datasetID=<s:property value="prediction.datasetId" />" border="0"/></a>
						</td>
					</tr>
				</s:iterator>
			</table>
			</td><td>
			<table STYLE="overflow: scroll">
				<tr>
				<!-- header for right side table -->
				<s:iterator value="predictors" status="predictorsStatus">
				<td><s:property value="name" />: Prediction</td>
				<td><s:property value="name" />: # Models</td>
				</s:iterator>
				</tr>
				<!-- body for right side table -->
				<s:iterator value="compoundPredictionValues" status="compoundPredictionValuesStatus">
					<tr>
					<s:iterator value="<s:property/>" status="compoundPredictionValuesStatus2">
					<td><s:property/> +/- <s:property/><!-- prediction value +/- stddev --></td>
					<td><s:property/><!-- number of models in prediction --></td>
					</s:iterator>
					</tr>
				</s:iterator>
			</table>
		</table>
	<!-- End Predictions -->
	
<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>