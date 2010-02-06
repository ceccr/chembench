<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<br />
	
	<table width="924" align="center">
		<tr><td>
			
		<p class="StandardTextDarkGray"><b><u>Activity Histogram</u></b>
		
		<!-- old way: have it open in a new window <s:a href="%{externalChartLink}" target="_blank"><u>Chart View</u></s:a> -->
		<!-- new way: ajax it onto the page, woots! -->
		<br />
		<sx:div id="activityChartLink" href="/activityChartVisualization.do?datasetID=<s:property value='dataset.fileId'>" theme="ajax">
		</sx:div>
		</p>
		</td></tr>
	</table>