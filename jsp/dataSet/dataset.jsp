<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
<title>C-CHEMBENCH | Dataset Management</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>

<script src="javascript/script.js"></script>
<script src="javascript/dataset.js"></script>

<script language="javascript">
	var usedDatasetNames = new Array(<s:iterator value="userDatasetNames">"<s:property />",</s:iterator>"");
	var usedPredictorNames = new Array(<s:iterator value="userPredictorNames">"<s:property />",</s:iterator>"");
	var usedPredictionNames = new Array(<s:iterator value="userPredictionNames">"<s:property />",</s:iterator>"");
	var usedTaskNames = new Array(<s:iterator value="userTaskNames">"<s:property />",</s:iterator>"");
</script>

</head>
<body>
<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>

<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td colspan="5" valign="top"  background="theme/img/backgrmodelbuilders.jpg">
      <table width="465" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td><p class="StandardTextDarkGrayParagraph"><b><br />
            C-Chembench Dataset Management</b></p>
              <p align="justify" class="StandardTextDarkGrayParagraph">This part of C-Chembench allows you to manage and explore data sets.  Aside from the basic functions of uploading and deleting data sets, this part of our web site lets you better understand the datasets that you are working with.<br />
                  <br />
                In order to properly evaluate the quality of a model, you will often want to understand the dataset from which it was built.  How similar or diverse are the compounds that you were using?  What were the key differentiators that exist? <br />
                <br />
                Capabilities offered in this section include the ability to look at distant matrices, various clustering techniques and principal component analysis of the datasets.  Options provide visualization and numeric results.<br />
                <br />
            </p></td>
        </tr>
      </table>
   </td>
  </tr>
  <tr>
    <td>
    <s:form action="submitDataset" enctype="multipart/form-data" method="post" theme="simple">
     
   	<!-- Upload Files -->
	<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>			
		<tr>
			<td height="24" align="left" colspan="2">
			<p class="StandardTextDarkGrayParagraph2">
			<br /><b>Upload Dataset Files</b>
			</p><br />
			</td>
		</tr>
		<tr><td colspan="2">
		<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
    	<sx:tabbedpanel id="datasetTypeTabs">
    	
	    	<sx:div id="typeModPred" href="/loadModAndPredSection" label="Modeling and Prediction Set" theme="ajax" loadingText="Loading dataset types...">
			</sx:div>
			
			<sx:div id="typeModOnly" href="/loadModelingOnlySection" label="Modeling-Only Set" theme="ajax" loadingText="Loading dataset types...">
			</sx:div>
			
			<sx:div id="typePredOnly" href="/loadPredictionOnlySection" label="Prediction-Only Set" theme="ajax" loadingText="Loading dataset types...">
			</sx:div>
			
    	</sx:tabbedpanel>
    	</td></tr></table>
    	</td></tr>
    	</tbody>
    </table>
	<br />
	
	<!-- Define External Set -->
	<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>		
		<tr>
			<td height="24" align="left" colspan="2">
			<p class="StandardTextDarkGrayParagraph2">
			<br /><b>Define External Set</b>
			</p></td>
		</tr>
		<tr>
			<td colspan="2">
			<div class="StandardTextDarkGrayParagraph"><i>A subset of the compounds in the dataset will be reserved for external validation of models.</i></div></td>
		</tr>	
		<tr>
			<td colspan="2">
			</td>
		</tr>	
		<tr>
			<td colspan="2">
			<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
			<sx:tabbedpanel id="splitTypeTabs">
    	
		    	<sx:div id="randomSplit" href="/loadAutoExternalSection" label="Random Split" theme="ajax" loadingText="Loading dataset types...">
				</sx:div>
	    	
				<sx:div id="userSplit" href="/loadManualExternalSection" label="Choose Compounds" theme="ajax" loadingText="Loading dataset types...">
				</sx:div>
				
	    	</sx:tabbedpanel>
	    	</td></tr></table>
	    	</td>
	    </tr>	
		</tbody>
	</table>
	<br />
	
	<!-- Generate Visualizations -->
<!-- 	<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>			
		<tr>
			<td height="24" align="left" colspan="2">
			<p class="StandardTextDarkGrayParagraph2">
			<br /><b>Generate Visualizations</b>
			</p></td>
		</tr>
		<tr>
			<td colspan="2">
			<div class="StandardTextDarkGrayParagraph"><i>Pick which visualizations you would like available for this dataset.</i><br /></div></td>
		 </tr>	
		<tr>
			<td colspan="2"><div class="StandardTextDarkGrayParagraph">
			<input type="checkbox">Images of each compound</input><br />
			<input type="checkbox">Heatmap based on Tanimoto distances</input><br />
			<input type="checkbox">Heatmap based on Mahalanobis distances</input><br />
			<input type="checkbox">PCA plots</input></div></td>
		</tr>	
	</tbody>
	</table>
	<br /> -->
	
	<!-- Submit Dataset -->
	<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>			
		<tr><td height="24" align="left" colspan="2">
			<p class="StandardTextDarkGrayParagraph2">
			<br /><b>Create Dataset</b>
			</p>
		</td></tr>
		<tr>
			<td colspan="2">
			<div class="StandardTextDarkGrayParagraph"><i>A job will be started to generate visualizations and images for this dataset.</i></div></td>
		 </tr>	
		<tr>
			<td colspan="2">
			</td>
		</tr>	
		<tr>
			<td width="200"><div class='StandardTextDarkGrayParagraph'><b>Dataset Name:</b></div></td>
			<td align="left"><s:textfield name="datasetName" id="datasetName" size="40"/>
		  </td>
		</tr>
		<tr>
			<td><b class='StandardTextDarkGrayParagraph'>Reference (optional):</b></td>
			<td align="left"><s:textfield name="paperReference" id="paperReference" size="40"/>
			</td>
		</tr>
		<tr>
			<td><b class='StandardTextDarkGrayParagraph'>Dataset description:</b></td>
			<td align="left"><s:textarea name="dataSetDescription" id="dataSetDescription" style="height: 50px; width: 70%"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td align="left">
			<input name="userAction" id="userAction" onclick="if(validateObjectNames(document.getElementById('datasetName').value, usedDatasetNames, usedPredictorNames, usedPredictionNames, usedTaskNames)){ submitForm(this,document.getElementById('datasetName').value); }" value="Create Dataset" type="button" />
			</td>
		</tr>
		<tr>
			<!-- used for the "Please Wait..." box. Do not remove. -->
			<td colspan="2"><span id="pleaseWaitText"></span>
			<div id="messageDiv" style="color: red;"></div></td>
		</tr>
	</tbody>
	</table>
	
    </s:form>
    </td>
  </tr>
</table>
<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html>
