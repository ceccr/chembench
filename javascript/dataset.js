function submitForm(btn, value){
	//validate form; on error, alert and return false 
	var msg = "";
	//get selected dataset type tab
	var datasetType = document.getElementById("datasetType").value;
	if(datasetType === "MODELING"){
		var actFileName = document.getElementById("actFileModeling").value;
		var sdfileName = document.getElementById("sdfFileModeling").value;	
		alert("act: " + actFileName + "\nsdf: " + sdfileName);
	}
	else if(datasetType === "PREDICTION"){
		var sdfileName = document.getElementById("sdfFilePrediction").value;
	}
	else if(datasetType === "MODELINGWITHDESCRIPTORS"){
		var actFileName = document.getElementById("actFileModDesc").value;
		var xFileName = document.getElementById("xFileModDesc").value;		
	}
	else if(datasetType === "PREDICTIONWITHDESCRIPTORS"){
		var xFileName = document.getElementById("xFilePredDesc").value;		
	}	
	
	//get selected external split type tab
	var splitType = document.getElementById("splitType").value
	if(splitType === "RANDOM"){
		
	}
	else if(splitType === "USERDEFINED"){
		//if the user types nothing in the external compund box, that's OK. 
		//They will get a size-0 external set.
	}
	else if(splitType === "NFOLD"){
		
	}

	if(msg === ""){
		showLoading("UPLOADING FILES. PLEASE WAIT...");
		return true;
	}
	else{
		alert(msg);
		return false;
	}
}
