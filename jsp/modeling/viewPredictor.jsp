<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>

<%@ page import="edu.unc.ceccr.persistence.Predictor" %>

<jsp:useBean id="user" class="edu.unc.ceccr.persistence.User" 	scope="session" />

<html:html>
<head>
<title>C-CHEMBENCH | Modeling Results</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link href="theme/standard.css" rel="stylesheet" 	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />

<script src="javascript/script.js"></script>
<script text="javascript">
function showYrandom()
{

  if(document.getElementById("yrandom").style.display=='inline')
{
document.getElementById("yrandom").style.display='none';}
else{document.getElementById("yrandom").style.display='inline';}

}
</script>


</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td>
<table width="924" border="0" align="center" cellpadding="0"	cellspacing="0">
	<%@include file="/jsp/main/header.jsp" %>
<%@include file="/jsp/main/centralNavigationBar.jsp" %>
	</td>
		</span>
	</tr>
	<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top">
		<p class="StandardTextDarkGray">&nbsp;</p>
		<span class="Errors"><b><html:errors /></b></span> 
		<span class="StandardTextDarkGray">
		<table width="924">
		<tr>
			<td class="TableRowText01">Job Name</td>
			<td class="TableRowText01">Date Created</td>
			<td class="TableRowText01">SD File</td>
			<td class="TableRowText01">ACT File</td>
			<td class="TableRowText01">Modeling Method</td>
			<td class="TableRowText01">Descriptor Generation Method</td>
		</tr>
		<tr>
			<td class="TableRowText02" ><bean:write name="selectedPredictor" property="name" /></td>
			<td class="TableRowText02" ><bean:write name="selectedPredictor" property="dateCreated" /></td>
			
			<td class="TableRowText02" ><span style="width:100"><bean:write name="selectedPredictor" property="sdFileName" /></span></td>
			<td class="TableRowText02" ><span style="width:100"><bean:write name="selectedPredictor" property="actFileName" /></span></td>
			
			<td class="TableRowText02" ><bean:write name="selectedPredictor" property="modelMethodDisplay" /></td>
			<td class="TableRowText02" ><bean:write name="selectedPredictor" property="descriptorGenerationDisplay" /></td>
		</tr>
		</table>
		<logic:empty name="allkNNValues">
		<br/><br/><br/>	<b class="StandardTextDarkGray">No models generated.</b><br/><br/><br/>
			<form action="modelbuilders.do"><input type="submit" value="Back to Model Building" /></form>&nbsp&nbsp&nbsp&nbsp&nbsp
			<form action="cleanmb.do"><input type="submit" value="Discard Model" /></form>
		</logic:empty>
		<logic:notEmpty name="allkNNValues">
		<p style="background:white;width:924;">
			<b>Of the <bean:write name="selectedPredictor" property="numTotalModels" /> models generated, 
			<bean:write name="selectedPredictor" property="numTrainModels" /> passed the training set criteria and 
			<bean:write name="selectedPredictor" property="numTestModels" /> passed both training and test set criteria. 
			<br />
			<logic:greaterEqual name="selectedPredictor" property="numTestModels" value="10">
                                                             The top 10 models are displayed below.</b></p>
			<br /></logic:greaterEqual>
                <logic:lessThan name="selectedPredictor" property="numTestModels" value="10">

                           The top <bean:write name="selectedPredictor" property="numTestModels" />
			models are displayed below.</b></p>
			<br /></logic:lessThan>
			
		<br />
		<table width="924" align="center">
		<tr>
		<td><form action="savemb.do"><input type="submit" value="Save Models" /></form></td>
		<td><form action="cleanmb.do"><input type="submit" value="Discard Models" /></form></td>
	<logic:equal name="KnnType" value="CONTINUOUS">
	<td>	<a href="model?modelName=<bean:write name='selectedPredictor' property='name' />&user=<bean:write name='user' property='userName' />"><u>Download Modeling Report</u></a></td>
	</logic:equal>	</tr>
	
	
	<!-- big included section starts here! -->
	<table width="100%" align="center">
	<logic:equal name="KnnType" value="CONTINUOUS">
	<tr>
		<td width="93" height="23" align="center" valign="middle"
			bgcolor="#0D439D" class="TableRowText01">nnn</td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">q<sup>2</sup></td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">n</td>
		<td class="TableRowText01">r</td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">r<sup>2</sup></td>
		<td width="94" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">R<sub>01</sub><sup>2</sup></td>
		<td width="94" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">R<sub>02</sub><sup>2</sup></td>
		<td width="94" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">k1</td>
		<td width="94" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">k2</td>
	</tr>
	<logic:iterate id="knnOutput" name="allkNNValues"
		type="edu.unc.ceccr.persistence.KnnOutput">
		<tr>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="nnn" format="#" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="q_squared" format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput" property="n"
				format="#" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput" property="r"
				format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="r_squared" format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="r01_squared" format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="r02_squared" format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="k1" format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="k2" format="#0.000" /></td>
		</tr>
	</logic:iterate>
	</logic:equal>
	<logic:equal name="KnnType" value="CATEGORY">
	<tr>
		<td width="93" height="23" align="center" valign="middle"
			bgcolor="#0D439D" class="TableRowText01">nnn</td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">Training Accuracy</td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">Normalized Training Accuracy</td>
		<td class="TableRowText01">Test Accuracy</td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">Normalized Test Accuracy</td>
	</tr>
	<logic:iterate id="knnOutput" name="allkNNValues" type="edu.unc.ceccr.persistence.KnnOutput">
		<tr>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="nnn" format="#" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="trainingAcc" format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput" property="normalizedTrainingAcc"
				format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput" property="testAcc"
				format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="normalizedTestAcc" format="#0.000" /></td>
		</tr>
	</logic:iterate>
	</logic:equal>		
	</table>
