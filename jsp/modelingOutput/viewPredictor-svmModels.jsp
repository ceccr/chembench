<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>
	
<!-- SVM Models -->	

	<br />
		<p class="StandardTextDarkGray"><b><u>Models</u></b></p>
		
		<p class="StandardTextDarkGray">
		<s:if test="svmModels.size==0">
			<s:if test="selectedPredictor.activityType=='CONTINUOUS'">
				No models that passed your r<sup>2</sup> cutoff were generated.<br/>
			</s:if>
			<s:else>
				No models that passed your CCR cutoff were generated.<br/>
			</s:else>
		</s:if>
		<s:elseif test="selectedPredictor.userName=='_all'">
			<br/>Model information is not available for public predictors.<br/>
		</s:elseif>
		<s:else>
			<table width="100%" align="center" class="sortable" id="models">
			<s:if test="dataType=='CONTINUOUS'">
			<s:if test="svmModels.size!=0">
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
			<s:elseif test="dataType=='CATEGORY'">
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
		</s:else>


<!-- | id | predictorId | isYRandomModel | gamma | cost | nu   | loss | degree | rSquaredTest   | mseTest | ccrTest |
+----+-------------+----------------+-------+------+------+------+--------+----------------+---------+---------+
|  1 |        1512 | NO             | 4.0   | 2.0  | NA   | 0.0  | 5.0    | 0.79695516349  | NA      | NA      |
|  2 |        1512 | NO             | 0.0   | 2.0  | NA   | 0.0  | 5.0    | 0.79695516349  | NA      | NA      |
|  3 |        1512 | NO             | 8.0   | 2.0  | NA   | 0.0  | 5.0    | 0.79695516349  | NA      | NA      |
|  4 |        1512 | NO             | 4.0   | 2.0  | NA   | 0.0  | 2.0    | 0.79695516349  | NA      | NA      |
|  5 |        1512 | NO             | 0.0   | 2.0  | NA   | 0.0  | 2.0    | 0.79695516349  | NA      | NA      |
|-->