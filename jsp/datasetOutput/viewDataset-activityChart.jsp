<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<br />
	
	<table width="924" align="center">
		<tr><td>
		<p class="StandardTextDarkGray"><b><u>Activity Histogram</u></b></p>
		<p class="StandardTextDarkGray">The range of activity values is divided into 10 bins.<br /><br />
		<img src="/imageServlet?projectType=dataset&user=<s:property value='dataset.userName' />&project=<s:property value='dataset.name' />&compoundId=activityChart" border="0"/>
		<br />
		</p>
		</td></tr>
	</table>