<br></br>

<p class="StandardTextDarkGray"><a href="#" onclick="showYrandom()"><u>Y Randomization Results</u></a></p>
<div id="yrandom" name="yrandom" style="display:none">

<p style="background:white;width:100%;">

<b>Y-randomization</b> is a statistical QSAR model validation technique where the performance of the model built with original data is compared to that of models built for multiple artificial datasets with
randomly shuffled activities. Ideally, there will be no models with high values(> 0.6) of both q<sup>2</sup> (training set) and R<sup>2 </sup>(test set) found.
			<br/>For your data, <font color="red"><bean:write name="selectedPredictor" property="numyTotalModels" /> </font> models for randomized datasets were built and <font color="red"><bean:write name="selectedPredictor" property="numyTestModels" /> </font>models were found to have high prediction accuracy.</p>
			<logic:greaterEqual name="selectedPredictor" property="numyTestModels" value="10">
             The top 10 models are displayed below.</b></p>
			<br /></logic:greaterEqual>


<table width="100%" align="center">
	<logic:equal name="KnnType" value="CONTINUOUS">
	<tr>
		<td width="93" height="23" align="center" valign="middle"
			bgcolor="#0D439D" class="TableRowText01">nnn</td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">q<sup>2</sup></td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">n</td>
		<td class="TableRowText01">r</td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">r<sup>2</sup></td>
		<td width="94" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">R<sub>01</sub><sup>2</sup></td>
		<td width="94" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">R<sub>02</sub><sup>2</sup></td>
		<td width="94" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">k1</td>
		<td width="94" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">k2</td>
	</tr>
	<logic:iterate id="knnOutput" name="randomKNNValues"
		type="edu.unc.ceccr.persistence.KnnOutput">
		<tr>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="nnn" format="#" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="q_squared" format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput" property="n"
				format="#" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput" property="r"
				format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="r_squared" format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="r01_squared" format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="r02_squared" format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="k1" format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="k2" format="#0.000" /></td>
		</tr>
	</logic:iterate>
	</logic:equal>
	<logic:equal name="KnnType" value="CATEGORY">
	<tr>
		<td width="93" height="23" align="center" valign="middle"
			bgcolor="#0D439D" class="TableRowText01">nnn</td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">Training Accuracy</td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">Normalized Training Accuracy</td>
		<td class="TableRowText01">Test Accuracy</td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">Normalized Test Accuracy</td>
	</tr>
	<logic:iterate id="knnOutput" name="randomKNNValues" type="edu.unc.ceccr.persistence.KnnOutput">
		<tr>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="nnn" format="#" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="trainingAcc" format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput" property="normalizedTrainingAcc"
				format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput" property="testAcc"
				format="#0.000" /></td>
			<td class="TableRowText02"><bean:write name="knnOutput"
				property="normalizedTestAcc" format="#0.000" /></td>
		</tr>
	</logic:iterate>
	</logic:equal>		
	</table>
