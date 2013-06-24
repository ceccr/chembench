<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page language="java" import="java.util.*"%>

<html>
<head>
    <sx:head debug="false" cache="false" compressed="true" />
    <title>CHEMBENCH | Select Predictors</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	
    <link href="theme/ccbStyle.css" rel="stylesheet" type="text/css">
    <link href="theme/ccbStyleNavBar.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="theme/screen.css" type="text/css"
          media="screen, projection">
    <link rel="stylesheet" href="theme/print.css" type="text/css"
          media="print">
    <link href="theme/standard.css" rel="stylesheet" type="text/css">
    <link href="theme/links.css" rel="stylesheet" type="text/css">
    <link href="theme/dynamicTab.css" rel="stylesheet" type="text/css">
    <link rel="icon" href="/theme/img/mml.ico" type="image/ico">
    <link rel="SHORTCUT ICON" href="/theme/img/mml.ico">
    <link href="theme/customStylesheet.css" rel="stylesheet"
          type="text/css">
	<link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/smoothness/jquery-ui.min.css" />
		  
    <script src="javascript/script.js"></script>
    <script language="JavaScript" src="javascript/sortableTable.js"></script>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
    <script language="javascript" src="javascript/modeling.js"></script>
    <script src="javascript/predictorFormValidation.js"></script>
	<script language="javascript">
        var usedDatasetNames = new Array(<s:iterator value="userDatasetNames">"<s:property />",</s:iterator>"");
        var usedPredictorNames = new Array(<s:iterator value="userPredictorNames">"<s:property />",</s:iterator>"");
        var usedPredictionNames = new Array(<s:iterator value="userPredictionNames">"<s:property />",</s:iterator>"");
        var usedTaskNames = new Array(<s:iterator value="userTaskNames">"<s:property />",</s:iterator>"");

        function predictSmiles(){
            var smiles = document.getElementById("smiles").value;
            var cutoff = document.getElementById("cutOffSmiles").value;
            if(cutoff == "" || smiles == ""){
                alert("Please enter a SMILES string and cutoff value.");
                return false;
            }
            else{

                //prepare the AJAX object
                var ajaxObject = GetXmlHttpObject();
                ajaxObject.onreadystatechange=function(){
                    if(ajaxObject.readyState==4){
                        hideLoading();
                        document.getElementById("smilesResults").innerHTML=ajaxObject.responseText;
                    }
                }

                showLoading("PREDICTING. PLEASE WAIT.")

                //send request
                var url="makeSmilesPrediction?smiles=" + smiles + "&cutoff=" + cutoff + "&predictorIds=" + '<s:property value="selectedPredictorIds" />';
                ajaxObject.open("GET",url,true);
                ajaxObject.send(null);

                return true;
            }
        }
    </script>
</head>
<body onload="setTabToPrediction();">
<div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. -->
<div class="outer">
<div class="includesHeader"><%@include file="/jsp/main/header.jsp" %></div>
<div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp" %></div>

<!--<div
        class="StandardTextDarkGrayParagraph predictionBackground benchAlign">
    <div class="homeLeft">
        <br /> <br />
        <p style="margin-left:20px">
            <b>Chembench Predictor Selection</b> <br /> <br /> Here you may
            use predictors to identify computational hits in external compound
            libraries. Predictors generated and validated by UNC's Molecular
            Modeling Laboratory are available under <b>Drug Discovery
            Predictors</b>, <b>ADME Predictors</b>, and <b>Toxicity Predictors</b>.
            Predictors you create using the MODELING tab appear under <b>Private
            Predictors</b>. <br /> <br />For more information about making
            predictions, see the <a href="/help-prediction">Prediction help
            page</a>. <br /> <br />Click the checkboxes to the left of each
            predictor you want to use, and hit the "Select Predictors" button.
            Then you may predict the activity of a dataset, a SMILES string, or
            a molecule sketch. <br /> <br /> If you wish to share predictors
            you have developed with the Chembench community, please contact us
            at <a href="mailto:ceccr@email.unc.edu">ceccr@email.unc.edu</a>.
        </p>
    </div>
