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
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<%@include file="/jsp/main/header.jsp" %>
	<%@include file="/jsp/main/centralNavigationBar.jsp" %>
	</td>
		</span>
	</tr>
	
	
	<!-- Header Info -->
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
	<!-- End Header Info -->
	
	<!-- Predictions For Each Compound -->	
	
	<!-- End Predictions For Each Compound -->	
	
	
<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>