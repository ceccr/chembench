<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>
	
<!-- Models Page -->	
	<br />
		<s:if test="isYRandomPage=='YES'">
			<p class="StandardTextDarkGray"><b><u>Y-Random Models</u></b></p>
			<p class="StandardTextDarkGray">
					In y-Randomization modeling, Chembench attempts to create a second predictor from a copy of your
					data where the compound activities have been shuffled. Ideally, no models with a high q<sup>2</sup>
					and R<sup>2</sup> will be produced. If the y-Randomized models are similar to the real models built on
					your data (see Models tab), the predictor should be considered invalid and the dataset or parameters must
					be revised. Y-randomized models are only created for validation purposes and are not used in predictions.
			</p>
		</s:if>
		<s:else>
			<p class="StandardTextDarkGray"><b><u>Models</u></b></p>
		</s:else>
		<p class="StandardTextDarkGray">
		<s:if test="knnPlusModelSets.size==0">
			No models that passed your training and test set cutoffs were generated.<br/>
		</s:if>
		<s:elseif test="selectedPredictor.userName=='all-users'">
			<br/>Model information is not available for public predictors.<br/>
		</s:elseif>
		<s:else>
			<s:if test="selectedPredictor.childType!='NFOLD'">
				<s:if test="isYRandomPage=='YES'">
					<s:if test="selectedPredictor.modelMethod=='KNN-SA'">
						Of the <s:property value="selectedPredictor.numTotalModels" /> models generated, 
						<s:property value="selectedPredictor.numyTestModels" /> passed both training and test set criteria. 	
					</s:if>
					<s:else>
						<s:property value="selectedPredictor.numyTestModels" /> models passed both training and test set criteria. 		
					</s:else>
				</s:if>
				<s:else>
					<s:if test="selectedPredictor.modelMethod=='KNN-SA'">
						Of the <s:property value="selectedPredictor.numTotalModels" /> models generated, 
						<s:property value="selectedPredictor.numTestModels" /> passed both training and test set criteria. 	
					</s:if>
					<s:else>
						<s:property value="selectedPredictor.numTestModels" /> models passed both training and test set criteria. 		
					</s:else>
				</s:else>
			</s:if>
			<br />
		
			<!-- Table of Models -->
			<p class="StandardTextDarkGray">
			<s:iterator value="knnPlusModelSets" status="knnPlusModelSetsStatus">
			<s:if test="knnPlusModelSets.size>1">
				<p class="StandardTextDarkGray"><u>Fold <s:property value="#knnPlusModelSetsStatus.index+1" /></u></p>
			</s:if>
			
				<table width="100%" align="center" class="sortable" id="knnPlusModels">
				<s:if test="selectedPredictor.activityType=='CONTINUOUS'">
				<s:if test="knnPlusModelSets[#knnPlusModelSetsStatus.index].size!=0">
				<tr>
				<th class="TableRowText01narrow">k</th>
				<th class="TableRowText01narrow">q<sup>2</sup> (Training)</th>
				<th class="TableRowText01narrow">r<sup>2</sup> (Test)</th>
				<th class="TableRowText01narrow">R<sub>0</sub><sup>2</sup></th>
				<th class="TableRowText01narrow">R<sub>01</sub><sup>2</sup></th>
				<th class="TableRowText01narrow">k1</th>
				<th class="TableRowText01narrow">k2</th>
				<th class="TableRowText01narrow_unsortable" colspan="2">Descriptors</th>
				</tr>
				</s:if>
				<s:iterator value="knnPlusModelSets[#knnPlusModelSetsStatus.index]" status="modelsStatus">
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
				
				<s:elseif test="selectedPredictor.activityType=='CATEGORY'">
				<s:if test="knnPlusModelSets[#knnPlusModelSetsStatus.index].size()!=0">
				<tr>
					<th class="TableRowText01">k</th>
					<th class="TableRowText01">Training Accuracy</th>
					<th class="TableRowText01">Normalized Training Accuracy</th>
					<th class="TableRowText01">Test Accuracy</th>
					<th class="TableRowText01">Normalized Test Accuracy</th>
					<th class="TableRowText01narrow_unsortable" colspan="2">Descriptors</th>
				</tr>
				</s:if>
				
				<s:iterator value="knnPlusModelSets[#knnPlusModelSetsStatus.index]" status="modelsStatus">
					<tr>
						<td class="TableRowText02"><s:property value="kOrR" /></td>
						<td class="TableRowText02"><s:property value="AccuracyTraining" /></td>
						<td class="TableRowText02"><s:property value="CCRNormalizedAccuracyTraining" /></td>
						<td class="TableRowText02"><s:property value="AccuracyTest" /></td>
						<td class="TableRowText02"><s:property value="CCRNormalizedAccuracyTest" /></td>
						<td class="TableRowText02" colspan="2"><s:property value="dimsNames" /></td>
					</tr>
				</s:iterator>
			
				</s:elseif>
				</table>
			</s:iterator>
			
		<!-- End Table of Models -->
		
			<s:if test="mostFrequentDescriptors!=''">
				<br />
				<p class="StandardTextDarkGray"><b><u>Descriptor Frequencies</u></b></p>
				<p class="StandardTextDarkGray"><s:property value="mostFrequentDescriptors" /></p>
			</s:if>
		</s:else>
		
<!-- End Models Page -->