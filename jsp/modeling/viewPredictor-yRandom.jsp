<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

	<script language="JavaScript" src="javascript/sortableTable.js"></script>
	
<!-- y-Random Models -->	
	<br />
	<p class="StandardTextDarkGray"><b><u>y-Randomization Results</u></b></p>
	
	<p class="StandardTextDarkGray">
			<b>Y-randomization</b> is a statistical QSAR model validation technique where the performance of the model 
			built with original data is compared to that of models built for multiple artificial datasets with
			randomly shuffled activities. The models of the randomized data are built using the same parameters
			used for the actual predictor. Ideally, there will be no models from the randomized data with high 
			values of both q<sup>2</sup> (internal test set) and R<sup>2</sup> (external set). Your modeling parameters
			need to be adjusted if many y-randomized models are being produced with q<sup>2</sup> and R<sup>2</sup> 
			above your cutoff values.
	</p>
	<p class="StandardTextDarkGray">
			<br/>For your data, <font color="red"><s:property value="selectedPredictor.numyTotalModels" /> </font> 
			models for randomized datasets were built and <font color="red">
			<s:property value="selectedPredictor.numyTestModels" /> </font>models were found that passed your cutoffs. <br />
	</p>

	<s:if test="selectedPredictor.numyTestModels>0">
	<table width="100%" align="center" class="sortable" id="yRandomModels">
	<s:if test="dataType=='CONTINUOUS'">
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
	
	<s:iterator value="randomModels" status="randomModelsStatus">
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
	</s:iterator>
	</s:if>
	<s:elseif test="dataType=='CATEGORY'">
	<tr>
		<th class="TableRowText01">nnn</th>
		<th class="TableRowText01">Training Accuracy</th>
		<th class="TableRowText01">Normalized Training Accuracy</th>
		<th class="TableRowText01">Test Accuracy</th>
		<th class="TableRowText01">Normalized Test Accuracy</th>
		<th class="TableRowText01narrow_unsortable" colspan="2">Descriptors</th>
	</tr>
	
	<s:iterator value="randomModels" status="randomModelsStatus">
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
	</s:if>
<!-- End y-Random Models -->	