<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<!DOCTYPE html>
<html>
<head>
<title>CHEMBENCH | View Prediction</title>
<%@ include file="/jsp/main/head.jsp"%>
<sx:head />
</head>

<body onload="setTabToMyBench();">

  <div class="outer">

    <div class="includesHeader"><%@ include file="/jsp/main/header.jsp"%></div>
    <div class="includesNavbar"><%@ include file="/jsp/main/centralNavigationBar.jsp"%></div>

    <span id="maincontent" style="overflow: auto;">
      <table width="924" align="center">
        <tr>
          <td>
            <div class="StandardTextDarkGray">
              <br /> <b>Prediction Name: </b>
              <s:property value="prediction.name" />
              <br /> <b>Dataset Predicted: </b><a href="viewDataset?id=<s:property value="prediction.datasetId" />"><s:property
                  value="prediction.datasetDisplay" /></a><br /> <b>Predictors Used:</b>
              <s:iterator value="predictors" status="predictorsStatus1">
                <s:url id="predictorLink" value="/viewPredictor" includeParams="none">
                  <s:param name="id" value='id' />
                </s:url>
                <s:a href="%{predictorLink}">
                  <s:property value="name" />
                </s:a>
            </s:iterator>
              <br /> <b>Date Created: </b>
              <s:date name="prediction.dateCreated" format="yyyy-MM-dd HH:mm" />
              <br /> <b>Similarity Cutoff: </b>
              <s:if test="prediction.similarityCutoff==99999.0"> N/A </s:if>
              <s:elseif test="prediction.similarityCutoff==0.0"> 0&sigma; </s:elseif>
              <s:elseif test="prediction.similarityCutoff==1.0"> 1&sigma; </s:elseif>
              <s:elseif test="prediction.similarityCutoff==2.0"> 2&sigma; </s:elseif>
              <s:elseif test="prediction.similarityCutoff==3.0"> 3&sigma; </s:elseif>
              <br /> <br /> <a
                href="fileServlet?id=<s:property value="prediction.id" />&user=<s:property value="userName" />&jobType=PREDICTION&file=predictionAsCSV">Download
                This Prediction Result (CSV)</a> <br /> <a href="jobs#predictions">Back to Predictions</a>
            </div>
          </td>
        </tr>
      </table> <!-- End Header Info --> <!-- Page description -->
      <p class="StandardTextDarkGray" width="550">The predicted values for the compounds in your dataset are below.</p>

      <p class="StandardTextDarkGray" width="550">For each predictor, there are two columns. The first column
        contains the prediction. If more than one of the predictor's models were used to make the prediction, the
        average value across all models is displayed, &plusmn; the standard deviation.</p>

      <p class="StandardTextDarkGray" width="550">The second column for each predictor tells how many models'
        predictions were used to calculate the value in the first column. It is often the case that not all of the
        models in a predictor can be used to predict a compound, because the compounds lie outside the cutoff range of
        some of the models.</p>

      <p class="StandardTextDarkGray" width="550">
        You can click on a compound's sketch to enlarge it. Note: if the sketch applet does not load, your Java version
        may be out of date. You can download an updated version <a href="https://www.java.com/en/download/index.jsp">here</a>.
      </p> <!-- End page description --> <!-- load tabs --> <a name="tabs"></a>
      <div id="bodyDIV"></div> <!-- used for the "Please Wait..." box. Do not remove. --> <sx:tabbedpanel
        id="viewPredictionTabs">
        <s:if test="prediction.similarityCutoff==0.0">
          <s:url id="predictionsLink" value="/viewPredictionPredictionsSection" includeParams="none">
            <s:param name="currentPageNumber" value='currentPageNumber' />
            <s:param name="orderBy" value='orderBy' />
            <s:param name="id" value='objectId' />
            <s:param name="cutoff" value='' />
          </s:url>
        </s:if>
        <s:else>
          <s:url id="predictionsLink" value="/viewPredictionPredictionsSection" includeParams="none">
            <s:param name="currentPageNumber" value='currentPageNumber' />
            <s:param name="orderBy" value='orderBy' />
            <s:param name="id" value='objectId' />
            <s:param name="cutoff" value='prediction.similarityCutoff' />
          </s:url>
        </s:else>

        <sx:div href="%{predictionsLink}" id="predictionValuesDiv" label="Prediction Values" theme="ajax"
          loadingText="Loading predictions..." showLoadingText="true" preload="false"></sx:div>
      </sx:tabbedpanel> <!-- end load tabs -->
    </span>
    <div id="image_hint" style="display: none; border: #FFF solid 1px; width: 300px; height: 300px; position: absolute">
      <img src="" width="300" height="300" />
    </div>
    <div class="includes"><%@ include file="/jsp/main/footer.jsp"%></div>
  </div>

  <script src="javascript/script.js"></script>
  <script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
  <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js"></script>
  <script src="javascript/jquery.doubleScroll.js"></script>
  <script src="javascript/viewPrediction.js"></script>
</body>
</html>
