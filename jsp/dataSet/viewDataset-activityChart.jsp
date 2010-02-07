<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<br />
	
	<table width="924" align="center">
		<tr><td>
			
		<p class="StandardTextDarkGray"><b><u>Activity Histogram</u></b>
		<p class="StandardTextDarkGray">
		fileId: <s:property value='dataset.fileId'/> <br />
		
		<br />
		
		<img src="/imageServlet?projectType=dataset&user=<s:property value='user.userName' />&project=<s:property value='dataset.fileName' />&compoundId=mychartActivity&datasetID=<s:property value='dataset.fileId' />" border="0"/>
			
		<br />
		<sx:div href="activityChartVisualization.do?datasetID=%{dataset.fileId}" theme="ajax">
		</sx:div>
		
<!-- 		
function showActivityHistogram()
{
	//make sure that they've picked an existing continuous or category dataset
	if(document.getElementById("continuousDataset").checked){
		window.open("activityChartVisualization.do?datasetID="+document.getElementById("selectedContinuousDataset").value);
		return true;
	}
	else if(document.getElementById("categoryDataset").checked){
		window.open("activityChartVisualization.do?datasetID="+document.getElementById("selectedCategoryDataset").value);
		return true;
	}
	else{
		window.alert("Please specify a dataset to generate the activity histogram on.");
		return false;
	}
} -->
		
		<br />
		</p>
		</td></tr>
	</table>