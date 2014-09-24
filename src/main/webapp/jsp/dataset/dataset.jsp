<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page language="java" import="java.util.*"%>

<html>
<head>
<sx:head debug="false" cache="false" compressed="true" />
<title>CHEMBENCH | Dataset Management</title>
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
<script src="javascript/dataset.js"></script>
<script language="javascript" src="javascript/jquery-1.6.4.min.js"></script>

<script language="javascript">

        var usedDatasetNames = new Array(<s:iterator value="userDatasetNames">"<s:property />",</s:iterator>"");
        var usedPredictorNames = new Array(<s:iterator value="userPredictorNames">"<s:property />",</s:iterator>"");
        var usedPredictionNames = new Array(<s:iterator value="userPredictionNames">"<s:property />",</s:iterator>"");
        var usedTaskNames = new Array(<s:iterator value="userTaskNames">"<s:property />",</s:iterator>"");

        function getSelectedDescriptor(){
            if(document.getElementById("newDescriptorName").checked){
                document.getElementById("descriptorNewName").disabled = false;
                document.getElementById("descriptorUsedName").disabled = true;
                document.getElementById("newDescriptorName").checked = "checked";
            }
            else if(document.getElementById("usedDescriptorName").checked){
                document.getElementById("descriptorUsedName").disabled = false;
                document.getElementById("descriptorNewName").disabled = true;
                document.getElementById("usedDescriptorName").checked = "checked";
            }
        }

        function getSelectedDescriptorD(){
            if(document.getElementById("newDescriptorNameD").checked){
                document.getElementById("descriptorNewNameD").disabled = false;
                document.getElementById("descriptorUsedNameD").disabled = true;
                document.getElementById("newDescriptorNameD").checked = "checked";
            }
            else if(document.getElementById("usedDescriptorNameD").checked){
                document.getElementById("descriptorUsedNameD").disabled = false;
                document.getElementById("descriptorNewNameD").disabled = true;
                document.getElementById("usedDescriptorNameD").checked = "checked";
            }
        }
    </script>
