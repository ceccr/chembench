// Enable or disable parts of the form //
var categoryParamIDs = new Array('selectedCategoryDataset', 'knnCat13', 'knnCat14', 'knnCategoryOptimization1', 'knnCategoryOptimization2', 'knnCategoryOptimization3', 'knnCategoryOptimization4', 'svmTypeCategory0', 'svmTypeCategory1');
var continuousParamIDs = new Array('selectedContinuousDataset', 'knnCon13', 'knnCon14', 'knnCon15', 'knnCon16', 'knnCon17', 'knnCon18', 'svmTypeContinuous3', 'svmTypeContinuous4');

var datasetId = -1;
var selectedDatasetNumCompounds = -1;
var selectedDatasetAvailableDescriptors = "";
var selectedDatasetHasBeenScaled = "";

function setToContinuous(){
	var i;
	for(i = 0; i < categoryParamIDs.length; i++){
		if(document.getElementById(categoryParamIDs[i]) != null){
			document.getElementById(categoryParamIDs[i]).disabled = true;
		}
	}
	for(var i = 0; i < continuousParamIDs.length; i++){
		if(document.getElementById(continuousParamIDs[i]) != null){
			document.getElementById(continuousParamIDs[i]).disabled = false;
		}
	}
	document.getElementById("actFileDataType").value = "CONTINUOUS";
	changeSvmType();
}

function setToCategory(){
	var i;
	for(i = 0; i < categoryParamIDs.length; i++){
		if(document.getElementById(categoryParamIDs[i]) != null){
			document.getElementById(categoryParamIDs[i]).disabled = false;
		}
	}
	for(var i = 0; i < continuousParamIDs.length; i++){
		if(document.getElementById(continuousParamIDs[i]) != null){
			document.getElementById(continuousParamIDs[i]).disabled = true;
		}
	}
	document.getElementById("actFileDataType").value = "CATEGORY";
	changeSvmType();
}

function changeSvmType(){
	//enable or disable parameter ranges for SVM modeling
	
	//first, enable them all
	document.getElementById("svmPEpsilonFrom").disabled = false;
	document.getElementById("svmPEpsilonTo").disabled = false;
	document.getElementById("svmPEpsilonStep").disabled = false;

	document.getElementById("svmNuFrom").disabled = false;
	document.getElementById("svmNuTo").disabled = false;
	document.getElementById("svmNuStep").disabled = false;

	document.getElementById("svmCostFrom").disabled = false;
	document.getElementById("svmCostTo").disabled = false;
	document.getElementById("svmCostStep").disabled = false;

	document.getElementById("svmDegreeFrom").disabled = false;
	document.getElementById("svmDegreeTo").disabled = false;
	document.getElementById("svmDegreeStep").disabled = false;

	document.getElementById("svmGammaFrom").disabled = false;
	document.getElementById("svmGammaTo").disabled = false;
	document.getElementById("svmGammaStep").disabled = false;
	
	//disable the ones not relevant to this SVM type
	var svmType;
	if(document.getElementById("categoryDataset").checked==true){
		if(document.getElementById("svmTypeCategory0").checked==true){
			svmType = document.getElementById("svmTypeCategory0").value;
		}
		else{
			svmType = document.getElementById("svmTypeCategory1").value;
		}
	}
	else{
		if(document.getElementById("svmTypeContinuous3").checked==true){
			svmType = document.getElementById("svmTypeContinuous3").value;
		}
		else{
			svmType = document.getElementById("svmTypeContinuous4").value;
		}
	}
	
	if(svmType == '0'){
		document.getElementById("svmPEpsilonFrom").disabled = true;
		document.getElementById("svmPEpsilonTo").disabled = true;
		document.getElementById("svmPEpsilonStep").disabled = true;

		document.getElementById("svmNuFrom").disabled = true;
		document.getElementById("svmNuTo").disabled = true;
		document.getElementById("svmNuStep").disabled = true;
	}
	else if(svmType == '1'){
		document.getElementById("svmPEpsilonFrom").disabled = true;
		document.getElementById("svmPEpsilonTo").disabled = true;
		document.getElementById("svmPEpsilonStep").disabled = true;

		document.getElementById("svmCostFrom").disabled = true;
		document.getElementById("svmCostTo").disabled = true;
		document.getElementById("svmCostStep").disabled = true;
	}
	else if(svmType == '3'){
		document.getElementById("svmNuFrom").disabled = true;
		document.getElementById("svmNuTo").disabled = true;
		document.getElementById("svmNuStep").disabled = true;
	}
	else if(svmType == '4'){
		document.getElementById("svmPEpsilonFrom").disabled = true;
		document.getElementById("svmPEpsilonTo").disabled = true;
		document.getElementById("svmPEpsilonStep").disabled = true;
	}
	
	//disable the ones not relevant to this kernel type
	var kernelType;
	if(document.getElementById("svmKernel0").checked==true){
		kernelType = '0';
	}
	else if(document.getElementById("svmKernel1").checked==true){
		kernelType = '1';
	}
	else if(document.getElementById("svmKernel2").checked==true){
		kernelType = '2';
	}
	else if(document.getElementById("svmKernel3").checked==true){
		kernelType = '3';
	}
	
	if(kernelType == '0'){
		document.getElementById("svmDegreeFrom").disabled = true;
		document.getElementById("svmDegreeTo").disabled = true;
		document.getElementById("svmDegreeStep").disabled = true;
		
		document.getElementById("svmGammaFrom").disabled = true;
		document.getElementById("svmGammaTo").disabled = true;
		document.getElementById("svmGammaStep").disabled = true;
	}
	else if(kernelType == '1'){
		//polynomial kernel uses both
	}
	else if(kernelType == '2'){
		document.getElementById("svmDegreeFrom").disabled = true;
		document.getElementById("svmDegreeTo").disabled = true;
		document.getElementById("svmDegreeStep").disabled = true;
	}
	else if(kernelType == '3'){
		document.getElementById("svmDegreeFrom").disabled = true;
		document.getElementById("svmDegreeTo").disabled = true;
		document.getElementById("svmDegreeStep").disabled = true;
	}
}

