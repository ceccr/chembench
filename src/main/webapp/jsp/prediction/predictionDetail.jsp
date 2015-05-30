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
            <s:url var="predictionDatasetUrl">
              <s:param name="id" value="prediction.datasetId" />
            </s:url>
            <s:a href="%{predictionDatasetUrl}"><s:property value="prediction.datasetDisplay" /></s:a>
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
              <s:a href="%{predictorUrl}"><s:property value="name" /></s:a>
              (<s:property value="descriptorGeneration" />, <s:property value="modelMethod" />)
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

  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
</body>
</html>
