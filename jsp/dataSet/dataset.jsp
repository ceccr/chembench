<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
<title>C-CHEMBENCH | Dataset Management</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>

<script src="javascript/script.js"></script>
<script src="javascript/datasetScripts.js"></script>

<script language="javascript">
	var usedDatasetNames = new Array(<s:iterator value="userDatasetNames">"<s:property />",</s:iterator>"");
	var usedPredictorNames = new Array(<s:iterator value="userPredictorNames">"<s:property />",</s:iterator>"");
	var usedPredictionNames = new Array(<s:iterator value="userPredictionNames">"<s:property />",</s:iterator>"");
	var usedTaskNames = new Array(<s:iterator value="userTaskNames">"<s:property />",</s:iterator>"");
</script>

</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>

<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td colspan="5" valign="top"  background="theme/img/backgrmodelbuilders.jpg">
      <table width="465" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td><p class="StandardTextDarkGrayParagraph"><b><br />
            C-Chembench Dataset Management</b></p>
              <p align="justify" class="StandardTextDarkGrayParagraph">This part of C-Chembench allows you to manage and explore data sets.  Aside from the basic functions of uploading and deleting data sets, this part of our web site lets you better understand the datasets that you are working with.<br />
                  <br />
                In order to properly evaluate the quality of a model, you will often want to understand the dataset from which it was built.  How similar or diverse are the compounds that you were using?  What were the key differentiators that exist? <br />
                <br />
                Capabilities offered in this section include the ability to look at distant matrices, various clustering techniques and principal component analysis of the datasets.  Options provide visualization and numeric results.<br />
                <br />
            </p></td>
        </tr>
      </table>
   </td>
  </tr>
  <tr>
    <td>
    <s:form action="/submitDataset" enctype="multipart/form-data" theme="simple">
     
    <table><tr><td>
    	Upload Dataset	
    	<sx:tabbedpanel id="datasetTypeTabs">
    	
	    	<sx:div id="typeModPred" href="/loadModAndPredSection" label="Modeling and Prediction Set" theme="ajax" loadingText="Loading dataset types...">
			owls
			</sx:div>
			
			<sx:div id="typeModOnly" href="/loadModelingOnlySection" label="Modeling-Only Set" theme="ajax" loadingText="Loading dataset types...">
			yay
			</sx:div>
			
			<sx:div id="typePredOnly" href="/loadPredictionOnlySection" label="Prediction-Only Set" theme="ajax" loadingText="Loading dataset types...">
			stuff
			</sx:div>
    	
    	</sx:tabbedpanel>
    </td></tr></table>

    </s:form>
    </td>
  </tr>
</table>
<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html>
