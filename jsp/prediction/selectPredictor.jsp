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
<script language="JavaScript" src="javascript/sortableTable.js"></script>

</head>
<body onload="setTabToPrediction();">

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
		From this page, predictors are available. You may use them to make predictions on compounds. 
		To start with, predictors generated and validated by UNC's Molecular Modeling Laboratory are available. 
		When you create new predictors using the MODELING page, they will appear here as well, under "Private Predictors". 
		<br><br>For more information about making predictions, try the <a href="/help-prediction">Prediction help page</a>.  
		<br><br>Click the checkboxes to the left of each predictor you want to use, and hit the "Select Predictors" button. Then you may predict the activity of a dataset, a SMILES string, or a molecule sketch.
			
		<br /><br />
		</p></td>
          </tr>
        </table>
		<b></b>
		
		<s:form theme="simple" action="selectPredictor" enctype="multipart/form-data" method="post">
		<table border="0" align="left" cellpadding="4" cellspacing="4">
		<tbody>
		<tr>
		<td>
		
		<s:if test="user.showPublicPredictors!='NONE'" >		
			<p class="StandardTextDarkGrayParagraph">
			<b>Drug Discovery Predictors</b>
			</p>
			<p align="justify" class="StandardTextDarkGrayParagraph">
			These are public predictors useful for virtual screening.
			</p>
				<table width="100%" class="sortable" id="drugdisc">
				<tr>
					<th class="TableRowText01narrow_unsortable">Select</th>
					<th class="TableRowText01narrow">Name</td>
					<th class="TableRowText01narrow">Date Created</th>
					<th class="TableRowText01narrow">Modeling Method</th>
					<th class="TableRowText01narrow">Descriptor Type</th>
					<th class="TableRowText01narrow_unsortable" colspan="2">Description</th>
				</tr>
				<s:iterator value="userPredictors">
					<s:if test="predictorType=='DrugDiscovery'">
						<tr>
						<td class="TableRowText02narrow"><s:checkbox name="predictorCheckBoxes" fieldValue="%{predictorId}" /></td>
						<td class="TableRowText02narrow"><s:property value="name" /></td>
						<td class="TableRowText02narrow"><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
						<td class="TableRowText02narrow"><s:property value="modelMethod" /></td>
						<td class="TableRowText02narrow"><s:property value="descriptorGeneration" /></td>
						<td class="TableRowText02narrow" colspan="2"><s:property value="description" /></td>
						</tr> 
					</s:if>
				</s:iterator>
				
				</table>
				<br /><br />
			
			<p class="StandardTextDarkGrayParagraph">
			<b>ADME Predictors</b>
			</p>
			<p align="justify" class="StandardTextDarkGrayParagraph">
			These are public predictors useful for prediction of absorption, distribution, metabolism, and excretion properties.
			</p>
				<table width="100%" class="sortable" id="adme">
				<tr>
					<th class="TableRowText01narrow_unsortable">Select</th>
					<th class="TableRowText01narrow">Name</td>
					<th class="TableRowText01narrow">Date Created</th>
					<th class="TableRowText01narrow">Modeling Method</th>
					<th class="TableRowText01narrow">Descriptor Type</th>
					<th class="TableRowText01narrow_unsortable" colspan="2">Description</th>
				</tr>
				<s:iterator value="userPredictors">
					<s:if test="predictorType=='ADME'">
						<tr>
						<td class="TableRowText02narrow"><s:checkbox name="predictorCheckBoxes" fieldValue="%{predictorId}" /></td>
						<td class="TableRowText02narrow"><s:property value="name" /></td>
						<td class="TableRowText02narrow"><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
						<td class="TableRowText02narrow"><s:property value="modelMethod" /></td>
						<td class="TableRowText02narrow"><s:property value="descriptorGeneration" /></td>
						<td class="TableRowText02narrow" colspan="2"><s:property value="description" /></td>
						</tr> 
					</s:if>
				</s:iterator>
				</table>
				<br /><br />
				
				
			<p class="StandardTextDarkGrayParagraph">
			<b>Toxicity Predictors</b>
			</p>
			<p align="justify" class="StandardTextDarkGrayParagraph">
			These are public predictors useful for toxicity prediction.
			</p>
				<table width="100%" class="sortable" id="toxicity">
				<tr>
					<th class="TableRowText01narrow_unsortable">Select</th>
					<th class="TableRowText01narrow">Name</td>
					<th class="TableRowText01narrow">Date Created</th>
					<th class="TableRowText01narrow">Modeling Method</th>
					<th class="TableRowText01narrow">Descriptor Type</th>
					<th class="TableRowText01narrow_unsortable" colspan="2">Description</th>
				</tr>
				<s:iterator value="userPredictors">
					<s:if test="predictorType=='Toxicity'">
						<tr>
						<td class="TableRowText02narrow"><s:checkbox name="predictorCheckBoxes" fieldValue="%{predictorId}" /></td>
						<td class="TableRowText02narrow"><s:property value="name" /></td>
						<td class="TableRowText02narrow"><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
						<td class="TableRowText02narrow"><s:property value="modelMethod" /></td>
						<td class="TableRowText02narrow"><s:property value="descriptorGeneration" /></td>
						<td class="TableRowText02narrow" colspan="2"><s:property value="description" /></td>
						</tr> 
					</s:if>
				</s:iterator>
				</table>
				<br /><br />
		</s:if>	
	
		<p class="StandardTextDarkGrayParagraph">
			<b>Private Predictors</b>
			</p>
			<p align="justify" class="StandardTextDarkGrayParagraph">
			These are private predictors you have created. Other users cannot access them.
			</p>
			<table width="100%" class="sortable" id="private">
				<tr>
				<th class="TableRowText01narrow_unsortable">Select</th>
				<th class="TableRowText01narrow">Name</td>
				<th class="TableRowText01narrow">Date Created</th>
				<th class="TableRowText01narrow">Modeling Method</th>
				<th class="TableRowText01narrow">Descriptor Type</th>
				<th class="TableRowText01">Dataset</th>
			</tr>			
			<s:iterator value="userPredictors">
				<s:if test="predictorType=='Private'">
					<tr>
					<td class="TableRowText02"><s:checkbox name="predictorCheckBoxes" fieldValue="%{predictorId}" /></td>
					<td class="TableRowText02"><s:property value="name" /></td>
					<td class="TableRowText02"><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
					<td class="TableRowText02"><s:property value="modelMethod" /></td>
					<td class="TableRowText02"><s:property value="descriptorGeneration" /></td>
					<td class="TableRowText02"><s:property value="datasetDisplay" /></td>
					</tr> 
				</s:if>
			</s:iterator>
			</table>
			<br />	
			
			<br />
			<p class="StandardTextDarkGrayParagraph">
			<b>Choose Predictors</b>
			</p>
			<p align="justify" class="StandardTextDarkGrayParagraph">
			When you have checked the boxes next to the predictors you want to use, click on the button below. 
			</p>
			<table>
				<tr><td>
					&nbsp;&nbsp;&nbsp;&nbsp;<s:submit value="Select Predictors" />
				</td></tr>
			</table>
		
	
		</tr>
		</tbody>
		</table>
		</s:form>
		<br />
		</p></td></tr></table></td></span></tr></table>
<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html>