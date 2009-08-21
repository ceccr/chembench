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

<%@page import="edu.unc.ceccr.utilities.DatasetFileOperations"%><html>
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
<script src="javascript/AC_RunActiveContent.js"></script>
<script src="javascript/hookMouseWheel.js"></script>
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

if(!(document.attachEvent)) {
    window.addEventListener("DOMMouseScroll", handleWheel, false);
}
function handleWheel(event) {
    var app = window.document["${application}"];
    if (app) {
        var o = {x: event.screenX, y: event.screenY, 
            delta: event.detail,
            ctrlKey: event.ctrlKey, altKey: event.altKey, 
            shiftKey: event.shiftKey}
        
        app.handleWheel(o);
    }
}

</script>
</head>
<body>
<table width="924px" border="0" align="center" cellpadding="0"	cellspacing="0">
<%@include file="/jsp/main/header.jsp" %>
<%@include file="/jsp/main/centralNavigationBar.jsp" %>
		<tr>
		<td height="557" colspan="5" valign="top" background="theme/img/backgrmodelbuilders.jpg">
		<span id="maincontent">

<%DataSet ds = (DataSet)session.getAttribute("ds"); %>

calling flash with params:
web-addr=http://chembench-dev.mml.unc.edu
dataset=<%=ds.getFileName()%>
ncom=<%=ds.getNumCompound()%>
type_=<%=ds.getModelType()%>
creation_date=<%=ds.getCreatedTime().toString().substring(0,ds.getCreatedTime().toString().indexOf(" "))%>
desc=<%=ds.getDescription()%>
actFile=<%=ds.getActFile()%>
sdfFile=<%=ds.getSdfFile()%>
user=<%=ds.getUserName()%>"

<table width="924px" align="center" border="0">
<tr align="center" id="download" style="display:none;">
<td align="center">
</td>
</tr>
<tr>
<td align="center">
<script type="text/javascript">
AC_FL_RunContent( 'codebase','http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,28,0','width','924','height','924','src','/jchem/myroslav/vis/heatmap','quality','high','pluginspage','http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash','flashvars','web-addr=http://chembench-dev.mml.unc.edu&dataset=<%=ds.getFileName()%>&ncom=<%=ds.getNumCompound()%>&type_=<%=ds.getModelType()%>&creation_date=<%=ds.getCreatedTime().toString().substring(0,ds.getCreatedTime().toString().indexOf(" "))%>&desc=<%=ds.getDescription()%>&actFile=<%=ds.getActFile()%>&sdfFile=<%=ds.getSdfFile()%>&user=<%=ds.getUserName()%>','movie','/jchem/myroslav/vis/heatmap' ); //end AC code
</script>

  <noscript>
  <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,28,0" width="924" height="924">
    <param name="movie" value="/jchem/myroslav/vis/heatmap.swf" />
    <param name="quality" value="high" />
    <param name="FlashVars" value="web-addr=http://chembench-dev.mml.unc.edu&dataset=<%=ds.getFileName()%>&ncom=<%=ds.getNumCompound()%>&type_=<%=ds.getModelType()%>&creation_date=<%=ds.getCreatedTime().toString().substring(0,ds.getCreatedTime().toString().indexOf(" "))%>&desc=<%=ds.getDescription()%>&actFile=<%=ds.getActFile()%>&sdfFile=<%=ds.getSdfFile()%>&user=<%=ds.getUserName()%>" />
    <embed src="/jchem/myroslav/vis/heatmap.swf" width="924" height="924" quality="high" pluginspage="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" flashvars="web-addr=http://chembench-dev.mml.unc.edu&dataset=<%=ds.getFileName()%>&ncom=<%=ds.getNumCompound()%>&type_=<%=ds.getModelType()%>&creation_date=<%=ds.getCreatedTime().toString().substring(0,ds.getCreatedTime().toString().indexOf(" "))%>&desc=<%=ds.getDescription()%>&actFile=<%=ds.getActFile()%>&sdfFile=<%=ds.getSdfFile()%>&user=<%=ds.getUserName()%>">
    </embed>
  </object>
  </noscript>  

</td>
</tr>
</table>

		</span></td>
	</tr>

	<%@include file ="/jsp/main/footer.jsp" %>
</table>
</body>
</html>
