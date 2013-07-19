<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<%@page language="java" import="java.util.*" %>
<html>
<head>
<sx:head debug="false" cache="false" compressed="true" />
<title>CHEMBENCH | Modeling</title>

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
<script src="javascript/script.js"></script>
<script src="javascript/dataset.js"></script>
<script language="javascript"
        src="javascript/jquery-1.6.4.min.js"></script>

<script language="javascript">
var usedDatasetNames = new Array(<s:iterator value="userDatasetNames">"<s:property />",</s:iterator>"");
var usedPredictorNames = new Array(<s:iterator value="userPredictorNames">"<s:property />",</s:iterator>"");
var usedPredictionNames = new Array(<s:iterator value="userPredictionNames">"<s:property />",</s:iterator>"");
var usedTaskNames = new Array(<s:iterator value="userTaskNames">"<s:property />",</s:iterator>"");

var datasetId = -1;
var selectedDatasetNumCompounds = -1;
var selectedDatasetAvailableDescriptors = "";
var selectedDatasetHasBeenScaled = "";


function getSelectedDataset(){
    //get the numCompounds and availableDescriptors for the currently selected dataset
    if(document.getElementById("categoryDataset").checked==true){
        datasetId = document.getElementById("selectedCategoryDataset").value;
        <s:iterator value="userCategoryDatasets">
        if(datasetId==<s:property value="id" />){
            selectedDatasetNumCompounds = <s:property value='numCompound' />;
            selectedDatasetAvailableDescriptors = "<s:property value='availableDescriptors' />";
            selectedDatasetHasBeenScaled = "<s:property value='hasBeenScaled' />";
        }
        </s:iterator>
    }
    else{
        datasetId = document.getElementById("selectedContinuousDataset").value;
        <s:iterator value="userContinuousDatasets">
        if(datasetId==<s:property value="id" />){
            selectedDatasetNumCompounds = <s:property value='numCompound' />;
            selectedDatasetAvailableDescriptors = "<s:property value='availableDescriptors' />";
            selectedDatasetHasBeenScaled = "<s:property value='hasBeenScaled' />";
        }
        </s:iterator>
    }

    //enable / disable based on the availableDescriptors
    //these are ordered based on defaults (e.g. if uploaded is available, it will be the default checked)
    if(selectedDatasetAvailableDescriptors.indexOf("MOE2D") > -1){
        document.getElementById("descriptorGenerationType" + "MOE2D").disabled = false;
        document.getElementById("descriptorGenerationType" + "MOE2D").checked = "checked";
    }
    else{
        document.getElementById("descriptorGenerationType" + "MOE2D").disabled = true;
    }

    if(selectedDatasetAvailableDescriptors.indexOf("MACCS") > -1){
        document.getElementById("descriptorGenerationType" + "MACCS").disabled = false;
        document.getElementById("descriptorGenerationType" + "MACCS").checked = "checked";
    }
    else{
        document.getElementById("descriptorGenerationType" + "MACCS").disabled = true;
    }

    if(selectedDatasetAvailableDescriptors.indexOf("DRAGONNOH") > -1){
        document.getElementById("descriptorGenerationType" + "DRAGONNOH").disabled = false;
        document.getElementById("descriptorGenerationType" + "DRAGONNOH").checked = "checked";
    }
    else{
        document.getElementById("descriptorGenerationType" + "DRAGONNOH").disabled = true;
    }

    if(selectedDatasetAvailableDescriptors.indexOf("DRAGONH") > -1){
        document.getElementById("descriptorGenerationType" + "DRAGONH").disabled = false;
        document.getElementById("descriptorGenerationType" + "DRAGONH").checked = "checked";
    }
    else{
        document.getElementById("descriptorGenerationType" + "DRAGONH").disabled = true;
    }

    if(selectedDatasetAvailableDescriptors.indexOf("MOLCONNZ") > -1){
        document.getElementById("descriptorGenerationType" + "MOLCONNZ").disabled = false;
        document.getElementById("descriptorGenerationType" + "MOLCONNZ").checked = "checked";
    }
    else{
        document.getElementById("descriptorGenerationType" + "MOLCONNZ").disabled = true;
    }

    if(selectedDatasetAvailableDescriptors.indexOf("CDK") > -1){
        document.getElementById("descriptorGenerationType" + "CDK").disabled = false;
        document.getElementById("descriptorGenerationType" + "CDK").checked = "checked";
    }
    else{
        document.getElementById("descriptorGenerationType" + "CDK").disabled = true;
    }

    if(selectedDatasetAvailableDescriptors.indexOf("UPLOADED") > -1){
        document.getElementById("descriptorGenerationType" + "UPLOADED").disabled = false;
        document.getElementById("descriptorGenerationType" + "UPLOADED").checked = "checked";
    }
    else{
        document.getElementById("descriptorGenerationType" + "UPLOADED").disabled = true;
    }

    setDescriptorScaling();

    //recalculate time estimate for the newly selected dataset
    calculateRuntimeEstimate();
}


