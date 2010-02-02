<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

	<!-- Predictions -->	
	<br />
		<p class="StandardTextDarkGray"><b><u>Prediction Results</u></b></p>
		
	<table width="924" align="center">
		<tr><td>
		
			<p class="StandardTextDarkGray" width="550">Go To Page: 
			<s:iterator value="pageNums" status="pageNumsStatus">
			<s:if test="pageNums[#pageNumsStatus.index]==currentPageNumber"><u></s:if>
			<a href="viewPrediction?id=<s:property value='prediction.predictionId' />&pagenum=<s:property/>"><s:property/></a><s:if test="pageNums[#pageNumsStatus.index]==currentPageNumber"></u> </s:if> 
			</s:iterator>
			</p>
			
			<table>
				<tr>
				<!-- header for left side table -->
				<td class="TableRowText01">Compound ID</td>
				<td class="TableRowText01">Structure</td>
				<s:iterator value="predictors" status="predictorsStatus">
				<td class="TableRowText01">(<s:property value="name" />) Prediction</td>
				<td class="TableRowText01">(<s:property value="name" />) Number of Predicting Models / Total Models</td>
				</s:iterator>
				</tr>
				<!-- body for left side table -->
				<s:iterator value="compoundPredictionValues" status="compoundPredictionValuesStatus">
					<tr>
						<td class="TableRowText02"><s:property value="compound" /></td>
						<td class="TableRowText02">
							<a href="#" onclick="window.open('compound3D?compoundId=<s:property value="compound" />&project=<s:property value="prediction.jobName" />&projectType=predictor&user=<s:property value="user.userName" />&datasetID=<s:property value="prediction.datasetId" />, '<% new java.util.Date().getTime(); %>','width=350, height=350'); return false;">
							<img src="/imageServlet?user=<s:property value="user.userName" />&projectType=predictor&compoundId=<s:property value='compound' />&project=<s:property value="prediction.jobName" />&datasetID=<s:property value="prediction.datasetId" />" border="0" height="150" onmouseover='enlargeImage(this);' onmouseout='shrinkImage(this)'/></a>
						</td>
						<s:iterator value="predictionValues" status="predictionValuesStatus">
						<td class="TableRowText02"><s:property value="predictedValue" /><s:if test="standardDeviation!=null"> &#177; </s:if><s:property value="standardDeviation" /><!-- prediction value +/- stddev --></td>
						<td class="TableRowText02"><s:property  value="numModelsUsed" /><!-- number of models in prediction --> / <s:property  value="numTotalModels" /></td>
						</s:iterator>
					</tr>
				</s:iterator>
			</table>
		</td></tr>
	</table>
	<!-- End Predictions -->