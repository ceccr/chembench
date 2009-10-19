<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
    <title>C-CHEMBENCH | Modeling</title>
    
    <link href="theme/ss.css" rel="stylesheet" type="text/css" />
	<link href="theme/ajaxtabs.css" rel="stylesheet" type="text/css" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
	<link href="theme/links.css" rel="stylesheet" type="text/css" />
	<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
	<link rel="icon"  href="theme/img/mml.ico" type="image/ico" />
	<link rel="SHORTCUT ICON" href="theme/img/mml.ico" />
	
	<script language="javascript" src="javascript/script.js"></script>
	<script language="javascript" src="javascript/modeling.js"></script>
	
	<script language="javascript">
	var usedDatasetNames = new Array(<s:iterator value="userDatasetNames">"<s:property />",</s:iterator>"");
	var usedPredictorNames = new Array(<s:iterator value="userPredictorNames">"<s:property />",</s:iterator>"");
	var usedPredictionNames = new Array(<s:iterator value="userPredictionNames">"<s:property />",</s:iterator>"");
	var usedTaskNames = new Array(<s:iterator value="userTaskNames">"<s:property />",</s:iterator>"");
	</script>
</head>

<body bgcolor="#ffffff">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
	
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrmodelbuilders.jpg" style="background-repeat: no-repeat;"><span id="maincontent">
		
		<table width="465" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
				<p class="StandardTextDarkGrayParagraph"><b><br>C-Chembench Model
		Development</b></p>
		<p align="justify" class="StandardTextDarkGrayParagraph">kNN QSAR models can currently
		be developed for any dataset using the Model Building section of this
		page. Upload an sdf file and activity file below. Jobs will be queued
		and will run as soon as computer resources are available. Some sample
		datasets are available for your use.<br><br>
		</p>
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
					<i>(Use the "DATA MGMT" page to create datasets.)</i>
					</p> 
					</td>
			    </tr> 
				<tr>
					<td colspan="2">
					<s:hidden id="actFileDataType" name="actFileDataType" />
					<div class="StandardTextDarkGrayParagraph"><b><input type="radio" name="actFileDataTypeRadio" id="continuousDataset" onclick="setToContinuous()" checked>Choose a Continuous Dataset:</input></b>
					<br /><s:select name="selectedDatasetId" list="userContinuousDatasets" id="selectedContinuousDataset" listKey="fileId" listValue="fileName" />
					</div>
					</td>
			    </tr> 
			    <tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><b><input type="radio" name="actFileDataTypeRadio" id="categoryDataset" onclick="setToCategory()">Choose a Category Dataset:</input></b>
					<br /><s:select name="selectedDatasetId" disabled="true" list="userCategoryDatasets" id="selectedCategoryDataset" listKey="fileId" listValue="fileName" />
					</div>
					</td>
			    </tr>
				<tr>
				<td colspan="2">
				<div class="StandardTextDarkGrayParagraph"><br /><input type="button" value="View Activity Histogram" property="text" onclick="showActivityHistogram()"/> <i> Opens in a new window. Check your browser settings if the new window does not appear.</i></div>
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
					<br /><b>Set Descriptor Generation Parameters</b>
					</p>
					</td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Descriptor Generation Type:</b></div></td>
					<td align="left" valign="top">
					<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="descriptorGenerationType" value="descriptorGenerationType" list="#{'MOLCONNZ':'MolconnZ','DRAGON':'Dragon','MACCS':'Maccs','MOE2D':'MOE2D'}" /></div>
					</td>
				</tr>		
				<tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><i>Note: The Dragon license file has expired. Use one of the other descriptor types for now.<br /></i></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Descriptor Scaling Type:</b></div></td>
					<td align="left" valign="top">
					<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="scalingType" value="scalingType" list="#{'RANGESCALING':'Range Scaling','AUTOSCALING':'Auto Scaling','NOSCALING':'None'}" /><br /><br /></div>
					</td>
				</tr>
				<!--<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Minimum Standard Deviation:</b></div>
					</td>
					<td align="left" valign="top"><s:textfield name="stdDevCutoff" id="stdDevCutoff" size="5" disabled="true" /></td>
				</tr>		
				<tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><i>Each descriptor that has values with lower standard deviation than the minimum will be removed.<br /></i></div>
					</td>
				</tr>	
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Maximum Corellation:</b></div>
					</td>
					<td align="left" valign="top"><s:textfield name="corellationCutoff" id="corellationCutoff" size="5" disabled="true" /></td>
				</tr>
				<tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><i>For each pair of descriptors, if the corellation coefficient is above the maximum, one of the two will be removed (chosen randomly).<br /><br /></i></div>
					</td>
				</tr>	-->
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
 	

		<!-- Modeling Method (kNN, SVM) --> 
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
				   });
				</script>
				<s:hidden id="modelingType" name="modelingType" />
				<!-- end script -->
				
				<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
				<sx:tabbedpanel id="modelingTypeTabbedPanel" afterSelectTabNotifyTopics="/modelingTypeSelect" >
					<sx:div id="KNN" value="KNN" theme="ajax" label="k-Nearest Neighbors" href="/loadKnnSection" loadingText="Loading kNN parameters...">
					</sx:div>
					
					<sx:div id="SVM" value="SVM" theme="ajax" label="Support Vector Machine" href="/loadSvmSection" loadingText="Loading SVM parameters...">
					</sx:div>
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
