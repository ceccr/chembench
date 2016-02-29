<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sj" uri="/struts-jquery-tags" %>
<!DOCTYPE html>
<html>
<head>
  <sj:head />
  <title>CHEMBENCH | View Predictor</title>

  <link href="theme/ccbStyle.css" rel="stylesheet" type="text/css">
  <link href="theme/ccbStyleNavBar.css" rel="stylesheet" type="text/css">
  <link rel="stylesheet" href="theme/screen.css" type="text/css" media="screen, projection">
  <link rel="stylesheet" href="theme/print.css" type="text/css" media="print">
  <link href="theme/standard.css" rel="stylesheet" type="text/css">
  <link href="theme/links.css" rel="stylesheet" type="text/css">
  <link href="theme/dynamicTab.css" rel="stylesheet" type="text/css">
  <link rel="icon" href="/theme/img/mml.ico" type="image/ico">
  <link href="theme/customStylesheet.css" rel="stylesheet" type="text/css">
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
    </s:if> <!-- End description --> <br />

    <div id="bodyDIV"></div>
    <!-- used for the "Please Wait..." box. Do not remove. -->
    <s:property value="selectedTab" />

    <sj:tabbedpanel id="viewPredictionTabs">
      <s:url id="externalValidationLink" action="viewPredictorExternalValidationSection" escapeAmp="false">
        <s:param name="id" value='selectedPredictor.id' />
      </s:url>
      <sj:tab href="%{externalValidationLink}" label="External Validation" />

      <s:if test="selectedPredictor.modelMethod=='KNN'">
        <s:url id="modelsLink" action="viewPredictorKnnModelsSection" escapeAmp="false">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'NO'" />
        </s:url>
        <sj:tab href="%{modelsLink}" label="Models" />

        <s:url id="yRandomLink" action="viewPredictorKnnModelsSection" escapeAmp="false">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'YES'" />
        </s:url>
        <sj:tab href="%{yRandomLink}" label="Y-Randomization" />
      </s:if>

      <s:elseif test="selectedPredictor.modelMethod=='KNN-GA' || selectedPredictor.modelMethod=='KNN-SA'">
        <s:url id="modelsLink" action="viewPredictorKnnPlusModelsSection" escapeAmp="false">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'NO'" />
        </s:url>
        <sj:tab href="%{modelsLink}" label="Models" />

        <s:url id="yRandomLink" action="viewPredictorKnnPlusModelsSection" escapeAmp="false">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'YES'" />
        </s:url>
        <sj:tab href="%{yRandomLink}" label="Y-Randomization" />
      </s:elseif>

      <s:elseif test="selectedPredictor.modelMethod=='RANDOMFOREST'">
        <s:url id="treesLink" action="viewPredictorRandomForestTreesSection" escapeAmp="false">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'NO'" />
        </s:url>
        <sj:tab href="%{treesLink}" label="Trees" />

        <s:url id="treesYRandomLink" action="viewPredictorRandomForestTreesSection" escapeAmp="false">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'YES'" />
        </s:url>
        <sj:tab href="%{treesYRandomLink}" label="Y-Randomized Trees" />

        <s:url id="descriptorImportanceLink" action="viewPredictorRandomForestDescriptorImportanceSection"
               escapeAmp="false">
          <s:param name="id" value="selectedPredictor.id" />
        </s:url>
        <sj:tab href="%{descriptorImportanceLink}" label="Descriptor Importance" />
      </s:elseif>

      <s:elseif test="selectedPredictor.modelMethod=='SVM'">
        <s:url id="modelsLink" action="viewPredictorSvmModelsSection" escapeAmp="false">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'NO'" />
        </s:url>
        <sj:tab href="%{modelsLink}" label="Models" />

        <s:url id="yRandomLink" action="viewPredictorSvmModelsSection" escapeAmp="false">
          <s:param name="id" value='selectedPredictor.id' />
          <s:param name="isYRandomPage" value="'YES'" />
        </s:url>
        <sj:tab href="%{yRandomLink}" label="Y-Randomization" />
      </s:elseif>

      <!-- All modeling methods should display their parameters at the end. -->
      <s:url id="parametersLink" action="viewPredictorParametersSection" escapeAmp="false">
        <s:param name="id" value="%{selectedPredictor.id}" />
      </s:url>
      <sj:tab href="%{parametersLink}" label="Modeling Parameters" />
    </sj:tabbedpanel> <!-- end load tabs -->
  </div>
  <div class="includes">
    <%@include file="/jsp/main/footer.jsp" %>
  </div>
</div>

<script src="javascript/chembench.js"></script>
<script src="javascript/sortableTable-delay.js"></script>
<script src="javascript/folds.js"></script>
</body>
</html>
