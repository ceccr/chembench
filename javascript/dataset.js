function submitForm(btn, value){
	//validate form; on error, alert and return false 
	var msg = "";
	//get selected dataset type tab
	var datasetType = document.getElementById("datasetType").value;
	if(datasetType === "MODELING"){
		var actFileName = document.getElementById("actFileModeling").value;
		var sdfFileName = document.getElementById("sdfFileModeling").value;	
		if(actFileName === ""){
			msg += "Please choose an activity file to upload.\n"
		}
		if(sdfFileName === ""){
			msg += "Please choose an SDF file to upload.\n"
		}
	}
	else if(datasetType === "PREDICTION"){
		var sdfFileName = document.getElementById("sdfFilePrediction").value;
		if(sdfFileName === ""){
			msg += "Please choose an SDF file to upload.\n"
		}
	}
	else if(datasetType === "MODELINGWITHDESCRIPTORS"){
		var actFileName = document.getElementById("actFileModDesc").value;
		var xFileName = document.getElementById("xFileModDesc").value;		
		if(actFileName === ""){
			msg += "Please choose an activity file to upload.\n"
		}
		if(xFileName === ""){
			msg += "Please choose a descriptors file to upload.\n"
		}
	}
	else if(datasetType === "PREDICTIONWITHDESCRIPTORS"){
		var xFileName = document.getElementById("xFilePredDesc").value;		
		if(xFileName === ""){
			msg += "Please choose a descriptors file to upload.\n"
		}
	}	
	
	//get selected external split type tab
	var splitType = document.getElementById("splitType").value;
	if(splitType === "RANDOM"){
		var numExternalCompounds = document.getElementById("numExternalCompounds").value;
		alert("numext: " + numExternalCompounds + "\nparseint: " + parseInt(numExternalCompounds));
		if(parseInt(numExternalCompounds) < 0 || isNan(parseInt(numExternalCompounds))){
			msg += "Invalid amount of external compounds.\n";
		}
	}
	else if(splitType === "USERDEFINED"){
		//if the user types nothing in the external compund box, that's OK. 
		//They will get a size-0 external set.
		//Any errors in the entered compound names will be caught in a separate check, done in Java.
	}
	else if(splitType === "NFOLD"){
		var numExternalFolds = document.getElementById("numExternalFolds").value;
		if(parseInt(numExternalFolds) < 0 || isNan(parseInt(numExternalFolds))){
			msg += "Invalid number of external folds.\n";
		}
		if(parseInt(numExternalFolds) > 30){
			msg += "You may not have more than 30 external folds.\n";
		}
	}

	if(msg === ""){
		showLoading("UPLOADING FILES. PLEASE WAIT...");
		btn.form.submit();
		return true;
	}
	else{
		alert(msg);
		return false;
	}
}