</div>
<br/>


<p class="StandardTextDarkGray"><b>External Validation Results:</b>
<logic:equal name="KnnType" value="CONTINUOUS"> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp;
<a href="chart?user=<bean:write name='user' property='userName' />&project=<bean:write name='selectedPredictor' property='name' />" target="_blank"><u>Chart View</u></a>
</logic:equal></p>
<br></br>
<table width="100%" align="center">
	<!--DWLayoutTable-->
	<tr>
	<td width="93" height="31" align="center" valign="middle"
			bgcolor="#0D439D" class="TableRowText01">Compound ID</td>
		<td width="93" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">Structure</td>
		<td width="94" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">Observed Value</td>
		<td width="94" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">Predicted Value</td>
		<td width="94" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01">Residual</td>
		<td width="94" align="center" valign="middle" bgcolor="#0D439D"
			class="TableRowText01"># of Models</td>
	</tr>
	<logic:iterate id="externalValidation" name="allExternalValues"
		type="edu.unc.ceccr.persistence.ExternalValidation">
		<tr>
			<td class="TableRowText02"><bean:write name="externalValidation" property="compoundId" /></td>
			<td class="TableRowText02">
				<a href="#" onclick="window.open('compound3D?project=<bean:write name='selectedPredictor' property='name' />&projectType=modelbuilder&compoundId=<bean:write name='externalValidation' property='compoundId' />&user=<bean:write name='user' property='userName' />&datasetID=<bean:write name='selectedPredictor' property='datasetId' />', '<% new java.util.Date().getTime(); %>','width=350, height=350');">
				<img src="/imageServlet?projectType=modelbuilder&user=<bean:write name='user' property='userName' />&project=<bean:write name='selectedPredictor' property='name' />&compoundId=<bean:write name='externalValidation' property='compoundId' />&datasetID=<bean:write name='selectedPredictor' property='datasetId' />" border="0"/>
	            </a>
            </td>
			<td class="TableRowText02">
			<bean:write	name="externalValidation" property="actualValue"	format="#0.00" /></td>
			
			<td class="TableRowText02">
			<% if(externalValidation.getNumModels()>=3) { %>
			<bean:write	name="externalValidation" property="predictedValue"
				format="#0.00" />&#177;<bean:write name="externalValidation" property="standDev" format="#0.00" /></td>
			<%}else{
                        if(externalValidation.getNumModels()!=0) { %><bean:write
				name="externalValidation" property="predictedValue"
				format="#0.00" /><% } }%>
				</td>
				
			<%session.setAttribute("diff", externalValidation.getActualValue() - externalValidation.getPredictedValue());
				%>
			<td class="TableRowText02">
			<% if(externalValidation.getNumModels()!=0) { %><bean:write name="diff" format="#0.00" /><% } %></td>
			<td class="TableRowText02"><bean:write name="externalValidation" property="numModels" /></td>
		</tr>
	</logic:iterate>
</table>
</td>
</tr>
</table>
	
	
			<!-- big included section ends here! -->
			
	</logic:notEmpty>
	</span>
	<p>&nbsp;</p>
	</td>
	</span>
</tr>
<tr>
<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>
</html:html>