</div> -->
<div>
<table style="border:0px solid black">
<tbody>
<tr valign="top">
<td valign="top" style="border:0px solid black; vertical-align:top">
<div valign="top" style="margin:0px; vertical-align:top">
<!--<p class="StandardTextDarkGrayParagraph2 boxHeadingText">
    <br/>
    <b>Prediction Set Selection</b>
</p>-->
<!--<div><br /></div>-->
<!--<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
        <td height="557" colspan="5" valign="top"
            background="theme/img/backgrmodelbuilders.jpg" style="background-repeat: no-repeat;"><span id="maincontent">
			
			<table width="465" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td>
                        <!-- <p class="StandardTextDarkGrayParagraph"><b><br>Chembench Predictions</b></p> -->
<!--                        <p align="justify" class="StandardTextDarkGrayParagraph">
                            <!-- description of predictions process goes here -->
<!--                            <br><br>
                        </p>
                    </td>
                </tr>
            </table>-->

            <!-- script sets hidden field so we know which tab was selected -->
			<s:if test="%{singleCompoundPredictionAllowed}">
             <script>
               $(function() {
                   $( "#tabs" ).tabs();
				   $( "#tabs" ).tabs({ active: 1 });
               });
             </script>
			</s:if>
			<s:else>
			 <script>
               $(function() {
                   $( "#tabs" ).tabs();
				   $( "#tabs" ).tabs({ active: 1 });
				   $( "#tabs" ).tabs( { disabled: [2] } );
               });
             </script>
			</s:else>
            <!-- end script -->

<table width="100%" align="center" cellpadding="0" cellspacing="4"
                   colspan="2">
                <tr>
                    <td>
	<div id="tabs">
	    <ul>
		    <li><a href="#selectTab">Select Predictors</a></li>
		    <li><a href="#datasetTab">Dataset</a></li>
			<li><a href="#compTab">Compound</a></li>
		</ul>

		<div id="selectTab">
<script>
	function openShutManager(oSourceObj,oTargetObj,shutAble,oOpenTip,oShutTip){
    var sourceObj = typeof oSourceObj == "string" ? document.getElementById(oSourceObj) : oSourceObj;
    var targetObj = typeof oTargetObj == "string" ? document.getElementById(oTargetObj) : oTargetObj;
    var openTip = oOpenTip || "";
    var shutTip = oShutTip || "";
    if(targetObj.style.display!="none"){
	if(shutAble) return;
	targetObj.style.display="none";
	if(openTip  &&  shutTip){
	    sourceObj.innerHTML = shutTip; 
	}
    } else {
	targetObj.style.display="block";
	if(openTip  &&  shutTip){
	    sourceObj.innerHTML = openTip; 
	}
    }
}
</script>
<s:form theme="simple" action="selectPredictor"
        enctype="multipart/form-data" method="post" >
		<br/>
<s:if test="user.showPublicPredictors!='NONE'">
        <div valign="top" style="width:550px; margin:0px; vertical-align:top">
            <!--<p class="StandardTextDarkGrayParagraph2 boxHeadingText">
			    <br/>
                <b>Chembench Predictor Selection</b>
            </p>-->
            <p style="cursor:pointer; font-weight:bold" class="StandardTextDarkGrayParagraph" onclick="openShutManager(this, 'drugdisc', false, '- Drug Discovery Predictors', '+ Drug Discovery Predictors')">
                <b>+ Drug Discovery Predictors</b>
            </p>
<!--            <p align="justify" class="StandardTextDarkGrayParagraph">These
                are public predictors useful for virtual screening. * - predictor
                based on the uploaded dataset</p> -->
            <table width="100%" class="sortable" id="drugdisc" style="display:none">
                <tr>
                    <th class="TableRowText01narrow_unsortable">Select</th>
                    <th class="TableRowText01narrow">Name</th>
                    <th class="TableRowText01narrow">Date Created</th>
                    <th class="TableRowText01narrow">Modeling Method</th>
                    <th class="TableRowText01narrow">Descriptor Type</th>
