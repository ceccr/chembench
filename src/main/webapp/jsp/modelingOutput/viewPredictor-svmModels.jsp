<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" import="java.util.*" %>
	
<!-- SVM Models -->
	
	<br />
		<s:if test="isYRandomPage=='NO'">
			<p class="StandardTextDarkGray"><b><u>Models</u></b></p>
			
			<s:if test="dataset.splitType=='NFOLD'">
				<p class="StandardTextDarkGray">View Fold: 
				<s:iterator value="foldNums" status="foldNumsStatus">
				<s:if test="#foldNumsStatus.index+1==currentFoldNumber"><b><s:property/></b></s:if>
				<s:else><a href="#tabs" onclick=replaceTabContents("modelsDiv","viewPredictorSvmModelsSection?id=<s:property value="selectedPredictor.id" />&isYRandomPage=<s:property value="isYRandomPage" />&currentFoldNumber=<s:property value="%{#foldNumsStatus.index}"/>")><s:property /></a></s:else>
				</s:iterator>
				</p>
				<p class="StandardTextDarkGray">Models for fold <s:property value="currentFoldNumber"/>:</p>
			</s:if>
		</s:if>
		<s:else>
			<p class="StandardTextDarkGray"><b><u>Y-Randomized Models</u></b></p>
			
			<p class="StandardTextDarkGray">
					In y-Randomization modeling, Chembench attempts to create a second predictor from a copy of your
					data where the compound activities have been shuffled. Ideally, no models with a high
					and R<sup>2</sup> will be produced. If the y-Randomized models are similar to the real models built on
					your data (see Models tab), the predictor should be considered invalid and the dataset or parameters must
					be revised. Y-randomized models are only created for validation purposes and are not used in predictions.
			</p>
			
			<s:if test="dataset.splitType=='NFOLD'">
				<p class="StandardTextDarkGray">View Fold: 
				<s:iterator value="foldNums" status="foldNumsStatus">
				<s:if test="#foldNumsStatus.index+1==currentFoldNumber"><b><s:property/></b></s:if>
				<s:else><a href="#tabs" onclick=replaceTabContents("yRandomDiv","viewPredictorSvmModelsSection?id=<s:property value="selectedPredictor.id" />&isYRandomPage=<s:property value="isYRandomPage" />&currentFoldNumber=<s:property value="%{#foldNumsStatus.index}"/>")><s:property /></a></s:else>
				</s:iterator>
				</p>
			</s:if>
			<p class="StandardTextDarkGray">Models for fold <s:property value="currentFoldNumber"/>:</p>
		</s:else>		
		
		<p class="StandardTextDarkGray">
		<s:if test="svmModels.size==0">
			<s:if test="selectedPredictor.activityType=='CONTINUOUS'">
				No models that passed your r<sup>2</sup> cutoff were generated.<br/>
			</s:if>
			<s:else>
				No models that passed your CCR cutoff were generated.<br/>
			</s:else>
		</s:if>
		<s:elseif test="selectedPredictor.userName=='all-users'">
			<br/>Model information is not available for public predictors.<br/>
		</s:elseif>
		<s:else>
			<table width="100%" align="center" class="sortable" id="models">
			<s:if test="selectedPredictor.activityType=='CONTINUOUS'">
			<s:if test="svmModels.size!=0">
			<tr>
			<s:if test='svmParameters.svmKernel=="1"'>
				<th class="TableRowText01">degree</th>
			</s:if>
			<s:if test='svmParameters.svmKernel!="0"'>
				<th class="TableRowText01">gamma</th>
			</s:if>
			<th class="TableRowText01">cost</sup></th>
			<s:if test='svmParameters.svmTypeContinuous=="4"'>
				<th class="TableRowText01">nu</th>
			</s:if>
			<s:if test='svmParameters.svmTypeContinuous=="3"'>
				<th class="TableRowText01">epsilon (loss)</th>
			</s:if>
			<th class="TableRowText01">r<sup>2</sup></th>
			</tr>
			</s:if>
			<s:iterator value="svmModels" status="modelsStatus">
				<tr>
				<s:if test='svmParameters.svmKernel=="1"'>
					<td class="TableRowText02"><s:property value="degree" /></td>
				</s:if>				
				<s:if test='svmParameters.svmKernel!="0"'>
					<td class="TableRowText02"><s:property value="gamma" /></td>
				</s:if>				
				<td class="TableRowText02"><s:property value="cost" /></td>
				<s:if test='svmParameters.svmTypeContinuous=="4"'>
					<td class="TableRowText02"><s:property value="nu" /></td>
				</s:if>
				<s:if test='svmParameters.svmTypeContinuous=="3"'>
					<td class="TableRowText02"><s:property value="loss" /></td>
				</s:if>				
				<td class="TableRowText02"><s:property value="rSquaredTest" /></td>
				</tr> 
			</s:iterator>
			</s:if>
			<s:elseif test="selectedPredictor.activityType=='CATEGORY'">
			<s:if test="svmModels.size!=0">
			<tr>
				<s:if test='svmParameters.svmKernel=="1"'>
					<th class="TableRowText01">degree</th>
				</s:if>
				<s:if test='svmParameters.svmKernel!="0"'>
					<th class="TableRowText01">gamma</th>
				</s:if>
				<s:if test='svmParameters.svmTypeCategory=="0"'>
					<th class="TableRowText01">cost</sup></th>
				</s:if>
				<s:if test='svmParameters.svmTypeCategory=="1"'>
					<th class="TableRowText01">nu</th>
				</s:if>
				<th class="TableRowText01">CCR</th>
			</tr>
			</s:if>
			
			<s:iterator value="svmModels" status="modelsStatus">
				<tr>
				<s:if test='svmParameters.svmKernel=="1"'>
					<td class="TableRowText02"><s:property value="degree" /></td>
				</s:if>
				<s:if test='svmParameters.svmKernel!="0"'>
					<td class="TableRowText02"><s:property value="gamma" /></td>
				</s:if>
				<s:if test='svmParameters.svmTypeCategory=="0"'>
					<td class="TableRowText02"><s:property value="cost" /></td>
				</s:if>
				<s:if test='svmParameters.svmTypeCategory=="1"'>
					<td class="TableRowText02"><s:property value="nu" /></td>
				</s:if>
				<td class="TableRowText02"><s:property value="ccrTest" /></td>
				</tr>
			</s:iterator>
		
			</s:elseif>
			</table>
		</s:else>