function setDescriptorScaling(){
    //turns scaling options on or off
    //If a user has uploaded scaled descriptors we don't want to scale them any further

    if(document.getElementById("descriptorGenerationType" + "UPLOADED").checked &&
            selectedDatasetHasBeenScaled == "true"){
        document.getElementById("scalingType" + "RANGESCALING").disabled = true;
        document.getElementById("scalingType" + "AUTOSCALING").disabled = true;
        document.getElementById("scalingType" + "NOSCALING").checked = "checked";
    }
    else{
        document.getElementById("scalingType" + "RANGESCALING").disabled = false;
        document.getElementById("scalingType" + "AUTOSCALING").disabled = false;
        document.getElementById("scalingType" + "RANGESCALING").checked = "checked";
    }
}

function calculateRuntimeEstimate(){
    //estimates runtime based on input parameters and displays it.
    //assumes that getDataset has assigned the value of selectedDatasetNumCompounds already.

    var timeEstimateDays = 0;
    var timeEstimateHours = 0;
    var timeEstimateMins = 0;

    var dataSplitMethod = document.getElementById("trainTestSplitType").value;
    var modelMethod = document.getElementById("modelingType").value;

    var numSplits;
    if(dataSplitMethod=="RANDOM"){
        numSplits = document.getElementById("numSplitsInternalRandom").value;
    }
    else{
        //sphere exclusion
        numSplits = document.getElementById("numSplitsInternalSphere").value;
    }

    //Time estimates were generated by taking the results from around 200 jobs
    //then removing outliers and plotting a trendline in Excel. Predictions
    //look pretty accurate in there at least.
    if(modelMethod=="RANDOMFOREST"){
        timeEstimateMins = (selectedDatasetNumCompounds*numSplits*0.003)-5.718;
    }
    else if(modelMethod=="KNN-GA"){
        //var maxNumGenerations = document.getElementById("gaMaxNumGenerations").value;
        timeEstimateMins = (selectedDatasetNumCompounds*numSplits*0.05);
    }
    else if(modelMethod=="KNN-SA"){
        //depends on numRuns and needs a factor for convergence parameters (temperature etc).
        var numRuns = document.getElementById("saNumRuns").value;
        var numBest = document.getElementById("saNumBestModels").value;
        var numDifferentDescriptors = 1;
        var minDesc = document.getElementsByName("knnMinNumDescriptors")[1].value;
        var maxDesc = document.getElementsByName("knnMaxNumDescriptors")[1].value;
        var descSteps = document.getElementById("knnDescriptorStepSize").value;

        if(descSteps != 0){
            numDifferentDescriptors += Math.floor((maxDesc - minDesc)/descSteps);
        }

        timeEstimateMins = numSplits *(numRuns*numBest*numDifferentDescriptors)*selectedDatasetNumCompounds*0.018;
    }
    else if(modelMethod=="SVM"){

        var numDifferentDegrees = Math.floor((document.getElementById("svmDegreeTo").value -
                document.getElementById("svmDegreeFrom").value) / document.getElementById("svmDegreeStep").value + 0.001);
        var numDifferentGammas = Math.floor((document.getElementById("svmGammaTo").value -
                document.getElementById("svmGammaFrom").value) / document.getElementById("svmGammaStep").value + 0.001);
        var numDifferentCosts = Math.floor((document.getElementById("svmCostTo").value -
                document.getElementById("svmCostFrom").value) / document.getElementById("svmCostStep").value + 0.001);
        var numDifferentNus = Math.floor((document.getElementById("svmNuTo").value -
                document.getElementById("svmNuFrom").value) / document.getElementById("svmNuStep").value + 0.001);
        var numDifferentPEpsilons = Math.floor((document.getElementById("svmPEpsilonTo").value -
                document.getElementById("svmPEpsilonFrom").value) / document.getElementById("svmPEpsilonStep").value + 0.001);

        var svmType;
        if(document.getElementById("categoryDataset").checked==true){
            if(document.getElementById("svmTypeCategory0").checked==true){
                svmType = document.getElementById("svmTypeCategory0").value;
            }
            else{
                svmType = document.getElementById("svmTypeCategory1").value;
            }
        }
        else{
            if(document.getElementById("svmTypeContinuous3").checked==true){
                svmType = document.getElementById("svmTypeContinuous3").value;
            }
            else{
                svmType = document.getElementById("svmTypeContinuous4").value;
            }
        }

        if(svmType == '0'){
            numDifferentPEpsilons = 1;
            numDifferentNus = 1;
        }
        else if(svmType == '1'){
            numDifferentPEpsilons = 1;
            numDifferentCosts = 1;
        }
        else if(svmType == '3'){
            numDifferentNus = 1;
        }
        else if(svmType == '4'){
            numDifferentPEpsilons = 1;
        }

        var kernelType;
        if(document.getElementById("svmKernel0").checked==true){
            numDifferentGammas = 1;
            numDifferentDegrees = 1;
        }
        else if(document.getElementById("svmKernel1").checked==true){
            //both gamma and degree are used
        }
        else if(document.getElementById("svmKernel2").checked==true){
            numDifferentDegrees = 1;
        }
        else if(document.getElementById("svmKernel3").checked==true){
            numDifferentDegrees = 1;
        }

        var numModelsPerSplit = numDifferentPEpsilons * numDifferentNus * numDifferentCosts * numDifferentGammas * numDifferentDegrees;

        timeEstimateMins = selectedDatasetNumCompounds * numSplits * numModelsPerSplit * 0.00022;
    }

    var errorMargin = 1.8;
    timeEstimateMins = timeEstimateMins * errorMargin;

    timeEstimateMins = Math.ceil(timeEstimateMins);
    if(timeEstimateMins <= 0){
        timeEstimateMins = 2;
    }


    var timeEstimateString = timeEstimateMins + " minutes";
    if(timeEstimateMins == 1){
        timeEstimateString = timeEstimateMins + " minute";
    }

    if(timeEstimateMins > 120){
        timeEstimateHours = Math.ceil(timeEstimateMins / 60);
        timeEstimateString = timeEstimateHours + " hours";
        if(timeEstimateHours > 48){
            timeEstimateDays =  Math.ceil(timeEstimateHours / 24);
            timeEstimateString = timeEstimateDays + " days";
        }
    }

    document.getElementById("timeEstimateDiv").innerHTML = "<br />This modeling job will take about <b>" + timeEstimateString + "</b> to finish.";
}