</head>
<body onload="setTabToDataset();">
  <div class="outer">
    <div id="bodyDIV"></div>
    <!-- used for the "Please Wait..." box. Do not remove. -->

    <div class="includesHeader"><%@include file="/jsp/main/header.jsp"%></div>
    <div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp"%></div>
    <div class="StandardTextDarkGrayParagraph">
      <div class="datasetBackground" style="margin-left: -18px; margin-right: 20px;">
        <div class="homeLeft">
          <br />
          <p class="StandardTextDarkGrayParagraph2">
            <b> Chembench Dataset Creation</b>
          </p>
          <p style="margin-left: 20px">
            Here, you may create a dataset by uploading compound structures with or without associated activities. The
            activity data is required for building models. <br /> <br /> You can either create a Modeling dataset,
            which has both structures and activities, or a Prediction dataset, which only has structures. <br /> <br />
            Each modeling dataset you create will appear as an option under the "Modeling" tab and under the
            "Prediction" tab. Prediction datasets will only appear under the "Prediction" tab.<br /> <br /> When you
            submit a dataset, chemical structure images will be generated for each compound. A pairwise compound
            similarity matrix will be created and displayed as a heatmap. <br /> <br /> For more information about
            dataset creation and defining external sets, see the <a href="/help-dataset">Dataset help section</a>.
          </p>
        </div>
      </div>
      <s:form action="submitDataset" enctype="multipart/form-data" method="post" theme="simple"
        style="margin-right:20px;margin-left:-18px;">

        <!-- Upload Files -->
        <div class="border">
          <p class="StandardTextDarkGrayParagraph2 boxHeadingText">
            <br /> <b>Upload Dataset Files</b>
          </p>
          <div class="boxDescriptionText">
            <i>Select the type of dataset to create.<br /> For the "Modeling Set" and "Prediction Set", you do not
              need to provide descriptors; Chembench will generate descriptors as needed for visualization, modeling,
              and prediction. <br /> For the "Modeling Set With Descriptors" and "Prediction Set With Descriptors", you
              will need to <br /> upload an <a href="/help-fileformats#X">X</a> file containing the descriptor values.
              <br />
            </i>
          </div>
          <!-- script sets hidden field so we know which tab was selected -->
          <script type="text/javascript">
                dojo.event.topic.subscribe('/datasetTypeSelect', function(tab, tabContainer) {
                    //alert("Tab "+ tab.widgetId + " was selected");
                    document.getElementById("datasetType").value = tab.widgetId;
                    changeDatasetType();
                });
            </script>
          <s:hidden id="datasetType" name="datasetType" />
          <!-- end script -->

          <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
            <tr>
              <td><sx:tabbedpanel id="datasetTypeTabbedPanel" afterSelectTabNotifyTopics="/datasetTypeSelect">

                  <sx:div id="MODELING" href="/loadModelingSection" label="Modeling Set" theme="ajax"
                    loadingText="Loading dataset types...">
                  </sx:div>

                  <sx:div id="PREDICTION" href="/loadPredictionSection" label="Prediction Set" theme="ajax"
                    loadingText="Loading dataset types..." preload="false">
                  </sx:div>

                  <sx:div id="MODELINGWITHDESCRIPTORS" href="/loadModelingWithDescriptorsSection"
                    label="Modeling Set With Descriptors" theme="ajax" loadingText="Loading dataset types...">
                  </sx:div>

                  <sx:div id="PREDICTIONWITHDESCRIPTORS" href="/loadPredictionWithDescriptorsSection"
                    label="Prediction Set With Descriptors" theme="ajax" loadingText="Loading dataset types...">
                  </sx:div>

                </sx:tabbedpanel></td>
            </tr>
          </table>
        </div>
        <br />
        <!-- Define External Set -->
        <div class="border">

          <p class="StandardTextDarkGrayParagraph2 boxHeadingText">
            <br /> <b>Define External Set</b>
          </p>
          <div class="boxDescriptionText">
            <i>A subset of the compounds in the dataset will be reserved for testing of the models you build. If you
              already have a test set defined, use the "Choose Compounds" tab to pick those compounds as your external
              test set. <br />These parameters only apply to modeling sets. <br />
            </i>
          </div>

          <!-- script sets hidden field so we know which tab was selected -->
          <script type="text/javascript">
                dojo.event.topic.subscribe('/splitTypeSelect', function(tab, tabContainer) {
                    //alert("Tab "+ tab.widgetId + " was selected");
                    document.getElementById("splitType").value = tab.widgetId;
                    changeDatasetType();
                });
            </script>
          <s:hidden id="splitType" name="splitType" />
          <!-- end script -->

          <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
            <tr>
              <td><sx:tabbedpanel id="splitTypeTabbedPanel" afterSelectTabNotifyTopics="/splitTypeSelect">

                  <sx:div id="RANDOM" href="/loadAutoExternalSection" label="Random Split" theme="ajax"
                    loadingText="Loading split type...">
                  </sx:div>

                  <sx:div id="USERDEFINED" href="/loadManualExternalSection" label="Choose Compounds" theme="ajax"
                    loadingText="Loading split type..." preload="true">
                  </sx:div>

                  <sx:div id="NFOLD" href="/loadNFoldExternalSection" label="n-Fold Split" theme="ajax"
                    loadingText="Loading split type..." preload="true">
                  </sx:div>

                </sx:tabbedpanel></td>
            </tr>
          </table>
          </td>
          </tr>
          </tbody>
        </div>
        <br />

        <!-- Submit Dataset -->
        <div class="border">

          <div class="StandardTextDarkGrayParagraph2 boxHeadingText">
            <br /> <b>Create Dataset</b>
          </div>
          <div class="boxDescriptionText">
            <br> <i>A job will be started to generate visualizations and chemical sketches for this dataset. <br />
            </i>
          </div>
          <table class="datasetLastTable">
            <tbody>
              <tr>
                <td width="200"><div class='StandardTextDarkGrayParagraph'>
                    <b>Dataset Name:</b>
                  </div></td>
                <td align="left"><s:textfield name="datasetName" id="datasetName" size="40" /></td>
              </tr>
              <tr>
                <td><b class='StandardTextDarkGrayParagraph'>Reference (optional):</b></td>
                <td align="left"><s:textfield name="paperReference" id="paperReference" size="40" /></td>
              </tr>
              <tr>
                <td><b class='StandardTextDarkGrayParagraph'>Description (optional):</b></td>
                <td align="left"><s:textarea name="dataSetDescription" id="dataSetDescription"
                    style="height: 50px; width: 274px;" /></td>
              </tr>
              <tr>
                <td></td>
                <td align="left"><input name="userAction" id="userAction"
                  onclick="if(validateObjectNames(document.getElementById('datasetName').value, usedDatasetNames, usedPredictorNames, usedPredictionNames, usedTaskNames)){ submitForm(this,document.getElementById('datasetName').value); }"
                  value="Create Dataset" type="button" /></td>
              </tr>
              <tr>
                <!-- used for the "Please Wait..." box. Do not remove. -->
                <td colspan="2"><span id="pleaseWaitText"></span>
                  <div id="messageDiv" style="color: red;"></div></td>
              </tr>
            </tbody>
          </table>
        </div>
      </s:form>
    </div>


    <div class="includes"><%@include file="/jsp/main/footer.jsp"%></div>
    <script language="javascript">
    $(document).ready(function() {
        $("#sdfFilePredDesc").live('change',function(e){
            f_name=jQuery.trim($(this).val());
            if(f_name!=""){
                $("#generateImages_trp").show();
            }
        });
        $("#sdfFileModDesc").live('change',function(e){
            f_name=jQuery.trim($(this).val());
            if(f_name!=""){
                $("#generateImages_trm").show();
            }
        });



    });

</script>

  </div>
</body>
</html>
