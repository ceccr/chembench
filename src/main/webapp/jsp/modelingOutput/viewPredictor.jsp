<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
  <sx:head />
  <title>CHEMBENCH | View Predictor</title>

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

  <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
  <script language="JavaScript" src="javascript/chembench.js"></script>
  <script language="JavaScript" src="javascript/sortableTable-delay.js"></script>

  <script language="JavaScript">

    function replaceTabContents(divId, newUrl) {
      //updates the contents of a div with data loaded from the given url
      //for example, when a user changes which external fold they are looking at inside of a tab
      //prepare the AJAX object
      var ajaxObject = GetXmlHttpObject();
      ajaxObject.onreadystatechange = function() {
        if (ajaxObject.readyState == 4) {
          hideLoading();
          document.getElementById(divId).innerHTML = ajaxObject.responseText;
          sortables_init_delay();
        }
      };
      showLoading("LOADING. PLEASE WAIT.");

      //send request
      ajaxObject.open("GET", newUrl, true);
      ajaxObject.send(null);
      return true;
    }
  </script>

</head>

<body onload="setTabToMyBench();">

<!-- End Header Info -->
<div class="outer">
  <div class="includesHeader">
    <%@include file="/jsp/main/header.jsp" %>
  </div>
  <div class="includesNavbar">
    <%@include file="/jsp/main/centralNavigationBar.jsp" %>
  </div>

  <div id="maincontent">
    <table width="924" align="center">
      <tr>
        <td>
          <div class="StandardTextDarkGray">
            <br /> <b>Predictor Name: </b>
            <s:property value="selectedPredictor.name" />
            <br /> <b>Dataset: </b><a
              href="viewDataset?id=<s:property value="selectedPredictor.datasetId" />"><s:property
              value="selectedPredictor.datasetDisplay" /></a><br /> <b>Date Created: </b>
            <s:date name="selectedPredictor.dateCreated" format="yyyy-MM-dd HH:mm" />
            <br /> <b>Modeling Method: </b>
            <s:property value="selectedPredictor.modelMethod" />
            <br /> <b>Descriptor Generation Method: </b>
            <s:property value="selectedPredictor.descriptorGeneration" />
            <br />
          </div>
          <s:if test="editable=='YES'">
            <br />
            <s:form action="updatePredictor" enctype="multipart/form-data" theme="simple">
              <div class="StandardTextDarkGray">
                <b>Description: </b>
              </div>
              <s:textarea id="predictorDescription" name="predictorDescription" align="left"
                          cssStyle="height: 50px; width: 50%" />
              <br />

              <div class="StandardTextDarkGray">
                <b>Paper Reference: </b>
              </div>
              <s:textarea id="predictorReference" name="predictorReference" align="left"
                          cssStyle="height: 50px; width: 50%" />
              <br />
              <input type="button" name="userAction" id="userAction" onclick="this.form.submit()"
                     value="Save Changes" />
              <s:hidden id="objectId" name="objectId" />
            </s:form>
          </s:if>
          <s:else>
            <div class="StandardTextDarkGray">
              <br /> <b>Description: </b>
              <s:property value="selectedPredictor.description" />
              <br /> <b>Paper Reference: </b>
              <s:property value="selectedPredictor.paperReference" />
              <br />
              <s:if test="selectedPredictor.userName!='all-users'||user.isAdmin=='YES'">
                <!-- display edit link -->
                <a href="viewPredictor?id=<s:property value="selectedPredictor.id" />&editable=YES">Edit
                  description and reference</a>
                <br />
              </s:if>
            </div>
          </s:else> <br /> <a
            href="fileServlet?id=<s:property value="selectedPredictor.id" />&user=<s:property value="userName" />&jobType=MODELING&file=externalPredictionsAsCSV">Download
          External Validation Results (CSV)</a> <br /> <a href="jobs#predictors">Back to Predictors</a>

        </td>
      </tr>
    </table>
    <s:if test="selectedPredictor.userName=='all-users'">
      <br />

      <p class="StandardTextDarkGrayParagraph">
        <b>Predictor Description:</b>
        <s:property value="selectedPredictor.description" />
      </p>

      <p class="StandardTextDarkGrayParagraph">For this and the other public predictors, there is no detailed
        information on the external set or models available for display.</p>
    </s:if> <!-- End description --> <br /> <!-- load tabs --> <a name="tabs"></a>

    <div id="bodyDIV"></div>
    <!-- used for the "Please Wait..." box. Do not remove. -->
    <script type="text/javascript">
      dojo.event.topic.subscribe('/modelingTabSelect', function(tab, tabContainer) {
        //alert("Tab "+ tab.widgetId + " was selected");
        sortables_init_delay();
      });
    </script>
    <!-- end script -->
    <s:property value="selectedTab" />
    <sx:tabbedpanel id="viewPredictionTabs"
                    afterSelectTabNotifyTopics="/modelingTabSelect">
      <s:url id="externalValidationLink" value="/viewPredictorExternalValidationSection" includeParams="none">
        <s:param name="id" value='selectedPredictor.id' />
      </s:url>
      <sx:div href="%{externalValidationLink}" id="externalValidationDiv" label="External Validation" theme="ajax"
              loadingText="Loading external validation..." executeScripts="true" preload="false" showLoadingText="true">
      </sx:div>

      <s:if test="selectedPredictor.modelMethod=='KNN'">

        <s:url id="modelsLink" value="/viewPredictorKnnModelsSection" includeParams="none">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'NO'" />
        </s:url>
        <sx:div href="%{modelsLink}" id="modelsDiv" label="Models" theme="ajax" loadingText="Loading models..."
                executeScripts="true" preload="false" showLoadingText="true">
        </sx:div>

        <s:url id="yRandomLink" value="/viewPredictorKnnModelsSection" includeParams="none">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'YES'" />
        </s:url>
        <sx:div href="%{yRandomLink}" id="yRandomDiv" label="Y-Randomization" theme="ajax"
                loadingText="Loading Y-Randomization Models..." executeScripts="true" preload="false"
                showLoadingText="true">
        </sx:div>
      </s:if>
      <s:elseif test="selectedPredictor.modelMethod=='KNN-GA' || selectedPredictor.modelMethod=='KNN-SA'">

        <s:url id="modelsLink" value="/viewPredictorKnnPlusModelsSection" includeParams="none">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'NO'" />
        </s:url>
        <sx:div href="%{modelsLink}" id="modelsDiv" label="Models" theme="ajax" loadingText="Loading models..."
                executeScripts="true" preload="false" showLoadingText="true">
        </sx:div>

        <s:url id="yRandomLink" value="/viewPredictorKnnPlusModelsSection" includeParams="none">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'YES'" />
        </s:url>
        <sx:div href="%{yRandomLink}" id="yRandomDiv" label="Y-Randomization" theme="ajax"
                loadingText="Loading Y-Randomization Models..." executeScripts="true" preload="false"
                showLoadingText="true">
        </sx:div>
      </s:elseif>

      <s:elseif test="selectedPredictor.modelMethod=='RANDOMFOREST'">
        <s:url id="treesLink" value="/viewPredictorRandomForestTreesSection" includeParams="none">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'NO'" />
        </s:url>
        <sx:div href="%{treesLink}" id="treesDiv" label="Trees" theme="ajax" loadingText="Loading trees..."
                executeScripts="true" preload="false" showLoadingText="true">
        </sx:div>

        <s:url id="treesYRandomLink" value="/viewPredictorRandomForestTreesSection" includeParams="none">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'YES'" />
        </s:url>
        <sx:div href="%{treesYRandomLink}" id="randomTreesDiv" label="Y-Randomized Trees" theme="ajax"
                loadingText="Loading trees..." executeScripts="true" preload="false" showLoadingText="true">
        </sx:div>

      </s:elseif>

      <s:elseif test="selectedPredictor.modelMethod=='SVM'">

        <s:url id="modelsLink" value="/viewPredictorSvmModelsSection" includeParams="none">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'NO'" />
        </s:url>
        <sx:div href="%{modelsLink}" id="modelsDiv" label="Models" theme="ajax" loadingText="Loading models..."
                executeScripts="true" preload="false" showLoadingText="true">
        </sx:div>

        <s:url id="yRandomLink" value="/viewPredictorSvmModelsSection" includeParams="none">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'YES'" />
        </s:url>
        <sx:div href="%{yRandomLink}" id="yRandomDiv" label="Y-Randomization" theme="ajax"
                loadingText="Loading Y-Randomization Models..." executeScripts="true" preload="false"
                showLoadingText="true">
        </sx:div>

      </s:elseif>

      <!-- All modeling methods should display their parameters at the end. -->
      <s:url id="parametersLink" value="/viewPredictorParametersSection" includeParams="none">
        <s:param name="id" value='selectedPredictor.id' />
      </s:url>
      <sx:div href="%{parametersLink}" id="parametersDiv" label="Modeling Parameters" theme="ajax"
              loadingText="Loading parameters..." preload="false" showLoadingText="true">
      </sx:div>

    </sx:tabbedpanel> <!-- end load tabs -->
  </div>
  <div class="includes">
    <%@include file="/jsp/main/footer.jsp" %>
  </div>
</div>
</body>
</html>
