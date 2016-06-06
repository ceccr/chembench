<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Prediction: <s:property value="prediction.name" /></title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Prediction Details: <s:property value="prediction.name" /></h2>

    <div class="list-group">
      <div class="list-group-item">
        <h4 class="list-group-item-heading">General information</h4>
        <dl class="dl-horizontal properties-list">
          <dt>Dataset predicted</dt>
          <dd>
            <s:url var="predictionDatasetUrl" action="datasetDetail">
              <s:param name="id" value="predictionDataset.id" />
            </s:url>
            <s:a href="%{predictionDatasetUrl}" target="_blank"><s:property value="predictionDataset.name" /></s:a>
          </dd>

          <dt>Date created</dt>
          <dd><s:date name="prediction.dateCreated" format="yyyy-MM-dd HH:mm" /></dd>

          <dt>Similarity cutoff used</dt>
          <dd>
            <s:if test="prediction.similarityCutoff == 99999.0">
              <span class="text-muted">Not used</span>
            </s:if>
            <s:else>
              <s:property value="prediction.similarityCutoff" />&sigma;
            </s:else>
          </dd>
        </dl>
      </div>

      <div class="list-group-item">
        <h4 class="list-group-item-heading">Model(s) used</h4>
        <ul>
          <s:iterator value="predictors">
            <li>
              <s:url var="predictorUrl" action="modelDetail">
                <s:param name="id" value="%{id}" />
              </s:url>
              <s:a href="%{predictorUrl}" target="_blank"><s:property value="name" /></s:a>
              (<span class="descriptor-type"><s:property value="descriptorGeneration" /></span>,
              <span class="modeling-method"><s:property value="modelMethod" /></span>)
            </li>
          </s:iterator>
        </ul>
      </div>

      <div class="list-group-item">
        <s:url var="csvDownloadUrl" action="fileServlet" escapeAmp="false">
          <s:param name="id" value="prediction.id" />
          <s:param name="user" value="userName" />
          <s:param name="jobType" value="'PREDICTION'" />
          <s:param name="file" value="'predictionAsCSV'" />
        </s:url>
        <s:a href="%{csvDownloadUrl}" cssClass="btn btn-sm btn-default" role="button">
          <span class="glyphicon glyphicon-save"></span> Download (.csv)
        </s:a>
      </div>
    </div>

    <div class="panel panel-default">
      <div class="panel-heading">
        <h3 class="panel-title">Predictions</h3>
      </div>

      <div class="panel-body">
        <p>The predicted values for the compounds in your dataset are below.</p>

        <p>
          For each predictor, there are two columns. The first column
          contains the prediction. If more than one of the predictor's models were used to make the prediction, the
          average value across all models is displayed, &plusmn; the standard deviation.
        </p>

        <p>
          The second column for each predictor tells how many models'
          predictions were used to calculate the value in the first column. It is often the case that not all of the
          models in a predictor can be used to predict a compound, because the compounds lie outside the cutoff range of
          some of the models.
        </p>
      </div>

      <table id="prediction-values" class="table table-bordered compound-list datatable">
        <thead>
        <tr>
          <s:if test="!predictionDataset.sdfFile.isEmpty()">
            <th colspan="2"><!-- spacer for compound name/structure --></th>
          </s:if>
          <s:else>
            <th><!-- spacer for compound name --></th>
          </s:else>
          <s:iterator value="predictors">
            <th colspan="3"><s:property value="name" /></th>
          </s:iterator>
        </tr>
        <tr>
          <th class="name">Compound Name</th>
          <s:if test="!predictionDataset.sdfFile.isEmpty()">
            <th class="unsortable">Structure</th>
          </s:if>
          <s:iterator value="predictors">
            <th>Prediction</th>
            <s:if test="childType == @edu.unc.ceccr.chembench.global.Constants@NFOLD">
              <th class="unsortable">Predicting Folds</th>
            </s:if>
            <s:else>
              <th class="unsortable">Predicting Models</th>
            </s:else>
            <th>&sigma;</th>
          </s:iterator>
        </tr>
        </thead>
        <tbody>
        <s:iterator value="compoundPredictionValues">
          <tr>
            <td class="name"><s:property value="compound" /></td>
            <s:if test="!predictionDataset.sdfFile.isEmpty()">
              <td class="structure">
                <s:url var="imageUrl" action="imageServlet" escapeAmp="false">
                  <s:param name="user" value="%{predictionDataset.userName}" />
                  <s:param name="projectType" value="'dataset'" />
                  <s:param name="compoundId" value="%{compound}" />
                  <s:param name="datasetName" value="%{predictionDataset.name}" />
                </s:url>
                <img src="<s:property value="imageUrl" />" class="img-thumbnail compound-structure" width="125"
                     height="125" alt="Compound structure">
              </td>
            </s:if>
            <s:if test="predictionValues != null && !predictionValues.isEmpty()">
              <s:iterator value="predictionValues">
                <td>
                  <s:if test="predictedValue != null">
                    <s:property value="predictedValue" />
                    <s:if test="standardDeviation != null">
                      &plusmn; <s:property value="standardDeviation" />
                    </s:if>
                  </s:if>
                  <s:else>
                    <span class="text-muted">Not predicted</span>
                  </s:else>
                </td>
                <td>
                  <s:property value="numModelsUsed" /> / <s:property value="numTotalModels" />
                </td>
                <td>
                  <s:if
                      test="prediction.computeZscore == @edu.unc.ceccr.chembench.global.Constants@YES && zScore != null">
                    <s:property value="zScore" />&sigma;
                  </s:if>
                  <s:else>
                    <span class="text-muted">N/A</span>
                  </s:else>
                </td>
              </s:iterator>
            </s:if>
            <s:else>
              <!-- datatables has no tbody > td colspan support, so we have to add hidden td's -->
              <s:iterator value="predictors" status="status">
                <s:if test="#status.first">
                  <td class="text-muted" colspan="<s:property value="predictors.size() * 3" />">
                    No predictions made for this compound.
                  </td>
                </s:if>
                <s:else>
                  <td class="datatables-spacer"></td>
                </s:else>
                <td class="datatables-spacer"></td>
                <td class="datatables-spacer"></td>
              </s:iterator>
            </s:else>
          </tr>
        </s:iterator>
        </tbody>
      </table>
    </div>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="${pageContext.request.contextPath}/assets/js/jquery.doubleScroll.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/predictionDetail.js"></script>
</body>
</html>
