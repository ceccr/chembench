<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Model: <s:property value="predictor.name" /></title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Model Details: <s:property value="predictor.name" /></h2>

    <div class="list-group">
      <div class="list-group-item">
        <h4 class="list-group-item-heading">General information</h4>
        <dl class="dl-horizontal properties-list">
          <dt>Modeling dataset</dt>
          <dd>
            <s:url var="modelingDatasetUrl" action="datasetDetail">
              <s:param name="id" value="predictor.datasetId" />
            </s:url>
            <a href="<s:property value="modelingDatasetUrl" />" target="_blank"><s:property
                value="predictor.datasetDisplay" /></a>
          </dd>

          <dt>Dataset activity type</dt>
          <dd class="activity-type"><s:property value="modelingDataset.modelType" /></dd>

          <dt>Model type</dt>
          <dd class="modeling-method"><s:property value="predictor.modelMethod" /></dd>

          <dt>Descriptors used</dt>
          <dd><s:property value="predictor.descriptorGeneration" /></dd>

          <dt>Date created</dt>
          <dd><s:date name="predictor.dateCreated" format="yyyy-MM-dd HH:mm" /></dd>
        </dl>
      </div>

      <div class="list-group-item">
        <h4 class="list-group-item-heading">External validation results</h4>

        <s:url var="externalValidationCsvUrl" action="fileServlet" escapeAmp="false">
          <s:param name="id" value="predictor.id" />
          <s:param name="user" value="predictor.userName" />
          <s:param name="jobType" value="'MODELING'" />
          <s:param name="file" value="'externalPredictionsAsCSV'" />
        </s:url>
        <s:a href="%{externalValidationCsvUrl}" cssClass="btn btn-sm btn-default" role="button">
          <span class="glyphicon glyphicon-save"></span> Download (.csv)
        </s:a>
      </div>
      <!-- TODO editable description and paper reference for models
      <div class="list-group-item">
        <h4 class="list-group-item-heading">Description and paper reference</h4>
      </div>-->
    </div>

    <ul class="nav nav-tabs">
      <li class="active"><a href="#external-validation" data-toggle="tab">External Validation</a></li>
      <s:if test="predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
        <li><a href="#trees" data-toggle="tab">Trees</a></li>
        <li><a href="#y-randomized-trees" data-toggle="tab">y-Randomized Trees</a></li>
      </s:if>
      <s:else>
        <li><a href="#models" data-toggle="tab">Models</a></li>
        <li><a href="#y-randomized-models" data-toggle="tab">y-Randomized Models</a></li>
      </s:else>
      <li><a href="#modeling-parameters" data-toggle="tab">Modeling Parameters</a></li>
    </ul>

    <div class="tab-content">
      <div id="external-validation" class="tab-pane active">
        <h3>External Validation</h3>
      </div>

      <s:if test="predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
        <div id="trees" class="tab-pane">
          <h3>Trees</h3>

          <s:if test="predictor.childType == @edu.unc.ceccr.chembench.global.Constants@NFOLD">
            <nav class="text-center">
              <ul class="pagination">
                <li class="previous">
                  <a href="#" aria-label="Previous">
                    <span aria-hidden="true">&laquo;</span>
                  </a>
                </li>
                <s:iterator value="foldNumbers">
                  <li><a href="#"><s:property /></a></li>
                </s:iterator>
                <li class="next">
                  <a href="#" aria-label="Next">
                    <span aria-hidden="true">&raquo;</span>
                  </a>
                </li>
              </ul>
            </nav>

            <input class="fold-base-url" type="hidden" value="<s:url action="viewPredictorFold" />" />
            <input class="object-id" type="hidden" value="<s:property value="predictor.id" />" />
            <input class="is-y-random" type="hidden" value="false" />
            <input class="fold-url" type="hidden" />
            <%@ include file="trees.jsp" %>
          </s:if>
        </div>

        <div id="y-randomized-trees" class="tab-pane">
          <h3><var>y</var>-Randomized Trees</h3>

          <p>In <var>y</var>-Randomization modeling, Chembench attempts to create a second predictor from a copy of your
            data where the compound activities have been shuffled. Ideally, no trees with a high R<sup>2</sup> will be
            produced. If the <var>y</var>-Randomized trees are similar to the real trees built on your data (see the <b>Trees</b>
            tab), the predictor should be considered invalid and the dataset or parameters must be revised. <var>y</var>-randomized
            trees are only created for validation purposes and are not used in predictions.
          </p>

          <s:if test="predictor.childType == @edu.unc.ceccr.chembench.global.Constants@NFOLD">
            <nav class="text-center">
              <ul class="pagination">
                <li class="previous">
                  <a href="#" aria-label="Previous">
                    <span aria-hidden="true">&laquo;</span>
                  </a>
                </li>
                <s:iterator value="foldNumbers">
                  <li><a href="#"><s:property /></a></li>
                </s:iterator>
                <li class="next">
                  <a href="#" aria-label="Next">
                    <span aria-hidden="true">&raquo;</span>
                  </a>
                </li>
              </ul>
            </nav>

            <input class="fold-base-url" type="hidden" value="<s:url action="viewPredictorFold" />" />
            <input class="object-id" type="hidden" value="<s:property value="predictor.id" />" />
            <input class="is-y-random" type="hidden" value="true" />
            <input class="fold-url" type="hidden" />
            <%@ include file="trees.jsp" %>
          </s:if>
        </div>
      </s:if>
      <s:else>
        <div id="models" class="tab-pane">
          <h3>Models</h3>
        </div>

        <div id="y-randomized-models" class="tab-pane">
          <h3><var>y</var>-Randomized Models</h3>
        </div>
      </s:else>

      <div id="modeling-parameters" class="tab-pane">
        <h3>Modeling Parameters</h3>

        <p>These are the parameters you used to generate this model.</p>

        <dl class="dl-horizontal properties-list">
          <dt>Correlation cutoff</dt>
          <dd><s:property value="predictor.correlationCutoff" /></dd>

          <s:if test="predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
            <dt>Number of trees</dt>
            <dd><s:property value="modelParameters.numTrees" /></dd>

            <s:if test="predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST">
              <dt>Random seed used</dt>
              <dd><s:property value="modelParameters.seed" /></dd>
            </s:if>
          </s:if>
        </dl>
      </div>
    </div>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="${pageContext.request.contextPath}/assets/js/folds.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/modelDetail.js"></script>
</body>
</html>
