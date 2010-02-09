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
				<br/>No models that passed your r<sup>2</sup> and q<sup>2</sup> cutoffs were generated.<br/>
			</s:if>
			<s:else>
				No models were generated that passed your cutoffs.<br/>
			</s:else>
		</s:if>
		<s:elseif test="selectedPredictor.userName=='_all'">
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
	
		<table width="100%" align="center">
		<s:if test="dataType=='CONTINUOUS'">
		<s:if test="models.size!=0">
		<tr>
			<td class="TableRowText01narrow">nnn</td>
			<td class="TableRowText01narrow">q<sup>2</sup></td>
			<td class="TableRowText01narrow">n</td>
			<td class="TableRowText01narrow">r</td>
			<td class="TableRowText01narrow">r<sup>2</sup></td>
			<td class="TableRowText01narrow">R<sub>01</sub><sup>2</sup></td>
			<td class="TableRowText01narrow">R<sub>02</sub><sup>2</sup></td>
			<td class="TableRowText01narrow">k1</td>
			<td class="TableRowText01narrow">k2</td>
			<td class="TableRowText01narrow" colspan="2">Descriptors</td>
		</tr>
		</s:if>
		<s:iterator value="models" status="modelsStatus">
			<s:if test="#modelsStatus.index<10">
			<tr>
			<td class="TableRowText02narrow"><s:property value="nnn" /></td>
			<td class="TableRowText02narrow"><s:property value="q_squared" /></td>
			<td class="TableRowText02narrow"><s:property value="n" /></td>
			<td class="TableRowText02narrow"><s:property value="r" /></td>
			<td class="TableRowText02narrow"><s:property value="r_squared" /></td>
			<td class="TableRowText02narrow"><s:property value="r01_squared" /></td>
			<td class="TableRowText02narrow"><s:property value="r02_squared" /></td>
			<td class="TableRowText02narrow"><s:property value="k1" /></td>
			<td class="TableRowText02narrow"><s:property value="k2" /></td>
			<td class="TableRowText02narrow" colspan="2"><s:property value="descriptorsUsed" /></td>
			</tr> 
			</s:if>
		</s:iterator>
		</s:if>
		<s:elseif test="dataType=='CATEGORY'">
		<s:if test="models.size!=0">
		<tr>
			<td class="TableRowText01">nnn</td>
			<td class="TableRowText01">Training Accuracy</td>
			<td class="TableRowText01">Normalized Training Accuracy</td>
			<td class="TableRowText01">Test Accuracy</td>
			<td class="TableRowText01">Normalized Test Accuracy</td>
			<td class="TableRowText01" colspan="2">Descriptors</td>
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