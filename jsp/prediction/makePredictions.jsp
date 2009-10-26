<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
    <title>C-CHEMBENCH | Make Predictions</title>
    
    <link href="theme/ss.css" rel="stylesheet" type="text/css" />
	<link href="theme/ajaxtabs.css" rel="stylesheet" type="text/css" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
	<link href="theme/links.css" rel="stylesheet" type="text/css" />
	<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
	<link rel="icon"  href="theme/img/mml.ico" type="image/ico" />
	<link rel="SHORTCUT ICON" href="theme/img/mml.ico" />
	
	<script language="javascript" src="javascript/script.js"></script>
	<script language="javascript" src="javascript/modeling.js"></script>
	<script src="javascript/predictorFormValidation.js"></script>
	
	<script language="javascript">
	var usedDatasetNames = new Array(<s:iterator value="userDatasetNames">"<s:property />",</s:iterator>"");
	var usedPredictorNames = new Array(<s:iterator value="userPredictorNames">"<s:property />",</s:iterator>"");
	var usedPredictionNames = new Array(<s:iterator value="userPredictionNames">"<s:property />",</s:iterator>"");
	var usedTaskNames = new Array(<s:iterator value="userTaskNames">"<s:property />",</s:iterator>"");
	
	function predictSmiles(){
		var smiles = document.getElementById("smiles").value;
		var cutoff = document.getElementById("cutOffSmiles").value;
		if(cutoff == "" || smiles == ""){
			alert("Please enter a SMILES string and cutoff value.");
			return false;
		}
		else{
			
			//prepare the AJAX object
			var ajaxObject = GetXmlHttpObject();
			ajaxObject.onreadystatechange=function(){
				if(ajaxObject.readyState==4){
					hideLoading();
				  	document.getElementById("smilesResults").innerHTML=ajaxObject.responseText;
				}
			}
		
			showLoading("PREDICTING. PLEASE WAIT.")
		
			//send request
			var url="makeSmilesPrediction?smiles=" + smiles + "&cutoff=" + cutoff + "&predictorIds=" + '<s:property value="selectedPredictorIds" />';
			ajaxObject.open("GET",url,true);
			ajaxObject.send(null);
			
			return true;
		}
	}
	</script>
</head>

