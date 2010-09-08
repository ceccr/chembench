<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>
	
<!-- Trees Page -->	
	<br />
		<p class="StandardTextDarkGray"><b><u>Random Forests</u></b></p>

		<p class="StandardTextDarkGray">
		<s:if test="randomForestYRandomTrees.size==0">
			No random forest trees were generated.<br/>
		</s:if>
		<s:elseif test="selectedPredictor.userName=='_all'">
			<br/>Model information is not available for public predictors.<br/>
		</s:elseif>
		<s:else>
			In y-Randomization modeling, Chembench attempts to create a second predictor from a copy of your
			data where the compound activities have been shuffled. Ideally, no trees with a high q<sup>2</sup>
			and R<sup>2</sup> will be produced. If the y-Randomized trees are similar to the real trees built on
			your data (see Trees tab), the predictor should be considered invalid and the dataset or parameters must
			be revised. Y-randomized trees are only created for validation purposes and are not used in predictions.
			
		<!--
			<b>Y-randomization</b> is a statistical QSAR model validation technique where the performance of the forests 
			built with original data is compared to those of forests built for multiple artificial datasets with
			randomly shuffled activities. The forests built from randomized data are built using the same parameters
			used for the actual predictor. Ideally, there will be no trees from the randomized data with high 
			values of both q<sup>2</sup> (internal test set) and R<sup>2</sup> (external set). Your parameters
			need to be adjusted if many y-randomized trees are being produced with high R<sup>2</sup> values. -->
			
			This page shows the trees from y-randomized modeling for each train-test split.
			<br />
		</s:else>	
		</p>
	
	<!-- Table of Trees -->
		
		<table width="100%" align="center" class="sortable" id="randomForestTreesTable">
		<s:if test="dataType=='CONTINUOUS'">
		<s:if test="randomForestYRandomTrees.size!=0">
		<tr>
		<th class="TableRowText01narrow">Split Number</th>
		<th class="TableRowText01narrow">R<sup>2</sup></th>
		<th class="TableRowText01narrow">MSE</sup></th>
		<th class="TableRowText01narrow_unsortable" colspan="2">Descriptors Chosen</th>
		</tr>
		</s:if>
		<s:iterator value="randomForestYRandomTrees" status="treesStatus">
			<tr>
			<td class="TableRowText02narrow"><s:property value="treeFileName" /></td>
			<td class="TableRowText02narrow"><s:property value="r2" /></td>
			<td class="TableRowText02narrow"><s:property value="mse" /></td>
			<td class="TableRowText02narrow" colspan="2"><s:property value="descriptorsUsed" /></td>
			</tr> 
		</s:iterator>
		</s:if>
		
		
		<s:elseif test="dataType=='CATEGORY'">
		<s:if test="randomForestYRandomTrees.size!=0">
			<tr>
			<th class="TableRowText01narrow">Split Number</th>
			<th class="TableRowText01narrow">R<sup>2</sup></th>
			<th class="TableRowText01narrow">MSE</sup></th>
			<th class="TableRowText01narrow_unsortable" colspan="3">Descriptors Chosen</th>
			</tr>
		</s:if>
		
		<s:iterator value="randomForestYRandomTrees" status="treesStatus">
			<tr>
				<td class="TableRowText02narrow"><s:property value="treeFileName" /></td>
				<td class="TableRowText02narrow"><s:property value="r2" /></td>
				<td class="TableRowText02narrow"><s:property value="mse" /></td>
				<td class="TableRowText02narrow" colspan="3"><s:property value="descriptorsUsed" /></td>
			</tr> 
		</s:iterator>
	
		</s:elseif>
		</table>
		
	<!-- End Table of Trees -->
	
<!-- End Trees Page -->