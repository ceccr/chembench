<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<script language="javascript">

</script>

<!-- Compounds -->	
	<div id="myDiv">
	
		<p class="StandardTextDarkGray"><u><b>All Compounds In Dataset</b></u></p>
		
	<table width="924" align="center">
		<tr><td>
			
			<p class="StandardTextDarkGray" width="550">Go To Page: 
			<s:iterator value="pageNums" status="pageNumsStatus">
			<s:if test="pageNums[#pageNumsStatus.index]==currentPageNumber"><u></s:if>
			<a href="#tabs" onclick=loadAllCompoundsTab("viewDatasetCompoundsSection?datasetId=<s:property value='dataset.fileId' />&currentPageNumber=<s:property/>&orderBy=<s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />")><s:property/></a><s:if test="pageNums[#pageNumsStatus.index]==currentPageNumber"></u> </s:if> 
			</s:iterator>
			</p>
			
			<table>
				<tr>
					<!-- header for left side table -->
					<td class="TableRowText01">Compound ID 
						<a href="#tabs" onclick=loadAllCompoundsTab("viewDatasetCompoundsSection?datasetId=<s:property value='dataset.fileId' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=compoundId&sortDirection=asc")><img src="/theme/img/sortArrowDown.png" /></a>
						<a href="#tabs" onclick=loadAllCompoundsTab("viewDatasetCompoundsSection?datasetId=<s:property value='dataset.fileId' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=compoundId&sortDirection=desc")><img src="/theme/img/sortArrowUp.png" /></a>
						</td>
					<td class="TableRowText01">Structure</td>
					<s:if test="dataset.datasetType=='MODELING'">
					<td class="TableRowText01">Activity
						<a href="#tabs" onclick=loadAllCompoundsTab("viewDatasetCompoundsSection?datasetId=<s:property value='dataset.fileId' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=activityValue&sortDirection=asc")><img src="/theme/img/sortArrowDown.png" /></a>
						<a href="#tabs" onclick=loadAllCompoundsTab("viewDatasetCompoundsSection?datasetId=<s:property value='dataset.fileId' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=activityValue&sortDirection=desc")><img src="/theme/img/sortArrowUp.png" /></a>
						</td>
					</s:if>
				</tr>
				<!-- body for left side table -->
				<s:iterator value="datasetCompounds" status="datasetCompoundsStatus">
				<tr>
					<td class="TableRowText02"><s:property value="compoundId" /></td>
					<td class="TableRowText02">
						<a href="#" onclick="window.open('compound3D?compoundId=<s:property value="compoundId" />&project=<s:property value="dataset.fileName" />&projectType=dataset&user=<s:property value="user.userName" />&datasetID=<s:property value="dataset.fileId" />', '','width=350, height=350'); return false;">
						<img src="/imageServlet?user=<s:property value="user.userName" />&projectType=dataset&compoundId=<s:property value='compoundId' />&project=<s:property value="dataset.fileName" />&datasetID=<s:property value="dataset.fileId" />" border="0" height="150" onmouseover='enlargeImage(this);' onmouseout='shrinkImage(this)'/>
						</a>					
					</td>
					<s:if test="dataset.datasetType=='MODELING'">
					<td class="TableRowText02"><s:property value="activityValue" /></td>
					</s:if>
				</tr>
				</s:iterator>
			</table>
		</td></tr>
	</table>
	</div>
	<!-- End Compounds -->