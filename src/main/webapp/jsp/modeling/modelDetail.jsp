<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Model: <s:property value="selectedPredictor.name" /></title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>
      Model Details: <s:property value="selectedPredictor.name" />
      <s:a action="jobs" anchor="models">
        <button class="btn btn-primary">Back to Models</button>
      </s:a>
    </h2>

    <div class="list-group">
      <div class="list-group-item">
        <h4 class="list-group-item-heading">General information</h4>
        <dl class="dl-horizontal properties-list">
          <dt>Modeling dataset</dt>
          <dd>
            <s:url var="modelingDatasetUrl" action="viewDataset">
              <s:param name="id" value="selectedPredictor.datasetId" />
            </s:url>
            <a href="<s:property value="modelingDatasetUrl" />" target="_blank"><s:property
                value="selectedPredictor.datasetDisplay" /></a>
          </dd>

          <dt>Model type</dt>
          <dd class="modeling-method"><s:property value="selectedPredictor.modelMethod" /></dd>

          <dt>Descriptors used</dt>
          <dd><s:property value="selectedPredictor.descriptorGeneration" /></dd>

          <dt>Date created</dt>
          <dd><s:date name="selectedPredictor.dateCreated" format="yyyy-MM-dd HH:mm" /></dd>
        </dl>
      </div>

      <div class="list-group-item">
        <h4 class="list-group-item-heading">External validation results</h4>

        <s:url var="externalValidationResults" action="fileServlet" escapeAmp="false">
          <s:param name="id" value="selectedPredictor.id" />
          <s:param name="user" value="selectedPredictor.userName" />
          <s:param name="jobType" value="'MODELING'" />
          <s:param name="file" value="'externalPredictionsAsCSV'" />
        </s:url>
        <a class="btn btn-sm btn-default" href="<s:property value="externalValidationResults" />" role="button"><span
            class="glyphicon glyphicon-save"></span>&nbsp;Download (.csv)</a>
      </div>
      <!-- TODO editable description and paper reference for models
      <div class="list-group-item">
        <h4 class="list-group-item-heading">Description and paper reference</h4>
      </div>-->
    </div>

    <ul class="nav nav-tabs">
      <li class="active"><a href="#external-validation" data-toggle="tab">External Validation</a></li>
      <li><a href="#trees" data-toggle="tab">Trees</a></li>
      <li><a href="#y-random-trees" data-toggle="tab">y-Randomized Trees</a></li>
      <li><a href="#modeling-parameters" data-toggle="tab">Modeling Parameters</a></li>
    </ul>

    <div class="tab-content">
      <div id="external-validation" class="tab-pane active">
        <h3>External Validation</h3>
      </div>

      <div id="trees" class="tab-pane">
        <h3>Trees</h3>
      </div>

      <div id="y-random-trees" class="tab-pane">
        <h3><var>y</var>-Randomized Trees</h3>
      </div>

      <div id="modeling-parameters" class="tab-pane">
        <h3>Modeling Parameters</h3>
      </div>
    </div>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
</body>
</html>
