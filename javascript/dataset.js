function knnType()
{
                  for (var i=0; i<document.forms[0].knnType.length; i++){
			if (document.forms[0].knnType[i].checked && document.forms[0].knnType[i].value =="CATEGORY") 
				return "CATEGORY"; 
			else if (document.forms[0].knnType[i].checked && document.forms[0].knnType[i].value =="CONTINUOUS")  
				return "CONTINUOUS";}
}

function displayChart()
{
	var datasetID=selectedDatasetID();
	var type=knnType();
	window.open("ActivityChartVisualization.do?selectedDatasetID="+datasetID+"&type="+type); 
	return false;
}

function setDatasetName(obj){
	var val = obj.value;
	if(val.indexOf("\\")!=-1)
		document.getElementById("datasetName").value =	val.substring(val.lastIndexOf("\\")+1, val.lastIndexOf("."));
	else if(val.indexOf("/")!=-1)
			document.getElementById("datasetName").value =	val.substring(val.lastIndexOf("/")+1, val.lastIndexOf("."));
	else document.getElementById("datasetName").value = val.substring(0, val.lastIndexOf("."));
}

function validateLoad()
{
	var sdfValue=document.getElementById("loadSdf").value;
	var actValue=document.getElementById("loadAct").value;
	if(sdfValue==""&&actValue==""){
	    window.alert("Please upload ACT file and SDF file.");
	    return false;
	}
	else{
        if(sdfValue==""){ 
        	window.alert("Please upload SDF file.");
        	return false;
        }
        else{
            if(actValue==""){
            	window.alert("Please upload ACT file");return false;
            }
            else{
            	
            	var rejectName = false;
            	var errorstring;
            	var datasetName = document.getElementById("datasetName").value;
            	if(datasetName.length == 0){
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
            }
        }
    }
}

function cutString(obj){
	var str = document.getElementById(obj).title;
	if(str.length>56)
		document.getElementById(obj).innerHTML = str.substring(0,56)+"...";
	else 
		document.getElementById(obj).innerHTML = str;
}

function extendColumn(obj){
	if(obj.title.length>56 && obj.innerHTML.length<=60){
		obj.innerHTML = obj.title;
	}
	else cutString(obj.id);
}

function submitForm(btn, value){
	//check that dataset name contains no spaces then submit form
	var rejectName = false;
	for(i=0; i < value.length; i++){
		if(value[i] == ' '){
			rejectName=true;
		}
	}
	if(rejectName){
		window.alert("The job name must not contain a space.");
    	return; 	
	}
	
	if(submitFilesForm(btn,document.getElementById('pleaseWaitText'))) showLoading("UPLOADING FILES. PLEASE WAIT...");
}

function submitFilesForm(button, textarea)
{
	if(!validateUpload(button.form))	
	{return false;}
	else{
		button.disabled=true;
		button.form.submit();
		textarea.innerHTML="Your data is being submitted, please wait";
		return true;
	}
}

function validateUpload(form) {
	var messageDiv = document.getElementById("messageDiv");
	messageDiv.innerHTML = "";					 			 
    var valid = true;
    
    for(i in form.elements) {
    	
		 var elem = form.elements[i];
		 
		 if(elem!=null) {
			 
			 var str = elem.value;
			 var msg;
			 
			 /*if(document.getElementById("modeling_select").checked){
				 if (elem.name=="sdFileModeling"&&str=="") {
				 	valid=false;
		            messageDiv.innerHTML+="<p>Modeling SD File is required if you have selected the \"Upload\" option.</p>";
		         } 		 
				 if (elem.name=="actFile"&&str=="") {
				 	valid=false;
		            messageDiv.innerHTML+="<p>Activity File is required if you have selected the \"Upload\" option.</p>";
		         }
			// }
			 //else{
				 if (elem.name=="sdFilePrediction"&&str=="") {
					 	valid=false;
			            messageDiv.innerHTML+="<p>Prediction SD File is required if you have selected the \"Upload\" option.</p>";
			         }
			// }*/
		
		}
		 
	 }
   
	 if(!valid)
		 {return false;}else{ return true;}
}

function deleteDataset(text_msg){
	var resp = confirm(text_msg);
	//return resp;
	if(resp){
		showLoading("DELETING. PLEASE WAIT...");
	}
	else return false;
}
function show_public_datasets()
{
  if(document.getElementById("public_datasets").style.display=='inline')
{
	  document.getElementById("public_datasets").style.display='none';
	  document.getElementById("panel_link").innerHTML = "Show public datasets";
}
else{
	document.getElementById("panel_link").innerHTML = "Hide public datasets";
	document.getElementById("public_datasets").style.display='inline';
	}

}