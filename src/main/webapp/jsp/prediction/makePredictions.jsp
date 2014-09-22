<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" import="java.util.*"%>

<html>
<head>
<title>CHEMBENCH | Select Predictors</title>
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
<link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/smoothness/jquery-ui.min.css" />

<script src="javascript/script.js"></script>
<script language="JavaScript" src="javascript/sortableTable.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
<script language="javascript" src="javascript/modeling.js"></script>
<script src="javascript/predictorFormValidation.js"></script>
<script type="text/javascript" src="javascript/jquery.zclip.min.js"></script>
<script src="javascript/jsme/jsme.nocache.js"></script>
<script language="javascript">
        var usedDatasetNames = new Array(<s:iterator value="userDatasetNames">"<s:property />",</s:iterator>"");
        var usedPredictorNames = new Array(<s:iterator value="userPredictorNames">"<s:property />",</s:iterator>"");
        var usedPredictionNames = new Array(<s:iterator value="userPredictionNames">"<s:property />",</s:iterator>"");
        var usedTaskNames = new Array(<s:iterator value="userTaskNames">"<s:property />",</s:iterator>"");
        var runSmilesPrediction = "false";
        var previousSmilesResults = "";

        function predictSmiles() {
            var smiles = document.getElementById("smiles").value;
            var cutoff = document.getElementById("cutOffSmiles").value;
            var url="makeSmilesPrediction?smiles=" + encodeURIComponent(smiles) + "&cutoff=" + cutoff + "&predictorIds=" + '<s:property value="selectedPredictorIds" />';
            if (smiles == "") {
                  alert("Please sketch a compound or enter a SMILES string.");
                  return false;
            } else {
                //prepare the AJAX object
                var ajaxObject = GetXmlHttpObject();
                ajaxObject.onreadystatechange=function() {
                    if (ajaxObject.readyState==4) {
                      hideLoading();
                      document.getElementById("smilesResults").innerHTML = ajaxObject.responseText + previousSmilesResults;
                      previousSmilesResults = document.getElementById("smilesResults").innerHTML;
                    }
                }

                showLoading("PREDICTING. PLEASE WAIT.");

                //send request
                ajaxObject.open("GET",url,true);
                ajaxObject.send(null);

                return true;
            }
        }

        function jsmeOnLoad() {
            var jsmeApplet = new JSApplet.JSME("jsme-container", "440px", "300px");
            document.JME = jsmeApplet;
        }
