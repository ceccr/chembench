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
            else{return true;}
        }
    }
}