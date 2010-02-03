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
	<script src="javascript/AC_RunActiveContent.js"></script>
	<script src="javascript/hookMouseWheel.js"></script>

	<script language="javascript">
		function loadAllCompoundsTab(newUrl){
			//When the user changes which page they're on in the All Compounds tab
			//or changes the sorted element, run this function to update the tab's content
			
			//prepare the AJAX object
			var ajaxObject = GetXmlHttpObject();
			ajaxObject.onreadystatechange=function(){
				if(ajaxObject.readyState==4){
				  	document.getElementById("allCompoundsDiv").innerHTML=ajaxObject.responseText;
				}
			}
			
			//send request
			ajaxObject.open("GET",newUrl,true);
			ajaxObject.send(null);
			
			return true;
		}

		function loadExternalCompoundsTab(newUrl){
			//When the user changes which page they're on in the External Compounds tab
			//or changes the sorted element, run this function to update the tab's content
			
			//prepare the AJAX object
			var ajaxObject = GetXmlHttpObject();
			ajaxObject.onreadystatechange=function(){
				if(ajaxObject.readyState==4){
				  	document.getElementById("externalCompoundsDiv").innerHTML=ajaxObject.responseText;
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
	<span class="StandardTextDarkGray"></span>

	<br />
	<table width="924" align="center">
		<tr>
			<td class="TableRowText01">Dataset Name</td>
			<td class="TableRowText01">Number of Compounds</td>
			<td class="TableRowText01">Dataset Type</td>
			<td class="TableRowText01">Date Created</td>
			<td class="TableRowText01">Description</td>
			<td class="TableRowText01">Paper Reference</td>
		</tr>
		<tr>
			<td class="TableRowText02"><s:property value="dataset.fileName" /></td>
			<td class="TableRowText02"><s:property value="dataset.numCompound" /></td>
			<td class="TableRowText02"><s:property value="dataset.datasetType" /></td>
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
	 The compounds of the external set are shown in the second tab.</p>
	</s:elseif>
	<!-- End page description -->
	
	
	<s:url id="datasetCompoundsLinkTwo" value="/viewDatasetCompoundsSection" includeParams="none">
		<s:param name="currentPageNumber" value='3' />
		<s:param name="orderBy" value='orderBy' />
		<s:param name="datasetId" value='datasetId' />
	</s:url>

	<s:url id="datasetCompoundsLink" value="/viewDatasetCompoundsSection" includeParams="none">
		<s:param name="currentPageNumber" value='currentPageNumber' />
		<s:param name="orderBy" value='orderBy' />
		<s:param name="datasetId" value='datasetId' />
	</s:url>
		
	<!-- load tabs -->
	<a name="tabs"></a> 
	<sx:tabbedpanel id="viewDatasetTabs" >
	
    	<sx:div href="%{datasetCompoundsLink}" id="allCompoundsDiv" executeScripts="true" label="All Compounds" theme="ajax" loadingText="Loading compounds..." preload="false">
		</sx:div>
		
		<s:if test="dataset.datasetType=='MODELING'">
			<s:url id="externalCompoundsLink" id="externalCompoundsDiv" value="/viewDatasetExternalCompoundsSection" includeParams="none">
				<s:param name="datasetId" value='datasetId' />
			</s:url>
			
			<sx:div href="%{externalCompoundsLink}" label="External Set" theme="ajax" loadingText="Loading external compounds...">
			</sx:div>
		</s:if>
		
		<s:url id="heatmapLink" value="/viewDatasetVisualizationSection" includeParams="none">
			<s:param name="datasetId" value='datasetId' />
		</s:url>
		<sx:div href="%{heatmapLink}" label="Heatmap" theme="ajax" loadingText="Loading heatmap..." preload="false">
		</sx:div>
		
		<s:url id="warningsLink" value="/viewDatasetWarningsSection" includeParams="none">
			<s:param name="datasetId" value='datasetId' />
		</s:url>
		<sx:div href="%{warningsLink}" label="Warnings" theme="ajax" loadingText="Loading warnings..." preload="false">
		</sx:div>
		
   	</sx:tabbedpanel>
   	
	<!-- end load tabs -->
	
	
<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>