<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<br />
	
	<table width="924" align="center">
		<tr><td>
			
		<p class="StandardTextDarkGray"><b><u>Activity Histogram</u></b>
		<p class="StandardTextDarkGray">
		<s:property value='dataset.fileId' />
		</s:property>
		<s:url id="activityChartLink" value="/activityChartVisualization.do" includeParams="none">
			<s:param name="datasetID" value="<s:property value='dataset.fileId' />" />
		</s:url>
		
		<!-- old way: have it open in a new window <s:a href="%{externalChartLink}" target="_blank"><u>Chart View</u></s:a> -->
		<!-- new way: ajax it onto the page, woots! -->
		<br />
		<sx:div id="activityChartSection" href="%{activityChartLink}" theme="ajax">
		</sx:div>
		</p>
		</td></tr>
	</table>