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
			<td class="TableRowText01" >Job Name</td>
			<td class="TableRowText01" >Date Created</td>
			<td class="TableRowText01" >SD File</td>
			<td class="TableRowText01" >ACT File</td>
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
			<%@include file ="/jsp/modeling/modelingResults.jsp" %>
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