</script>
<script language="javascript" src="javascript/script.js"></script>
<script language="javascript" src="javascript/modeling.js"></script>


</head>

<body bgcolor="#ffffff" onload="setTabToModeling(); getSelectedDataset();">
<div class="outer">
<div class="includesHeader"><%@include file="/jsp/main/header.jsp" %></div>
<div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp" %></div>
<div class="StandardTextDarkGrayParagraph">
<div class="modelingBackground" style="margin-left:-18px;margin-right:20px;">
    <div class="homeLeft">
        <br />
        <p class="StandardTextDarkGrayParagraph2">
            <b>Chembench Model Development</b>
        </p>
        <p style="margin-left:20px">
            Here you may develop QSAR
            predictors for any of your datasets. Public datasets are also available.<br /><br />
            For more information about creating predictors and selecting the right parameters, use the
            <a href="/help-modeling">Modeling help page</a>. </p>
        <p class="StandardTextDarkGrayParagraph">The full modeling workflow, as described in our
            <a href="/help-workflows">Workflow help page</a>, is detailed the publication:
            <a href="http://onlinelibrary.wiley.com/doi/10.1002/minf.201000061/full">Tropsha, A. Best Practices for QSAR Model Development,
                Validation, and Exploitation Mol. Inf., 2010, 29, 476-488</a>
        </p>
    </div>
