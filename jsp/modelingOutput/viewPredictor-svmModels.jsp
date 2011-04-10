<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>
	
<!-- SVM Models -->	

	<br />
		<p class="StandardTextDarkGray"><b><u>Models</u></b></p>
		<s:if test="dataset.splitType=='NFOLD'">
			<p class="StandardTextDarkGray">View Fold: 
			<s:iterator value="foldNums" status="foldNumsStatus">
			<s:if test="#foldNumsStatus.index+1==currentFoldNumber"><b><s:property/></b></s:if>
			<s:else><a href="#tabs" onclick=replaceTabContents("modelsDiv","viewPredictorSvmModelsSection?predictorId=<s:property value="selectedPredictor.id" />&isYRandomPage=<s:property value="isYRandomPage" />&currentFoldNumber=<s:property value="%{#foldNumsStatus.index+1}"/>")><s:property /></a></s:else>
			</s:iterator>/
			</p>
		</s:if>
		<p class="StandardTextDarkGray">
		
		<s:if test="svmModelSets.size==0">
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
		
			<s:iterator value="svmModelSets" status="svmModelSetsStatus">
				<s:if test="svmModelSets.size()>1">
					<p class="StandardTextDarkGray"><u>Fold <s:property value="#svmModelSetsStatus.index+1" /></u></p>
				</s:if>
				<table width="100%" align="center" class="sortable" id="models">
				<s:if test="selectedPredictor.activityType=='CONTINUOUS'">
				<s:if test="svmModelSets[#svmModelSetsStatus.index].size!=0">
				<tr>
				<th class="TableRowText01">gamma</th>
				<th class="TableRowText01">cost</sup></th>
				<th class="TableRowText01">nu</th>
				<th class="TableRowText01">epsilon (loss)</th>
				<th class="TableRowText01">degree</th>
				<th class="TableRowText01">r<sup>2</sup></th>
				</tr>
				</s:if>
				<s:iterator value="svmModelSets[#svmModelSetsStatus.index]" status="modelsStatus">
					<tr>
					<td class="TableRowText02"><s:property value="gamma" /></td>
					<td class="TableRowText02"><s:property value="cost" /></td>
					<td class="TableRowText02"><s:property value="nu" /></td>
					<td class="TableRowText02"><s:property value="loss" /></td>
					<td class="TableRowText02"><s:property value="degree" /></td>
					<td class="TableRowText02"><s:property value="rSquaredTest" /></td>
					</tr> 
				</s:iterator>
				</s:if>
				<s:elseif test="selectedPredictor.activityType=='CATEGORY'">
				<s:if test="svmModelSets[#svmModelSetsStatus.index].size!=0">
				<tr>
					<th class="TableRowText01">gamma</th>
					<th class="TableRowText01">cost</sup></th>
					<th class="TableRowText01">nu</th>
					<th class="TableRowText01">epsilon (loss)</th>
					<th class="TableRowText01">degree</th>
					<th class="TableRowText01">CCR</th>
				</tr>
				</s:if>
				
				<s:iterator value="svmModelSets[#svmModelSetsStatus.index]" status="modelsStatus">
					<tr>
					<td class="TableRowText02"><s:property value="gamma" /></td>
					<td class="TableRowText02"><s:property value="cost" /></td>
					<td class="TableRowText02"><s:property value="nu" /></td>
					<td class="TableRowText02"><s:property value="loss" /></td>
					<td class="TableRowText02"><s:property value="degree" /></td>
					<td class="TableRowText02"><s:property value="ccrTest" /></td>
					</tr>
				</s:iterator>
			
				</s:elseif>
				</table>
			</s:iterator>
			
		</s:else>
