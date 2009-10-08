<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
<title>C-CHEMBENCH | Select Predictors</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>

<script src="javascript/script.js"></script>

</head>
<body>

<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	
	<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrpredictors.jpg" STYLE="background-repeat: no-repeat;">
		<p class="StandardTextDarkGray">&nbsp;</p>
		<p class="StandardTextDarkGrayParagraph">
		<b>C-ChemBench Predictors</b>
		</p>
		<table width="465" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td><p align="justify" class="StandardTextDarkGrayParagraph">
		Here already-developed models are available to make predictions on sets of compounds. Models generated and validated by the Laboratory for Molecular Modeling at UNC-CH are available as well as models that you generated through the Model Development section of the website. Compounds to screen can be upload in sdf format below. Currently, only 500 compounds may be predicted at one time. 
		<br><br>Compound databases will soon be available for large scale virtual screening. 
		<br><br>Click the name of a predictor. Then you may predict the activity of a dataset, a SMILES string, or a molecule sketch.
		<br />Or, you may select multiple predictors. To do this, use the checkboxes next to each one you want to predict with, then click "Select Multiple Predictors" at the bottom of the page.
		
		<br /><br />
		</p></td>
          </tr>
        </table>
		<b></b>
		
		<s:form theme="simple" action="selectPredictors" enctype="multipart/form-data" method="post">
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
				<td class="TableRowText01">Select</td>
				<td class="TableRowText01">Name</td>
				<td class="TableRowText01">Date Created</td>
				<td class="TableRowText01">Modeling Method</td>
				<td class="TableRowText01">Descriptor Type</td>
				<td class="TableRowText01">Dataset</td>
				<td class="TableRowText01">Description</td>
			</tr>
			<s:iterator value="userPredictors">
				<s:if test="predictorType=='DrugDiscovery'">
					<tr>
					<s:url id="predictorLink" value="/selectPredictor" includeParams="none">
						<s:param name="id" value='predictorId' />
					</s:url>
					<td class="TableRowText02"><s:checkbox name="checkBoxSet" fieldValue="%{predictorLink}" /></td>
					<td class="TableRowText02"><s:a href="%{predictorLink}"><s:property value="name" /></s:a></td>
					<td class="TableRowText02"><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
					<td class="TableRowText02"><s:property value="modelMethodDisplay" /></td>
					<td class="TableRowText02"><s:property value="descriptorGenerationDisplay" /></td>
					<td class="TableRowText02"><s:property value="datasetDisplay" /></td>
					<td class="TableRowText02"><s:property value="description" /></td>
					</tr> 
				</s:if>
			</s:iterator>
			
			</table>
			<br /><br />
			
			
		<p class="StandardTextDarkGrayParagraph">
		<b>ADME/Tox Predictors</b>
		</p>
		<p align="justify" class="StandardTextDarkGrayParagraph">
		These are public predictors useful for toxicity prediction.
		</p>
			<table>
			<tr>
				<td class="TableRowText01">Select</td>
				<td class="TableRowText01">Name</td>
				<td class="TableRowText01">Date Created</td>
				<td class="TableRowText01">Modeling Method</td>
				<td class="TableRowText01">Descriptor Type</td>
				<td class="TableRowText01">Dataset</td>
				<td class="TableRowText01">Description</td>
			</tr>
			<s:iterator value="userPredictors">
				<s:if test="predictorType=='ADMETox'">
					<tr>
					<s:url id="predictorLink" value="/selectPredictor" includeParams="none">
						<s:param name="id" value='predictorId' />
					</s:url>
					<td class="TableRowText02"><s:checkbox name="checkBoxSet" fieldValue="%{predictorLink}" /></td>
					<td class="TableRowText02"><s:a href="%{predictorLink}"><s:property value="name" /></s:a></td>
					<td class="TableRowText02"><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
					<td class="TableRowText02"><s:property value="modelMethodDisplay" /></td>
					<td class="TableRowText02"><s:property value="descriptorGenerationDisplay" /></td>
					<td class="TableRowText02"><s:property value="datasetDisplay" /></td>
					<td class="TableRowText02"><s:property value="description" /></td>
					</tr> 
				</s:if>
			</s:iterator>
			</table>
			<br /><br />
		
		<p class="StandardTextDarkGrayParagraph">
			<b>Private Predictors</b>
			</p>
			<p align="justify" class="StandardTextDarkGrayParagraph">
			These are private predictors you have created or uploaded. Other users cannot access them.
			</p>
			<table>
			<tr>
				<td class="TableRowText01">Select</td>
				<td class="TableRowText01">Name</td>
				<td class="TableRowText01">Date Created</td>
				<td class="TableRowText01">Modeling Method</td>
				<td class="TableRowText01">Descriptor Type</td>
				<td class="TableRowText01">Dataset</td>
				<td class="TableRowText01">Description</td>
			</tr>
			<s:iterator value="userPredictors">
				<s:if test="predictorType=='Private'">
					<tr>
					<s:url id="predictorLink" value="/selectPredictor" includeParams="none">
						<s:param name="id" value='predictorId' />
					</s:url>
					<td class="TableRowText02"><s:checkbox name="checkBoxSet" fieldValue="%{predictorLink}" /></td>
					<td class="TableRowText02"><s:a href="%{predictorLink}"><s:property value="name" /></s:a></td>
					<td class="TableRowText02"><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
					<td class="TableRowText02"><s:property value="modelMethodDisplay" /></td>
					<td class="TableRowText02"><s:property value="descriptorGenerationDisplay" /></td>
					<td class="TableRowText02"><s:property value="datasetDisplay" /></td>
					<td class="TableRowText02"><s:property value="description" /></td>
					</tr> 
				</s:if>
			</s:iterator>
			</table>
			<br />	

			<p class="StandardTextDarkGrayParagraph">
			<b>Choose Predictors</b>
			</p>
			<p align="justify" class="StandardTextDarkGrayParagraph">
			When you have checked the boxes next to the predictors you want to use, click on the button below. 
			</p>
			<table>
				<tr><td>
					<s:submit value="Select Multiple Predictors" />
				</td></tr>
			</table>
			</s:form>
		
	
		</tr>
		</tbody>
		</table>
		<br />
		
<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html>