<!--                    <th class="TableRowText01narrow_unsortable" colspan="2">Description</th>-->
                </tr>
                <s:iterator value="userPredictors">
                    <s:if test="predictorType=='DrugDiscovery'">
                        <tr>
                            <td class="TableRowText02narrow"><s:checkbox
                                    name="predictorCheckBoxes" fieldValue="%{id}" /></td>
                            <td class="TableRowText02narrow"><s:property value="name" /></td>
                            <td class="TableRowText02narrow"><s:date
                                    name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
                            <td class="TableRowText02narrow"><s:property
                                    value="modelMethod" /></td>
                            <s:if test="descriptorGeneration=='UPLOADED'">
                                <td class="TableRowText02narrow">*<s:property
                                        value="uploadedDescriptorType" /></td>
                            </s:if>
                            <s:else>
                                <td class="TableRowText02narrow"><s:property
                                        value="descriptorGeneration" /></td>
                            </s:else>
<!--                            <td class="TableRowText02narrow" colspan="2"><s:property
                                    value="description" /></td> -->
                        </tr>
                    </s:if>
                </s:iterator>

            </table>
<!--            <br /> <br />-->
<!--        </div>-->
<!--        <div class="border benchAlign bottomMarginAdme">-->
            <p style="cursor:pointer; font-weight:bold" class="StandardTextDarkGrayParagraph"  onclick="openShutManager(this, 'adme', false, '- ADME Predictors', '+ ADME Predictors')">
                <b>+ ADME Predictors</b>
            </p>
<!--            <p align="justify" class="StandardTextDarkGrayParagraph">These
                are public predictors useful for prediction of absorption,
                distribution, metabolism, and excretion properties. * - predictor
                based on the uploaded dataset</p> -->
            <table width="100%" class="sortable" id="adme" style="display:none">
                <tr>
                    <th class="TableRowText01narrow_unsortable">Select</th>
                    <th class="TableRowText01narrow">Name</th>
                    <th class="TableRowText01narrow">Date Created</th>
                    <th class="TableRowText01narrow">Modeling Method</th>
                    <th class="TableRowText01narrow">Descriptor Type</th>
<!--                    <th class="TableRowText01narrow_unsortable" colspan="2">Description</th> -->
                </tr>
                <s:iterator value="userPredictors">
                    <s:if test="predictorType=='ADME'">
                        <tr>
                            <td class="TableRowText02narrow"><s:checkbox
                                    name="predictorCheckBoxes" fieldValue="%{id}" /></td>
                            <td class="TableRowText02narrow"><s:property value="name" /></td>
                            <td class="TableRowText02narrow"><s:date
                                    name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
                            <td class="TableRowText02narrow"><s:property
                                    value="modelMethod" /></td>
                            <s:if test="descriptorGeneration=='UPLOADED'">
                                <td class="TableRowText02narrow">*<s:property
                                        value="uploadedDescriptorType" /></td>
                            </s:if>
                            <s:else>
                                <td class="TableRowText02narrow"><s:property
                                        value="descriptorGeneration" /></td>
                            </s:else>
<!--                            <td class="TableRowText02narrow" colspan="2"><s:property
                                    value="description" /></td> -->
                        </tr>
                    </s:if>
                </s:iterator>
            </table>
<!--        </div>-->
<!--        <br />
        <br />-->

<!--        <div class="border benchAlign bottomMargin">-->
            <p style="cursor:pointer; font-weight:bold" class="StandardTextDarkGrayParagraph"  onclick="openShutManager(this, 'toxicity', false, '- Toxicity Predictors', '+ Toxicity Predictors')">
                <b>+ Toxicity Predictors</b>
            </p>
<!--            <p align="justify" class="StandardTextDarkGrayParagraph">These
                are public predictors useful for toxicity prediction. * -
                predictor based on the uploaded dataset</p>-->
            <table width="100%" class="sortable" id="toxicity" style="display:none">
                <tr>
                    <th class="TableRowText01narrow_unsortable">Select</th>
                    <th class="TableRowText01narrow">Name</th>
                    <th class="TableRowText01narrow">Date Created</th>
                    <th class="TableRowText01narrow">Modeling Method</th>
                    <th class="TableRowText01narrow">Descriptor Type</th>
