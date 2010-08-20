<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>
	
<!-- Parameters -->	
	<br />
	<p class="StandardTextDarkGray"><b><u>Modeling Parameters</u></b></p>
	
	<p class="StandardTextDarkGray">
	These are the parameters you used to generate this predictor.</p>
	
	<!-- Parameters common to all predictors -->
	
	<div class="StandardTextDarkGrayParagraph">
	<b>Dataset:</b> <s:property value="selectedPredictor.datasetDisplay" /><br />
	<b>Dataset Activity Type:</b> <s:property value="selectedPredictor.activityType" /><br />
	<br />
	<b>Descriptor Type:</b> <s:property value="selectedPredictor.descriptorGeneration" /><br />
	<b>Correlation Cutoff:</b> <s:property value="selectedPredictor.correlationCutoff" /><br />
	<b>Descriptor Scaling Type:</b> <s:property value="selectedPredictor.scalingType" /><br />
	<br />
	
	<b>Data Split Type:</b> <s:property value="selectedPredictor.trainTestSplitType" /><br />
	<b>Number of Splits:</b> <s:property value="selectedPredictor.numSplits" /><br />
	</div>
	<s:if test="selectedPredictor.trainTestSplitType=='RANDOM'">
	<div class="StandardTextDarkGrayParagraph">
		<b>Minimum Test Set Size:</b> <s:property value="selectedPredictor.randomSplitMinTestSize" />%<br />
		<b>Maximum Test Set Size:</b> <s:property value="selectedPredictor.randomSplitMaxTestSize" />%<br />
	</div>
	</s:if>
	<s:elseif test="selectedPredictor.trainTestSplitType=='SPHEREEXCLUSION'">
	<div class="StandardTextDarkGrayParagraph">
		<b>Minimum Test Set Size:</b> <s:property value="selectedPredictor.sphereSplitMinTestSize" />%<br />
		<b>Force Minimum Activity Compound into all Training Sets:</b> <s:property value="selectedPredictor.splitIncludesMin" /><br />
		<b>Force Maximum Activity Compound into all Training Sets:</b> <s:property value="selectedPredictor.splitIncludesMax" /><br />
		<b>Selection of Next Training Set Point is Based on:</b> <s:property value="selectedPredictor.selectionNextTrainPt" /><br />		
	</div>
	</s:elseif>
	<br />
	
	<!-- end parameters common to all predictors -->
	<div class="StandardTextDarkGrayParagraph">
	<b>Modeling Method:</b> <s:property value="selectedPredictor.modelMethod" /><br />
	</div>
	<!-- knn-sa specific parameters -->
	<s:if test="selectedPredictor.modelMethod=='KNN-SA'">
	<div class="StandardTextDarkGrayParagraph">
		<b>Descriptors Per Model:</b> From <s:property value="knnPlusParameters.knnMinNumDescriptors" /> to <s:property value="knnPlusParameters.knnMaxNumDescriptors" /> step <s:property value="knnPlusParameters.knnDescriptorStepSize" /><br />
		<b>Min. Nearest Neighbors:</b> <s:property value="knnPlusParameters.knnMinNearestNeighbors" /><br />
		<b>Max. Nearest Neighbors:</b> <s:property value="knnPlusParameters.knnMaxNearestNeighbors" /><br />
		<b>Number of Runs:</b> <s:property value="knnPlusParameters.saNumRuns" /><br />
		<b>Mutation Probability Per Descriptor:</b> <s:property value="knnPlusParameters.saMutationProbabilityPerDescriptor" /><br />
		<b>Number of Best Models To Store:</b> <s:property value="knnPlusParameters.saNumBestModels" /><br />
		<b>Temperature Decrease Coefficient:</b> <s:property value="knnPlusParameters.saTempDecreaseCoefficient" /><br />
		<b>Log Initial Temperature:</b> <s:property value="knnPlusParameters.saLogInitialTemp" /><br />
		<b>Final Temperature:</b> <s:property value="knnPlusParameters.saFinalTemp" /><br />
		<b>Temperature Convergence Range:</b> <s:property value="knnPlusParameters.saTempConvergence" /><br />
		<b>Applicability Domain Cutoff:</b> <s:property value="knnPlusParameters.knnApplicabilityDomain" /><br />
		<b>Minimum for Training Set:</b> <s:property value="knnPlusParameters.knnMinTraining" /><br />
		<b>Minimum for Test Set:</b> <s:property value="knnPlusParameters.knnMinTest" /><br />
		<b>Use Error Based Fit Index:</b> <s:property value="knnPlusParameters.knnSaErrorBasedFit" /><br />
	</div>
	</s:if>
	<!-- end knn-sa specific parameters -->
	
	<!-- knn-ga specific parameters -->
	<s:if test="selectedPredictor.modelMethod=='KNN-GA'">
	<div class="StandardTextDarkGrayParagraph">
		<b>Descriptors Per Model:</b> From <s:property value="knnPlusParameters.knnMinNumDescriptors" /> to <s:property value="knnPlusParameters.knnMaxNumDescriptors" /><br />
		<b>Min. Nearest Neighbors:</b> <s:property value="knnPlusParameters.knnMinNearestNeighbors" /><br />
		<b>Max. Nearest Neighbors:</b> <s:property value="knnPlusParameters.knnMaxNearestNeighbors" /><br />
		<b>Population Size:</b> <s:property value="knnPlusParameters.gaPopulationSize" /><br />
		<b>Maximum Number of Generations:</b> <s:property value="knnPlusParameters.gaMaxNumGenerations" /><br />
		<b>Stop if Stable For This Many Generations:</b> <s:property value="knnPlusParameters.gaNumStableGenerations" /><br />
		<b>Group Size for Tournament Selection:</b> <s:property value="knnPlusParameters.gaTournamentGroupSize" /><br />
		<b>Minimum Fitness Difference To Proceed:</b> <s:property value="knnPlusParameters.gaMinFitnessDifference" /><br />
		<b>Applicability Domain Cutoff:</b> <s:property value="knnPlusParameters.knnApplicabilityDomain" /><br />
		<b>Minimum for Training Set:</b> <s:property value="knnPlusParameters.knnMinTraining" /><br />
		<b>Minimum for Test Set:</b> <s:property value="knnPlusParameters.knnMinTest" /><br />
		<b>Use Error Based Fit Index:</b> <s:property value="knnPlusParameters.knnGaErrorBasedFit" /><br />
	</div>
	</s:if>
	<!-- end knn-ga specific parameters -->
	
	<!-- random forest specific parameters -->
	<s:if test="selectedPredictor.modelMethod=='RANDOMFOREST'">
	<div class="StandardTextDarkGrayParagraph">
		<b>Number of Trees Per Split:</b> <s:property value="randomForestParameters.numTrees" /><br />
		<b>Descriptors Per Tree:</b> <s:property value="randomForestParameters.descriptorsPerTree" /><br />
		<b>Minimum Terminal Node Size:</b> <s:property value="randomForestParameters.minTerminalNodeSize" /><br />
		<b>Maximum Number of Terminal Nodes (0 = no limit):</b> <s:property value="randomForestParameters.maxNumTerminalNodes" /><br />
	</div>
	</s:if>
	<!-- end random forest specific parameters -->
	
	<!-- 
	<p class="StandardTextDarkGray">To create a new modeling job based on these parameters, use the New Predictor 
	button. You will be taken to the Modeling page, where you can modify the parameters
	before resubmitting the job.</p> 
	<input type="submit" value="New Predictor..." />
	-->