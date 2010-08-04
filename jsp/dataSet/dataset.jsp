<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
<title>CHEMBENCH | Dataset Management</title>
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
<body onload="setTabToDataset();">
<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>

<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td colspan="5" valign="top"  background="theme/img/backgrmodelbuilders.jpg">
      <table width="465" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td><p class="StandardTextDarkGrayParagraph"><b><br />
            Chembench Dataset Creation</b></p>
              <p align="justify" class="StandardTextDarkGrayParagraph">
              Here, you may create a dataset by uploading compound structures with or without 
              associated activities. The activity data is required for building models. <br /><br />
			  You can either create a Modeling dataset, which has both structures and activities,
			   or a Prediction dataset, which only has structures. <br /><br />
			   Each modeling dataset you create will appear as an option under the 
              "Modeling" tab and under the "Prediction" tab. Prediction datasets will only 
              appear under the "Prediction" tab.<br /><br />
              <!-- Modeling sets may be used as prediction sets; thus, they automatically 
              appear on the Prediction tab. -->
			  When you submit a dataset, chemical structure images will be generated for each 
			  compound. A pairwise compound similarity matrix will be created and displayed as a heatmap. 
			  <br /><br />
              For more information about dataset creation and defining external sets, see the 
              <a href="/help-dataset">Dataset help section</a>.  
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
			</p>
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<div class="StandardTextDarkGrayParagraph"><i>Select the type of dataset to create.<br />
			<!-- For the "Modeling Set" and "Prediction Set", -->You do not need to provide descriptors; Chembench will
			generate descriptors as needed for visualization, modeling, and prediction. <br />
			<!-- For the "Modeling Set With Descriptors" and "Prediction Set With Descriptors", you will need to <br />
			upload an <a href="">X</a> file containing the descriptor values. -->
			<br />
			</i></div></td>
		</tr>
		<tr><td colspan="2">
		
		<!-- script sets hidden field so we know which tab was selected -->
		<script type="text/javascript">
		   dojo.event.topic.subscribe('/datasetTypeSelect', function(tab, tabContainer) {
		      //alert("Tab "+ tab.widgetId + " was selected");
		      document.getElementById("datasetType").value = tab.widgetId;
		      
		      var externalParameterIds = new Array("useActivityBinning", "numExternalCompounds", "externalCompoundList");
		      if(tab.widgetId == "MODELING" || tab.widgetId == "MODELINGWITHDESCRIPTORS"){
		      	//if it's a modeling dataset, enable the external split parameters 
		      	for (var i=0; i < externalParameterIds.length; i++){
					if(document.getElementById(externalParameterIds[i]) != null){
						document.getElementById(externalParameterIds[i]).disabled = false;
					}
				}
		      }
		      else{
		      	//if not, disable them
		      	for (var i=0; i < externalParameterIds.length; i++){
					if(document.getElementById(externalParameterIds[i]) != null){
						document.getElementById(externalParameterIds[i]).disabled = true;
					}
				}
		      }
		   });
		</script>
		<s:hidden id="datasetType" name="datasetType" />
		<!-- end script -->
		
		<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
    	<sx:tabbedpanel id="datasetTypeTabbedPanel" afterSelectTabNotifyTopics="/datasetTypeSelect">
    	
	    	<sx:div id="MODELING" href="/loadModelingSection" label="Modeling Set" theme="ajax" loadingText="Loading dataset types...">
			</sx:div>
			
			<sx:div id="PREDICTION" href="/loadPredictionSection" label="Prediction Set" theme="ajax" loadingText="Loading dataset types..." preload="false">
			</sx:div>
			
			<!-- 
			<sx:div id="MODELINGWITHDESCRIPTORS" href="/loadModelingWithDescriptorsSection" label="Modeling Set With Descriptors" theme="ajax" loadingText="Loading dataset types...">
			</sx:div>
			
			<sx:div id="PREDICTIONWITHDESCRIPTORS" href="/loadPredictionWithDescriptorsSection" label="Prediction Set With Descriptors" theme="ajax" loadingText="Loading dataset types...">
			</sx:div>
			 -->
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
			<div class="StandardTextDarkGrayParagraph"><i>A subset of the compounds in the dataset will be reserved for external validation of models. 
			<br />These parameters only apply to modeling sets.</i><br /></div></td>
		</tr>
		<tr>
			<td colspan="2">
			
			<!-- script sets hidden field so we know which tab was selected -->
			<script type="text/javascript">
			   dojo.event.topic.subscribe('/splitTypeSelect', function(tab, tabContainer) {
			      //alert("Tab "+ tab.widgetId + " was selected");
			      document.getElementById("splitType").value = tab.widgetId;
			   });
			</script>
			<s:hidden id="splitType" name="splitType" />
			<!-- end script -->
			
			<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
			<sx:tabbedpanel id="splitTypeTabbedPanel" afterSelectTabNotifyTopics="/splitTypeSelect">
    	
		    	<sx:div id="RANDOM" href="/loadAutoExternalSection" label="Random Split" theme="ajax" loadingText="Loading dataset types...">
				</sx:div>
	    	
				<sx:div id="USERDEFINED" href="/loadManualExternalSection" label="Choose Compounds" theme="ajax" loadingText="Loading dataset types..." preload="false">
				</sx:div>
				
	    	</sx:tabbedpanel>
	    	</td></tr></table>
	    	</td>
	    </tr>	
		</tbody>
	</table>
	<br />
	
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
			<div class="StandardTextDarkGrayParagraph"><i>A job will be started to generate visualizations and chemical sketches for this dataset.</i></div></td>
		 </tr>	
		<tr>
			<td colspan="2">
			</td>
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
			<td><b class='StandardTextDarkGrayParagraph'>Description (optional):</b></td>
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
