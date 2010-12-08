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
			<th class="TableRowText01">gamma</th>
			<th class="TableRowText01">cost</sup></th>
			<th class="TableRowText01">nu</th>
			<th class="TableRowText01">loss</th>
			<th class="TableRowText01">degree</th>
			<th class="TableRowText01">r<sup>2</sup></th>
			</tr>
			</s:if>
			<s:iterator value="svmModels" status="modelsStatus">
				<tr>
				<td class="TableRowText02"><s:property value="gamma" /></td>
				<td class="TableRowText02"><s:property value="cost" /></td>
				<td class="TableRowText02"><s:property value="nu" /></td>
				<td class="TableRowText02"><s:property value="epsilon (loss)" /></td>
				<td class="TableRowText02"><s:property value="degree" /></td>
				<td class="TableRowText02"><s:property value="rSquaredTest" /></td>
				</tr> 
			</s:iterator>
			</s:if>
			<s:elseif test="dataType=='CATEGORY'">
			<s:if test="svmModels.size!=0">
			<tr>
				<th class="TableRowText01">gamma</th>
				<th class="TableRowText01">cost</sup></th>
				<th class="TableRowText01">nu</th>
				<th class="TableRowText01">loss</th>
				<th class="TableRowText01">degree</th>
				<th class="TableRowText01">CCR</th>
			</tr>
			</s:if>
			
			<s:iterator value="svmModels" status="modelsStatus">
				<tr>
				<td class="TableRowText02"><s:property value="gamma" /></td>
				<td class="TableRowText02"><s:property value="cost" /></td>
				<td class="TableRowText02"><s:property value="nu" /></td>
				<td class="TableRowText02"><s:property value="epsilon (loss)" /></td>
				<td class="TableRowText02"><s:property value="degree" /></td>
				<td class="TableRowText02"><s:property value="ccrTest" /></td>
				</tr>
			</s:iterator>
		
			</s:elseif>
			</table>
		</s:else>
