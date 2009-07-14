<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<jsp:useBean id="user" class="edu.unc.ceccr.persistence.User" scope="session"/>	

<html:html>
<head>
<title>C-CHEMBENCH | Modeling</title>

<link href="theme/ss.css" rel="stylesheet" type="text/css" />
<link href="theme/ajaxtabs.css" rel="stylesheet" type="text/css" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico" />
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" />
<bean:define id="continuousDatasets" type="java.util.List" name="continuousDatasets" scope="session"></bean:define>
<bean:define id="categoryDatasets" type="java.util.List" name="categoryDatasets" scope="session"></bean:define>
<bean:define id="datasetNames" type="java.util.List" name="datasetNames" scope="session"></bean:define>
<bean:define id="predictorNames" type="java.util.List" name="predictorNames" scope="session"></bean:define>
<bean:define id="predictionNames" type="java.util.List" name="predictionNames" scope="session"></bean:define>
<bean:define id="taskNames" type="java.util.List" name="taskNames" scope="session"></bean:define>

<script language="javascript" src="javascript/script.js"></script>
<script language="javascript" src="javascript/modeling.js"></script>
<script language="javascript" src="javascript/ajaxtabs.js"></script>

<script language="javascript">
var usedDatasetNames = new Array(<logic:iterate id="dn" name="datasetNames" type="String">"<bean:write name='dn'/>",</logic:iterate>"");
var usedPredictorNames = new Array(<logic:iterate id="pn" name="predictorNames" type="String">"<bean:write name='pn'/>",</logic:iterate>"");
var usedPredictionNames = new Array(<logic:iterate id="pn" name="predictionNames" type="String">"<bean:write name='pn'/>",</logic:iterate>"");
var usedTaskNames = new Array(<logic:iterate id="tn" name="taskNames" type="String">"<bean:write name='tn'/>",</logic:iterate>"");
</script>
</head>
<body bgcolor="#ffffff">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
	
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrmodelbuilders.jpg"><span id="maincontent">
		
		<table width="465" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
				<p class="StandardTextDarkGrayParagraph"><b><br>C-Chembench Model
		Development</b></p>
		<p align="justify" class="StandardTextDarkGrayParagraph">kNN QSAR models can currently
		be developed for any dataset using the Model Building section of this
		page. Upload an sdf file and activity file below. Jobs will be queued
		and will run as soon as computer resources are available. Some sample
		datasets are available for your use. Additional modeling techniques
		will soon be added.<br><br>
		Models can also be investigated and refined using the model analysis tab. <br><br>
		Current abilities include filtering through models. Additional tools such as
		those necessary for viewing descriptors selected by a model and seeing
		plots of predicted vs actual values for training and test sets will
		soon be available.<br><br></p>
		</td>
          </tr>
        </table>
        
		<html:form action="/submitQsarWorkflow.do" enctype="multipart/form-data">
			<!-- Dataset Selection -->
			<table width="94%" frame="border" align="center" cellpadding="0"	cellspacing="4" colspan="2">
				<tbody>		
				<tr>
					<td width="100%" height="24" align="left" colspan="2">
					<p class="StandardTextDarkGrayParagraph2">
					<b>Select a Dataset</b>
					</p>
					</td>
				</tr>	
				<tr>
					<td>
					<p class="StandardTextDarkGrayParagraph">
					<i>(Use the "DATA MGMT" page to create datasets.)</i>
					</p>
					</td>
			    </tr> 
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b><html:radio value="CONTINUOUS" styleId="continuousDataset" property="datasetType" onclick="setToContinuous(); disableUpload()">Choose a Continuous Dataset:</html:radio></b></div>
					<p class="StandardTextDarkGrayParagraph">
					<html:select property="selectedDatasetId" styleId="selectedContinuousDataset">
						<html:options collection="continuousDatasets" labelProperty="fileName" property="fileId" />
					</html:select>
					</p>
					</td>
			    </tr> 
			    <tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b><html:radio value="CATEGORY" styleId="categoryDataset" property="datasetType" onclick="setToCategory(); disableUpload()">Choose a Category Dataset:</html:radio></b></div>
					<p class="StandardTextDarkGrayParagraph">
					<html:select property="selectedDatasetId" disabled="true" styleId="selectedCategoryDataset">
						<html:options collection="categoryDatasets" labelProperty="fileName" property="fileId" />
					</html:select>
					</p>
					</td>
			    </tr>
				<tr>
				<td>
				<div class="StandardTextDarkGrayParagraph"><br /><html:button value="View Activity Histogram" property="text" onclick="showActivityHistogram()"/> <i> Opens in a new window. Check your browser settings if the new window does not appear.</i></div>
				<br />
				</td>
				</tr>
			    </tbody>
			 </table>
			<br />
			
			<!-- Descriptor Type Selection -->
			<table width="94%" frame="border" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>			
				<tr>
					<td width="100%" height="24" align="left" colspan="2">
					<p class="StandardTextDarkGrayParagraph2">
					<b>Select Descriptors</b>
					</p>
					</td>
				</tr>
				<tr><td><table>
				<tr>
					<td>
					<div class='StandardTextDarkGrayParagraph'><b>Descriptor Type:</b></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class='StandardTextDarkGray'>&nbsp;&nbsp;&nbsp;&nbsp;<html:radio value="MOLCONNZ" property="descriptorGenerationType">MolconnZ</html:radio></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class='StandardTextDarkGray'>&nbsp;&nbsp;&nbsp;&nbsp;<html:radio value="DRAGON" property="descriptorGenerationType">DRAGON</html:radio></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class='StandardTextDarkGray'>&nbsp;&nbsp;&nbsp;&nbsp;<html:radio value="MOE2D" property="descriptorGenerationType">MOE2D</html:radio></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class='StandardTextDarkGray'>&nbsp;&nbsp;&nbsp;&nbsp;<html:radio value="MACCS" property="descriptorGenerationType">MACCS</html:radio></div>
					</td>
				</tr>
				</table><br /></td></tr>
				
				
				<tr><td><table>
				<tr>
					<td>
					<div class='StandardTextDarkGrayParagraph'><b>Scaling Type:</b></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class='StandardTextDarkGray'>&nbsp;&nbsp;&nbsp;&nbsp;<html:radio value="RANGESCALING" property="scalingType">Range Scaling</html:radio></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class='StandardTextDarkGray'>&nbsp;&nbsp;&nbsp;&nbsp;<html:radio value="AUTOSCALING" property="scalingType">Auto Scaling</html:radio></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class='StandardTextDarkGray'>&nbsp;&nbsp;&nbsp;&nbsp;<html:radio value="NOSCALING" property="scalingType">None</html:radio></div>
					</td>
				</tr>
				</table><br /></td></tr>
				
			    </tbody>
			 </table>
			 <br />
			 
			 <!-- Data Split Parameters -->
			 <table width="94%" frame="border" align="center" cellpadding="0"	cellspacing="4" colspan="2">
				<tbody>	
			 	<tr>
					<td width="100%" height="24" align="left" colspan="2">
					<p class="StandardTextDarkGrayParagraph2">
					<b>Set Data Splitting Parameters</b>
					</p>
					</td>
				</tr>	
				<tr><td><table>
				<tr>
					<td colspan="2">
					<div class='StandardTextDarkGrayParagraph'><b><u>Division of Dataset into Modeling and External Sets:</u></b></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Number of Compounds in the External Set:</b></div>
					</td>
					<td align="left" valign="top"><html:text styleId="numCompoundsExternalSet" property="numCompoundsExternalSet" size="5"/></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Random Seed:</b></div>
					</td>
					<td align="left" valign="top"><html:text styleId="externalRandomSeed" property="externalRandomSeed" size="5"/> <input type="button" value="Get New Seed" onclick="getNewSeed()" /></td>
				</tr>	
				<tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><i>Using the same random seed each time will cause the same compounds to be in the external set.<br /></i></div>
					</td>
				</tr>	
				<tr>
					<td colspan="2">
					<div class='StandardTextDarkGrayParagraph'><b><u><br />Division of Modeling Set into Training and Test Sets:</u></b></div>
					</td>
				</tr>		
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Number of Data Splits:</b></div>
					</td>
					<td align="left" valign="top"><html:text property="numSplits" size="5"/></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Division Method:</b></div>
					</td>
					<td align="left" valign="top"><html:radio value="SPHEREEXCLUSION" property="trainTestSplitType" styleId="sphereExclusionTrainTest" onclick="sphereTrainTest()">Sphere Exclusion</html:radio> <html:radio value="RANDOM" property="trainTestSplitType" styleId="randomTrainTest" onclick="randomTrainTest()">Random</html:radio></td>
				</tr>		
				<tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><br /><i>The following parameters apply only if the Division Method is Random:</i></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Minimum Test Set Size (percent):</b></div>
					</td>
					<td align="left" valign="top"><html:text property="randomSplitMinTestSize" styleId="randomSplitMinTestSize" size="5"/></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Maximum Test Set Size (percent):</b></div>
					</td>
					<td align="left" valign="top"><html:text property="randomSplitMaxTestSize" styleId="randomSplitMaxTestSize" size="5"/></td>
				</tr>
				<tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><br /><i>The following parameters apply only if the Division Method is Sphere Exclusion:</i></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Force Minimum Activity Compound into all Training Sets:</b></div>
					</td>
					<td align="left" valign="top">
					<html:radio value="1" property="splitIncludesMin" styleId="splitIncludesMin">Yes</html:radio> <html:radio value="0" property="splitIncludesMin" styleId="splitIncludesMin">No</html:radio></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Force Maximum Activity Compound into all Training Sets:</b></div>
					</td>
					<td align="left" valign="top">
					<html:radio value="1" property="splitIncludesMax" styleId="splitIncludesMax">Yes</html:radio> <html:radio value="0" property="splitIncludesMax" styleId="splitIncludesMax">No</html:radio></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Minimum Test Set Size (percent):</b></div>
					</td>
					<td align="left" valign="top"><html:text property="sphereSplitMinTestSize" styleId="sphereSplitMinTestSize" size="5"/></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Selection of Next Training Set Point is Based on:</b></div>
					</td>
					<td> 
					<select name="selectionNextTrainPt" id="selectionNextTrainPt" value="0">
						<option value="0">Random Selection</option>
						<option value="1">Expand Outwards from Already Selected Points</option> <!-- SUM-MIN, tumor-like -->
						<option value="2">Even Coverage of Descriptor Space</option> <!-- MIN_MAX, lattice-like -->
						<option value="3">Work Inwards from Boundaries of Descriptor Space</option> <!-- SUM-MAX -->
					</select>			
					</td>
				</tr>					
				<tr>
					<td><br /></td>
				</tr>
				</table></td></tr>
			    </tbody>
			 </table>
			 <br />
			 	 
			 	 <table width="94%" align="center" cellpadding="0" cellspacing="0" colspan="2"><tr><td>
		<ul styleId="countrytabs" id="countrytabs" class="shadetabs">
		<li><a href="jsp/modeling/modeling-knn.jsp" rel="countrycontainer" class="selected">k-Nearest Neighbors</a></li>
		<li><a href="jsp/modeling/modeling-svm.jsp" rel="countrycontainer">Support Vector Machine</a></li>
		</ul>
		
		<div styleId="countrydivcontainer" id="countrydivcontainer" align="center" style="border-left:1px solid grey; border-top:1px solid grey; border-bottom:1px solid #000; border-right:1px solid #000; width:100%; margin-bottom: 1em">
		Error loading modeling properties (Missing JSPs?)
		</div>
		<html:hidden property="modelingType" styleId="modelingType" value=""/>
		
		<script type="text/javascript">
			var countries=new ddajaxtabs("countrytabs", "countrydivcontainer")
			countries.setpersist(true)
			countries.setselectedClassTarget("link") //"link" or "linkparent"
			countries.init()
		</script> 
		</td></tr>
		</table>
		<br />
			
			 <!-- Begin Modeling Job -->
			 <table width="94%" frame="border" align="center" cellpadding="0"	cellspacing="4" colspan="2">
				<tbody>	
				 <tr>
					<td width="100%" height="24" align="left" colspan="2">
					<p class="StandardTextDarkGrayParagraph2">
					<b>Start Job</b>
					</p>
					</td>
				</tr>
				<tr>
					<td width="100%" colspan="2">
					<div class="StandardTextDarkGrayParagraph" id="submitMessage">
					<i>Please enter a name for the predictor you are creating.</i>
					</div>
					</td>
			    </tr> 
			    <tr>
					<th width="72" height="24" align="right">
					<div class="StandardTextDarkGrayParagraph"><b>Predictor Name:</b></div>
					</th>
					<td width="325" align="left" valign="top"><html:text name="knnCon19" styleId="jobName" property="jobName" size="19" value="" /></td>
				</tr>
				<tr>
					<th width="72" height="24" align="right"></th>
					<td class="" valign="top"><input type="button" name="userAction" id="userAction" onclick="if(validateObjectNames(document.getElementById('jobName').value ,usedDatasetNames, usedPredictorNames, usedPredictionNames, usedTaskNames)){ submitForm(this); }"
						value="Submit Modeling Job" /> <span id="textarea"></span></td>
				</tr>
				</tbody>
			</table>
			<br />
			
		</html:form>
		</span></td>
	</tr>
	
	<tr><td>
	<br />
	<p class="StandardTextDarkGrayParagraph2"><b>Previously Generated Predictors</b></p>
			<p class="StandardTextDarkGray">Select a predictor and press the Edit button to view the models associated with the predictor.</p>
			<html:form action="/viewModels.do">
			<table>
				<tr>
					<td>
					<div class="StandardTextDarkGray"></div>
					</td>
					<td class="TableRowText01">Name</td>
					<td class="TableRowText01">Date Created</td>
					<td class="TableRowText01">Modeling Type</td>
					<td class="TableRowText01">Descriptor Generation Method</td>
					<td class="TableRowText01">Download</td>
					
				</tr>
				<logic:iterate id="pred" name="predictors">
					<logic:notEqual name="pred" property="userName" value="_all">
					<tr>
						<td>
						<div class="StandardTextDarkGray"><input type="radio" name="selectedPredictorName" onclick="enableEdit()"
						value = '<bean:write name="pred" property="name" />'/></div>
						</td>
						<td class="TableRowText02"><bean:write name="pred" property="name" /></td>
						<td class="TableRowText02"><bean:write name="pred" property="dateCreated" /></td>
						<td class="TableRowText02"><bean:write name="pred" property="modelMethodDisplay" /></td>
						<td class="TableRowText02"><bean:write name="pred" property="descriptorGenerationDisplay" /></td>
						<td class="TableRowText02">
						<a href="projectFilesServlet?project=<bean:write name='pred' property='name' />&user=<bean:write name='user' property='userName' />&projectType=modelbuilder">
						download</a></td>
						
					</tr>
					</logic:notEqual>
				</logic:iterate>
				<tr>
					<td>
					<div class="StandardTextDarkGray"></div>
					</td>
					<td><input type="submit" value="Edit" id="Edit" disabled="true"></input></td>
					<td>
					<div class="StandardTextDarkGray"></div>
					</td>
					<td>
					<div class="StandardTextDarkGray"></div>
					</td>
				</tr>
			</table>
			</html:form>
	</td></tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>