<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<jsp:useBean id="anonUser" class="edu.unc.ceccr.persistence.User" scope="session" />
<%@ page import="edu.unc.ceccr.utilities.Utility" %>
<%@ page import="edu.unc.ceccr.persistence.User"%>
<% Utility u=new Utility();%>
<html:html>
<head>
<title>C-CHEMBENCH | Make Simple Predictions </title>
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

<jsp:useBean id="user" class="edu.unc.ceccr.persistence.User" scope="session" />
<%
if(anonUser == null){
	anonUser = new User();
	anonUser.setUserName(user.getUserName());	
}
%>

<body onUnLoad="document.MSketch=null">
<div id="bodyDIV"></div>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrpredictors.jpg">
		<p class="StandardTextDarkGray">&nbsp;</p>
		<p class="StandardTextDarkGrayParagraph">
		<b>C-ChemBench Predictors</b>
		</p>
		
		<table width="465" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td><p align="justify" class="StandardTextDarkGrayParagraph">
		Here already developed models are available to make predictions on sets of compounds. Models generated and validated by the Laboratory for Molecular Modeling at UNC-CH are available as well as models that you generated through the Model Development section of the website. Compounds to screen can be upload in sdf format below. Currently, only 500 compounds may be predicted at one time. 
		<br><br>Compound databases will soon be available for large scale virtual screening. 
		<br><br>Click the name of a predictor. Then you may predict the activity of a dataset, a SMILES string, or a molecule sketch.
<br><br></p></td>
          </tr>
        </table>
		<b><html:errors /></b>
		
		<html:form action="/execPredictor.do">
		<table border="0" align="left" cellpadding="4"	cellspacing="4">
		<tbody>
		<tr>
		<td>
			
		<p class="StandardTextDarkGrayParagraph">
		<b>Drug Discovery Predictors</b>
		</p>
		<p align="justify" class="StandardTextDarkGrayParagraph">
		These are public predictors useful for virtual screening.
		</p>
			<table>
			<tr>
				<td class="TableRowText01">Name</td>
				<td class="TableRowText01">Keywords</td>
				<td class="TableRowText01">Last Updated</td>
				<td class="TableRowText01">Paper Reference</td>
                   <td class="TableRowText01">Times Used</td>
			</tr>
			<logic:iterate id="p" type="edu.unc.ceccr.persistence.Predictor" name="predictors">
				<%
					if (p != null && p.getPredictorType() != null && p.getPredictorType().equalsIgnoreCase("DrugDiscovery")){
					%>
					<tr>
						<td class="TableRowText02"><a href="selectPredictor.do?id=<%=p.getPredictorId()%>"><bean:write name="p" property="name" /></a>
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="description" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="dateUpdated" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="paperReference" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="numPredictions" />
						</td>
					</tr>
				<%}%>
			</logic:iterate>
			</table>
			<br /><br />
			
		<p class="StandardTextDarkGrayParagraph">
		<b>Public ADME/Tox Predictors</b>
		</p>
		<p align="justify" class="StandardTextDarkGrayParagraph">
		These are public predictors useful for toxicity prediction.
		</p>
			<table>
			<tr>
				<td class="TableRowText01">Name</td>
				<td class="TableRowText01">Keywords</td>
				<td class="TableRowText01">Last Updated</td>
				<td class="TableRowText01">Paper Reference</td>
                   <td class="TableRowText01">Times Used</td>
			</tr>
			<logic:iterate id="p" type="edu.unc.ceccr.persistence.Predictor" name="predictors">
			<%
					if (p != null && p.getPredictorType() != null && p.getPredictorType().equalsIgnoreCase("ADMETox")){
					%>
					<tr>
						<td class="TableRowText02"><a href="selectPredictor.do?id=<%=p.getPredictorId()%>"><bean:write name="p" property="name" /></a>
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="description" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="dateUpdated" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="paperReference" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="numPredictions" />
						</td>
					</tr>
					<%} %>
			</logic:iterate>
			</table>
			<br />
			
		
			<table width="924" frame="border" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>
				<tr>
				<td valign="top">
					<table width="450" frame="border" align="center" cellpadding="0"	cellspacing="4" >
					<tbody>
						
					<tr>
						<td width="100%" height="24" align="left" colspan="2">
						<p class="StandardTextDarkGrayParagraph2">
						<b>Molecule to Predict</b>
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
						<td width="150" align="left" valign="top"><input type="text" name="smiles" id="smiles" size="30" value=""/>
						<html:hidden property="userName" value="<%=anonUser.getUserName()%>" /><span id="messageDiv2"></span></td>
						
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
	</html:form>

	</td>
	</tr>
</table>	

	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
	</tr>
</body>
</html:html>