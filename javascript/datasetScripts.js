function selectedDatasetID()
{ 
	var dpd = document.getElementById("file");
	var i = dpd.selectedIndex;

	var datasetIDsWithBlanks = [];
	var datasetIDs = [];
	for (var i=0; i<document.forms[0].knnType.length; i++){
		if (document.forms[0].knnType[i].checked && document.forms[0].knnType[i].value =="CONTINUOUS") 
		{	
			datasetIDsWithBlanks =[<logic:iterate id="df" name="continuousDatasets" type="edu.unc.ceccr.persistence.DataSet">"<bean:write name='df' property='fileId'/>",</logic:iterate>];
		}
		else if (document.forms[0].knnType[i].checked && document.forms[0].knnType[i].value =="CATEGORY")  {
			datasetIDsWithBlanks =[<logic:iterate id="dg" name="categoryDatasets" type="edu.unc.ceccr.persistence.DataSet">"<bean:write name='dg' property='fileId'/>",</logic:iterate>];
		}
	}
	var datasetIDs=[];
	for (i in datasetIDsWithBlanks){
		if (datasetIDsWithBlanks[i]!=null){
			datasetIDs[i] = datasetIDsWithBlanks[i];
		}
	}	
	
    return datasetIDs[i];
}

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
		document.getElementById("datasetname").value =	val.substring(val.lastIndexOf("\\")+1, val.lastIndexOf("."));
	else if(val.indexOf("/")!=-1)
			document.getElementById("datasetname").value =	val.substring(val.lastIndexOf("/")+1, val.lastIndexOf("."));
	else document.getElementById("datasetname").value = val.substring(0, val.lastIndexOf("."));
}

function validateLoad()
{
 var sdfValue=document.getElementById("loadSdf").value;
 var actValue=document.getElementById("loadAct").value;
 if(sdfValue==""&&actValue=="")
 {
    window.alert("Please upload ACT file and SDF file.");return false;
    }else{
        if(sdfValue==""){ window.alert("Please upload SDF file.");return false;}
        else{
            if(actValue==""){window.alert("Please upload ACT file");return false;}
            else{
            	
            	var rejectName = false;
            	var errorstring;
            	var datasetname = document.getElementById("datasetname").value;
            	for(i=0; i < datasetname.length; i++){
            		if(datasetname[i] == ' '){
            			rejectName = true;
            			errorstring="The job name must not contain a space.";
            		}
            	}
            	if(datasetname.length == 0){
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

function showUpload(){

	if(document.getElementById("modeling_select").checked){
			disableModelling(false);
			disablePrediction(true);
	}
	else{
			disableModelling(true);
			disablePrediction(false);
	}
}

function disableModelling(val){
	document.getElementById("con").disabled = val;
	document.getElementById("cat").disabled = val;
	document.getElementById("loadAct").disabled=val;
	document.getElementById("loadSdfModeling").disabled=val;
}

function disablePrediction(val){
	document.getElementById("loadSdfPrediction").disabled=val;
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

function checkSpaces(btn, value){
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
	
	if(submitFilesForm(btn,document.getElementById('textarea'))) showLoading("UPLOADING FILES. PLEASE WAIT...");
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