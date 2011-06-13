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
	
	<script language="javascript">
	var usedDatasetNames = new Array(<s:iterator value="userDatasetNames">"<s:property />",</s:iterator>"");
	var usedPredictorNames = new Array(<s:iterator value="userPredictorNames">"<s:property />",</s:iterator>"");
	var usedPredictionNames = new Array(<s:iterator value="userPredictionNames">"<s:property />",</s:iterator>"");
	var usedTaskNames = new Array(<s:iterator value="userTaskNames">"<s:property />",</s:iterator>"");
	

	function getSelectedDataset(){
		//get the numCompounds and availableDescriptors for the currently selected dataset
		if(document.getElementById("categoryDataset").checked==true){
			datasetId = document.getElementById("selectedCategoryDataset").value;
			<s:iterator value="userCategoryDatasets">
			if(datasetId==<s:property value="id" />){
				selectedDatasetNumCompounds = <s:property value='numCompound' />;
				selectedDatasetAvailableDescriptors = "<s:property value='availableDescriptors' />";
				selectedDatasetHasBeenScaled = "<s:property value='hasBeenScaled' />";
			}
			</s:iterator>
		}
		else{
			datasetId = document.getElementById("selectedContinuousDataset").value;
			<s:iterator value="userContinuousDatasets">
				if(datasetId==<s:property value="id" />){
					selectedDatasetNumCompounds = <s:property value='numCompound' />;
					selectedDatasetAvailableDescriptors = "<s:property value='availableDescriptors' />";
					selectedDatasethasBeenScaled = "<s:property value='hasBeenScaled' />";
				}
			</s:iterator>
		}
		
		//enable / disable based on the availableDescriptors
		if(selectedDatasetAvailableDescriptors.indexOf("MOE2D") > -1){
			document.getElementById("descriptorGenerationType" + "MOE2D").disabled = false;
			document.getElementById("descriptorGenerationType" + "MOE2D").checked = "checked";
		}
		else{
			document.getElementById("descriptorGenerationType" + "MOE2D").disabled = true;
		}
		
		if(selectedDatasetAvailableDescriptors.indexOf("MACCS") > -1){
			document.getElementById("descriptorGenerationType" + "MACCS").disabled = false;
			document.getElementById("descriptorGenerationType" + "MACCS").checked = "checked";
		}
		else{
			document.getElementById("descriptorGenerationType" + "MACCS").disabled = true;
		}
		
		if(selectedDatasetAvailableDescriptors.indexOf("DRAGONNOH") > -1){
			document.getElementById("descriptorGenerationType" + "DRAGONNOH").disabled = false;
			document.getElementById("descriptorGenerationType" + "DRAGONNOH").checked = "checked";
		}
		else{
			document.getElementById("descriptorGenerationType" + "DRAGONNOH").disabled = true;
		}
		
		if(selectedDatasetAvailableDescriptors.indexOf("DRAGONH") > -1){
			document.getElementById("descriptorGenerationType" + "DRAGONH").disabled = false;
			document.getElementById("descriptorGenerationType" + "DRAGONH").checked = "checked";
		}
		else{
			document.getElementById("descriptorGenerationType" + "DRAGONH").disabled = true;
		}
		
		if(selectedDatasetAvailableDescriptors.indexOf("MOLCONNZ") > -1){
			document.getElementById("descriptorGenerationType" + "MOLCONNZ").disabled = false;
			document.getElementById("descriptorGenerationType" + "MOLCONNZ").checked = "checked";
		}
		else{
			document.getElementById("descriptorGenerationType" + "MOLCONNZ").disabled = true;
		}
		
		if(selectedDatasetAvailableDescriptors.indexOf("UPLOADED") > -1){
			document.getElementById("descriptorGenerationType" + "UPLOADED").disabled = false;
			document.getElementById("descriptorGenerationType" + "UPLOADED").checked = "checked";
		}
		else{
			document.getElementById("descriptorGenerationType" + "UPLOADED").disabled = true;
		}
		
		setDescriptorScaling();
		
		//recalculate time estimate for the newly selected dataset
		calculateRuntimeEstimate();
	}
	
	
	</script>
	<script language="javascript" src="javascript/script.js"></script>
	<script language="javascript" src="javascript/modeling.js"></script>
	
	
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
		<a href="/help-modeling">Modeling help page</a>. </p>
		<p class="StandardTextDarkGrayParagraph">The full modeling workflow, as described in our 
		<a href="/help-workflows">Workflow help page</a>, is detailed the publication: 
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
					<br /><s:select name="selectedDatasetId" list="userContinuousDatasets" id="selectedContinuousDataset" listKey="id" listValue="name" onchange='getSelectedDataset();' />
					</div>
					</td>
			    </tr> 
			    <tr>
				<td colspan="2">&nbsp;</td>
			    </tr>
			    <tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><b><input type="radio" name="actFileDataTypeRadio" id="categoryDataset" onclick="setToCategory(); getSelectedDataset()">Choose a Category Dataset:</input></b>
					<br /><s:select name="selectedDatasetId" disabled="true" list="userCategoryDatasets" id="selectedCategoryDataset" listKey="id" listValue="name" onchange='getSelectedDataset();' />
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
					<td valign="top">
					<div class="StandardTextDarkGrayParagraph"><b>Descriptor Type:</b></div></td>
					<td align="left" valign="top">
					<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="descriptorGenerationType" onclick="setDescriptorScaling()" id="descriptorGenerationType" value="descriptorGenerationType" list="#{'MOLCONNZ':'MolconnZ','DRAGONH':'Dragon (with hydrogens)<br />','DRAGONNOH':'Dragon (no hydrogens)','MACCS':'Maccs','MOE2D':'MOE2D<br />','UPLOADED':'Uploaded Descriptors<br />'}" /></div>
					<br />
					</td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Scale Descriptors Using:</b></div></td>
					<td align="left" valign="top">
					<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="scalingType" id="scalingType" value="scalingType" list="#{'RANGESCALING':'Range Scaling','AUTOSCALING':'Auto Scaling','NOSCALING':'None'}" /></div>
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
					<div class="StandardTextDarkGrayParagraph"><b>Maximum Correlation:</b></div>
					</td>
					<td align="left" valign="top"><s:textfield name="correlationCutoff" id="correlationCutoff" size="5" /></td>
				</tr>
				<tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><i>For each pair of descriptors, if the correlation coefficient is above the maximum, one of the two will be removed.<br /><br /></i></div>
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
				<s:hidden id="trainTestSplitType" name="trainTestSplitType"  />
				<!-- end script -->
				
				<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
				<sx:tabbedpanel id="internalDataSplitTabbedPanel" afterSelectTabNotifyTopics="/internalDataSplitTypeSelect">
					<sx:div id="RANDOM" theme="ajax" label="Random Split" href="/loadRandomInternalSplitSection" loadingText="Loading data splitting parameters...">
					</sx:div>
					
					<sx:div id="SPHEREEXCLUSION" theme="ajax" label="Sphere Exclusion" href="/loadSphereInternalSplitSection" loadingText="Loading data splitting parameters...">
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
						changeSvmType();
						calculateRuntimeEstimate();
				   });
				</script>
				<s:hidden id="modelingType" name="modelingType" />
				<!-- end script -->
				
				<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
				<sx:tabbedpanel id="modelingTypeTabbedPanel" afterSelectTabNotifyTopics="/modelingTypeSelect" >
					
					<sx:div id="RANDOMFOREST" value="RANDOMFOREST" theme="ajax" label="Random Forest" href="/loadRFSection" loadingText="Loading randomForest parameters...">
					</sx:div>
					
					<sx:div id="SVM" value="SVM" theme="ajax" label="Support Vector Machines" href="/loadSvmSection" loadingText="Loading SVM parameters...">
					</sx:div>
					
					<sx:div id="KNN-GA" value="KNN-GA" theme="ajax" label="GA-kNN" href="/loadKnnPlusGASection" loadingText="Loading kNN+ parameters...">
					</sx:div>
					
					<sx:div id="KNN-SA" value="KNN-SA" theme="ajax" label="SA-kNN" href="/loadKnnPlusSASection" loadingText="Loading kNN+ parameters...">
					</sx:div>
					
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
					<div class="StandardTextDarkGrayParagraph"><br /><b>Send me an email when the job finishes: </b><br /></div>
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
