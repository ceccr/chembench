<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<!-- External Compounds -->	
	
		<p class="StandardTextDarkGray"><u><b>Compounds Chosen for Each External Fold</b></u></p>
		
	<table width="924" align="center">
		<tr><td>
			<p class="StandardTextDarkGray" width="550">View Fold: 
			<s:iterator value="foldNums" status="foldNumsStatus">
			<s:if test="foldNums[#foldNumsStatus.index]==currentFoldNumber"><s:property/></s:if>
			<s:else><a href="#tabs" onclick=loadNFoldCompoundsTab("viewDatasetNFoldSection?datasetId=<s:property value='dataset.fileId' />&currentFoldNumber=<s:property/>&orderBy=<s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />")><s:property/></a></s:else> 
			</s:iterator>
			</p>
		</td></tr>
		<tr><td>
			<s:if test="externalFolds.size()!=0">
				<!-- body for left side table -->
				<p class="StandardTextDarkGray" width="550">External Fold <s:property value="currentFoldNumber" />:</p>
				<s:iterator value="externalFolds" status="externalFoldsStatus">
					<table class="sortable" id="nfoldCompounds">
						<tr>
							<th class="TableRowText01">Compound ID</th>
							<th class="TableRowText01">Structure</th>
							<th class="TableRowText01">Activity</th>
						</tr>
						<s:iterator value="compounds" status="compoundsStatus">
							<tr>
							<td class="TableRowText02"><s:property value="compoundId" /></td>
							<td class="TableRowText02">
							<a href="#" onclick="window.open('compound3D?compoundId=<s:property value="compoundId" />&projectType=dataset&user=<s:property value="dataset.userName" />&datasetName=<s:property value="dataset.fileName" />', '','width=350, height=350'); return false;">
							<img src="/imageServlet?user=<s:property value="dataset.userName" />&projectType=dataset&compoundId=<s:property value='compoundId' />&datasetName=<s:property value="dataset.fileName" />" border="0" height="150" onmouseover='enlargeImage(this);' onmouseout='shrinkImage(this)'/>
							</a>					
							</td>
							<td class="TableRowText02"><s:property value="activityValue" /></td>
							</tr>
						</s:iterator>
					</tr>
					</table>
					<tr><td><br /></td></tr>
				</s:iterator>
			</table>
			</s:if>
			<s:else>
				<br/><p class="StandardTextDarkGray">There are no compounds in your dataset's external validation set.</p><br/><br/>
			</s:else>
		</td></tr>
	</table>
	<!-- End External Compounds -->