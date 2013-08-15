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
			<s:if test="pageNums[#pageNumsStatus.index]==currentPageNumber"><s:property /></s:if>
			<s:else><a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property value='prediction.id' />&currentPageNumber=<s:property/>&orderBy=<s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />")><s:property/></a></s:else> 
			</s:iterator>
			</p>
		</td></tr>
	</table>

        <div style="overflow:auto;">
	<table width="924" align="center">
        <!-- header line for predictor names -->
        <tr>
            <td class="TableRowText01" colspan="2"></td>  <!-- compound ID, sketch -->
            <s:iterator value="predictors" status="predictorsStatus">
                <td class="TableRowText01" colspan="3">   <!-- predictor name -->
                    <s:property value="name" />
                </td>
            </s:iterator>
        </tr>
		<tr>
		<!-- header for table -->
		<td class="TableRowText01">Compound ID<br />
		<a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=compoundId&sortDirection=asc")><img src="theme/img/sortArrowUp.png" /></a>
		<a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=compoundId&sortDirection=desc")><img src="theme/img/sortArrowDown.png" /></a>
		</td>
		<s:if test="!dataset.sdfFile.isEmpty()"><td class="TableRowText01">Structure</td></s:if>
		<s:iterator value="predictors" status="predictorsStatus">
		<td class="TableRowText01">Prediction<br />
		<a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=<s:property value="name" />&sortDirection=asc")><img src="theme/img/sortArrowUp.png" /></a>
		<a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=<s:property value="name" />&sortDirection=desc")><img src="theme/img/sortArrowDown.png" /></a>
		</td>
		<td class="TableRowText01"><s:if test="childType=='NFOLD'">Predicting Folds</s:if><s:else>Predicting Models</s:else></td>
		<td class="TableRowText01">Z-score <br />
		<a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=<s:property value='zScore' />&sortDirection=asc")><img src="theme/img/sortArrowUp.png" /></a>
		<a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=<s:property value='zScore' />&sortDirection=desc")><img src="theme/img/sortArrowDown.png" /></a>
		</td>
		</s:iterator>
		</tr>
		<!-- body for table -->
		<s:iterator value="compoundPredictionValues" status="compoundPredictionValuesStatus">
			<tr>
				<td class="TableRowText02"><s:property value="compound" /></td>
                                <s:if test="!dataset.sdfFile.isEmpty()">
				<td class="TableRowText02">
					<a class="compound_img_a" href="#" onclick="window.open('compound3D?compoundId=<s:property value="compound" />&project=<s:property value="prediction.name" />&projectType=predictor&user=<s:property value="user.userName" />&datasetName=<s:property value="dataset.name" />', '<% new java.util.Date().getTime(); %>','width=400, height=400'); return false;">
					<img  src="/imageServlet?user=<s:property value="dataset.userName" />&projectType=predictor&compoundId=<s:property value='compound' />&project=<s:property value="prediction.name" />&datasetName=<s:property value="dataset.name" />" border="0" height="150"/></a>
				</td>
                                </s:if>
				<s:iterator value="predictionValues" status="predictionValuesStatus">
				<td class="TableRowText02"><s:if test="predictedValue!=null"><s:property value="predictedValue"/></s:if><s:else>Not Predicted</s:else><s:if test="standardDeviation!=null"> &#177; </s:if> <s:property value="standardDeviation" /></td>
				<td class="TableRowText02"><s:if test="PredictedValue!=null"><s:property  value="numModelsUsed" /> / <s:property value="numTotalModels" /></s:if></td>
				</s:iterator>
			</tr>
		</s:iterator>
	</table>
        </div>
	<!-- End Predictions -->
