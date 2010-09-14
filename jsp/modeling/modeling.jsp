<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
    <title>CHEMBENCH | Modeling</title>
    
    <link href="theme/ss.css" rel="stylesheet" type="text/css" />
	<link href="theme/ajaxtabs.css" rel="stylesheet" type="text/css" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="theme/standard.css" rel="stylesheet" type="text/css" />
	<link href="theme/links.css" rel="stylesheet" type="text/css" />
	<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
	<link href="/theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />	
	<link rel="icon"  href="theme/img/mml.ico" type="image/ico" />
	<link rel="SHORTCUT ICON" href="theme/img/mml.ico" />
	
	<script language="javascript" src="javascript/script.js"></script>
	<script language="javascript" src="javascript/modeling.js"></script>
	
	<script language="javascript">
	var usedDatasetNames = new Array(<s:iterator value="userDatasetNames">"<s:property />",</s:iterator>"");
	var usedPredictorNames = new Array(<s:iterator value="userPredictorNames">"<s:property />",</s:iterator>"");
	var usedPredictionNames = new Array(<s:iterator value="userPredictionNames">"<s:property />",</s:iterator>"");
	var usedTaskNames = new Array(<s:iterator value="userTaskNames">"<s:property />",</s:iterator>"");
	
	var datasetId = -1;
	var selectedDatasetNumCompounds = -1;
	var selectedDatasetAvailableDescriptors = "";
	
	function getSelectedDataset(){
		//get the numCompounds and availableDescriptors for the currently selected dataset
		if(document.getElementById("categoryDataset").checked==true){
			datasetId = document.getElementById("selectedCategoryDataset").value;
			<s:iterator value="userCategoryDatasets">
			if(datasetId==<s:property value="fileId" />){
				selectedDatasetNumCompounds = <s:property value='numCompound' />;
				selectedDatasetAvailableDescriptors = "<s:property value='availableDescriptors' />";
			}
			</s:iterator>
		}
		else{
			datasetId = document.getElementById("selectedContinuousDataset").value;
			<s:iterator value="userContinuousDatasets">
				if(datasetId==<s:property value="fileId" />){
					selectedDatasetNumCompounds = <s:property value='numCompound' />;
					selectedDatasetAvailableDescriptors = "<s:property value='availableDescriptors' />";
				}
			</s:iterator>
		}
		
		//enable / disable based on the availableDescriptors
		if(selectedDatasetAvailableDescriptors.indexOf("MOLCONNZ") > -1){
			document.getElementById("descriptorGenerationType" + "MOLCONNZ").disabled = false;
		}
		else{
			document.getElementById("descriptorGenerationType" + "MOLCONNZ").disabled = true;
		}
		
		if(selectedDatasetAvailableDescriptors.indexOf("DRAGONH") > -1){
			document.getElementById("descriptorGenerationType" + "DRAGONH").disabled = false;
		}
		else{
			document.getElementById("descriptorGenerationType" + "DRAGONH").disabled = true;
		}
		
		if(selectedDatasetAvailableDescriptors.indexOf("DRAGONNOH") > -1){
			document.getElementById("descriptorGenerationType" + "DRAGONNOH").disabled = false;
		}
		else{
			document.getElementById("descriptorGenerationType" + "DRAGONNOH").disabled = true;
		}
		
		if(selectedDatasetAvailableDescriptors.indexOf("MACCS") > -1){
			document.getElementById("descriptorGenerationType" + "MACCS").disabled = false;
		}
		else{
			document.getElementById("descriptorGenerationType" + "MACCS").disabled = true;
		}
		
		if(selectedDatasetAvailableDescriptors.indexOf("MOE2D") > -1){
			document.getElementById("descriptorGenerationType" + "MOE2D").disabled = false;
		}
		else{
			document.getElementById("descriptorGenerationType" + "MOE2D").disabled = true;
		}
		
		if(selectedDatasetAvailableDescriptors.indexOf("UPLOADED") > -1){
			document.getElementById("descriptorGenerationType" + "UPLOADED").disabled = false;
		}
		else{
			document.getElementById("descriptorGenerationType" + "UPLOADED").disabled = true;
		}
		
		//recalculate time estimate for the newly selected dataset
		calculateRuntimeEstimate();
	}

	function calculateRuntimeEstimate(){
		//estimates runtime based on input parameters and displays it.
		//assumes that getDataset has assigned the value of selectedDatasetNumCompounds already.
		
		var timeEstimateDays = 0;
		var timeEstimateHours = 0;
		var timeEstimateMins = 0;
		
		var dataSplitMethod = document.getElementById("trainTestSplitType").value;
		var modelMethod = document.getElementById("modelingType").value;
		
		var numSplits;
		if(dataSplitMethod=="RANDOM"){
			numSplits = document.getElementById("numSplitsInternalRandom").value;
		}
		else{
			//sphere exclusion
			numSplits = document.getElementById("numSplitsInternalSphere").value;
		}
		
		//Time estimates were generated by taking the results from around 200 jobs
		//then removing outliers and plotting a trendline in Excel. Predictions 
		//look pretty accurate in there at least.
		if(modelMethod=="RANDOMFOREST"){
			timeEstimateMins = (selectedDatasetNumCompounds*numSplits*0.003)-5.718;
		}
		else if(modelMethod=="KNN-GA"){
			//var maxNumGenerations = document.getElementById("gaMaxNumGenerations").value;
			timeEstimateMins = (selectedDatasetNumCompounds*numSplits*0.05);
		}
		else if(modelMethod=="KNN-SA"){
			//depends on numRuns and needs a factor for convergence parameters (temperature etc).
			var numRuns = document.getElementById("saNumRuns").value;
			var numBest = document.getElementById("saNumBestModels").value;

			var numDifferentDescriptors = 1;

			var minDesc = document.getElementById("knnMinNumDescriptors").value;
			var maxDesc = document.getElementById("knnMaxNumDescriptors").value;
			var descSteps = document.getElementById("knnDescriptorStepSize").value;

			/*
			if(descSteps != 0){
				numDifferentDescriptors = Math.floor((maxDesc - minDesc)/ descSteps) + 1;
			}
			*/
						
			timeEstimateMins = numSplits *(numRuns*numBest*numDifferentDescriptors)*selectedDatasetNumCompounds*0.01;
		}
		
		
		//When UPS ships you a package, they artificially add one day to their 'estimated
		//arrival time'. When customers receive their package a day early? They say, wow,
		//UPS sure is fast! 
		//So we multiply job time estimates by an arbitrary amount, so that we can always
		//perform better than expectations :)
		var makeUsLookGood = 1.2;
		timeEstimateMins = timeEstimateMins * makeUsLookGood; 
		
		timeEstimateMins = Math.ceil(timeEstimateMins);
		if(timeEstimateMins <= 0){
			timeEstimateMins = 2;
		}
		
		
		var timeEstimateString = timeEstimateMins + " minutes";
		if(timeEstimateMins == 1){
			timeEstimateString = timeEstimateMins + " minute";
		}
		
		if(timeEstimateMins > 120){
			timeEstimateHours = Math.ceil(timeEstimateMins / 60);
			timeEstimateString = timeEstimateHours + " hours";
			if(timeEstimateHours > 48){
				timeEstimateDays =  Math.ceil(timeEstimateHours / 24);
				timeEstimateString = timeEstimateDays + " days";
			}
		}
		
		document.getElementById("timeEstimateDiv").innerHTML = "<br />This modeling job will take about <b>" + timeEstimateString + "</b> to finish.";
	}	
	</script>
	
