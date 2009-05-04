<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<jsp:useBean id="user" class="edu.unc.ceccr.persistence.User" scope="session"/>

<html:html>
<head>
<title>C-CHEMBENCH | My Files</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>

<bean:define id="datasetNames" type="java.util.List" name="datasetNames" scope="session"></bean:define>
<bean:define id="predictorNames" type="java.util.List" name="predictorNames" scope="session"></bean:define>
<bean:define id="predictionNames" type="java.util.List" name="predictionNames" scope="session"></bean:define>
<bean:define id="taskNames" type="java.util.List" name="taskNames" scope="session"></bean:define>

<script src="javascript/script.js"></script>
<script src="javascript/miscellaneous.js"></script>

<script src="javascript/yahoo/yahoo.js"></script>
<script src="javascript/dom/dom.js"></script>
<script src="javascript/event/event.js"></script>
<script src="javascript/container/container.js"></script>
<script src="javascript/datasetScripts.js"></script>

<script src="jsp/modelbuilders.js.jsp"></script>
<script src="jsp/mt.js.jsp"></script>

<script language="javascript">
var usedDatasetNames = new Array(<logic:iterate id="dn" name="datasetNames" type="String">"<bean:write name='dn'/>",</logic:iterate>"");
var usedPredictorNames = new Array(<logic:iterate id="pn" name="predictorNames" type="String">"<bean:write name='pn'/>",</logic:iterate>"");
var usedPredictionNames = new Array(<logic:iterate id="pn" name="predictionNames" type="String">"<bean:write name='pn'/>",</logic:iterate>"");
var usedTaskNames = new Array(<logic:iterate id="tn" name="taskNames" type="String">"<bean:write name='tn'/>",</logic:iterate>"");
</script>

<script type="text/javascript">

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


</script>

