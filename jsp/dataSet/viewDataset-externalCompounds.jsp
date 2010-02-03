<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<!-- External Compounds -->	
	<br />
		<p class="StandardTextDarkGray"><b><u>Compounds Chosen for External Set</u></b></p>
		
	<table width="924" align="center">
		<tr><td>
			
			<table>
				<tr>
					<!-- header for left side table -->
					<td class="TableRowText01">Compound ID
						<a href="#tabs" onclick=loadExternalCompoundsTab("viewDatasetExternalCompoundsSection?datasetId=<s:property value='dataset.fileId' />&orderBy=compoundId&sortDirection=asc")><img src="/theme/img/sortArrowDown.png" /></a>
						<a href="#tabs" onclick=loadExternalCompoundsTab("viewDatasetExternalCompoundsSection?datasetId=<s:property value='dataset.fileId' />&orderBy=compoundId&sortDirection=desc")><img src="/theme/img/sortArrowUp.png" /></a>
					</td>
					<td class="TableRowText01">Structure</td>
					<td class="TableRowText01">Activity
						<a href="#tabs" onclick=loadExternalCompoundsTab("viewDatasetExternalCompoundsSection?datasetId=<s:property value='dataset.fileId' />&orderBy=activityValue&sortDirection=asc")><img src="/theme/img/sortArrowDown.png" /></a>
						<a href="#tabs" onclick=loadExternalCompoundsTab("viewDatasetExternalCompoundsSection?datasetId=<s:property value='dataset.fileId' />&orderBy=activityValue&sortDirection=desc")><img src="/theme/img/sortArrowUp.png" /></a>
					</td>
				</tr>
				<!-- body for left side table -->
				<s:iterator value="externalCompounds" status="externalCompoundsStatus">
				<tr>
					<td class="TableRowText02"><s:property value="compoundId" /></td>
					<td class="TableRowText02">
<a href="#" onclick="window.open('compound3D?compoundId=<s:property value="compoundId" />&project=<s:property value="dataset.fileName" />&projectType=dataset&user=<s:property value="user.userName" />&datasetID=<s:property value="dataset.fileId" />, '','width=350, height=350'); return false;">
<img src="/imageServlet?user=<s:property value="user.userName" />&projectType=dataset&compoundId=<s:property value='compoundId' />&project=<s:property value="dataset.fileName" />&datasetID=<s:property value="dataset.fileId" />" border="0" height="150" onmouseover='enlargeImage(this);' onmouseout='shrinkImage(this)'/></a>					
					</td>
					<td class="TableRowText02"><s:property value="activityValue" /></td>
				</tr>
				</s:iterator>
			</table>
		</td></tr>
	</table>
	<!-- End External Compounds -->