<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<jsp:useBean id="user" class="edu.unc.ceccr.persistence.User" scope="session" />
<bean:define id="predictors" type="java.util.List" name="predictors" scope="session"></bean:define>
<html:html>
<head>
<title>C-CHEMBENCH | Select Predictor</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />

<script src="javascript/script.js"></script>
<script src="javascript/yahoo/yahoo.js"></script>
<script src="javascript/dom/dom.js"></script>
<script src="javascript/event/event.js"></script>
<script src="javascript/container/container_core.js"></script>
<script src="javascript/predictorFormValidation.js"></script>

<script language="javascript">

var showModule1 = false;
var showModule2 = false;

function init() {
	YAHOO.module1 = new YAHOO.widget.Module("module1", { visible:false });
	YAHOO.module2 = new YAHOO.widget.Module("module2", { visible:false });
	YAHOO.module1.render();
	YAHOO.module2.render();
	document.getElementById("linkFilter").onclick = function() {
		if(!showModule1)YAHOO.module1.show(); else YAHOO.module1.hide(); showModule1=!showModule1;
	};

	document.getElementById("linkGenerate").onclick=function() {
		if(!showModule2)YAHOO.module2.show(); else YAHOO.module2.hide(); showModule2=!showModule2;
	};
}

YAHOO.util.Event.addListener(window, "load", init);


function checkDuplicateName(value, btn) {
	
	var list = new Array(<logic:iterate id="pred" name="predictors">"<bean:write name="pred" property="name" />",</logic:iterate>"");
	
	var rejectName = false;
	for(i=0; i < value.length; i++){
		if(value[i] == ' '){
			rejectName=true;
		}
	}
	if(rejectName){
		window.alert("The job name must not contain a space.");
                  document.getElementById("jobName").value="";
   			btn.disabled=true;
			return ; 	
	}
	for(n in list){
		//alert(list[n]);
		if(value==list[n]&&value!="") {
                  window.alert("The model name of '"+value+"' is already in use.");
            btn.disabled=true;
   			return;
		}
		else{
			btn.disabled=false;
		}  
    }
}
							
							
function disableSDFDropdown(){
document.getElementById("SDFileSelection").disabled=true;
document.getElementById("sdFile").disabled=false;
//document.forms[1].SDFileSelection.disabled = true;
//document.forms[1].sdFile.disabled = false;
}

function disableSDInput(){
document.getElementById("SDFileSelection").disabled=false;
document.getElementById("sdFile").disabled=true;
//document.forms[1].SDFileSelection.disabled = false;
//document.forms[1].sdFile.disabled = true;
	}

function enableView()
{
  document.getElementById("view").disabled=false;
}

function showFiles()
{

  if(document.getElementById("fileManagementDiv").style.display=='inline')
{
document.getElementById("fileManagementDiv").style.display='none';}
else{document.getElementById("fileManagementDiv").style.display='inline';}

}
</script>

</head>
<body >

<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	
	<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrpredictors.jpg">
		<p class="StandardTextDarkGray">&nbsp;</p>
		<p class="StandardTextDarkGrayParagraph">
		<b>C-ChemBench Predictors</b>
		</p>
		<table width="465" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td><p align="justify" class="StandardTextDarkGrayParagraph">
		Here already developed models are available to make predictions on sets of compounds. Models generated and validated by the Laboratory for Molecular Modeling at UNC-CH are available as well as models that you generated through the Model Development section of the website. Compounds to screen can be upload in sdf format below. Currently, only 500 compounds may be predicted at one time. 
		<br><br>Compound databases will soon be available for large scale virtual screening. 
		<br><br>Click the name of a predictor. Then you may predict the activity of a dataset, a SMILES string, or a molecule sketch.
