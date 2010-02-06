<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<br />
	
	<table width="924" align="center">
		<tr><td>
			
		<p class="StandardTextDarkGray"><b><u>Activity Histogram</u></b>
		<p class="StandardTextDarkGray">
		<s:set name="link" value="/activityChartVisualization.do?datasetID=%{#dataset.fileId}" />
		Link1: <s:property value="%{#link}" /><br/>
		<s:set name="link2" value="/activityChartVisualization.do?datasetID=%{dataset.fileId}" />
		Link2: <s:property value="%{#link2}" /><br/>
		<s:set name="link3" value="/activityChartVisualization.do?datasetID=<s:property value=dataset.fileId" /> />
		Link3: <s:property value="%{#link3}" /><br/>
		
		<br />
		<sx:div id="activityChartSection" href="/activityChartVisualization.do?datasetID=%{#dataset.fileId} />" theme="ajax">
		</sx:div>
		</p>
		</td></tr>
	</table>