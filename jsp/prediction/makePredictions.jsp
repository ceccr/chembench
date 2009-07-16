<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<jsp:useBean id="user" class="edu.unc.ceccr.persistence.User" scope="session" />
<jsp:useBean id="selectedPredictor" class="edu.unc.ceccr.persistence.Predictor" scope="session" />

<html:html>
<head>
<title>C-CHEMBENCH | Make Predictions</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>

<bean:define id="datasetNames" type="java.util.List" name="datasetNames" scope="session"></bean:define>
<bean:define id="predictorNames" type="java.util.List" name="predictorNames" scope="session"></bean:define>
<bean:define id="predictionNames" type="java.util.List" name="predictionNames" scope="session"></bean:define>
<bean:define id="taskNames" type="java.util.List" name="taskNames" scope="session"></bean:define>

<script src="javascript/yahoo/yahoo.js"></script>
<script src="javascript/dom/dom.js"></script>
<script src="javascript/event/event.js"></script>
<script src="javascript/container/container_core.js"></script>
<script src="javascript/predictorFormValidation.js"></script>
<script src="javascript/script.js"></script>
<script language="javascript">
var usedDatasetNames = new Array(<logic:iterate id="dn" name="datasetNames" type="String">"<bean:write name='dn'/>",</logic:iterate>"");
var usedPredictorNames = new Array(<logic:iterate id="pn" name="predictorNames" type="String">"<bean:write name='pn'/>",</logic:iterate>"");
var usedPredictionNames = new Array(<logic:iterate id="pn" name="predictionNames" type="String">"<bean:write name='pn'/>",</logic:iterate>"");
var usedTaskNames = new Array(<logic:iterate id="tn" name="taskNames" type="String">"<bean:write name='tn'/>",</logic:iterate>"");

function predictSmiles(){
	var smiles = document.getElementById("smiles").value;
	var cutoff = document.getElementById("cutoff").value;
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
		var url="smilesPredict.do?smiles=" + smiles + "&cutoff=" + cutoff;
		ajaxObject.open("GET",url,true);
		ajaxObject.send(null);
		
		return true;
	}
}

</script>

</head>
<body onUnLoad="document.MSketch=null">
<div id="bodyDIV"></div>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
<%@include file="/jsp/main/header.jsp" %>
<%@include file="/jsp/main/centralNavigationBar.jsp" %>
	<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrpredictors.jpg">
		<p class="StandardTextDarkGray">&nbsp;</p>
		
		<html:form action="/execPredictor.do">
		
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
							<div class="StandardTextDarkGray"><b>Chosen Predictor:</b></div>
						</td>
						<td height="26">
						<div class="StandardTextDarkGray"><bean:write name="selectedPredictor" property="name" /></div>
							<html:hidden property="selectedPredictorId" value="<%= selectedPredictor.getPredictorId().toString() %>" />
						</td>
						</tr> 
						<tr>
							<td height="26">
							<div align="right" class="StandardTextDarkGray"><b>Select a Dataset:</b></div>
							</td>
							<td align="left" valign="top"><html:select styleId="SDFileSelection" property="selectedDatasetID">
								<html:options collection="predictorDatabases" property="fileId" labelProperty="fileName" />
							</html:select> <div class="StandardTextDarkGrayParagraph"><i>(Use the "DATA MGMT" page to create datasets.)</i></div>
							</td>
						</tr>		
						<tr>
							<td height="26">
							<div align="right" class="StandardTextDarkGray"><b>Similarity Cut
							Off:</b></div>
							</td>
							<td align="left" valign="top"><html:text property="cutOff"
								size="4" value="0.5" /><span id="messageDiv2"></span></td>
						</tr>
						<tr>
							<td height="26">
							<div align="right" class="StandardTextDarkGray"><b>Prediction Name:</b></div>
							</td>
							<td width="400" align="left" valign="top"><html:text 
								property="jobName" styleId="jobName" size="19" value="" onchange="" /><span id="messageDiv1"></span></td>
						</tr>
						<tr>
							<td></td>
							<td align="left" valign="top"><input type="button" name="userAction" id="userAction" onclick="if(validateObjectNames(document.getElementById('jobName').value, usedDatasetNames, usedPredictorNames, usedPredictionNames, usedTaskNames)){ submitForm3(this); }" 
							value="Submit Prediction Job" /> <span id="textarea"></span></td>
						</tr>
						</table></td></tr>
					</tbody>
				</table>
			</html:form>
					
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
						<div align="right
						" class="StandardTextDarkGray"><b>SMILES:</b></div>
						</td>
						<td width="150" align="left" valign="top"><input type="text" name="smiles" id="smiles" size="30" value=""/><html:hidden property="selectedPredictorId" value="<%= selectedPredictor.getPredictorId().toString() %>" />
						<html:hidden property="username" value="<%=user.getUserName()%>" /><span id="messageDiv2"></span></td>
						
					</tr>
					<tr>
						<td width="70" height="26" align="left">
						<div align="right" class="StandardTextDarkGray"><b>Similarity Cut
						Off:</b></div>
						</td>
						<td align="left" valign="top"><html:text styleId="cutoff" property="cutoff"
							size="4" value="0.5" /><span id="messageDiv3"></span></td>
					</tr>
					<tr>
						<td width="70" height="24" align="right">
						<div align="left" class="StandardTextDarkGray">&nbsp;</div>
						</td>
						<td align="left" valign="top"><html:submit property="userAction2" onclick="predictSmiles()" value="Predict" /> <span id="textarea"></span></td>
					</tr>
					<tr>
						<td width="70" height="26" align="left" colspan="3">
						<div class="StandardTextDarkGrayParagraph" id="smilesResults"><!-- <i> Opens in a new window. Check your browser settings if the new window does not appear.</i> --></div>
						</td>
						<td align="left" valign="top"><span id="messageDiv2"></span></td>
					</tr>
						</tbody>
						</table>
	
				</td>
				<td style="width: 10">
				</td>
				<td>
					<table frame="border" align="center" cellpadding="0"	cellspacing="4">
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

</script><br /><html:button value="Get SMILES" property="text" onclick="exportMol()"/>
	<html:button value="Clear" property="text" onclick="if(document.MSketch!=null) document.MSketch.setMol('');"/>

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
</body>
</html:html>