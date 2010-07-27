<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>
	
<!-- Trees Page -->	
	<br />
		<p class="StandardTextDarkGray"><b><u>Random Forests</u></b></p>

		<p class="StandardTextDarkGray">
		<s:if test="randomForestTrees.size==0">
			No random forest trees were generated.<br/>
		</s:if>
		<s:elseif test="selectedPredictor.userName=='_all'">
			<br/>Model information is not available for public predictors.<br/>
		</s:elseif>
		<s:else>
			To generate the random forest predictor, a random forest is generated for each 
			train-test split, and the trees from each forest are combined together. This 
			page shows the trees from each of the train-test splits.
			<br />
		</s:else>	
		</p>
	
	<!-- Table of Trees -->
		
		<table width="100%" align="center" class="sortable" id="randomForestTreesTable">
		<s:if test="dataType=='CONTINUOUS'">
		<s:if test="randomForestTrees.size!=0">
		<tr>
		<th class="TableRowText01narrow">Split Number</th>
		<th class="TableRowText01narrow">R<sup>2</sup></th>
		<th class="TableRowText01narrow">MSE</sup></th>
		<th class="TableRowText01narrow">CCR</sup></th>
		<th class="TableRowText01narrow_unsortable" colspan="2">Descriptors Chosen</th>
		</tr>
		</s:if>
		<s:iterator value="randomForestTrees" status="treesStatus">
			<tr>
			<td class="TableRowText02narrow"><s:property value="#treesStatus.count" /></td>
			<td class="TableRowText02narrow"><s:property value="r2" /></td>
			<td class="TableRowText02narrow"><s:property value="mse" /></td>
			<td class="TableRowText02narrow"><s:property value="ccr" /></td>
			<td class="TableRowText02narrow" colspan="2"><s:property value="descriptorsUsed" /></td>
			</tr> 
		</s:iterator>
		</s:if>
		
		
		<s:elseif test="dataType=='CATEGORY'">
		<s:if test="randomForestTrees.size!=0">
			<tr>
			<th class="TableRowText01narrow">Split Number</th>
			<th class="TableRowText01narrow">R<sup>2</sup></th>
			<th class="TableRowText01narrow">MSE</sup></th>
			<th class="TableRowText01narrow">CCR</sup></th>
			<th class="TableRowText01narrow_unsortable" colspan="3">Descriptors Chosen</th>
			</tr>
		</s:if>
		
		<s:iterator value="randomForestTrees" status="treesStatus">
			<tr>
				<td class="TableRowText02narrow"><s:property value="#treesStatus.count" /></td>
				<td class="TableRowText02narrow"><s:property value="r2" /></td>
				<td class="TableRowText02narrow"><s:property value="mse" /></td>
				<td class="TableRowText02narrow"><s:property value="ccr" /></td>
				<td class="TableRowText02narrow" colspan="3"><s:property value="descriptorsUsed" /></td>
			</tr> 
		</s:iterator>
	
		</s:elseif>
		</table>
		
	<!-- End Table of Trees -->
	

		<s:if test="mostFrequentDescriptors!=''">
			<br />
			<p class="StandardTextDarkGray"><b><u>Descriptor Frequencies</u></b></p>
			<!--<p class="StandardTextDarkGray"><s:property value="mostFrequentDescriptors" /></p>-->
		</s:if>
		
<!-- End Trees Page -->