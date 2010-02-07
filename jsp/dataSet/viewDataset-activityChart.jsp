<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<br />
	
	<table width="924" align="center">
		<tr><td>
			
		<p class="StandardTextDarkGray"><b><u>Activity Histogram</u></b></p>
		<br />
		<p class="StandardTextDarkGray">
		<img src="/imageServlet?projectType=dataset&user=<s:property value='user.userName' />&project=<s:property value='dataset.fileName' />&compoundId=mychartActivity&datasetID=<s:property value='dataset.fileId' />" border="0"/>
		<br />
		</p>
		</td></tr>
	</table>