<!--                    <th class="TableRowText01narrow_unsortable" colspan="2">Description</th> -->
                </tr>
                <s:iterator value="userPredictors">
                    <s:if test="predictorType=='Toxicity'">
                        <tr>
                            <td class="TableRowText02narrow"><s:checkbox
                                    name="predictorCheckBoxes" fieldValue="%{id}" /></td>
                            <td class="TableRowText02narrow"><s:property value="name" /></td>
                            <td class="TableRowText02narrow"><s:date
                                    name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
                            <td class="TableRowText02narrow"><s:property
                                    value="modelMethod" /></td>
                            <s:if test="descriptorGeneration=='UPLOADED'">
                                <td class="TableRowText02narrow">*<s:property
                                        value="uploadedDescriptorType" /></td>
                            </s:if>
                            <s:else>
                                <td class="TableRowText02narrow"><s:property
                                        value="descriptorGeneration" /></td>
                            </s:else>
<!--                            <td class="TableRowText02narrow" colspan="2"><s:property
                                    value="description" /></td> -->
                        </tr>
                    </s:if>
                </s:iterator>
            </table>
<!--            <br /> <br />-->
<!--        </div>-->
    </s:if>
<!--    <div class="border benchAlign bottomMargin">-->
        <p style="cursor:pointer; font-weight:bold" class="StandardTextDarkGrayParagraph"  onclick="openShutManager(this, 'private', false, '- Private Predictors', '+ Private Predictors')">
            <b>+ Private Predictors</b>
        </p>
<!--        <p align="justify" class="StandardTextDarkGrayParagraph">These
            are private predictors you have created. Other users cannot access
            them. * - predictor based on the uploaded dataset</p>-->
        <table width="100%" class="sortable" id="private" style="display:none">
            <tr>
                <th class="TableRowText01narrow_unsortable">Select</th>
                <th class="TableRowText01narrow">Name</th>
                <th class="TableRowText01narrow">Date Created</th>
                <th class="TableRowText01narrow">Modeling Method</th>
                <th class="TableRowText01narrow">Descriptor Type</th>
                <!--<th class="TableRowText01narrow">Dataset</th>-->
            </tr>
            <s:iterator value="userPredictors">
                <s:if test="predictorType=='Private'">
                    <tr>
                        <td class="TableRowText02narrow"><s:checkbox
                                name="predictorCheckBoxes" fieldValue="%{id}" /></td>
                        <td class="TableRowText02narrow"><s:property value="name" /></td>
                        <td class="TableRowText02narrow"><s:date name="dateCreated"
                                                           format="yyyy-MM-dd HH:mm" /></td>
                        <td class="TableRowText02narrow"><s:property value="modelMethod" /></td>
                        <s:if test="descriptorGeneration=='UPLOADED'">
                            <td class="TableRowText02narrow">*<s:property
                                    value="uploadedDescriptorType" /></td>
                        </s:if>
                        <s:else>
                            <td class="TableRowText02narrow"><s:property
                                    value="descriptorGeneration" /></td>
                        </s:else>
                        <!--<td class="TableRowText02narrow"><s:property
                                value="datasetDisplay" /></td>-->
                    </tr>
                </s:if>
            </s:iterator>
        </table>
<!--    </div>-->
<!--    <br />

    <br />-->
<!--    <div class="border benchAlign Choosepredictors" style="margin-top:-20px">-->
<!--        <p class="StandardTextDarkGrayParagraph">
            <b>Choose Predictors</b>
        </p>
        <p align="justify" class="StandardTextDarkGrayParagraph">When
            you have checked the boxes next to the predictors you want to use,
            click on the button below.</p> -->
        <table>
            <tr>
                <td>&nbsp;&nbsp;&nbsp;&nbsp;<s:submit
                        value="Select Predictors" />
                </td>
            </tr>
        </table>
    </div>
	</s:form>
    </div>	

	<div id="datasetTab" class="StandardTextDarkGrayParagraph">
	<s:form action="makeDatasetPrediction" enctype="multipart/form-data" theme="simple">
	<table width="924" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
        <tbody>