</head>
<body>
<div id="bodyDIV"></div>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td colspan="5" valign="top"  background="theme/img/backgrmodelbuilders.jpg"><span id="maincontent">
      <table width="465" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td><p class="StandardTextDarkGrayParagraph"><b><br />
            C-Chembench Dataset Management</b></p>
              <p align="justify" class="StandardTextDarkGrayParagraph">This part of C-Chembench allows you to manage and explore data sets.  Aside from the basic functions of uploading and deleting data sets,  this  part of our web site lets you better understand the datasets that you are working with.<br />
                  <br />
                In order to properly evaluate the quality of a model, you will often want to understand the dataset from which it was built.  How similar or diverse are the compounds that you were using?  What were the key differentiators that exist? <br />
                <br />
                Capabilities offered in this section include the ability to look at distant matrices, various clustering techniques and principal components analysis of the datasets.  Options provide visualization and numeric results.<br />
                <br />
            </p></td>
        </tr>
      </table>
    </span> </td>
  </tr>
  <tr>
    <td><table width="100%" align="center" border="0">
      <tr>
        <td valign="top" border="1"><br/>
              <a href="#dataset" >Manage Model Development Datasets</a><br/>
              <div id="dataset">
                <logic:notEqual name="userDatasets" value="">
                  <table>
                    <tr>
                      <td class="TableRowText01">Dataset</td>
                      <td class="TableRowText01"># Compounds</td>
                      <td class="TableRowText01">KNN Type</td>
                      <td class="TableRowText01">Date</td>
                      <td class="TableRowText01">Description</td>
                      <td colspan="2" class="TableRowText01">Download dataset</td>
                     </tr>

					 <%int counter=0; %>
                    <logic:iterate id="pd" name="publicDatasets">
                      <%counter++; %>
                      <tr>
                        <td class="TableRowText02"><a  href="viewDataset.do?fileName=<bean:write name="pd" property="fileName" />"> <b>
                          <bean:write name="pd" property="fileName" />
                        </b> </td>
                        <td class="TableRowText02"><bean:write name="pd" property="numCompound" /></td>
                        <td class="TableRowText02"><bean:write name="pd" property="modelType" /></td>
                        <td class="TableRowText02"><bean:write name="pd" property="createdTime" /></td>
                        <td class="TableRowText02" id="id_<%=counter%>" title='<bean:write name="pd" property="description"/>' onclick="extendColumn(this);"><script type="text/javascript">
			cutString('id_<%=counter%>');
			
  </script>
                        </td>
                        <td colspan="2" class="TableRowText02">Download</td>
                        </tr>
                    </logic:iterate>
 <tr>
                      <td class="TableRowText01">Dataset</td>
                      <td class="TableRowText01"># Compounds</td>
                      <td class="TableRowText01">KNN Type</td>
                      <td class="TableRowText01">Date</td>
                      <td class="TableRowText01">Description</td>
                      <td class="TableRowText01">Create Vis</td>
                      <td class="TableRowText01">Delete</td>
                    </tr>
                    <%counter=0; %>
                    <logic:iterate id="ud" name="userDatasets">
                      <%counter++; %>
                      <tr>
                        <td class="TableRowText02"><a  href="viewDataset.do?fileName=<bean:write name="ud" property="fileName" />"> <b>
                          <bean:write name="ud" property="fileName" />
                        </b> </td>
                        <td class="TableRowText02"><bean:write name="ud" property="numCompound" /></td>
                        <td class="TableRowText02"><bean:write name="ud" property="modelType" /></td>
                        <td class="TableRowText02"><bean:write name="ud" property="createdTime" /></td>
                        <td class="TableRowText02" id="id_<%=counter%>" title='<bean:write name="ud" property="description"/>' onclick="extendColumn(this);"><script type="text/javascript">
			cutString('id_<%=counter%>');
			
  </script>
                        </td>
                        <td class="TableRowText02"><a  href="setVisData.do?datasetname=<bean:write name="ud" property="fileName" />&knnType=<bean:write name="ud" property="modelType"/>&sdfName=<bean:write name="ud" property="sdfFile"/>&actName=<bean:write name="ud" property="actFile"/>">
                          create</a></td>
                        <td class="TableRowText02"><a  href="deleteFile.do?userName=<bean:write name="user" property="userName"/>&fileName=<bean:write name="ud" property="fileName"/>" onclick="return deleteDataset('Are you sure to delete this dataset?')">
                          delete</a></td>
                      </tr>
                    </logic:iterate>
                  </table>
                </logic:notEqual>
                <logic:equal name="userDatasets" value=""> <span class="StandardTextDarkGrayParagraph">Not datasets avaibale.</span> </logic:equal>
              </div>
          <br/>
              <a href="#predFile" >Manage Uploaded Prediction Files</a><br/>
              <br/>
              <div id="predFile">
                <logic:notEqual name="userPredictionFiles" value="">
                  <table>
                    <tr>
                      <td class="TableRowText01">File Name</td>
                      <td class="TableRowText01"># Compounds</td>
                      <td class="TableRowText01">Date</td>
					  <td class="TableRowText01">Create Vis</td>
                      <td class="TableRowText01">Delete</td>
                    </tr>
                    <logic:iterate id="up" name="userPredictionFiles">
                      <tr>
                        <td class="TableRowText02"><a  href="viewDataset.do?fileName=<bean:write name="up" property="fileName" />"> <b>
                          <bean:write name="up" property="fileName" /></b></td>
                        <td class="TableRowText02"><bean:write name="up" property="numCompound" /></td>
                        <td class="TableRowText02"><bean:write name="up" property="createdTime" /></td>
						<td class="TableRowText02"><a  href="setVisData.do?datasetname=<bean:write name="up" property="fileName" />&knnType=<bean:write name="up" property="modelType"/>&sdfName=<bean:write name="up" property="sdfFile"/>&actName=<bean:write name="up" property="actFile"/>">
                          create</a></td>
                        <td class="TableRowText02"><a href="deleteFile.do?userName=<bean:write name="user" property="userName"/>&fileName=<bean:write name="up" property="fileName" />
                          " onclick="return deleteDataset('Are you sure to delete this dataset?')">
                          delete</a></td>
                      </tr>
                    </logic:iterate>
                  </table>
                </logic:notEqual >
                <logic:equal name="userPredictionFiles" value=""> <span class="StandardTextDarkGrayParagraph">Not prediction files avaibale.</span> </logic:equal>
            </div><br /><br /></td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td colspan="5" valign="top">
    <html:form action="/submitDataset.do" enctype="multipart/form-data">
    <table cellpadding="3" cellspacing="3" style="border-color:#000000;border-style:solid; border-width:thin">
                <tr>
                    <td align="left" width="49%">
                    <input type="radio" name="upload" checked="true" value="MODELING" id="modeling_select" onclick="showUpload();"/>
                      <b class='StandardTextDarkGrayParagraph'>Upload dataset for modeling</b>
                    <table cellpadding="3" cellspacing="3" style="border-color:#000000;border-style:solid; border-width:thin" width="100%">
                              <tr>
                                <td colspan="2"><b class='StandardTextDarkGrayParagraph'>Data type:</b></td>
                              </tr>
                              <tr>
                                <td colspan="2"><input id="con" name="knnType" value="CONTINUOUS" checked="true"  type="radio" />
                                    <b class="StandardTextDarkGrayParagraph"> QSAR Continuous</b><br />
                                    <input id="cat" name="knnType" value="CATEGORY"  type="radio" />
                                    <b class="StandardTextDarkGrayParagraph"> QSAR Category</b></td>
                              </tr>
                              <tr>
                                <td colspan="2"><b class='StandardTextDarkGrayParagraph'>Upload:</b></td>
                              </tr>
                              <tr>
                                <td><span tooltip="Upload an ACT file."><b class="StandardTextDarkGrayParagraph">ACT File:</b></span><br />
                                    <span tooltip="Upload an SD file."><b class="StandardTextDarkGrayParagraph">SDF File:</b></span></td>
                                <td><input id="loadAct" name="actFile" type="file"/>
                                    <br />
                                    <input id="loadSdfModeling" name="sdFileModeling" onchange="setDatasetName(this)" type="file" /></td>
                              </tr>
                              </table>
                    </td>
                    <td style="width:2%" align="left">
                    </td>
                    <td align="left" valign="top">
                    <input type="radio" name="upload" id="prediction_select" value="PREDICTION" onclick="showUpload();"/>
                      <b class='StandardTextDarkGrayParagraph'>Upload dataset for prediction</b>
                    <table cellpadding="3" cellspacing="3" style="border-color:#000000;border-style:solid; border-width:thin" width="100%">
                              <tr>
                                <td colspan="2"><b class='StandardTextDarkGrayParagraph'>Upload:</b></td>
                              </tr>
                              <tr>
                                <td align="right"><span tooltip="Upload an SD file."><b class="StandardTextDarkGrayParagraph">SDF File:</b></span></td>
                                <td><input id="loadSdfPrediction" name="sdFilePrediction" onchange="setDatasetName(this)" type="file" disabled="disabled"/></td>
                              </tr>
                              
                            </table>
                       
                    </td>
                </tr>
                <tr>
                    <td><div class='StandardTextDarkGrayParagraph'><b>Dataset name:</b>&nbsp;
                            <input type="text" id="datasetname" name="datasetname" size="60"/>
                    </div></td>
              </tr>
              <tr>
                    <td><b class='StandardTextDarkGrayParagraph'>Files description:</b><br />
                        <textarea class='StandardTextDarkGrayParagraph' name="dataSetDescription" id="dataSetDescription" style="height: 50px; width: 100%"></textarea></td>
              </tr>
              <tr>
                    <td colspan="3"><input class='StandardTextDarkGrayParagraph' name="userAction" id="userAction" onclick="if(validateObjectNames(document.getElementById('datasetname').value, usedDatasetNames, usedPredictorNames, usedPredictionNames, usedTaskNames)){ checkSpaces(this,document.getElementById('datasetname').value); }" value="Create Dataset" type="button" />
                    </td>
              </tr>
              <tr>
                    <td colspan="3"><span id="textarea"></span>
                        <div id="messageDiv" style="color: red;"></div></td>
              </tr>
    </table>
    </html:form>
    </td>
    </tr>
  
</table>
<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
