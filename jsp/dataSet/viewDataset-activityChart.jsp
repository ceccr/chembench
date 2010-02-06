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
		1
		<sx:div href="/activityChartVisualization.do?datasetID=%{#dataset.fileId} />" theme="ajax">
		</sx:div>
		<br />
		2
		<sx:div href="/activityChartVisualization.do?datasetID=633>" theme="ajax">
		</sx:div>
		<br />
		3
		<sx:div href="/activityChartVisualization.do?datasetID=<s:property value='dataset.fileId' />" theme="ajax">
		</sx:div>
		<br />
		4
		<sx:div href="/activityChartVisualization.do?datasetID=%{#dataset.fileId} />" theme="ajax">
		</sx:div>
		<br />
		45
		<sx:div href="/activityChartVisualization.do?datasetID=%{dataset.fileId} />" theme="ajax">
		</sx:div>
		<br />
		</p>
		</td></tr>
	</table>