</head>

<body bgcolor="#ffffff" onload="setTabToModeling(); getSelectedDataset();">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
	
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrmodelbuilders.jpg" style="background-repeat: no-repeat;"><span id="maincontent">
		
		<table width="600" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
		<p class="StandardTextDarkGrayParagraph"><b><br>Chembench Model Development</b></p>
		<p align="justify" class="StandardTextDarkGrayParagraph">Here you may develop QSAR 
		predictors for any of your datasets. Public datasets are also available.<br /><br />
		For more information about creating predictors and selecting the right parameters, use the 
		<a href="/help-modeling">Modeling help page</a>. 
   		</p>
		<p class="StandardTextDarkGrayParagraph"><a href="/help-workflows"><img src="/theme/img/overall-workflow.png" border="0" height="200" onmouseover='enlargeImage(this);' onmouseout='shrinkImage(this)' /></a></p>
		<p class="StandardTextDarkGrayParagraph">The full modeling workflow, represented by the above diagram, is described in the following publication: 
		<a href="http://onlinelibrary.wiley.com/doi/10.1002/minf.201000061/full">Tropsha, A. Best Practices for QSAR Model Development, 
		Validation, and Exploitation Mol. Inf., 2010, 29, 476-488</a></p>
		</td>
        </tr>
        </table>