<br><br></p></td>
          </tr>
        </table>
		<b><html:errors /></b>
		
		<table border="0" align="left" cellpadding="4	"	cellspacing="4">
		<tbody>
		<tr>
		<td>
			
		<p class="StandardTextDarkGrayParagraph">
		<b>Drug Discovery Predictors</b>
		</p>
		<p align="justify" class="StandardTextDarkGrayParagraph">
		These are public predictors useful for virtual screening.
		</p>
			<table>
			<tr>
				<td class="TableRowText01">Name</td>
				<td class="TableRowText01">Keywords</td>
				<td class="TableRowText01">Last Updated</td>
				<td class="TableRowText01">Paper Reference</td>
                   <td class="TableRowText01">Times Used</td>
			</tr>
			<logic:iterate id="p" type="edu.unc.ceccr.persistence.Predictor" name="predictors">
				<%
					if (p != null && p.getPredictorType() != null && p.getPredictorType().equalsIgnoreCase("DrugDiscovery")){
					%>
					<tr>
						<td class="TableRowText02"><a href="selectPredictor.do?id=<%=p.getPredictorId()%>"><bean:write name="p" property="name" /></a>
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="description" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="dateUpdated" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="paperReference" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="numPredictions" />
						</td>
					</tr>
				<%}%>
			</logic:iterate>
			</table>
			<br /><br />
			
		<p class="StandardTextDarkGrayParagraph">
		<b>Public ADME/Tox Predictors</b>
		</p>
		<p align="justify" class="StandardTextDarkGrayParagraph">
		These are public predictors useful for toxicity prediction.
		</p>
			<table>
			<tr>
				<td class="TableRowText01">Name</td>
				<td class="TableRowText01">Keywords</td>
				<td class="TableRowText01">Last Updated</td>
				<td class="TableRowText01">Paper Reference</td>
                   <td class="TableRowText01">Times Used</td>
			</tr>
			<logic:iterate id="p" type="edu.unc.ceccr.persistence.Predictor" name="predictors">
			<%
					if (p != null && p.getPredictorType() != null && p.getPredictorType().equalsIgnoreCase("ADMETox")){
					%>
					<tr>
						<td class="TableRowText02"><a href="selectPredictor.do?id=<%=p.getPredictorId()%>"><bean:write name="p" property="name" /></a>
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="description" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="dateUpdated" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="paperReference" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="numPredictions" />
						</td>
					</tr>
					<%} %>
			</logic:iterate>
			</table>
			<br /><br />
			
			<p class="StandardTextDarkGrayParagraph">
			<b>Private Predictors</b>
			</p>
			<p align="justify" class="StandardTextDarkGrayParagraph">
			These are private predictors you have created or uploaded. Other users cannot access them.
			</p>
			<table>
			<tr>
				<td class="TableRowText01">Name</td>
				<td class="TableRowText01">Keywords</td>
				<td class="TableRowText01">Last Updated</td>
				<td class="TableRowText01">Paper Reference</td>
                   <td class="TableRowText01">Times Used</td>
			</tr>
			<logic:iterate id="p" type="edu.unc.ceccr.persistence.Predictor" name="predictors">
			<%
					if (p != null && p.getPredictorType() != null && p.getPredictorType().equalsIgnoreCase("Private")){
					%>
					<tr>
						<td class="TableRowText02"><a href="selectPredictor.do?id=<%=p.getPredictorId()%>"><bean:write name="p" property="name" /></a>
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="description" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="dateUpdated" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="paperReference" />
						</td>
						<td class="TableRowText02">
						<bean:write name="p" property="numPredictions" />
						</td>
					</tr>
					<%} %>
			</logic:iterate>
			</table>
			<br />
		</td>
		</tr>
		<tr>
		<td>
			</td>
			</tr>
			<tr>
			<td>
			<br />
			<div class="StandardTextDarkGrayParagraph2" id="linkFilter"><b>View Saved Predictions</b></div>
			<br />
			<div>
			<html:form action="/viewPredOutput.do">
			<table>
				<tr>
					<td>
					<div class="StandardTextDarkGray"></div>
					</td>
					<td class="TableRowText01">Name</td>
					<td class="TableRowText01">Date Created</td>
					<td class="TableRowText01">Predictor</td>
					<td class="TableRowText01">Database Predicted</td>
                    <td class="TableRowText01">Download</td>

				</tr>
				<logic:iterate id="p" name="predictions">
					<tr>
						<td>
						<div class="StandardTextDarkGray"><input type="radio" onclick="enableView()" name="predictionJobId"
						value = '<bean:write name="p" property="predictionJobId" />'/></div>
						</td>
						<td class="TableRowText02"><bean:write name="p" property="jobName" /></td>
						<td class="TableRowText02"><bean:write name="p" property="dateCreated" /></td>
						<td class="TableRowText02"><bean:write name="p" property="predictorName" /></td>
						<td class="TableRowText02"><bean:write name="p" property="database" /></td>
              <!--                      
              <a href="file?flow=main&name=<bean:write name='p' property='jobName'/>+.txt&user=<bean:write name='user' property='userName' />&predId=<bean:write name='p' property='predictionJobId' />&predictor=<bean:write name='p' property='predictorName' />">
                       -->   
                        <td class="TableRowText02"><a href="projectFilesServlet?project=<bean:write name='p' property='jobName'/>&user=<bean:write name='user' property='userName' />&projectType=predictor">          
                                    download</a></td>

					</tr>
				</logic:iterate>
				<tr>
					<td>
					<div class="StandardTextDarkGray"></div>
					</td>
					<td><html:submit  styleId="view" disabled="true">View</html:submit></td>
					<td>
					<div class="StandardTextDarkGray"></div>
					</td>
					<td>
					<div class="StandardTextDarkGray"></div>
					</td>
				</tr>
			</table>
			</html:form>
			</div>
		 	</td>			
		</tr>
		
		</tbody>
		</table>
		</span>
	</tr>
	<tr>
<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>

