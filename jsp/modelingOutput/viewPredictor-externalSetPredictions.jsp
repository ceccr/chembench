<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

	<br />
	<s:if test="dataset.splitType=='NFOLD'">
		<p class="StandardTextDarkGray">View Fold: 
		<s:iterator value="foldNums" status="foldNumsStatus">
		<s:if test="foldNumsStatus.index==currentFoldNumber"><s:property/></s:if>
		<s:else><a href="#tabs" onclick=replaceTabContents("externalValidationDiv","viewPredictorExternalValidationSection?predictorId=<s:property value="selectedPredictor.predictorId" />&foldIndex=<s:property value="%{foldNumsStatus.index}"/>")><s:property /></a></s:else>
		</s:iterator>
		</p>
	</s:if>
	<!-- External Validation Chart -->
	<s:if test="externalValValues.size!=0&&hasGoodModels=='YES'">
	
	<s:if test="selectedPredictor.activityType=='CONTINUOUS'">
		<p class="StandardTextDarkGray"><b><u>External Validation Chart</u></b>
		<s:url id="externalChartLink" value="/externalValidationChart.do />" includeParams="none">
			<s:param name="user" value="user.userName" />
			<s:param name="project" value="selectedPredictor.name" />
			<s:param name="foldNumber" value="currentFoldNumber" />
		</s:url>
		<br />
		<sx:div id="extValidationChart" href="%{externalChartLink}" theme="ajax">
		</sx:div>
		</p>
		<s:if test="dataset.splitType=='NFOLD'&&currentFoldNumber=='0'">
			<p class="StandardTextDarkGray">
			Overall R<sup>2</sup> for compounds in all external sets: <b><s:property value="rSquared" /></b>
			</p>
			<p class="StandardTextDarkGray">
			Average and standard deviation of R<sup>2</sup> among sets: <b><s:property value="rSquaredAverageAndStddev" /></b><br />
			</p>
		</s:if>
		<s:else>
			<p class="StandardTextDarkGray">
			R<sup>2</sup> for external set: <b><s:property value="rSquared" /></b>
			</p>
		</s:else>
	</s:if>
	<s:elseif test="selectedPredictor.activityType=='CATEGORY'">
		<p class="StandardTextDarkGray"><b><u>Confusion Matrix</u></b>
		</p><p class="StandardTextDarkGray">Predicted category is based on the consensus 
		prediction of the compound's activity rounded to the nearest whole number.</p>
		<table>
		<tr>
		<td></td>
		<s:iterator value="confusionMatrix.uniqueObservedValues">
			<td class="TableRowText01">Predicted <s:property /></td>
		</s:iterator>
		</tr>
		<s:iterator value="confusionMatrix.matrixValues" status="confusionMatrixStatus">
		<tr>
			<td class="TableRowText01">Observed <s:property value="confusionMatrix.uniqueObservedValues[#confusionMatrixStatus.index]" /></td>
			<s:iterator value="confusionMatrix.matrixValues[#confusionMatrixStatus.index]">
			<td class="TableRowText02"><s:property /></td>
			</s:iterator>
		</tr>
		</s:iterator>
		</table>
		
		<s:if test="dataset.splitType=='NFOLD'&&currentFoldNumber=='0'">
			<p class="StandardTextDarkGray">
			Overall CCR for compounds in all external sets: <b><s:property value="confusionMatrix.ccrAsString" /></b>
			</p>
			<p class="StandardTextDarkGray">
			Average and standard deviation of CCR among sets: <b><s:property value="ccrAverageAndStddev" /></b><br />
			</p>
		</s:if>
		<s:else>
			<p class="StandardTextDarkGray">
			CCR for external set: <b><s:property value="confusionMatrix.ccrAsString" /></b>
			</p>
		</s:else>
	</s:elseif>
	</s:if>
	<br />
	<!-- End External Validation Chart -->
	
	<!-- External Validation Compound Predictions -->
		<p class="StandardTextDarkGray"><b><u>Predictions for External Validation Set</u></b></p>
	
	<s:if test="hasGoodModels=='NO'">
			<br/><p class="StandardTextDarkGray">No models were generated that passed your cutoffs.</p><br/><br/>
	</s:if>
	<s:elseif test="externalValValues.size==0">
		<br/><p class="StandardTextDarkGray">There were no compounds in the dataset's external validation set.</p><br /><br />
	</s:elseif>
	<s:else>
	<table width="100%" align="center" class="sortable" id="externalSetPredictions">
		<!--DWLayoutTable-->
		<tr>
		<th class="TableRowText01">Compound ID</th>
		<th class="TableRowText01_unsortable">Structure</th>
		<th class="TableRowText01">Observed Value</th>
		<th class="TableRowText01">Predicted Value</th>
		<th class="TableRowText01">Residual</th>
		<th class="TableRowText01">Predicting Models / Total Models</th>
		</tr>
		
	<s:iterator value="externalValValues" status="extValStatus">
		<tr>
			<td class="TableRowText02"><s:property value="compoundId" /></td>
			<td class="TableRowText02">
			<a href="#" onclick="window.open('compound3D?project=<s:property value='selectedPredictor.name' />&projectType=modeling&compoundId=<s:property value='compoundId' />&user=<s:property value='user.userName' />&datasetID=<s:property value='selectedPredictor.datasetId' />', '<% new java.util.Date().getTime(); %>','width=350, height=350'); return false;">
			<img src="/imageServlet?projectType=modeling&user=<s:property value='dataset.userName' />&project=<s:property value='selectedPredictor.name' />&compoundId=<s:property value='compoundId' />&datasetName=<s:property value='dataset.fileName' />" border="0" height="150" onmouseover='enlargeImage(this);' onmouseout='shrinkImage(this)'/>
			</a>
			</td>
			<td class="TableRowText02"><s:property value="actualValue" /></td>
			<td class="TableRowText02">
			<s:if test="numModels>=2">
				<s:property value="predictedValue" /> &#177; <s:property value="standDev" />
			</s:if>
			<s:elseif test="numModels==1">
				<s:property value="predictedValue" />
			</s:elseif>
			</td>
			<td class="TableRowText02">
				<s:property value="residuals[#extValStatus.index]" />
			</td>
			<td class="TableRowText02"><s:property value="numModels" /> / <s:property value="selectedPredictor.numTestModels" /></td>
		</tr>
	</s:iterator>
	</s:else>
	</table>
	<br />
	<!-- End External Validation Compound Predictions -->
	