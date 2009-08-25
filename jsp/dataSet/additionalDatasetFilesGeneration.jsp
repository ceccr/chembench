<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib prefix="c" uri="/application/jstl/core"%>
<%@ page isELIgnored="false" %>

<%@ page import="edu.unc.ceccr.global.Constants" %>
<html:html>
<head>
<title>C-CHEMBENCH | Additional dataset files generation!</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<script src="javascript/script.js"></script>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
function selectTanimoto(obj){
	if(obj.checked){
	if(document.viz.similarity_measure[0].checked!=true)
		//document.viz.similarity_measure[0].checked = true;
		document.viz.similarity_measure[0].checked=true;
	}
	if(document.viz.represent[0].checked==false && document.viz.represent[1].checked==false && document.viz.represent[2].checked==false) {
		document.viz.similarity_measure[0].checked=false;
		document.viz.similarity_measure[1].checked=false;
	}
}

function checkTanimoto(){
	if(document.viz.represent[0].checked==true || document.viz.represent[1].checked==true || document.viz.represent[2].checked==true){
		if(document.viz.similarity_measure[0].checked!=true && document.viz.similarity_measure[1].checked!=true){
			document.viz.similarity_measure[0].checked=true;
			}
	}
	else{
		document.viz.represent[0].checked = true;
	} 
	

}

function checkHeatmap(){
	document.viz.represent[0].checked=true;
}

function getParameter ( parameterName ) {
			var queryString = window.top.location.search.substring(1);
		// Add "=" to the parameter name (i.e. parameterName=value)
		var parameterName = parameterName + "=";
		if ( queryString.length > 0 ) {
		// Find the beginning of the string
		begin = queryString.indexOf ( parameterName );
		// If the parameter name is not found, skip it, otherwise return the value
		if ( begin != -1 ) {
		// Add the length (integer) to the beginning
		begin += parameterName.length;
		// Multiple parameters are separated by the "&" sign
		end = queryString.indexOf ( "&" , begin );
		if ( end == -1 ) {
		end = queryString.length
		}
		// Return the string
		
		return unescape ( queryString.substring ( begin, end ) );
		}
		// Return "null" if no parameter has been found
		
		return "null";
		}
} 

function readParam(){
	document.getElementById("datasetName").value = getParameter("datasetName");
	document.getElementById("sdfName").value = getParameter("sdfName");
	document.getElementById("actName").value = getParameter("actName");
	document.getElementById("knnType").value = getParameter("knnType");
}
</script>
</head>
<body onload="readParam();">
<table width="924px" border="0" align="center" cellpadding="0"	cellspacing="0">
	<%@include file="/jsp/main/header.jsp" %>
<%@include file="/jsp/main/centralNavigationBar.jsp" %>
		</td>
		</span>
	</tr>
		<tr>
		<span id="maincontent">
		<table width="924px" border="0" align="center" cellpadding="0"	cellspacing="0">
<tr>
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrlibrary.jpg">
		<p class="StandardTextDarkGray">&nbsp;</p>
<form name="viz" action="/generateDatasetVis.do"
			enctype="multipart/form-data">
		<div class="StandardTextDarkGray" align="left"><strong>Please choose what data representation you want to have:</strong><br/>
  	<input type="hidden" name="datasetName" id="datasetName" value="" />
	<input type="hidden" name="knnType" id="knnType" value=""/> 
	<input type="hidden" name="sdfName" id="sdfName" value=""/> 
	<input type="hidden" name="actName" id="actName" value=""/> 
  <!--&nbsp;&nbsp;&nbsp;&nbsp; <input name="sketches" type="checkbox" value="Sketches" checked/> 
  Sketches<br/> -->
  &nbsp;&nbsp;<strong>Representations:</strong><br/>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <input name="represent" type="checkbox" value="HeatmapAndTree" onclick="selectTanimoto(this);" checked/> 
  Heat map and Tree<br/>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <!--<input name="represent" type="checkbox" value="Trees" onclick="selectTanimoto(this);selectHeatmap();"/> 
  Tree<br/>
  &nbsp;&nbsp;&nbsp;&nbsp;-->
  <input name="represent" type="checkbox" value="PCA"/> 
  PCA plots<br/>    
  &nbsp;&nbsp;<strong>Similarity measure</strong><br/>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <input name="similarity_measure"  type="checkbox" value="Tanimoto" checked onclick="checkTanimoto();" /> 
  Tanimoto<br/>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <input name="similarity_measure"  type="checkbox" value="Mahalanobis" onclick="checkTanimoto();"/> 
  Mahalanobis<br/> 
  <br/>
</div>
<div class='StandardTextDarkGray'><input name="userAction" id="userAction" onclick="submitAdditionalDatasetOperations(this,document.getElementById('textarea'));" value="Process" type="button"></input></div>
<br/>
<span id="textarea"></span>
</form>
		&nbsp;&nbsp;<a href="manageFile.do"><font size=3 color='blue'><b><u>BACK TO DATASET</u></b></font></a></p><br/><br/><br/><br/>
            </td>
</tr>
</table>
		</span>

	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
