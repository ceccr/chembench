function changeDatasetType(){
	//disable / enable external split parameters based on 
	//the currently selected dataset type.
	//Triggers whenever dataset type or ext split type is changed.

    var datasetType = document.getElementById("datasetType").value;
    
    var externalParameterIds = new Array("useActivityBinning", "numExternalCompounds", "externalCompoundList", "numExternalFolds", "useActivityBinningNFold", "externalCompoundsCountOrPercent");
    if(datasetType == "MODELING" || datasetType == "MODELINGWITHDESCRIPTORS"){
    	//if it's a modeling dataset, enable the external split parameters 
    	for (var i=0; i < externalParameterIds.length; i++){
			if(document.getElementById(externalParameterIds[i]) != null){
				document.getElementById(externalParameterIds[i]).disabled = false;
			}
		}
    }
    else{
    	//if not, disable them
    	for (var i=0; i < externalParameterIds.length; i++){
			if(document.getElementById(externalParameterIds[i]) != null){
				document.getElementById(externalParameterIds[i]).disabled = true;
			}
		}
    }
}

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
		if(document.getElementById("dataTypeModelingCONTINUOUS").checked == false && document.getElementById("dataTypeModelingCATEGORY").checked == false){
			msg += "Please choose ACT data type (CATEGORY OR CONTNUOUS).\n"
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
		if(document.getElementById("newDescriptorName").checked && document.getElementById("descriptorNewName").value.replace(/^\s*/, "").replace(/\s*$/, "")===""){
			msg += "Please enter a descriptors type name. Or choose from the previously used list.\n"
		}
		if(document.getElementById("newDescriptorName").checked && document.getElementById("descriptorNewName").value.replace(/^\s*/, "").replace(/\s*$/, "")!=="" && document.getElementById("descriptorUsedName")!=null){
			var selectobject=document.getElementById("descriptorUsedName");
			for (var i=0; i<selectobject.length; i++){
				if(selectobject.options[i].value.replace(/^\s*/, "").replace(/\s*$/, "")===document.getElementById("descriptorNewName").value.replace(/^\s*/, "").replace(/\s*$/, "")){
					msg += "The descriptor type name you've enterd is already used. Please select it from your Used type names list.\n";
					break;
				}  
			}
		}
		if(document.getElementById("dataTypeModDescCATEGORY").checked == false && document.getElementById("dataTypeModDescCONTINUOUS").checked == false){
			msg += "Please choose ACT data type (CATEGORY OR CONTNUOUS).\n"
		}
		
	}
	else if(datasetType === "PREDICTIONWITHDESCRIPTORS"){
		var xFileName = document.getElementById("xFilePredDesc").value;		
		if(xFileName === ""){
			msg += "Please choose a descriptors file to upload.\n"
		}
		if(document.getElementById("newDescriptorNameD").checked && document.getElementById("descriptorNewNameD").value.replace(/^\s*/, "").replace(/\s*$/, "")===""){
			msg += "Please choose enter a descriptors type. Or choose from the previously used list.\n"
		}
		if(document.getElementById("newDescriptorNameD").checked && document.getElementById("descriptorNewNameD").value.replace(/^\s*/, "").replace(/\s*$/, "")!==""){
			var selectobject=document.getElementById("descriptorUsedNameD");
			for (var i=0; i<selectobject.length; i++){
				if(selectobject.options[i].value.replace(/^\s*/, "").replace(/\s*$/, "")===document.getElementById("descriptorNewNameD").value.replace(/^\s*/, "").replace(/\s*$/, "")){
					msg += "The descriptor type name you've enterd is already used. Please select it from your Used type names list.\n";
					break;
				}  
			}

		}
	}	
	
	//get selected external split type tab
	var splitType = document.getElementById("splitType").value;
	if(splitType === "RANDOM"){
		var numExternalCompounds = document.getElementById("numExternalCompounds").value;
		if(parseInt(numExternalCompounds) < 0 || isNaN(parseInt(numExternalCompounds))){
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
		if(parseInt(numExternalFolds) < 2 || isNaN(parseInt(numExternalFolds))){
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
