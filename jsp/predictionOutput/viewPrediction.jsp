<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
    <title>CHEMBENCH | View Prediction</title>
      
    <link href="theme/ss.css" rel="stylesheet" type="text/css" />
	<link href="theme/ajaxtabs.css" rel="stylesheet" type="text/css" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	
	<link href="/theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />
	<link href="theme/standard.css" rel="stylesheet" type="text/css" />
	<link href="theme/links.css" rel="stylesheet" type="text/css" />
	<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
	<link rel="icon"  href="theme/img/mml.ico" type="image/ico" />
	<link rel="SHORTCUT ICON" href="theme/img/mml.ico" />
	
	<script language="javascript" src="javascript/script.js"></script>

	<script language="javascript">
	function loadPredictionValuesTab(newUrl){
		//When the user changes which page they're on in the Prediction Values tab
		//or changes the sorted element, run this function to update the tab's content
		
		//prepare the AJAX object
		var ajaxObject = GetXmlHttpObject();
		ajaxObject.onreadystatechange=function(){
			if(ajaxObject.readyState==4){
				hideLoading();
			  	document.getElementById("predictionValuesDiv").innerHTML=ajaxObject.responseText;
			}
		}
		showLoading("LOADING. PLEASE WAIT.")
		
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
	<table width="924" align="center"><tr><td>
			<div class="StandardTextDarkGray"><br />
			<b>Prediction Name: </b><s:property value="prediction.name" /><br />
			<b>Dataset Predicted: </b><a href="viewDataset?id=<s:property value="prediction.datasetId" />"><s:property value="prediction.datasetDisplay" /></a><br />
			<b>Predictors Used: &nbsp;</b>
			<s:iterator value="predictors" status="predictorsStatus1">
			<s:url id="predictorLink" value="/viewPredictor" includeParams="none">
				<s:param name="predictorId" value='id' />
			</s:url>
			<s:a href="%{predictorLink}"><s:property value="name" /></s:a>&nbsp;&nbsp;
			</s:iterator>
			<br />
			<b>Date Created: </b><s:date name="prediction.dateCreated" format="yyyy-MM-dd HH:mm" /><br />
			<b>Similarity Cutoff: </b><s:property value="prediction.similarityCutoff" />
			<br /><br />
			<a href="fileServlet?id=<s:property value="prediction.id" />&user=<s:property value="userName" />&jobType=PREDICTION&file=predictionAsCSV">Download This Prediction Result (CSV)</a>
			<br />
			<a href="jobs#predictions">Back to Predictors</a>
			</div>
	</td></tr></table>
	<!-- End Header Info -->
	
	<!-- Page description -->	
	<p class="StandardTextDarkGray" width="550">The predicted values for the compounds in your dataset are below.</p>
	<p class="StandardTextDarkGray" width="550">For each predictor, there are two columns. The first column contains the
	prediction. If more than one of the predictor's models were used to make the prediction, the average value
	across all models is displayed, &#177; the standard deviation. If there is no value shown, the compound
	is outside the applicability domain of all models in the predictor for the cutoff you defined, 
	so no prediction can be made.</p>
	<p class="StandardTextDarkGray" width="550">The second column for each predictor tells how many models' predictions were
	used to calculate the value in the first column. It is often the case that not all of the models in a predictor
	can be used to predict a compound, because the compounds lie outside the cutoff range of some of the models.</p>	
	<!-- End page description -->
			
	<!-- load tabs -->
	<a name="tabs"></a> 
	<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
	<sx:tabbedpanel id="viewPredictionTabs" >

		<s:url id="predictionsLink" value="/viewPredictionPredictionsSection" includeParams="none">
			<s:param name="currentPageNumber" value='currentPageNumber' />
			<s:param name="orderBy" value='orderBy' />
			<s:param name="predictionId" value='predictionId' />
		</s:url>
		
    	<sx:div href="%{predictionsLink}" id="predictionValuesDiv" label="Prediction Values" theme="ajax" loadingText="Loading predictions..." showLoadingText="true" preload="false">
		</sx:div>
		
		<!--  
		<s:url id="warningsLink" value="/viewPredictionWarningsSection" includeParams="none">
			<s:param name="predictionId" value='predictionId' />
		</s:url>
		<sx:div href="%{warningsLink}" label="Warnings" theme="ajax" loadingText="Loading warnings..." showLoadingText="true">
		</sx:div>
		-->
		
   	</sx:tabbedpanel>
	<!-- end load tabs -->
	
	
<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>