// End Enable-Disable functions //


function submitForm(button){
	// Form validation //
	var msg = "";
	
	//checks go here (if any)
	
	if(msg === ""){
		button.disabled=true;
		button.form.submit();
		document.getElementById('submitMessage').innerHTML="<i>Your workflow is being submitted, please wait.</i>";
		return true;
	}
	else{
		alert(msg);
		return false;
	}
}

function showDataset(){
	//Open dataset in new window
	//make sure that they've picked an existing continuous or category dataset
	if(document.getElementById("continuousDataset").checked){
		window.open("viewDataset?id="+document.getElementById("selectedContinuousDataset").value);
		return true;
	}
	else if(document.getElementById("categoryDataset").checked){
		window.open("viewDataset?id="+document.getElementById("selectedCategoryDataset").value);
		return true;
	}
	else{
		window.alert("Please specify a dataset.");
		return false;
	}
}
function setDescriptorScaling(){
	//turns scaling options on or off
	//If a user has uploaded scaled descriptors we don't want to scale them any further
	if(document.getElementById("descriptorGenerationType" + "UPLOADED").checked &&
			selectedDatasetHasBeenScaled == "true"){
		document.getElementById("scalingType" + "RANGESCALING").disabled = true;
		document.getElementById("scalingType" + "AUTOSCALING").disabled = true;
		document.getElementById("scalingType" + "NOSCALING").disabled = true;
		document.getElementById("scalingType" + "NOSCALING").checked = "checked";
	}
	else{
		document.getElementById("scalingType" + "RANGESCALING").disabled = false;
		document.getElementById("scalingType" + "AUTOSCALING").disabled = false;
		document.getElementById("scalingType" + "NOSCALING").disabled = false;
		document.getElementById("scalingType" + "RANGESCALING").checked = "checked";
	}
}

