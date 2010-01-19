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
			<td class="TableRowText01">Prediction Name</td>
			<td class="TableRowText01">Date Created</td>
			<td class="TableRowText01">Predictors Used</td>
			<td class="TableRowText01">Dataset Predicted</td>
			<td class="TableRowText01">Similarity Cutoff</td>
		</tr>
		<tr>
			<td class="TableRowText02"><s:property value="prediction.jobName" /></td>
			<td class="TableRowText02"><s:property value="prediction.dateCreated" /></td>
			<td class="TableRowText02">
			<s:iterator value="predictors" status="predictorsStatus1">
			<s:url id="predictorLink" value="/viewPredictor" includeParams="none">
				<s:param name="id" value='predictorId' />
			</s:url>
			<s:a href="%{predictorLink}"><s:property value="name" /></s:a><br />
			</s:iterator>
			
			</td>
			<td class="TableRowText02">
			<s:url id="datasetLink" value="/viewDataset" includeParams="none">
				<s:param name="id" value='predictorId' />
			</s:url>
			<s:property value="prediction.datasetDisplay" />
			</td>
			<td class="TableRowText02"><s:property value="prediction.similarityCutoff" /></td>
		</tr>
	</table>
	<!-- End Header Info -->
	
	<!-- Page description -->	
	<br />
	<p class="StandardTextDarkGray" width="550">The predicted values for the compounds in your dataset are below.</p>
	<p class="StandardTextDarkGray" width="550">For each predictor, there are two columns. The first column contains the
	prediction. If more than one of the predictor's models were used to make the prediction, the average value
	across all models is displayed, &#177; the standard deviation. If there is no value shown, the compound
	could not be predicted using any of the predictor's models with the cutoff value you specified.</p>
	<p class="StandardTextDarkGray" width="550">The second column for each predictor tells how many models' predictions were
	used to calculate the value in the first column. It is often the case that not all of the models in a predictor
	can be used to predict a compound, because the compounds lie outside the cutoff range of some of the models.</p>	
	<!-- End page description -->
	
	<!-- Predictions -->	
	<br />
		<p class="StandardTextDarkGray"><b><u>Prediction Results</u></b></p>
		
	<table width="924" align="center">
		<tr><td>
			<table>
				<tr>
				<!-- header for left side table -->
				<td class="TableRowText01">Compound ID</td>
				<td class="TableRowText01">Structure</td>
				<s:iterator value="predictors" status="predictorsStatus">
				<td class="TableRowText01">(<s:property value="name" />) Prediction</td>
				<td class="TableRowText01">(<s:property value="name" />) Number of Predicting Models / Total Models</td>
				</s:iterator>
				</tr>
				<!-- body for left side table -->
				<s:iterator value="compoundPredictionValues" status="compoundPredictionValuesStatus">
					<tr>
						<td class="TableRowText02"><s:property value="compound" /></td>
						<td class="TableRowText02">
							<a href="#" onclick="window.open('compound3D?compoundId=<s:property value="compound" />&project=<s:property value="prediction.jobName" />&projectType=predictor&user=<s:property value="user.userName" />&datasetID=<s:property value="prediction.datasetId" />, '<% new java.util.Date().getTime(); %>','width=350, height=350'); return false;">
							<img src="/imageServlet?user=<s:property value="compound" value="user.userName" />&projectType=predictor&compoundId=<s:property value='compound' />&project=<s:property value="prediction.jobName" />&datasetID=<s:property value="prediction.datasetId" />" border="0" height="150" onmouseover='enlargeImage(this);' onmouseout='shrinkImage(this)'/></a>
						</td>
						<s:iterator value="predictionValues" status="predictionValuesStatus">
						<td class="TableRowText02"><s:property value="predictedValue" /><s:if test="standardDeviation!=null"> &#177; </s:if><s:property value="standardDeviation" /><!-- prediction value +/- stddev --></td>
						<td class="TableRowText02"><s:property  value="numModelsUsed" /><!-- number of models in prediction --> / <s:property  value="numTotalModels" /></td>
						</s:iterator>
					</tr>
				</s:iterator>
			</table>
		</td></tr>
	</table>
	<!-- End Predictions -->
	
<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>