</script>
</head>
<body onload="setTabToPrediction();">
  <div id="bodyDIV"></div>
  <!-- used for the "Please Wait..." box. Do not remove. -->
  <div class="outer">
    <div class="includesHeader"><%@ include file="/jsp/main/header.jsp"%></div>
    <div class="includesNavbar"><%@ include file="/jsp/main/centralNavigationBar.jsp"%></div>
    <!--
        <div class="StandardTextDarkGrayParagraph predictionBackground benchAlign"> <div class="homeLeft"> <br /> <br />
        <p style="margin-left:20px"> <b>Chembench Predictor Selection</b> <br /> <br /> Here you may use predictors to
        identify computational hits in external compound libraries. Predictors generated and validated by UNC's
        Molecular Modeling Laboratory are available under <b>Drug Discovery Predictors</b>, <b>ADME Predictors</b>, and
        <b>Toxicity Predictors</b>. Predictors you create using the MODELING tab appear under <b>Private Predictors</b>.
        <br /> <br />For more information about making predictions, see the <a href="/help-prediction">Prediction help
        page</a>. <br /> <br />Click the checkboxes to the left of each predictor you want to use, and hit the "Select
        Predictors" button. Then you may predict the activity of a dataset, a SMILES string, or a molecule sketch. <br
        /> <br /> If you wish to share predictors you have developed with the Chembench community, please contact us at
        <a href="mailto:ceccr@email.unc.edu">ceccr@email.unc.edu</a>. </p> </div> </div>
      -->
    <div>
      <table style="border: 0px solid black">
        <tbody>
          <tr valign="top">
            <td valign="top" style="border: 0px solid black; vertical-align: top">
              <div valign="top" style="margin: 0px; vertical-align: top">
                <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
                  <tr>
                    <td>
                      <div id="tabs">
                        <ul>
                          <li><a href="#selectTab">Select Predictors</a></li>
                          <li><a href="#compTab">Compounds</a></li>
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
                          <s:form theme="simple" action="selectPredictor" enctype="multipart/form-data" method="post">
                            <br />
                            <s:if test="user.showPublicPredictors!='NONE'">
                              <div valign="top" style="width: 550px; margin: 0px; vertical-align: top">
                                <p style="cursor: pointer; font-weight: bold" class="StandardTextDarkGrayParagraph"
                                  onclick="openShutManager(this, 'drugdisc', false, '- Drug Discovery Predictors', '+ Drug Discovery Predictors')">

                                  <b>+ Drug Discovery Predictors</b>
                                </p>
                                <table width="100%" class="sortable" id="drugdisc" style="display: none">
                                  <tr>
                                    <th class="TableRowText01narrow_unsortable">Select</th>
                                    <th class="TableRowText01narrow">Name</th>
                                    <th class="TableRowText01narrow">Date Created</th>
                                    <th class="TableRowText01narrow">Modeling Method</th>
                                    <th class="TableRowText01narrow">Descriptor Type</th>
                                  </tr>
                                  <s:iterator value="userPredictors">
                                    <s:if test="predictorType=='DrugDiscovery'">
                                      <tr>
                                        <td class="TableRowText02narrow"><s:checkbox name="predictorCheckBoxes"
                                            fieldValue="%{id}" /></td>
                                        <td class="TableRowText02narrow"><s:property value="name" /></td>
                                        <td class="TableRowText02narrow"><s:date name="dateCreated"
                                            format="yyyy-MM-dd HH:mm" /></td>
                                        <td class="TableRowText02narrow"><s:property value="modelMethod" /></td>
                                        <s:if test="descriptorGeneration=='UPLOADED'">
                                          <td class="TableRowText02narrow">* <s:property
                                              value="uploadedDescriptorType" />
                                          </td>
                                        </s:if>
                                        <s:else>
                                          <td class="TableRowText02narrow"><s:property value="descriptorGeneration" />
                                          </td>
                                        </s:else>
                                      </tr>
                                    </s:if>
                                  </s:iterator>

                                </table>
                                <p style="cursor: pointer; font-weight: bold" class="StandardTextDarkGrayParagraph"
                                  onclick="openShutManager(this, 'adme', false, '- ADME Predictors', '+ ADME Predictors')">

                                  <b>+ ADME Predictors</b>
                                </p>
                                <table width="100%" class="sortable" id="adme" style="display: none">
                                  <tr>
                                    <th class="TableRowText01narrow_unsortable">Select</th>
                                    <th class="TableRowText01narrow">Name</th>
                                    <th class="TableRowText01narrow">Date Created</th>
                                    <th class="TableRowText01narrow">Modeling Method</th>
                                    <th class="TableRowText01narrow">Descriptor Type</th>
                                  </tr>
                                  <s:iterator value="userPredictors">
                                    <s:if test="predictorType=='ADME'">
                                      <tr>
                                        <td class="TableRowText02narrow"><s:checkbox name="predictorCheckBoxes"
                                            fieldValue="%{id}" /></td>
                                        <td class="TableRowText02narrow"><s:property value="name" /></td>
                                        <td class="TableRowText02narrow"><s:date name="dateCreated"
                                            format="yyyy-MM-dd HH:mm" /></td>
                                        <td class="TableRowText02narrow"><s:property value="modelMethod" /></td>
                                        <s:if test="descriptorGeneration=='UPLOADED'">
                                          <td class="TableRowText02narrow">* <s:property
                                              value="uploadedDescriptorType" />
                                          </td>
                                        </s:if>
                                        <s:else>
                                          <td class="TableRowText02narrow"><s:property value="descriptorGeneration" />
                                          </td>
                                        </s:else>
                                      </tr>
                                    </s:if>
                                  </s:iterator>
                                </table>

                                <p style="cursor: pointer; font-weight: bold" class="StandardTextDarkGrayParagraph"
                                  onclick="openShutManager(this, 'toxicity', false, '- Toxicity Predictors', '+ Toxicity Predictors')">

                                  <b>+ Toxicity Predictors</b>
                                </p>
                                <table width="100%" class="sortable" id="toxicity" style="display: none">
                                  <tr>
                                    <th class="TableRowText01narrow_unsortable">Select</th>
                                    <th class="TableRowText01narrow">Name</th>
                                    <th class="TableRowText01narrow">Date Created</th>
                                    <th class="TableRowText01narrow">Modeling Method</th>
                                    <th class="TableRowText01narrow">Descriptor Type</th>
                                  </tr>
                                  <s:iterator value="userPredictors">
                                    <s:if test="predictorType=='Toxicity'">
                                      <tr>
                                        <td class="TableRowText02narrow"><s:checkbox name="predictorCheckBoxes"
                                            fieldValue="%{id}" /></td>
                                        <td class="TableRowText02narrow"><s:property value="name" /></td>
                                        <td class="TableRowText02narrow"><s:date name="dateCreated"
                                            format="yyyy-MM-dd HH:mm" /></td>
                                        <td class="TableRowText02narrow"><s:property value="modelMethod" /></td>
                                        <s:if test="descriptorGeneration=='UPLOADED'">
                                          <td class="TableRowText02narrow">* <s:property
                                              value="uploadedDescriptorType" />
                                          </td>
                                        </s:if>
                                        <s:else>
                                          <td class="TableRowText02narrow"><s:property value="descriptorGeneration" />
                                          </td>
                                        </s:else>
                                      </tr>
                                    </s:if>
                                  </s:iterator>
                                </table>
                            </s:if>
                            <p style="cursor: pointer; font-weight: bold" class="StandardTextDarkGrayParagraph"
                              onclick="openShutManager(this, 'private', false, '- Private Predictors', '+ Private Predictors')">

                              <b>+ Private Predictors</b>
                            </p>
                            <table width="100%" class="sortable" id="private" style="display: none">
                              <tr>
                                <th class="TableRowText01narrow_unsortable">Select</th>
                                <th class="TableRowText01narrow">Name</th>
                                <th class="TableRowText01narrow">Date Created</th>
                                <th class="TableRowText01narrow">Modeling Method</th>
                                <th class="TableRowText01narrow">Descriptor Type</th>
                              </tr>
                              <s:iterator value="userPredictors">
                                <s:if test="predictorType=='Private'">
                                  <tr>
                                    <td class="TableRowText02narrow"><s:checkbox name="predictorCheckBoxes"
                                        fieldValue="%{id}" /></td>
                                    <td class="TableRowText02narrow"><s:property value="name" /></td>
                                    <td class="TableRowText02narrow"><s:date name="dateCreated"
                                        format="yyyy-MM-dd HH:mm" /></td>
                                    <td class="TableRowText02narrow"><s:property value="modelMethod" /></td>
                                    <s:if test="descriptorGeneration=='UPLOADED'">
                                      <td class="TableRowText02narrow">* <s:property value="uploadedDescriptorType" />
                                      </td>
                                    </s:if>
                                    <s:else>
                                      <td class="TableRowText02narrow"><s:property value="descriptorGeneration" />
                                      </td>
                                    </s:else>
                                  </tr>
                                </s:if>
                              </s:iterator>
                            </table>
                            <table>
                              <tr>
                                <td>&nbsp;&nbsp;&nbsp;&nbsp; <s:submit value="Select Predictors" />
                                </td>
                              </tr>
                            </table>
                        </div>
                        </s:form>
                      </div>

                      <div id="compTab">
                        <s:form action="makeDatasetPrediction" enctype="multipart/form-data" theme="simple">
                          <table width="924" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4"
                            colspan="2" style="border: 0pt solid black">

                            <tbody>
                              <tr>
                                <td colspan="2">
                                  <div class="StandardTextDarkGray">
                                    <b>Chosen Predictors:</b>
                                  </div>
                                  <div class="StandardTextDarkGray">
                                    <table width="100%" class="sortable" id="private">
                                      <tr>
                                        <th class="TableRowText01">Name
                                        </td>
                                        <th class="TableRowText01">Date Created</th>
                                        <th class="TableRowText01">Modeling Method</th>
                                        <th class="TableRowText01">Descriptor Type</th>
                                      </tr>
                                      <s:iterator value="selectedPredictors">
                                        <tr>
                                          <td class="TableRowText02"><s:property value="name" /></td>
                                          <td class="TableRowText02"><s:date name="dateCreated"
                                              format="yyyy-MM-dd HH:mm" /></td>
                                          <td class="TableRowText02"><s:property value="modelMethod" /></td>
                                          <s:if test="descriptorGeneration=='UPLOADED'">
                                            <td class="TableRowText02">* <s:property value="uploadedDescriptorType" />
                                            </td>
                                          </s:if>
                                          <s:else>
                                            <td class="TableRowText02"><s:property value="descriptorGeneration" />
                                            </td>
                                          </s:else>
                                        </tr>
                                      </s:iterator>
                                    </table>
                                  </div>
                                </td>
                              </tr>

                              <s:if test="%{singleCompoundPredictionAllowed}">
                                <tr>
                                  <td style="text-align: left" colspan="2"><b
                                    class="StandardTextDarkGrayParagraph2" style="color: blue">Select a Dataset</b></td>
                                </tr>
                              </s:if>
                              <tr>
                                <td colspan="2"><table style="border: 0pt solid black">
                                    <tbody>
                                      <tr>
                                        <td height="26" width="115" align="left">
                                          <div align="left" class="StandardTextDarkGray">
                                            <b>Select a Dataset:</b>
                                          </div>
                                        </td>
                                        <td align="left" valign="top" colspan="1"><s:if
                                            test="%{userDatasets.size()>
                                        0}">
                                            <s:select name="selectedDatasetId" list="userDatasets" id="selectedDataset"
                                              listKey="id" listValue="name" />
                                            <input type="button" value="View Dataset" property="text"
                                              onclick="window.open('viewDataset?id='+document.getElementById('selectedDataset').value)" />
                                          </s:if> <s:else>
                                            <div class="StandardTextDarkGrayParagraph">
                                              <i> There is no datasets with descriptors. Use the "DATASET" page to
                                                create datasets. </i>
                                            </div>
                                          </s:else>
                                          <div class="StandardTextDarkGrayParagraph">
                                            <i>(Use the "DATASET" page to create datasets.)</i>
                                          </div></td>
                                      </tr>
                                      <tr>
                                        <td height="26" width="115" align="left">
                                          <div align="left" id="datasetCutoff" class="StandardTextDarkGray">
                                            <b>Applicability Cut Off:</b>
                                          </div>
                                          <div id="cutoff_hint"
                                            style="display: none; border: #FFF solid 1px; width: 300px; height: 300px; position: absolute">Global
                                            Applicability Domain Similarity Cut Off</div>
                                        </td>
                                        <td align="left" valign="top"><s:select name="cutOff" id="cutOff"
                                            theme="simple"
                                            list="#{'99999':'Do not use','3':'3\u03c3','2':'2\u03c3','1':'1\u03c3','0':'0\u03c3'}"
                                            value="N/A" /> <span id="messageDiv2"></span></td>
                                      </tr>
                                      <tr>
                                        <td height="26" width="115" align="left">
                                          <div align="left" class="StandardTextDarkGray">
                                            <b>Prediction Name:</b>
                                          </div>
                                        </td>
                                        <td width="400" align="left" valign="top"><s:textfield name="jobName"
                                            id="jobName" size="19" /> <span id="messageDiv1"></span></td>
                                      </tr>
                                      <tr>
                                        <td align="left"><s:hidden name="selectedPredictorIds" /></td>
                                        <td align="left" valign="top"><input type="button" name="userAction"
                                          id="userAction"
                                          onclick="if(validateObjectNames(document.getElementById('jobName').value, usedDatasetNames, usedPredictorNames, usedPredictionNames, usedTaskNames)){ submitForm3(this); }"
                                          value="Submit Prediction Job" /> <span
                                          id="textarea"></span></td>
                                      </tr>
                                    </tbody>
                                  </table></td>
                              </tr>
                              </s:form>
                              <s:if test="%{singleCompoundPredictionAllowed}">
                                <tr>
                                  <td style="text-align: left; border: 0pt solid black" colspan="2">
                                    <p>
                                      <br /> <b class="StandardTextDarkGrayParagraph2" style="color: blue">Or Enter
                                        a Compound</b> <b class="StandardTextDarkGray" style="color: blue"> (Sketch a
                                        Compound Or Enter a SMILES String)</b>
                                    </p>
                                  </td>
                                </tr>

                                <tr>
                                  <td valign="top" style="vertical-align: top">
                                    <table width="450" frame="border" rules="none" align="center" cellpadding="0"
                                      cellspacing="4">
                                      <tbody>
                                        <tr>
                                          <div id="jsme-container"></div>
                                        </tr>
                                        <tr>
                                          <td><input id="get-smiles-and-predict" type="button"
                                            value="Get SMILES and Predict"> <input id="clear-canvas"
                                            type="button" value="Clear"></td>
                                        </tr>
                                      </tbody>
                                    </table>
                                  </td>

                                  <td valign="top" style="vertical-align: top">
                                    <table width="450" frame="border" rules="none" align="center" cellpadding="0"
                                      cellspacing="4">
                                      <tbody>
                                        <tr>
                                          <td align="left" colspan="2">
                                            <p class="StandardTextDarkGrayParagraph">
                                              Enter a molecule in SMILES format, e.g. <b>C1=CC=C(C=C1)CC(C(=O)O)N</b>
                                              (phenylalanine). Or, use the applet on the left to draw a molecule, then
                                              click "Get SMILES and Predict".
                                            </p>
                                        </tr>
                                        <tr>
                                          <td width="200" height="24" align="right">
                                            <div align="right" class="StandardTextDarkGray">
                                              <b>SMILES:</b>
                                            </div>
                                          </td>
                                          <td align="left" valign="top"><input type="text" name="smiles"
                                            id="smiles" size="30" value="" /> <span id="messageDiv2"></span></td>
                                        </tr>
                                        <tr>
                                          <td width="200" height="26" align="left" colspan="2">
                                            <p class="StandardTextDarkGray">
                                              <b>Applicability Cut Off:</b>
                                              <s:select name="cutOffSmiles" id="cutOffSmiles" theme="simple"
                                                list="#{'N/A':'Do not use','3':'3\u03c3','2':'2\u03c3','1':'1\u03c3','0':'0\u03c3'}"
                                                value="N/A" />
                                              <span id="messageDiv3"></span> <input type="button"
                                                onclick="predictSmiles()" value="Predict" />
                                            </p>
                                          </td>
                                        </tr>
                                      </tbody>
                                    </table>
                                  </td>
                                </tr>
                                <tr>
                                  <td style="text-align: left;" align="right" valign="top">
                                    <div style="position: relative; vertical-align: right">
                                      <input id='copy' type="button" value="Copy Results" />
                                    </div> <span id="textarea"></span>
                                  </td>
                                </tr>
                                <tr>
                                  <td height="26" align="left" colspan="3">
                                    <div class="StandardTextDarkGrayParagraph" id="smilesResults"
                                      style="height: 300px; overflow: auto;">
                                      <i> Your SMILES prediction results will appear here. Prediction will take 3-5
                                        minutes on average per predictor. </i>
                                    </div>
                                  </td>
                                  <td align="left" valign="top"><span id="messageDiv2"></span></td>
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
    <div class="includes"><%@ include file="/jsp/main/footer.jsp"%></div>
  </div>
  <script>
        $(document).ready(function() {
            $("#tabs").tabs({ active: 1 });

            $("#copy").zclip({
                path: "javascript/ZeroClipboard.swf",
                copy: function() { return $("#smilesResults").text().trim(); },
            });

            $("#get-smiles-and-predict").click(function(event) {
                event.preventDefault();
                $("#smiles").val(document.JME.smiles());
                predictSmiles();
            });

            $("#clear-canvas").click(function(event) {
                event.preventDefault();
                document.JME.reset();
            });
        });
    </script>
</body>
</html>
