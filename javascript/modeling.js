// Enable or disable parts of the form //
var categoryParamIDs = new Array('selectedCategoryDataset', 'knnCat13', 'knnCat14', 'knnCategoryOptimization1', 'knnCategoryOptimization2', 'knnCategoryOptimization3', 'knnCategoryOptimization4', 'svmTypeCategory0', 'svmTypeCategory1');
var continuousParamIDs = new Array('selectedContinuousDataset', 'knnCon13', 'knnCon14', 'knnCon15', 'knnCon16', 'knnCon17', 'knnCon18', 'svmTypeContinuous3', 'svmTypeContinuous4');

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

