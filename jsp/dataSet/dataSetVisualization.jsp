<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<%@ page import="edu.unc.ceccr.utilities.Utility" %>
<%@ page import="edu.unc.ceccr.persistence.DataSet" %>
<%@ page import="edu.unc.ceccr.persistence.ExternalValidation" %>
<jsp:useBean id="user" class="edu.unc.ceccr.persistence.User" scope="session"/>
<html>
<head>
<title>C-CHEMBENCH | Tanimoto calculations</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<link href="theme/yahoo_ui/menu.css" rel="stylesheet" type="text/css"></link>
<link href="theme/yahoo_ui/text.css" rel="stylesheet" type="text/css"></link>
<link href="theme/yahoo_ui/container/container.css" rel="stylesheet"	type="text/css"></link>

<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<script src="javascript/script.js"></script>
<script type="text/javascript">
function show_vis_panel()
{
  if(document.getElementById("vis_panel").style.display=='inline')
{
	  document.getElementById("vis_panel").style.display='none';
	  document.getElementById("panel_link").innerHTML = "Show visualization control panel";
}
else{
	document.getElementById("panel_link").innerHTML = "Hide visualization control panel";
	document.getElementById("vis_panel").style.display='inline';
	}

}

function checkPublicDataset(){
	if(<%=session.getAttribute("isPublic")%>){
		document.getElementById("download").style.display='inline';
	}
	else document.getElementById("vizualization").style.display='inline';
}

</script>
</head>
<body onload="checkPublicDataset();">
<table width="924px" border="0" align="center" cellpadding="0"	cellspacing="0">
<%@include file="/jsp/main/header.jsp" %>
<%@include file="/jsp/main/centralNavigationBar.jsp" %>
		<tr>
		<td height="557" colspan="5" valign="top" background="theme/img/backgrmodelbuilders.jpg">
		<span id="maincontent">

		

<table width="924px" align="center" border="0">
<%DataSet ds = (DataSet)session.getAttribute("ds"); %>
<tr align="center" id="download" style="display:none;">
<td align="center">
<a href="datasetFilesServlet?datasetName=<%=ds.getFileName()%>&user=all-users">
						Download this dataset</a><strong class="StandardTextDarkGray">(NOTE: Only SDF and ACT files are accessible for download!)</strong>
</td>
</tr>
<tr align="center" id="vizualization" style="display:none;">
<td align="center" >
<a href="#panel" onclick="show_vis_panel();" id="panel_link">Show visualization control panel</a>
<br />
<div id="vis_panel" style="display:none;" style="border: 1px black solid;">
<form name="viz" action="/generateDatasetVis.do"
			enctype="multipart/form-data">
		<div class="StandardTextDarkGray" align="left"><strong>Please choose what data representation you want to have:</strong><br/>
  	<input type="hidden" name="datasetname" id="datasetname" value="<%=ds.getFileName()%>" />
	<input type="hidden" name="knnType" id="knnType" value="<%=ds.getModelType()%>"/> 
	<input type="hidden" name="sdfName" id="sdfName" value="<%=ds.getSdfFile().replace(".sdf","")%>"/> 
	<input type="hidden" name="actName" id="actName" value="<%=ds.getActFile()%>"/> 
  <!--&nbsp;&nbsp;&nbsp;&nbsp; <input name="sketches" type="checkbox" value="Sketches" checked/> 
  Sketches<br/> -->
  &nbsp;&nbsp;<strong>Representations:</strong><br/>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <input name="represent" type="checkbox" value="HeatmapAndTree" onclick="selectTanimoto(this);" checked/> 
  Heat map and Tree<br/>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <!-- <input name="represent" type="checkbox" value="Trees" onclick="selectTanimoto(this);"/> 
  Tree<br/>
  &nbsp;&nbsp;&nbsp;&nbsp;-->
  <input name="represent" type="checkbox" value="PCA"/> 
  PCA plots<br/>    
  &nbsp;&nbsp;<strong>Similarity measure</strong><br/>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <input name="similarity_measure"  type="checkbox" value="Tanimoto" checked onclick="checkTanimoto();" /> 
  Tanimoto<br/>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <input name="similarity_measure"  type="checkbox" value="Mahalanobis" onclick="checkTanimoto();"/> 
  Mahalanobis<br/> 
  <br/>
	<span id="textarea"></span>
</div>
<div class='StandardTextDarkGray'><input name="userAction" id="userAction" onclick="submitAdditionalDatasetOperations(this,document.getElementById('textarea'));" value="Process" type="button"></input></div>
<br/>
<span id="textarea"></span>
</form>
</div> 
</td>
</tr>

<tr>
<td align="center">
<jsp:plugin type="applet" codebase="/jchem/myroslav/"  code="applet.CECCRApplet" archive="Applet.jar" width="100%" height="800" jreversion="1.6">
<jsp:params>
<jsp:param name="progressbar" value="true"/>
<jsp:param name="java_arguments" value="-Xmx1024m"/>
<jsp:param name="dataset" value="<%=ds.getFileName()%>" />
  <jsp:param name="ncom" value="<%=ds.getNumCompound()%>" />
   <jsp:param name="type_" value="<%=ds.getModelType()%>" />
    <jsp:param name="creation_date" value="<%=ds.getCreatedTime()%>" />
     <jsp:param name="desc" value="<%=ds.getDescription()%>" />
		<jsp:param name="actFile" value='<%=session.getAttribute("actFile")%>' />
			<jsp:param name="viz_path" value='<%=session.getAttribute("viz_path")%>' />
</jsp:params>
<jsp:fallback>
<strong>You should download JAVA to view this applet! </strong>
</jsp:fallback>
</jsp:plugin>

</td>
</tr>
</table>
		</span></td>
	</tr>

	<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>
</html>
