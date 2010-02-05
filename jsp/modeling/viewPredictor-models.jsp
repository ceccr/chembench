<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<!-- Models -->	
	<br />
		<p class="StandardTextDarkGray"><b><u>Models</u></b></p>
		<br />
		<s:if test="models.size==0">
			<s:if test="selectedPredictor.activityType=='CONTINUOUS'">
				<br/><b class="StandardTextDarkGray">No models that passed your r<sup>2</sup> and q<sup>2</sup> cutoffs were generated.</b><br/><br/>
			</s:if>
			<s:else>
				<b class="StandardTextDarkGray">No models were generated.</b><br/><br/>
			</s:else>
		</s:if>
		<s:elseif test="">
			<br/><b class="StandardTextDarkGray">Model information is not available for public predictors.</b><br/><br/>
		</s:elseif>
		<s:else>
		<p class="StandardTextDarkGray">
			<b>Of the <s:property value="selectedPredictor.numTotalModels" /> models generated, 
			<s:property value="selectedPredictor.numTrainModels" /> passed the training set criteria and 
			<s:property value="selectedPredictor.numTestModels" /> passed both training and test set criteria. 
			<br />
			<s:if test="selectedPredictor.numTestModels>=10">
               The top 10 models are displayed below.</b></p>
			<br />
			</s:if>
			<s:elseif test="selectedPredictor.numTestModels<10">
				The top <s:property value="selectedPredictor.numTestModels" />
				models are displayed below.</b></p><br />
			</s:elseif>
		</s:else>	
	
		<table width="100%" align="center">
		<s:if test="dataType=='CONTINUOUS'">
		<tr>
			<td class="TableRowText01">nnn</td>
			<td class="TableRowText01">q<sup>2</sup></td>
			<td class="TableRowText01">n</td>
			<td class="TableRowText01">r</td>
			<td class="TableRowText01">r<sup>2</sup></td>
			<td class="TableRowText01">R<sub>01</sub><sup>2</sup></td>
			<td class="TableRowText01">R<sub>02</sub><sup>2</sup></td>
			<td class="TableRowText01">k1</td>
			<td class="TableRowText01">k2</td>
		</tr>
		
		<s:iterator value="models" status="modelsStatus">
			<s:if test="#modelsStatus.index<10">
			<tr>
			<td class="TableRowText02"><s:property value="nnn" /></td>
			<td class="TableRowText02"><s:property value="q_squared" /></td>
			<td class="TableRowText02"><s:property value="n" /></td>
			<td class="TableRowText02"><s:property value="r" /></td>
			<td class="TableRowText02"><s:property value="r_squared" /></td>
			<td class="TableRowText02"><s:property value="r01_squared" /></td>
			<td class="TableRowText02"><s:property value="r02_squared" /></td>
			<td class="TableRowText02"><s:property value="k1" /></td>
			<td class="TableRowText02"><s:property value="k2" /></td>
			</tr> 
			</s:if>
		</s:iterator>
		</s:if>
		<s:elseif test="dataType=='CATEGORY'">
		<tr>
			<td class="TableRowText01">nnn</td>
			<td class="TableRowText01">Training Accuracy</td>
			<td class="TableRowText01">Normalized Training Accuracy</td>
			<td class="TableRowText01">Test Accuracy</td>
			<td class="TableRowText01">Normalized Test Accuracy</td>
		</tr>
		
		<s:iterator value="models" status="modelsStatus">
			<s:if test="#modelsStatus.index<10">
			<tr>
				<td class="TableRowText02"><s:property value="nnn" /></td>
				<td class="TableRowText02"><s:property value="trainingAcc" /></td>
				<td class="TableRowText02"><s:property value="normalizedTrainingAcc" /></td>
				<td class="TableRowText02"><s:property value="testAcc" /></td>
				<td class="TableRowText02"><s:property value="normalizedTestAcc" /></td>
			</tr>
			</s:if>
		</s:iterator>
	
		</s:elseif>
		</table>
<!-- End Models -->