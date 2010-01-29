<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>


<!-- Compounds -->	
	<br />
		<p class="StandardTextDarkGray"><b><u>Dataset Compounds</u></b></p>
		
	<table width="924" align="center">
		<tr><td>
		
			<p class="StandardTextDarkGray" width="550">Go To Page: 
			<s:iterator value="pageNums" status="pageNumsStatus">
			<s:if test="pageNums[%{#pageNumsStatus.index}]==currentPageNumber"><u></s:if>
			<a href="viewDataset?id=<s:property value='dataset.fileId' />&pagenum=<s:property/>"><s:property/></a>
			<s:if test="pageNums[%{#pageNumsStatus.index}]==currentPageNumber"></u></s:if> 
			</s:iterator>
			</p>
			
			<p class="StandardTextDarkGray" width="550">Go To Page: 
			<s:iterator value="pageNums" status="pageNumsStatus">
			<s:if test="pageNums[#pageNumsStatus.index]==currentPageNumber"><u></s:if>
			<a href="viewDataset?id=<s:property value='dataset.fileId' />&pagenum=<s:property/>"><s:property/></a>
			<s:if test="pageNums[#pageNumsStatus.index]==currentPageNumber"></u></s:if> 
			</s:iterator>
			</p>
			
			<p class="StandardTextDarkGray" width="550">Go To Page: 
			<s:iterator value="pageNums" status="pageNumsStatus"><br />
			<s:property value="pageNums[%{pageNumsStatus.index}]" /><br />
			<s:property value="%{pageNums[pageNumsStatus.index]}" /><br />
			<s:property value="pageNums[pageNumsStatus.index]" /><br />
			full array: <s:property value="pageNums" /><br />
			of 0: <s:property value="pageNums[0]" /><br />
			<s:set name="varname" value="pageNums[0]" />
			set1: <s:property value="varname" />
			set2: <s:property value="%{varname}" />
			set3: <s:property value="%{#varname}" />
			index0: <s:property value="%{#pageNumsStatus.index}" /><br />
			of index0: <s:property value="pageNums[%{#pageNumsStatus.index}]" /><br />
			index1: <s:property value="%{pageNumsStatus.index}" /><br />
			index2: <s:property value="#pageNumsStatus.index" /><br />
			of index2: <s:property value="pageNums[#pageNumsStatus.index]" /><br />
			index3: <s:property value="pageNumsStatus.index" /><br />
			
			<s:if test="pageNums[%{pageNumsStatus.index}]==currentPageNumber"><u></s:if>
			<a href="viewDataset?id=<s:property value='dataset.fileId' />&pagenum=<s:property/>"><s:property/></a>
			<s:if test="pageNums[%{pageNumsStatus.index}]==currentPageNumber"></u></s:if> 
			</s:iterator>
			</p>
			
	<p class="StandardTextDarkGray" width="550"><s:property value="currentPageNumber" /> </p>
			<table>
				<tr>
					<!-- header for left side table -->
					<td class="TableRowText01">Compound ID</td>
					<td class="TableRowText01">Structure</td>
					<s:if test="dataset.datasetType=='MODELING'">
					<td class="TableRowText01">Activity</td>
					</s:if>
				</tr>
				<!-- body for left side table -->
				<s:iterator value="datasetCompounds" status="datasetCompoundsStatus">
				<tr>
					<td class="TableRowText02"><s:property value="compoundId" /></td>
					<td class="TableRowText02">
<a href="#" onclick="window.open('compound3D?compoundId=<s:property value="compoundId" />&project=<s:property value="dataset.fileName" />&projectType=dataset&user=<s:property value="user.userName" />&datasetID=<s:property value="dataset.fileId" />, '<% new java.util.Date().getTime(); %>','width=350, height=350'); return false;">
<img src="/imageServlet?user=<s:property value="user.userName" />&projectType=dataset&compoundId=<s:property value='compoundId' />&project=<s:property value="dataset.fileName" />&datasetID=<s:property value="dataset.fileId" />" border="0" height="150" onmouseover='enlargeImage(this);' onmouseout='shrinkImage(this)'/></a>					
					</td>
					<s:if test="dataset.datasetType=='MODELING'">
					<td class="TableRowText02"><s:property value="activityValue" /></td>
					</s:if>
				</tr>
				</s:iterator>
			</table>
		</td></tr>
	</table>
	<!-- End Compounds -->