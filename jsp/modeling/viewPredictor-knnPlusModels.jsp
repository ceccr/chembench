<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>
	
<!-- Models Page -->	
	<br />
		<p class="StandardTextDarkGray"><b><u>Models</u></b></p>

		<p class="StandardTextDarkGray">
		<s:if test="knnPlusModels.size==0">
			No models that passed your training and test set cutoffs were generated.<br/>
		</s:if>
		<s:elseif test="selectedPredictor.userName=='_all'">
			<br/>Model information is not available for public predictors.<br/>
		</s:elseif>
		<s:else>
		
			<s:if test="selectedPredictor.modelMethod==KNN-SA">
				Of the <s:property value="selectedPredictor.numTotalModels" /> models generated, 
				<s:property value="selectedPredictor.numTestModels" /> passed both training and test set criteria. 
			</s:if>
			<s:else>
				<s:property value="selectedPredictor.numTestModels" /> models were generated that
				passed both training and test set criteria. 
			</s:else>
			
			<br />
		</s:else>	
		</p>
	
	<!-- Table of Models -->
		
		<table width="100%" align="center" class="sortable" id="knnPlusModels">
		<s:if test="dataType=='CONTINUOUS'">
		<s:if test="knnPlusModels.size!=0">
		<tr>
		<th class="TableRowText01narrow">k</th>
		<th class="TableRowText01narrow">q<sup>2 (Training)</sup></th>
		<th class="TableRowText01narrow">r<sup>2 (Test)</sup></th>
		<th class="TableRowText01narrow">R<sub>0</sub><sup>2</sup></th>
		<th class="TableRowText01narrow">R<sub>01</sub><sup>2</sup></th>
		<th class="TableRowText01narrow">k1</th>
		<th class="TableRowText01narrow">k2</th>
		<th class="TableRowText01narrow_unsortable" colspan="2">Descriptors</th>
		</tr>
		</s:if>
		<s:iterator value="knnPlusModels" status="modelsStatus">
			<tr>
			<td class="TableRowText02narrow"><s:property value="kOrR" /></td>
			<td class="TableRowText02narrow"><s:property value="Q2Training" /></td>
			<td class="TableRowText02narrow"><s:property value="R2Test" /></td>
			<td class="TableRowText02narrow"><s:property value="R02Test" /></td>
			<td class="TableRowText02narrow"><s:property value="R012Test" /></td>
			<td class="TableRowText02narrow"><s:property value="k1Test" /></td>
			<td class="TableRowText02narrow"><s:property value="k2Test" /></td>
			<td class="TableRowText02narrow" colspan="2"><s:property value="dimsNames" /></td>
			</tr> 
		</s:iterator>
		</s:if>
		
		
		<s:elseif test="dataType=='CATEGORY'">
		<s:if test="knnPlusModels.size!=0">
		<tr>
			<th class="TableRowText01">k</th>
			<th class="TableRowText01">Training Accuracy</th>
			<th class="TableRowText01">Training Accuracy (with group weights)</th>
			<th class="TableRowText01">Normalized Training Accuracy</th>
			<th class="TableRowText01">Normalized Training Accuracy (with group weights)</th>
			<th class="TableRowText01">Test Accuracy</th>
			<th class="TableRowText01">Test Accuracy (with group weights)</th>
			<th class="TableRowText01">Normalized Test Accuracy</th>
			<th class="TableRowText01">Normalized Test Accuracy (with group weights)</th>
			<th class="TableRowText01narrow_unsortable" colspan="2">Descriptors</th>
		</tr>
		</s:if>
		
		<s:iterator value="knnPlusModels" status="modelsStatus">
			<tr>
				<td class="TableRowText02"><s:property value="kOrR" /></td>
				<td class="TableRowText02"><s:property value="AccuracyTraining" /></td>
				<td class="TableRowText02"><s:property value="AccuracyWithGroupWeightsTraining" /></td>
				<td class="TableRowText02"><s:property value="CCRNormalizedAccuracyTraining" /></td>
				<td class="TableRowText02"><s:property value="CCRWithGroupWeightsTraining" /></td>
				<td class="TableRowText02"><s:property value="AccuracyTest" /></td>
				<td class="TableRowText02"><s:property value="AccuracyWithGroupWeightsTest" /></td>
				<td class="TableRowText02"><s:property value="CCRNormalizedAccuracyTest" /></td>
				<td class="TableRowText02"><s:property value="CCRWithGroupWeightsTest" /></td>
				<td class="TableRowText02" colspan="2"><s:property value="descriptorsUsed" /></td>
			</tr>
		</s:iterator>
	
		</s:elseif>
		</table>
		
	<!-- End Table of Models -->
	

		<s:if test="mostFrequentDescriptors!=''">
			<br />
			<p class="StandardTextDarkGray"><b><u>Descriptor Frequencies</u></b></p>
			<p class="StandardTextDarkGray"><s:property value="mostFrequentDescriptors" /></p>
		</s:if>
		
<!-- End Models Page -->