<!--        <tr>
            <td align="left" colspan="2">
                <div class="StandardTextDarkGrayParagraph2" align="left"><b>Dataset Prediction</b></div><br />
            </td>
        </tr>-->
    <tr>
            <td>
                <table><tr><td colspan="2">
                    <div class="StandardTextDarkGray"><b>Chosen Predictors:</b></div>
                    <div class="StandardTextDarkGray">
                        <table width="100%" class="sortable" id="private">
                            <tr>
                                <th class="TableRowText01narrow">Name</td>
                                <th class="TableRowText01narrow">Date Created</th>
                                <th class="TableRowText01narrow">Modeling Method</th>
                                <th class="TableRowText01narrow">Descriptor Type</th>
                            </tr>
                            <s:iterator value="selectedPredictors">
                                <tr>
                                    <td class="TableRowText02"><s:property value="name" /></td>
                                    <td class="TableRowText02"><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
                                    <td class="TableRowText02"><s:property value="modelMethod" /></td>
                                    <s:if test="descriptorGeneration=='UPLOADED'">
                                        <td class="TableRowText02">*<s:property value="uploadedDescriptorType" /></td>
                                    </s:if>
                                    <s:else>
                                        <td class="TableRowText02"><s:property value="descriptorGeneration" /></td>
                                    </s:else>
                                </tr>
                            </s:iterator>
                        </table>
                        <!--<p class="StandardTextDarkGray"><a href="prediction">Back to Predictors selection page</a></p>-->


                </div>
                
                </td>
                </tr>
                    <tr>
                        <td height="26" width="150" align="left">
                            <div align="left" class="StandardTextDarkGray"><b>Select a Dataset:</b></div>
                        </td>
                        <td align="left" valign="top">
                            <s:if test="%{userDatasets.size()>0}">
                                <s:select name="selectedDatasetId" list="userDatasets" id="selectedDataset" listKey="id" listValue="name" />
                                <input type="button" value="View Dataset" property="text" onclick="window.open('viewDataset?id='+document.getElementById('selectedDataset').value)"/>
                            </s:if>
                            <s:else>
                                <div class="StandardTextDarkGrayParagraph"><i>There is no datasets with descriptors. Use the "DATASET" page to create datasets.</i></div>
                            </s:else>
                            <div class="StandardTextDarkGrayParagraph"><i>(Use the "DATASET" page to create datasets.)</i></div>
                        </td> 
                    </tr>
                    <tr>
                        <td height="26" width="150" align="left">
                            <div align="left" class="StandardTextDarkGray"><b>Similarity Cut
                                Off:</b></div>
                        </td>
                        <td align="left" valign="top"><s:textfield name="cutOff" id="cutOff" size="4" /><span id="messageDiv2"></span></td>
                    </tr>
                    <tr>
                        <td height="26" width="150" align="left">
                            <div align="left" class="StandardTextDarkGray"><b>Prediction Name:</b></div>
                        </td>
                        <td width="400" align="left" valign="top"><s:textfield name="jobName" id="jobName" size="19"/><span id="messageDiv1"></span></td>
                    </tr>
                    <tr>
                        <td align="left"><s:hidden name="selectedPredictorIds" /></td>
                        <td align="left" valign="top"><input type="button" name="userAction" id="userAction" onclick="if(validateObjectNames(document.getElementById('jobName').value, usedDatasetNames, usedPredictorNames, usedPredictionNames, usedTaskNames)){ submitForm3(this); }"
                                                             value="Submit Prediction Job" /> <span id="textarea"></span></td>
                    </tr>
                </table></td></tr>
        </tbody>
    </table>
	</s:form>
</div>