<body bgcolor="#ffffff">
	<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
	<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
	<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
	
	<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
		<tr>
			<td height="557" colspan="5" valign="top"
				background="theme/img/backgrmodelbuilders.jpg" style="background-repeat: no-repeat;"><span id="maincontent">
			
			<table width="465" border="0" cellspacing="0" cellpadding="0">
	          <tr>
	            <td>
					<!-- <p class="StandardTextDarkGrayParagraph"><b><br>C-Chembench Predictions</b></p> -->
			<p align="justify" class="StandardTextDarkGrayParagraph">
			<!-- description of predictions process goes here -->
			<br><br>
			</p>
			</td>
	   </tr>
	</table>

	<s:form action="makeDatasetPrediction" enctype="multipart/form-data" theme="simple">
	<table width="924" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
			<tbody>
				<tr>
					<td align="left" colspan="2">
					<div class="StandardTextDarkGrayParagraph2" align="left"><b>Dataset Prediction</b></div><br />
					</td>
					<td>
					</td>
			    </tr> 
				<tr>
				<td>
				<table><tr><td>
					<div class="StandardTextDarkGray"><b>Chosen Predictors:</b></div>
				</td>
				<td height="26">
				<div class="StandardTextDarkGray"><b><s:iterator value="selectedPredictors"><s:property value="name"/> &nbsp;</s:iterator></b></div>
				</td>
				</tr> 
				<tr>
					<td height="26">
					<div align="right" class="StandardTextDarkGray"><b>Select a Dataset:</b></div>
					</td>
					<td align="left" valign="top"><s:select name="selectedDatasetId" list="userDatasets" id="selectedDataset" listKey="fileId" listValue="fileName" />
					<div class="StandardTextDarkGrayParagraph"><i>(Use the "DATA MGMT" page to create datasets.)</i></div>
					</td>
				</tr>		
				<tr>
					<td height="26">
					<div align="right" class="StandardTextDarkGray"><b>Similarity Cut
					Off:</b></div>
					</td>
					<td align="left" valign="top"><s:textfield name="cutOff" id="cutOff" size="4" /><span id="messageDiv2"></span></td>
				</tr>
				<tr>
					<td height="26">
					<div align="right" class="StandardTextDarkGray"><b>Prediction Name:</b></div>
					</td>
					<td width="400" align="left" valign="top"><s:textfield name="jobName" id="jobName" size="19"/><span id="messageDiv1"></span></td>
				</tr>
				<tr>
					<td><s:hidden name="selectedPredictorIds" /></td>
					<td align="left" valign="top"><input type="button" name="userAction" id="userAction" onclick="if(validateObjectNames(document.getElementById('jobName').value, usedDatasetNames, usedPredictorNames, usedPredictionNames, usedTaskNames)){ submitForm3(this); }" 
					value="Submit Prediction Job" /> <span id="textarea"></span></td>
				</tr>
				</table></td></tr>
			</tbody>
		</table>
	</s:form>


	<br />
	<table width="924" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>
		<tr>
		<td valign="top">
			<table width="450" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" >
			<tbody>
				
			<tr>
				<td width="100%" height="24" align="left" colspan="2">
				<p class="StandardTextDarkGrayParagraph2">
				<b>Enter a SMILES string</b>
				</p>
				</td>
			</tr>
			<tr>
				<td align="left" colspan="2">
				<p  class="StandardTextDarkGrayParagraph">
				Enter a molecule in SMILES format, e.g. <b>C1=CC=C(C=C1)CC(C(=O)O)N</b> (phenylalanine).
				Or, use the applet on the right to draw a molecule, then click "Get SMILES".
				</p>
			</tr>
			<tr>
				<td width="70" height="24" align="right">
				<div align="right" class="StandardTextDarkGray"><b>SMILES:</b></div>
				</td>
				<td width="150" align="left" valign="top"><input type="text" name="smiles" id="smiles" size="30" value=""/>
				<span id="messageDiv2"></span></td>
			</tr>
			<tr>
				<td width="70" height="26" align="left">
				<div align="right" class="StandardTextDarkGray"><b>Similarity Cut
				Off:</b></div>
				</td>
				<td align="left" valign="top"><input type="text" id="cutOffSmiles" size="4" value="0.5" /><span id="messageDiv3"></span></td>
			</tr>
			<tr>
				<td width="70" height="24" align="right">
				<div align="left" class="StandardTextDarkGray">&nbsp;</div>
				</td>
				<td align="left" valign="top"><input type="button" onclick="predictSmiles()" value="Predict" /> <span id="textarea"></span></td>
			</tr>
			<tr>
				<td width="70" height="26" align="left" colspan="3">
				<div class="StandardTextDarkGrayParagraph" id="smilesResults"><i>Your SMILES prediction results will appear here.</i></div>
				</td>
				<td align="left" valign="top"><span id="messageDiv2"></span></td>
			</tr>
				</tbody>
				</table>
		</td>
		<td style="width: 10">
		</td>
		<td>
			<table frame="border" align="center" cellpadding="0" cellspacing="4">
			<tbody>
			<tr>
			<td>
<script language="JavaScript1.1" src="javascript/marvin.js"></script>
<script language="JavaScript1.1">
<!--
function exportMol() {
	
	if(document.MSketch != null) {
		var s = document.MSketch.getMol('smiles:');
		s = unix2local(s); // Convert "\n" to local line separator
		document.getElementById("smiles").value = s;
	} else {
		alert("Cannot import molecule:\n"+
		      "no JavaScript to Java communication in your browser.\n");
	}
}

msketch_name = "MSketch";
msketch_mayscript = true;
msketch_begin("/jchem/marvin/", 440, 300);
msketch_end();
document.MSketch.style.zIndex="-1";
//-->

</script><br /><input type="button" value="Get SMILES" property="text" onclick="exportMol()"/>
	<input type="button" value="Clear" property="text" onclick="if(document.MSketch!=null) document.MSketch.setMol('');"/>

 	</td>
	</tr>
	</tbody>
	</table>
		</td>
	</tr>
		
	</tbody>
	</table>

	</td>
	</tr>
</table>	

<%@include file ="/jsp/main/footer.jsp" %>
</div>
</body>
</html>	