function calculateRuntimeEstimate(){
	//estimates runtime based on input parameters and displays it.
	//assumes that getDataset has assigned the value of selectedDatasetNumCompounds already.
	
	var timeEstimateDays = 0;
	var timeEstimateHours = 0;
	var timeEstimateMins = 0;
	
	var dataSplitMethod = document.getElementById("trainTestSplitType").value;
	var modelMethod = document.getElementById("modelingType").value;
	
	var numSplits;
	if(dataSplitMethod=="RANDOM"){
		numSplits = document.getElementById("numSplitsInternalRandom").value;
	}
	else{
		//sphere exclusion
		numSplits = document.getElementById("numSplitsInternalSphere").value;
	}
	
	//Time estimates were generated by taking the results from around 200 jobs
	//then removing outliers and plotting a trendline in Excel. Predictions 
	//look pretty accurate in there at least.
	if(modelMethod=="RANDOMFOREST"){
		timeEstimateMins = (selectedDatasetNumCompounds*numSplits*0.003)-5.718;
	}
	else if(modelMethod=="KNN-GA"){
		//var maxNumGenerations = document.getElementById("gaMaxNumGenerations").value;
		timeEstimateMins = (selectedDatasetNumCompounds*numSplits*0.05);
	}
	else if(modelMethod=="KNN-SA"){
		//depends on numRuns and needs a factor for convergence parameters (temperature etc).
		var numRuns = document.getElementById("saNumRuns").value;
		var numBest = document.getElementById("saNumBestModels").value;
		var numDifferentDescriptors = 1;
		var minDesc = document.getElementsByName("knnMinNumDescriptors")[1].value;
		var maxDesc = document.getElementsByName("knnMaxNumDescriptors")[1].value;
		var descSteps = document.getElementById("knnDescriptorStepSize").value;

		if(descSteps != 0){
			numDifferentDescriptors += Math.floor((maxDesc - minDesc)/descSteps);
		}

		timeEstimateMins = numSplits *(numRuns*numBest*numDifferentDescriptors)*selectedDatasetNumCompounds*0.018;
	}
	else if(modelMethod=="SVM"){

		var numDifferentDegrees = Math.floor((document.getElementById("svmDegreeTo").value - 
				document.getElementById("svmDegreeFrom").value) / document.getElementById("svmDegreeStep").value + 0.001);
		var numDifferentGammas = Math.floor((document.getElementById("svmGammaTo").value - 
				document.getElementById("svmGammaFrom").value) / document.getElementById("svmGammaStep").value + 0.001);
		var numDifferentCosts = Math.floor((document.getElementById("svmCostTo").value - 
				document.getElementById("svmCostFrom").value) / document.getElementById("svmCostStep").value + 0.001);
		var numDifferentNus = Math.floor((document.getElementById("svmNuTo").value - 
				document.getElementById("svmNuFrom").value) / document.getElementById("svmNuStep").value + 0.001);
		var numDifferentPEpsilons = Math.floor((document.getElementById("svmPEpsilonTo").value - 
				document.getElementById("svmPEpsilonFrom").value) / document.getElementById("svmPEpsilonStep").value + 0.001);

		var svmType;
		if(document.getElementById("categoryDataset").checked==true){
			if(document.getElementById("svmTypeCategory0").checked==true){
				svmType = document.getElementById("svmTypeCategory0").value;
			}
			else{
				svmType = document.getElementById("svmTypeCategory1").value;
			}
		}
		else{
			if(document.getElementById("svmTypeContinuous3").checked==true){
				svmType = document.getElementById("svmTypeContinuous3").value;
			}
			else{
				svmType = document.getElementById("svmTypeContinuous4").value;
			}
		}
		
		if(svmType == '0'){
			numDifferentPEpsilons = 1;
			numDifferentNus = 1;
		}
		else if(svmType == '1'){
			numDifferentPEpsilons = 1;
			numDifferentCosts = 1;
		}
		else if(svmType == '3'){
			numDifferentNus = 1;
		}
		else if(svmType == '4'){
			numDifferentPEpsilons = 1;
		}

		var kernelType;
		if(document.getElementById("svmKernel0").checked==true){
			numDifferentGammas = 1;
			numDifferentDegrees = 1;
		}
		else if(document.getElementById("svmKernel1").checked==true){
			//both gamma and degree are used
		}
		else if(document.getElementById("svmKernel2").checked==true){
			numDifferentDegrees = 1;
		}
		else if(document.getElementById("svmKernel3").checked==true){
			numDifferentDegrees = 1;
		}
		
		var numModelsPerSplit = numDifferentPEpsilons * numDifferentNus * numDifferentCosts * numDifferentGammas * numDifferentDegrees;

		timeEstimateMins = selectedDatasetNumCompounds * numSplits * numModelsPerSplit * 0.00022;
	}
	
	var errorMargin = 1.8;
	timeEstimateMins = timeEstimateMins * errorMargin; 
	
	timeEstimateMins = Math.ceil(timeEstimateMins);
	if(timeEstimateMins <= 0){
		timeEstimateMins = 2;
	}
	
	
	var timeEstimateString = timeEstimateMins + " minutes";
	if(timeEstimateMins == 1){
		timeEstimateString = timeEstimateMins + " minute";
	}
	
	if(timeEstimateMins > 120){
		timeEstimateHours = Math.ceil(timeEstimateMins / 60);
		timeEstimateString = timeEstimateHours + " hours";
		if(timeEstimateHours > 48){
			timeEstimateDays =  Math.ceil(timeEstimateHours / 24);
			timeEstimateString = timeEstimateDays + " days";
		}
	}
	
	document.getElementById("timeEstimateDiv").innerHTML = "<br />This modeling job will take about <b>" + timeEstimateString + "</b> to finish.";
}	
