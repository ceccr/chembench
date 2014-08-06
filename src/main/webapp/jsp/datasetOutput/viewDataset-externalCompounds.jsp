<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" import="java.util.*" %>

<!-- External Compounds -->	
	
		<p class="StandardTextDarkGray"><u><b>Compounds Chosen for External Set</b></u></p>
		
	<table width="924" align="center">
		<tr><td>
			
			<s:if test="externalCompounds.size()!=0">
			<table width="924" align="center">
				<tr>
					<!-- header for left side table -->
					<th class="TableRowText01">Compound ID<br />
						<a href="#tabs" onclick=loadExternalCompoundsTab("viewDatasetExternalCompoundsSection?id=<s:property value='dataset.id' />&orderBy=compoundId&sortDirection=asc")><img src="theme/img/sortArrowUp.png" /></a>
						<a href="#tabs" onclick=loadExternalCompoundsTab("viewDatasetExternalCompoundsSection?id=<s:property value='dataset.id' />&orderBy=compoundId&sortDirection=desc")><img src="theme/img/sortArrowDown.png" /></a>
					</th>
					<s:if test="!dataset.sdfFile.isEmpty()"><th class="TableRowText01_unsortable">Structure</th></s:if>
					<th class="TableRowText01">Activity<br />
						<a href="#tabs" onclick=loadExternalCompoundsTab("viewDatasetExternalCompoundsSection?id=<s:property value='dataset.id' />&orderBy=activityValue&sortDirection=asc")><img src="theme/img/sortArrowUp.png" /></a>
						<a href="#tabs" onclick=loadExternalCompoundsTab("viewDatasetExternalCompoundsSection?id=<s:property value='dataset.id' />&orderBy=activityValue&sortDirection=desc")><img src="theme/img/sortArrowDown.png" /></a>
					</th>
				</tr>
				<!-- body for left side table -->
				<s:iterator value="externalCompounds" status="externalCompoundsStatus">
				<tr>
					<td class="TableRowText02"><s:property value="compoundId" /></td>
					<s:if test="!dataset.sdfFile.isEmpty()">
						<td class="TableRowText02">
						<a class="compound_img_a" href="#" onclick="window.open('compound3D?compoundId=<s:property value="url_friendly_id" />&projectType=dataset&user=<s:property value="dataset.userName" />&datasetName=<s:property value="dataset.name" />', '','width=350, height=350'); return false;">
		
						<img src="imageServlet?user=<s:property value="dataset.userName" />&projectType=dataset&compoundId=<s:property value='url_friendly_id' />&datasetName=<s:property value="dataset.name" />" border="0" height="150"/>
						</a>					
						</td>
					</s:if>
					<td class="TableRowText02"><s:property value="activityValue" /></td>
				</tr>
				</s:iterator>
			</table>
			</s:if>
			<s:else>
				<br/><p class="StandardTextDarkGray">There are no compounds in your dataset's external validation set.</p><br/><br/>
			</s:else>
		</td></tr>
	</table>
	<!-- End External Compounds -->