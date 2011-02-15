<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>
	
<!-- Models -->	
	<br />
		<p class="StandardTextDarkGray"><b><u>Models</u></b></p>
		
		<p class="StandardTextDarkGray">
		<s:if test="models.size==0">
			<s:if test="selectedPredictor.activityType=='CONTINUOUS'">
				No models that passed your r<sup>2</sup> and q<sup>2</sup> cutoffs were generated.<br/>
			</s:if>
			<s:else>
				No models were generated that passed your cutoffs.<br/>
			</s:else>
		</s:if>
		<s:elseif test="selectedPredictor.userName=='all-users'">
			<br/>Model information is not available for public predictors.<br/>
		</s:elseif>
		<s:else>
			Of the <s:property value="selectedPredictor.numTotalModels" /> models generated, 
			<s:property value="selectedPredictor.numTrainModels" /> passed the training set criteria and 
			<s:property value="selectedPredictor.numTestModels" /> passed both training and test set criteria. 
			For information on what each statistic means, check the <a href="/help-faq#05">FAQ</a> in the help pages.
			<br />
		</s:else>	
		</p>
	
		<table width="100%" align="center" class="sortable" id="models">
		<s:if test="selectedPredictor.activityType=='CONTINUOUS'">
		<s:if test="models.size!=0">
		<tr>
		<th class="TableRowText01narrow">nnn</th>
		<th class="TableRowText01narrow">q<sup>2</sup></th>
		<th class="TableRowText01narrow">n</th>
		<th class="TableRowText01narrow">r</th>
		<th class="TableRowText01narrow">r<sup>2</sup></th>
		<th class="TableRowText01narrow">R<sub>01</sub><sup>2</sup></th>
		<th class="TableRowText01narrow">R<sub>02</sub><sup>2</sup></th>
		<th class="TableRowText01narrow">k1</th>
		<th class="TableRowText01narrow">k2</th>
		<th class="TableRowText01narrow_unsortable" colspan="2">Descriptors</th>
		</tr>
		</s:if>
		<s:iterator value="models" status="modelsStatus">
			<tr>
			<td class="TableRowText02narrow"><s:property value="nnn" /></td>
			<td class="TableRowText02narrow"><s:property value="QSquared" /></td>
			<td class="TableRowText02narrow"><s:property value="n" /></td>
			<td class="TableRowText02narrow"><s:property value="r" /></td>
			<td class="TableRowText02narrow"><s:property value="RSquared" /></td>
			<td class="TableRowText02narrow"><s:property value="R01Squared" /></td>
			<td class="TableRowText02narrow"><s:property value="R02Squared" /></td>
			<td class="TableRowText02narrow"><s:property value="k1" /></td>
			<td class="TableRowText02narrow"><s:property value="k2" /></td>
			<td class="TableRowText02narrow" colspan="2"><s:property value="descriptorsUsed" /></td>
			</tr> 
		</s:iterator>
		</s:if>
		<s:elseif test="selectedPredictor.activityType=='CATEGORY'">
		<s:if test="models.size!=0">
		<tr>
			<th class="TableRowText01">nnn</th>
			<th class="TableRowText01">Training Accuracy</th>
			<th class="TableRowText01">Normalized Training Accuracy</th>
			<th class="TableRowText01">Test Accuracy</th>
			<th class="TableRowText01">Normalized Test Accuracy</th>
			<th class="TableRowText01narrow_unsortable" colspan="2">Descriptors</th>
		</tr>
		</s:if>
		
		<s:iterator value="models" status="modelsStatus">
			<tr>
				<td class="TableRowText02"><s:property value="nnn" /></td>
				<td class="TableRowText02"><s:property value="trainingAcc" /></td>
				<td class="TableRowText02"><s:property value="normalizedTrainingAcc" /></td>
				<td class="TableRowText02"><s:property value="testAcc" /></td>
				<td class="TableRowText02"><s:property value="normalizedTestAcc" /></td>
				<td class="TableRowText02" colspan="2"><s:property value="descriptorsUsed" /></td>
			</tr>
		</s:iterator>
	
		</s:elseif>
		</table>
		
		<s:if test="mostFrequentDescriptors!=''">
			<br />
			<p class="StandardTextDarkGray"><b><u>Descriptor Frequencies</u></b></p>
			<p class="StandardTextDarkGray"><s:property value="mostFrequentDescriptors" /></p>
		</s:if>
		
<!-- End Models -->