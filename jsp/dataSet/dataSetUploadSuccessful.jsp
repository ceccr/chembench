<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib prefix="c" uri="/application/jstl/core"%>
<%@ page isELIgnored="false" %>

<%@ page import="edu.unc.ceccr.global.Constants" %>
<html:html>
<head>
<title>C-CHEMBENCH | Files submited successfully!</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<script src="javascript/script.js"></script>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
function selectTanimoto(){
	alert(similarity_measure[0].checked);
	if(similarity_measure[0].checked!=true)
		similarity_measure[0].checked = true;
}
</script>
</head>
<body>
<table width="924px" border="0" align="center" cellpadding="0"	cellspacing="0">
<tr><td>
	<%@include file="/jsp/main/header.jsp" %>
<%@include file="/jsp/main/centralNavigationBar.jsp" %>
		</td>
	</tr>
		<tr>
		<span id="maincontent">
		<td height="557" valign="top"
			background="theme/img/backgrlibrary.jpg">
		<p class="StandardTextDarkGray">&nbsp;</p>
		<p class="StandardTextDarkGray"><strong>Success</strong>		</p>
		<p class="StandardTextDarkGrayParagraph">Your  data files has been submit successfully. Image creation task was started on the background process.</p>
		&nbsp;&nbsp;<a href="myjobs.do"><font size=3 color='blue'><b><u>GO TO JOB</u></b></font></a><br/>
		&nbsp;&nbsp;<a href="manageFile.do"><font size=3 color='blue'><b><u>BACK TO DATASET</u></b></font></a><br/><br/><br/><br/>
		<% if(/*session.getAttribute("KnnType")!="PREDICTION"*/ true){ 
			String vis = "setVisData.do?datasetName="+session.getAttribute("datasetName")+"&knnType="+session.getAttribute("KnnType");
			out.println("&nbsp;&nbsp;<a href="+vis+"><font size=3 color='blue'><b><u>GO TO DATASET VISUALISATION SETTINGS</u></b></font></a></p><br/><br/><br/><br/>");
		}
		%>
		
		
            </td>
		</span>
	</tr>
	<tr>
<td>
	<%@include file ="/jsp/main/footer.jsp" %>
</td>
</tr>
</table>
</body>
</html:html>
