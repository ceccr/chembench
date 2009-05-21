<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<%@ page import="edu.unc.ceccr.utilities.ActiveUser" %>
<% ActiveUser au= new ActiveUser();%>
<%@ page import="edu.unc.ceccr.utilities.Utility" %>
<%@ page import="edu.unc.ceccr.persistence.User"%>
<% Utility u=new Utility();%>

<html:html>
<head>
<title>C-CHEMBENCH | Make Predictions </title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="/theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" src="javascript/script.js"> </script>

</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br />





<body onUnLoad="document.MSketch=null">
<div id="bodyDIV"></div>
<table width="924" border="0" align="center" cellpadding="0"	cellspacing="0">
<%@include file="/jsp/main/header.jsp" %>
<%@include file="/jsp/main/centralNavigationBar.jsp" %>
	<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrpredictors.jpg">
		<p class="StandardTextDarkGray">&nbsp;</p>
		
		<html:form action="/execPredictor.do">
		
				<table width="924" frame="border" align="center" cellpadding="0"	cellspacing="4" colspan="2">
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
			<table width="924" frame="border" align="center" cellpadding="0"	cellspacing="4" colspan="2">
				<tbody>
				<tr>
				<td valign="top">
					<table width="450" frame="border" align="center" cellpadding="0"	cellspacing="4" >
					<tbody>
						
					<tr>
						<td width="100%" height="24" align="left" colspan="2">
						<p class="StandardTextDarkGrayParagraph2">
						<b>Predict by SMILES string</b>
						</p>
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2">
						<p  class="StandardTextDarkGrayParagraph">
						Enter a molecule in SMILES format, e.g. <b>C1=CC=C(C=C1)CC(C(=O)O)N</b> (phenylalanine).
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