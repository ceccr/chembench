// Enable or disable parts of the form //
var categoryParamIDs = new Array('selectedCategoryDataset', 'knnCat13', 'knnCat14', 'knnCategoryOptimization1', 'knnCategoryOptimization2', 'knnCategoryOptimization3', 'knnCategoryOptimization4', 'svmTypeCategory0', 'svmTypeCategory1');
var continuousParamIDs = new Array('selectedContinuousDataset', 'knnCon13', 'knnCon14', 'knnCon15', 'knnCon16', 'knnCon17', 'knnCon18', 'svmTypeContinuous0', 'svmTypeContinuous1');

function calculateNumModels(){
	alert("hi");
	alert(document.getElementById("numSplitsInternalRandom").value);
	
}

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
}

function setCategoryOrContinuous(){
	//check which type of dataset we've got, then set continuous / category options accordingly
	alert("oh hai");
}

function getNewSeed(){
	document.getElementById("externalRandomSeed").value = Math.floor(Math.random()*16000);
}
	
function enableEdit(){
	document.getElementById("Edit").disabled = false;
}

// End Enable-Disable functions //

// Form validation functions //

function validateForm(){
	//check that all the relevant kNN parameters are entered as numbers
	
	//check that the job name is not the same as any of the user's existing
	//job names, model names, predictor names, or dataset names
	
	//check that there are no spaces in the job name.
	
	var rejectName = false;
	var errorstring;
	var jobname = document.getElementById("jobName").value;
	if(jobname.length == 0){
		rejectName = true;
		errorstring="Please enter a name for this job.";
	}
	if(rejectName){
		window.alert(errorstring);
		return false; 	
	}
	else{
		return true;
	}
	/*for(n in ls)
		if(value==ls[n]&&value!="") {
                  window.alert("The model name of '"+value+"' is already in use.");
                  document.getElementById("jobName").value="";
   			btn.disabled=true;
			return ; 	
	}
	btn.disabled=false;  
	*/
}

function submitForm(button)
{
	if(validateForm()){
		button.disabled=true;
		button.form.submit();
		document.getElementById('submitMessage').innerHTML="<i>Your workflow is being submitted, please wait.</i>";
		return true;
	}
	else{
		return false;
	}
}

// End Form validation functions //


function showDataset()
{
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