<s:form action="createModelingJob" enctype="multipart/form-data" theme="simple">

	<!-- Dataset Selection -->
			<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>		
				<tr>
					<td width="100%" height="24" align="left" colspan="2">
					<p class="StandardTextDarkGrayParagraph2">
					<br /><b>Select a Dataset</b>
					</p>
					</td>
				</tr>	
				<tr>
					<td width="100%" height="24" align="left" colspan="2">
					<p class="StandardTextDarkGrayParagraph">
					<i>(Use the "DATASET" page to create datasets.)</i>
					</p> 
					</td>
			    </tr> 
				<tr>
					<td colspan="2">
					<s:hidden id="actFileDataType" name="actFileDataType" />
					<div class="StandardTextDarkGrayParagraph"><b><input type="radio" name="actFileDataTypeRadio" id="continuousDataset" onclick="setToContinuous(); getSelectedDataset()" checked>Choose a Continuous Dataset:</input></b>
					<br /><s:select name="selectedDatasetId" list="userContinuousDatasets" id="selectedContinuousDataset" listKey="fileId" listValue="fileName" onchange='getSelectedDataset()' />
					</div>
					</td>
			    </tr> 
			    <tr>
				<td colspan="2">&nbsp;</td>
			    </tr>
			    <tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><b><input type="radio" name="actFileDataTypeRadio" id="categoryDataset" onclick="setToCategory(); getSelectedDataset()">Choose a Category Dataset:</input></b>
					<br /><s:select name="selectedDatasetId" disabled="true" list="userCategoryDatasets" id="selectedCategoryDataset" listKey="fileId" listValue="fileName" onchange='getSelectedDataset()' />
					</div>
					</td>
			    </tr>
				<!-- Commented out until it's implemented...
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Weight Categories By:</b></div></td>
					<td align="left" valign="top">
					<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="categoryWeights" value="categoryWeights" list="#{'INVERSESIZE':'Inverse of Size','NOWEIGHTING':'No Weighting','MANUAL':'Set Manually...'}" /></div>
					</td>
				</tr>
				<tr>
					<td width="100%" colspan="2">
					<div class="StandardTextDarkGrayParagraph" id="submitMessage">
					<i>Weighting will improve modeling on imbalanced datasets. 
					When optimizing the model, the accuracy on members of different categories will be 
					weighted depending on the values you input.</i>
					</div>
					</td>
			    </tr>
			    -->

				<tr>
				<td colspan="2">
				<div class="StandardTextDarkGrayParagraph"><br /><input type="button" value="View Dataset" property="text" onclick="showDataset()"/> <i> Opens in a new window. Check your browser settings if the new window does not appear.</i></div>
				<br />
				</td>
				</tr>
			    </tbody>
			 </table>
			<br />

		<!-- Descriptor Type Selection -->
			<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>			
				<tr>
					<td height="24" align="left" colspan="2">
					<p class="StandardTextDarkGrayParagraph2">
					<br /><b>Select Descriptors</b>
					</p>
					</td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Descriptor Type:</b></div></td>
					<td align="left" valign="top">
					<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="descriptorGenerationType" id="descriptorGenerationType" value="descriptorGenerationType" list="#{'MOLCONNZ':'MolconnZ','DRAGONH':'Dragon (with hydrogens)<br />','DRAGONNOH':'Dragon (no hydrogens)','MACCS':'Maccs','MOE2D':'MOE2D<br />','UPLOADED':'Uploaded Descriptors<br />'}" /></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Scale Descriptors Using:</b></div></td>
					<td align="left" valign="top">
					<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="scalingType" value="scalingType" list="#{'RANGESCALING':'Range Scaling','AUTOSCALING':'Auto Scaling','NOSCALING':'None'}" /></div>
					</td>
				</tr>
				<tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><i><br /></i></div>
					</td>
				</tr>	
				<!-- <tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Minimum Standard Deviation:</b></div>
					</td>
					<td align="left" valign="top"><s:textfield name="stdDevCutoff" id="stdDevCutoff" size="5" /></td>
				</tr>		
				<tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><i>Each descriptor that has values with lower standard deviation than the minimum will be removed.<br /></i></div>
					</td>
				</tr>	 -->
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Maximum Corellation:</b></div>
					</td>
					<td align="left" valign="top"><s:textfield name="corellationCutoff" id="corellationCutoff" size="5" /></td>
				</tr>
				<tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><i>For each pair of descriptors, if the corellation coefficient is above the maximum, one of the two will be removed.<br /><br /></i></div>
					</td>
				</tr>
			    </tbody>
			 </table>
			 <br />

	
		<!-- Internal Data Split Parameters -->
 			<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>	
				<tr>
					<td align="left" colspan="2">
					<br />
					<div class="StandardTextDarkGrayParagraph2">
					<b>Choose Internal Data Splitting Method</b>
					</div>
					<br />
					</td>
				</tr>
				<tr><td colspan="2">
				
				<!-- script sets hidden field so we know which tab was selected -->
				<script type="text/javascript">
				   dojo.event.topic.subscribe('/internalDataSplitTypeSelect', function(tab, tabContainer) {
				      //alert("Tab "+ tab.widgetId + " was selected");
				      document.getElementById("trainTestSplitType").value = tab.widgetId;
				      calculateRuntimeEstimate();
				   });
				</script>
				<s:hidden id="trainTestSplitType" name="trainTestSplitType" />
				<!-- end script -->
				
				<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
				<sx:tabbedpanel id="internalDataSplitTabbedPanel" afterSelectTabNotifyTopics="/internalDataSplitTypeSelect">
					<sx:div id="RANDOM" theme="ajax" label="Random Split" href="/loadRandomInternalSplitSection" loadingText="Loading kNN parameters...">
					</sx:div>
					
					<sx:div id="SPHEREEXCLUSION" theme="ajax" label="Sphere Exclusion" href="/loadSphereInternalSplitSection" loadingText="Loading kNN parameters...">
					</sx:div>
			    </sx:tabbedpanel>
				</td></tr></table>
				</td></tr></tbody>
			</table>
			<br />
 	

		<!-- Modeling Method (kNN, kNN+, SVM) --> 
			<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>	
				<tr>
					<td align="left" colspan="2">
					<br />
					<div class="StandardTextDarkGrayParagraph2">
					<b>Choose Model Generation Method</b>
					</div>
					<br />
					</td>
				</tr>
				<tr><td colspan="2">
				
				<!-- script sets hidden field so we know which tab was selected -->
				<script type="text/javascript">
				   dojo.event.topic.subscribe('/modelingTypeSelect', function(tab, tabContainer) {
				      //alert("Tab "+ tab.widgetId + " was selected");
				      document.getElementById("modelingType").value = tab.widgetId;
				      calculateRuntimeEstimate();
				   });
				</script>
				<s:hidden id="modelingType" name="modelingType" />
				<!-- end script -->
				
				<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
				<sx:tabbedpanel id="modelingTypeTabbedPanel" afterSelectTabNotifyTopics="/modelingTypeSelect" >
					
					<sx:div id="RANDOMFOREST" value="RANDOMFOREST" theme="ajax" label="Random Forest" href="/loadRFSection" loadingText="Loading randomForest parameters...">
					</sx:div>
					
					<sx:div id="KNN-GA" value="KNN-GA" theme="ajax" label="GA-kNN" href="/loadKnnPlusGASection" loadingText="Loading kNN+ parameters...">
					</sx:div>
					
					<sx:div id="KNN-SA" value="KNN-SA" theme="ajax" label="SA-kNN" href="/loadKnnPlusSASection" loadingText="Loading kNN+ parameters...">
					</sx:div>
					
					<!--
					<sx:div id="SVM" value="SVM" theme="ajax" label="Support Vector Machines" href="/loadSvmSection" loadingText="Loading SVM parameters...">
					</sx:div>
					-->
					
					<!--
					Since knn+ seems to be working well, I'm taking out the original kNN option for generating
					predictors. Predictions can still be made using these older kNN models.
					
					<sx:div id="KNN" value="KNN" theme="ajax" label="kNN" href="/loadKnnSection" loadingText="Loading kNN parameters...">
					</sx:div>
					-->
					
			    </sx:tabbedpanel>
			    
				</td></tr></table>
				
				</td></tr></tbody>
			</table>
			<br />
	

		<!-- Begin Modeling Job -->
			<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>	
				 <tr>
					<td width="100%" height="24" align="left" colspan="2">
					<p class="StandardTextDarkGrayParagraph2">
					<br /><b>Start Job</b>
					</p>
					</td>
				</tr>
				
				<tr>
				    <td colspan="2">
				  	  <div class="StandardTextDarkGrayParagraph" id="timeEstimateDiv"></div>
				    </td>
			    </tr>
			     
			    <s:if test="user.getUserName()!='guest'">
			    <tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Send me an email when the job finishes: </b></div>
					</td>
					<td align="left" valign="top"><s:checkbox name="emailOnCompletion" id="emailOnCompletion" /></td>
				</tr>
				</s:if>
			     
				<tr>
					<td width="100%" colspan="2">
					<div class="StandardTextDarkGrayParagraph" id="submitMessage">
					<i>Please enter a name for the predictor you are creating.</i>
					</div>
					</td>
			    </tr>
			    <tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Predictor Name:</b></div>
					</td>
					<td align="left" valign="top"><s:textfield name="jobName" id="jobName" size="19"/></td>
				</tr>
			     
			    
				<tr>
					<td ></td>
					<td class="" valign="top"><input type="button" name="userAction" id="userAction" onclick="if(validateObjectNames(document.getElementById('jobName').value ,usedDatasetNames, usedPredictorNames, usedPredictionNames, usedTaskNames)){ submitForm(this); }"
						value="Submit Modeling Job" /> <span id="textarea"></span> <br /></td>
				</tr>
				</tbody>
			</table>
			<br />
</s:form>

<br />

<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html>
