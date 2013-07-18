<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<%@ page language="java" import="java.util.*" %>

<html>
<head>
    <sx:head debug="false" cache="false" compressed="true" />
    <title>CHEMBENCH | Administration</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link href="theme/ccbStyle.css" rel="stylesheet" type="text/css">
    <link href="theme/ccbStyleNavBar.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="theme/screen.css" type="text/css" media="screen, projection">
    <link rel="stylesheet" href="theme/print.css" type="text/css" media="print">
    <link href="theme/standard.css" rel="stylesheet" type="text/css">
    <link href="theme/links.css" rel="stylesheet" type="text/css">
    <link href="theme/dynamicTab.css" rel="stylesheet" type="text/css">
    <link rel="icon" href="/theme/img/mml.ico" type="image/ico">
    <link rel="SHORTCUT ICON" href="/theme/img/mml.ico">
    <link href="theme/customStylesheet.css" rel="stylesheet" type="text/css">

    <script src="javascript/script.js"></script>
    <script src="javascript/admin.js"></script>
    <script language="JavaScript" src="javascript/sortableTable.js"></script>
    <script language="javascript" src="javascript/jquery-1.6.4.min.js"></script>

    <script type="text/javascript">
        function checkContent()
        {
            if(document.getElementById("content").value=="")
            {return (window.confirm("Send emails without content?"));}
            else{return true;}
        }
    </script>
</head>
<body onload="setTabToHome();">

<!-- headers -->
<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
    <!--  page content -->

	<div class="StandardTextDarkGrayParagraph">
		<div class="StandardTextDarkGrayParagraph2">
            <b>Dataset Management</b>
        </div>
		
		<div class="StandardTextDarkGrayParagraph">
		    You can promote anyone's dataset to public with dataset name and user name. If you want to delete a public dataset, you only need to give the dataset name.
            <form onsubmit="return confirm('Are you sure?')">
                <table width="680" border="0">
                    <tr><td>Dataset Name:</td><td><s:textfield name="datasetName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td>User name:</td><td><s:textfield name="userName" value="" size="43" theme="simple" /></td></tr>
                    <tr>
					<td></td>
					<td>
					<input type="submit" name="promote" onclick="this.form.action='makeDatasetPublicAction'"   value="Promote" />
					<input type="submit" name="delete" onclick="this.form.action='deletePublicDatasetAction'" value="Delete"/>
					</td>
					</tr>
                </table>
            </form>
        </div>
	</div>
	
	<div class="StandardTextDarkGrayParagraph">
		<div class="StandardTextDarkGrayParagraph2">
            <b>Predictor Management</b>
        </div>
		
		<div class="StandardTextDarkGrayParagraph">
			You can promote anyone's predictor to public with predictor name, user name and set the predictor type. If you want to delete a public predictor, you only need to give the predictor name.
		<form onsubmit="return confirm('Are you sure?')">
		    <table width="680" border="0">
                    <tr><td>Predictor name:</td><td><s:textfield name="predictorName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td>User name:</td><td><s:textfield name="userName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td>Predictor type:</td><td><s:select name="predictorType" theme="simple" list="#{'':'','DrugDiscovery':'Drug Discovery','ADME':'ADME','Toxicity':'Toxicity'}" value=""/></td></tr>
					<tr>
					<td></td>
					<td>
					<input type="submit" name="promote" onclick="this.form.action='makePredictorPublicAction'" value="Promote"/>
					<input type="submit" name="delete" onclick="this.form.action='deletePublicPredictorAction'" value="delete"/>
					<!--<input type="submit" name="rename" onclick="this.form.action='renamePublicPredictorAction'" value="Rename">-->
					</td>
					</tr>
            </table>
		</form>
        </div>		
	</div>
	
<!--        <div class="StandardTextDarkGrayParagraph">
            Delete Public Prediction:<br />
            <form action="deletePublicPredictionAction">
                <table width="680" border="0">
                    <tr><td>Prediction ID:</td><td><s:textfield name="predictionName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td>User name:</td><td><s:textfield name="userName" value="" size="43" theme="simple" /></td></tr>
                    <tr><td></td><td><input type="submit" value="Delete" /></td></tr>
                </table>
            </form>
        </div>-->
</body>
</html>