</div>
<s:form action="createModelingJob" enctype="multipart/form-data" theme="simple">

    <!-- Dataset Selection -->
    <div class="border StandardTextDarkGrayParagraph benchAlign">
        <p class="StandardTextDarkGrayParagraph2">
            <br /><b>Select a Dataset</b>
        </p>
        <p class="StandardTextDarkGrayParagraph">
            <i>(Use the "DATASET" page to create datasets.)</i>
        </p>
        <s:hidden id="actFileDataType" name="actFileDataType" />
        <div class="StandardTextDarkGrayParagraph"><b><input type="radio" name="actFileDataTypeRadio" id="continuousDataset" onclick="setToContinuous(); getSelectedDataset()" checked>Choose a Continuous Dataset:</input></b>
            <br /><s:select name="selectedDatasetId" list="userContinuousDatasets" id="selectedContinuousDataset" listKey="id" listValue="name" onchange='getSelectedDataset();' />
        </div>
        <div class="StandardTextDarkGrayParagraph"><b><input type="radio" name="actFileDataTypeRadio" id="categoryDataset" onclick="setToCategory(); getSelectedDataset()">Choose a Category Dataset:</input></b>
            <br /><s:select name="selectedDatasetId" disabled="true" list="userCategoryDatasets" id="selectedCategoryDataset" listKey="id" listValue="name" onchange='getSelectedDataset();' />
        </div>
        <!-- Commented out until it's implemented...
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Weight Categories By:</b></div></td>
					<td align="left" valign="top">
					<div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="categoryWeights" value="categoryWeights" list="#{'INVERSESIZE':'Inverse of Size','NOWEIGHTING':'No Weighting','MANUAL':'Set Manually...'}" /></div>
					</td>
				</tr>
				<tr>
					<td width="100%" colspan="2">
					<div class="StandardTextDarkGrayParagraph" id="submitMessage">
					<i>Weighting will improve modeling on imbalanced datasets. 
					When optimizing the model, the accuracy on members of different categories will be 
					weighted depending on the values you input.</i>
					</div>
					</td>
			    </tr>
			    -->
        <div class="StandardTextDarkGrayParagraph"><br /><input type="button" value="View Dataset" property="text" onclick="showDataset()"/> <i> Opens in a new window. Check your browser settings if the new window does not appear.</i></div>
        <br />
    </div>
    <br />

    <!-- Descriptor Type Selection -->
    <div class="border StandardTextDarkGrayParagraph benchAlign">
        <p class="StandardTextDarkGrayParagraph2">
            <br /><b>Select Descriptors</b>
        </p>

        <div class="StandardTextDarkGrayParagraph"><b>Descriptor Type:</b></div>
        <div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="descriptorGenerationType" onclick="setDescriptorScaling()" id="descriptorGenerationType" value="descriptorGenerationType" list="#{'CDK':'CDK [202 descriptors]<br />', 'MOLCONNZ':'MolconnZ [375 descriptors]<br />','DRAGONH':'Dragon (with hydrogens) [2489 descriptors]<br />','DRAGONNOH':'Dragon (no hydrogens) [900 descriptors]<br />','MACCS':'MACCS [166 descriptors]<br />','MOE2D':'MOE2D [184 descriptors]<br />','UPLOADED':'Uploaded Descriptors<br />'}" /></div>
        <br />


        <div class="StandardTextDarkGrayParagraph"><b>Scale Descriptors Using:</b></div>

        <div class="StandardTextDarkGrayParagraphNoIndent"><s:radio name="scalingType" id="scalingType" value="scalingType" list="#{'RANGESCALING':'Range Scaling','AUTOSCALING':'Auto Scaling','NOSCALING':'None'}" /></div>

        <!-- <tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Minimum Standard Deviation:</b></div>
					</td>
					<td align="left" valign="top"><s:textfield name="stdDevCutoff" id="stdDevCutoff" size="5" /></td>
				</tr>		
				<tr>
					<td colspan="2">
					<div class="StandardTextDarkGrayParagraph"><i>Each descriptor that has values with lower standard deviation than the minimum will be removed.<br /></i></div>
					</td>
				</tr>	 -->

        <div class="StandardTextDarkGrayParagraph"><b>Maximum Correlation:</b></div>
        <s:textfield name="correlationCutoff" id="correlationCutoff" size="5" />
        <div class="StandardTextDarkGrayParagraph"><i>For each pair of descriptors, if the correlation coefficient is above the maximum, one of the two will be removed.<br />In addition, descriptors with zero variance across compounds will always be removed.<br /></i></div>

    </div>
    <br />


    <!-- Internal Data Split Parameters -->
    <div class="border StandardTextDarkGrayParagraph benchAlign">
        <p class="StandardTextDarkGrayParagraph2">
            <b>Choose Internal Data Splitting Method</b>
        </p>
        <br />


        <!-- script sets hidden field so we know which tab was selected -->
        <script type="text/javascript">
            dojo.event.topic.subscribe('/internalDataSplitTypeSelect', function(tab, tabContainer) {
                //alert("Tab "+ tab.widgetId + " was selected");
                document.getElementById("trainTestSplitType").value = tab.widgetId;
                calculateRuntimeEstimate();
            });
        </script>
        <s:hidden id="trainTestSplitType" name="trainTestSplitType"  />
        <!-- end script -->

        <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
            <sx:tabbedpanel id="internalDataSplitTabbedPanel" afterSelectTabNotifyTopics="/internalDataSplitTypeSelect">
                <sx:div id="SPHEREEXCLUSION" theme="ajax" label="Sphere Exclusion" href="/loadSphereInternalSplitSection" loadingText="Loading data splitting parameters...">
                </sx:div>

                <sx:div id="RANDOM" theme="ajax" label="Random Split" href="/loadRandomInternalSplitSection" loadingText="Loading data splitting parameters...">
                </sx:div>
            </sx:tabbedpanel>
        </td></tr></table>
    </div>
    <br />


    <!-- Modeling Method (kNN, kNN+, SVM) -->
    <div class="border StandardTextDarkGrayParagraph benchAlign">
        <p class="StandardTextDarkGrayParagraph2">
            <b>Choose Model Generation Method</b>
        </p>
        <br />

        <!-- script sets hidden field so we know which tab was selected -->
        <script type="text/javascript">
            dojo.event.topic.subscribe('/modelingTypeSelect', function(tab, tabContainer) {
                //alert("Tab "+ tab.widgetId + " was selected");
                document.getElementById("modelingType").value = tab.widgetId;
                changeSvmType();
                calculateRuntimeEstimate();
            });
        </script>
        <s:hidden id="modelingType" name="modelingType" />
        <!-- end script -->

        <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2"><tr><td>
            <sx:tabbedpanel id="modelingTypeTabbedPanel" afterSelectTabNotifyTopics="/modelingTypeSelect" >

                <sx:div id="RANDOMFOREST" value="RANDOMFOREST" theme="ajax" label="Random Forest" href="/loadRFSection" loadingText="Loading randomForest parameters...">
                </sx:div>
                <s:if test="!#session['user'].userName.contains('guest')">
                    <sx:div id="SVM" value="SVM" theme="ajax" label="Support Vector Machines" href="/loadSvmSection" loadingText="Loading SVM parameters...">
                    </sx:div>

                    <sx:div id="KNN-GA" value="KNN-GA" theme="ajax" label="GA-kNN" href="/loadKnnPlusGASection" loadingText="Loading kNN+ parameters...">
                    </sx:div>

                    <sx:div id="KNN-SA" value="KNN-SA" theme="ajax" label="SA-kNN" href="/loadKnnPlusSASection" loadingText="Loading kNN+ parameters...">
                    </sx:div>
                </s:if>
                <!--
                Since knn+ seems to be working well, I'm taking out the original kNN option for generating
                predictors. Predictions can still be made using these older kNN models.

                <sx:div id="KNN" value="KNN" theme="ajax" label="kNN" href="/loadKnnSection" loadingText="Loading kNN parameters...">
                </sx:div>
                -->

            </sx:tabbedpanel>

        </td></tr></table>

    </div>
    <br />


    <!-- Begin Modeling Job -->
    <div class="border StandardTextDarkGrayParagraph benchAlign">
        <p class="StandardTextDarkGrayParagraph2">
            <br /><b>Start Job</b>
        </p>


        <div class="StandardTextDarkGrayParagraph" id="timeEstimateDiv"></div>

        <s:if test="user.getUserName().contains('guest_')">

            <div class="StandardTextDarkGrayParagraph"><br /><b>Send me an email when the job finishes: </b><br /></div>
            <s:checkbox name="emailOnCompletion" id="emailOnCompletion" />

        </s:if>



        <div class="StandardTextDarkGrayParagraph" id="submitMessage">
            <i>Please enter a name for the predictor you are creating.</i>
        </div>



        <div class="StandardTextDarkGrayParagraph"><b>Predictor Name:</b></div>
        <s:textfield name="jobName" id="jobName" size="19"/>

        <input type="button" name="userAction" id="userAction" onclick="if(validateObjectNames(document.getElementById('jobName').value ,usedDatasetNames, usedPredictorNames, usedPredictionNames, usedTaskNames)){ submitForm(this); }"
               value="Submit Modeling Job" /> <span id="textarea"></span> <br />
    </div>
    <br />
</s:form>
<br />
</div>
<div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>

</body>
</html>
