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
		soon be available.<br><br></p></td>
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
					<div class="StandardTextDarkGrayParagraph"><b><html:radio value="CONTINUOUS" styleId="continuousDataset" property="knnType" onclick="setToContinuous(); disableUpload()">Choose a Continuous Dataset:</html:radio></b></div>
					<p class="StandardTextDarkGrayParagraph">
					<html:select property="selectedDatasetId" styleId="selectedContinuousDataset">
						<html:options collection="continuousDatasets" labelProperty="fileName" property="fileId" />
					</html:select>
					</p>
					</td>
			    </tr> 
			    <tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b><html:radio value="CATEGORY" styleId="categoryDataset" property="knnType" onclick="setToCategory(); disableUpload()">Choose a Category Dataset:</html:radio></b></div>
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
					<td>
					<div class='StandardTextDarkGray'><html:radio value="MOLCONNZ" property="descriptorGenerationType" onclick="">MolconnZ</html:radio></div>
					</td>
					<td>
					<div class='StandardTextDarkGray'><html:radio value="DRAGON" property="descriptorGenerationType" onclick="">DRAGON</html:radio></div>
					</td>
					<td>
					<div class='StandardTextDarkGray'><html:radio value="MOE2D" property="descriptorGenerationType" onclick="">MOE2D</html:radio></div>
					</td>
					<td>
					<div class='StandardTextDarkGray'><html:radio value="MACCS" property="descriptorGenerationType" onclick="">MACCS</html:radio></div>
					</td>
					<td width="30%">
					&nbsp;
					</td>
				</tr>
				<tr>
					<td colspan="4">
					<div class='StandardTextDarkGray'><i>Note: For some datasets, DRAGON descriptors cannot be calculated. You will see an error message when your job is running if DRAGON descriptors cannot be calculated for your chosen dataset.</i></div>
					</td>
				</tr>
				</table><br /></td></tr>
				<!--  We may want to implement other scaling types some day...
				If so, uncomment this.
				<tr>
					<td>
					<div class='StandardTextDarkGray'><b>Scaling:</b></div>
					</td>
					<td>
					<html:radio value="1" property="descriptorGenerationType" onclick="disableSDInput()">Range Scaling</html:radio>
					</td>
					<td>
					<html:radio value="0" property="descriptorGenerationType" onclick="disableSDInput()">Auto Scaling</html:radio>
					</td>	
					<td>
					<html:radio value="0" property="descriptorGenerationType" onclick="disableSDInput()">None</html:radio>
					</td>	
				</tr>	
				-->
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
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Number of Sphere Radii:</b></div>
					</td>
					<td align="left" valign="top"><input name="numSphereRadii" size="5" value="1"/></td>
				</tr>		
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Number of Starting Points:</b></div>
					</td>
					<td align="left" valign="top"><input name="numStartingPoints" size="5" value="2"/></td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Selection of Next Training Set Point is Based on:</b></div>
					</td>
					<td> 
					<select name="selectionNextTrainPt" value="3">
						<option value="3">Random Selection of Next Training Set Point</option>
						<option value="1">Minimum Sphere Center Distances</option>
						<option value="2">Maximum Sphere Center Distances</option>
					</select>			
					</td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Number of Compounds in the External Set:</b></div>
					</td>
					<td align="left" valign="top"><input name="numCompoundsExternalSet" size="5" value="5"/></td>
				</tr>						
				<tr>
					<td><br /></td>
				</tr>
				</table></td></tr>
			    </tbody>
			 </table>
			 <br />
			 
			 <!-- kNN Parameters -->
			 <table width="94%" frame="border" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>	
				<tr>
					<td width="100%" height="24" align="left" colspan="2">
					<p class="StandardTextDarkGrayParagraph2">
					<b>Set kNN Parameters</b>
					</p>
					</td>
				</tr>	
				<tr><td><table>
				
				<!-- kNN, Basic parameters  -->
				<tr><td colspan="2"><div class="StandardTextDarkGrayParagraph"><i>For information on what these parameters do, refer to the <u><a href="#help" onclick="window.open('/help.do'); return true;" >help pages</a></u>.<br /></i></div></td>
				</tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Descriptor Step Size:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon01" property="stepSize" size="5" value="5"/></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum Number of Descriptors:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon02" property="minNumDescriptors" size="5" value="5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Maximum Number of Descriptors:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon03" property="maxNumDescriptors" size="5" value="20"/></td></tr>
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Number of Runs:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon11" property="numRuns" size="5" value="5"/></td></tr>	
				
				<!-- kNN, Advanced Parameters -->
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Number of Nearest Neighbors:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon06" property="Nearest_Neighbors" size="5" value="5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Percentage of Pseudo Neighbors:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon07" property="Pseudo_Neighbors" size="5" value="100"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Number of Permutations:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon04" property="numMutations" size="5" value="2"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Number of Cycles:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon05" property="numCycles" size="5" value="1000"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Log Initial Temperature:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon08" property="T1" size="5" value="2"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Log Final Temperature:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon09" property="T2" size="5" value="-5"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Mu:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon10" property="mu" size="5" value="0.90"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Applicability Domain Cutoff:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon19" styleId="knnCon19" property="cutoff" size="5" value="1.0"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Stop Condition:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon20" styleId="knnCon20" property="stop_cond" size="5" value="50"/></td></tr>	
				<!-- Everything up to this point is used by both Continuous and Category kNN. -->
			 
				<!-- The following parameters are JUST for continuous kNN. -->
				<tr><td><div class="StandardTextDarkGrayParagraph"><b><u><br />Continuous kNN Parameters:</u></b></div></td>
				<td><br /><br /></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum q<sup>2</sup>:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon13" styleId="knnCon13" property="minAccTraining" size="5" value="0.6"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum r<sup>2</sup>:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon14" styleId="knnCon14" property="minAccTest" size="5" value="0.6"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum Slope:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon15" styleId="knnCon15" property="minSlopes" size="5" value="0.8"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Maximum Slope:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon16" styleId="knnCon16" property="maxSlopes" size="5" value="1.2"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Relative_diff_R_R0:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon17" styleId="knnCon17" property="relativeDiffRR0" size="5" value="0.2"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Diff_R01_R02:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCon18" styleId="knnCon18" property="diffR01R02" size="5" value="0.4"/></td></tr>	
				<!-- End continuous kNN parameters -->
				
				<!-- The parameters below are specific to Category kNN -->
				<tr><td><div class="StandardTextDarkGrayParagraph"><b><u><br />Category kNN Parameters:</u></b></div></td>
				<td><br /><br /></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum Accuracy for Training Set:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCat13" styleId="knnCat13" disabled="true" property="minAccTraining" size="5" value="0"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Minimum Accuracy for Test Set:</b></div></td>
				<td align="left" valign="top"><html:text name="knnCat14" styleId="knnCat14" disabled="true" property="minAccTest" size="5" value="0"/></td></tr>	
				<tr><td><div class="StandardTextDarkGrayParagraph"><b>Optimization Method:</b></div></td>
				<td>			
				<html:radio value="1" property="knnCategoryOptimization" disabled="true" styleId="knnCatOpt01"><img src="/theme/img/formula01.gif" /></html:radio>
				</td></tr><tr><td>&nbsp;</td><td>
				<html:radio value="2" property="knnCategoryOptimization" disabled="true" styleId="knnCatOpt02"><img src="/theme/img/formula02.gif" /></html:radio>
				</td></tr><tr><td>&nbsp;</td><td>
				<html:radio value="3" property="knnCategoryOptimization" disabled="true" styleId="knnCatOpt03"><img src="/theme/img/formula03.gif" /></html:radio>
				</td></tr><tr><td>&nbsp;</td><td>
				<html:radio value="4" property="knnCategoryOptimization" disabled="true" styleId="knnCatOpt04"><img src="/theme/img/formula04.gif" /></html:radio>
				</td></tr>
				<!-- End Category Specific kNN Parameters  -->
				
			 
				</table><br /></td></tr>
				</tbody>
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