<div id="compTab">
<s:if test="%{singleCompoundPredictionAllowed}">
	<br />
	<table width="450" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
        <tbody>
			<tr>
            <td valign="top" align="center">
                <p class="StandardTextDarkGrayParagraph2">
                    <b>Sketch your compound OR Enter a SMILES string</b>
                </p>

			</td>
			</tr>
			<tr>
            <td valign="top">
                <table width="450" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4">
                    <tbody>
					<tr>
                        <td width="100%" height="24" align="left" colspan="2">
                            <p class="StandardTextDarkGrayParagraph2">
                                <b>Sketch</b>
                            </p>
                        </td>
                    </tr>
					
                    <tr>
					    <td style="width: 20px"></td>
                        <td>
                            <script language="JavaScript1.1" src="jchem/marvin/marvin.js"></script>
                            <script language="JavaScript1.1">
                                <!--
                                function exportMol() {
                                    if(document.MSketch != null) {
                                        var s = document.MSketch.getMol('smiles:');
                                        s = unix2local(s); // Convert "\n" to local line separator
                                        document.getElementById("smiles").value = s;
                                    } else {
                                        alert("Cannot import molecule:\n"+
                                                "no JavaScript to Java communication in your browser.\n");
                                    }
                                }

                                msketch_name = "MSketch";
                                msketch_mayscript = true;
                                msketch_begin("/jchem/marvin/", 440, 300);
                                msketch_end();
                                document.MSketch.style.zIndex="-1";
                                //-->

    <!--                        </script><br /><input type="button" value="Get SMILES" property="text" onclick="exportMol()"/>
                            <input type="button" value="Clear" property="text" onclick="if(document.MSketch!=null) document.MSketch.setMol('');"/>

                        </td>
                    </tr>
                    </tbody>
                </table>
            </td>
			
            <td valign="top">
                <table width="450" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" >
                    <tbody>

                    <tr>
                        <td width="100%" height="24" align="left" colspan="2">
                            <p class="StandardTextDarkGrayParagraph2">
                                <b>Enter</b>
                            </p>
                        </td>
                    </tr>
                    <tr>
                        <td align="left" colspan="2">
                            <p  class="StandardTextDarkGrayParagraph">
                                Enter a molecule in SMILES format, e.g. <b>C1=CC=C(C=C1)CC(C(=O)O)N</b> (phenylalanine).
                                Or, use the applet on the right to draw a molecule, then click "Get SMILES".
                            </p>
                    </tr>
                    <tr>
                        <td width="70" height="24" align="right">
                            <div align="right" class="StandardTextDarkGray"><b>SMILES:</b></div>
                        </td>
                        <td width="150" align="left" valign="top"><input type="text" name="smiles" id="smiles" size="30" value=""/>
                            <span id="messageDiv2"></span></td>
                    </tr>
                    <tr>
                        <td width="70" height="26" align="left">
                            <div align="right" class="StandardTextDarkGray"><b>Similarity Cut
                                Off:</b></div>
                        </td>
                        <td align="left" valign="top"><input type="text" id="cutOffSmiles" size="4" value="0.5" /><span id="messageDiv3"></span></td>
                    </tr>
                    <tr>
                        <td width="70" height="24" align="right">
                            <div align="left" class="StandardTextDarkGray">&nbsp;</div>
                        </td>
                        <td align="left" valign="top"><input type="button" onclick="predictSmiles()" value="Predict" /> <span id="textarea"></span></td>
                    </tr>
                    <tr>
                        <td height="26" align="left" colspan="3">
                            <div class="StandardTextDarkGrayParagraph" id="smilesResults"><i>Your SMILES prediction results will appear here. Prediction will take 3-5 minutes on average per predictor.</i></div>
                        </td>
                        <td align="left" valign="top"><span id="messageDiv2"></span></td>
                    </tr>
                    </tbody>
                </table>
            </td>
			</tr>
        </tbody>
    </table>
	</s:if>
</div>
</div>
        </td>
    </tr>
</table>
</div>
</td>
</tr>
</tbody>
</table>
</div>
<br />
<div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>
</div>
</body>
</html>
