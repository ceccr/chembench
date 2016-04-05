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
        <h4 class="list-group-item-heading">
          Description and paper reference
          <s:if test="editable">
            <button id="edit-description-reference" class="btn btn-primary btn-xs">
              <span class="glyphicon glyphicon-pencil"></span> Edit
            </button>
          </s:if>
            <span id="description-reference-buttons">
              <button id="cancel-changes" class="btn btn-default btn-xs">
                <span class="glyphicon glyphicon-remove"></span> Cancel
              </button>
              <button id="save-changes" type="submit" class="btn btn-primary btn-xs">
                <span class="glyphicon glyphicon-floppy-disk"></span> Save
              </button>
            </span>
        </h4>
        <dl id="description-reference-text" class="properties-list">
          <dt>Description</dt>
          <dd id="description-wrapper">
            <div class="value"><s:property value="predictor.description" /></div>
            <div class="placeholder">
              <span class="text-muted">(No description given.)</span>
            </div>
          </dd>

          <dt>Paper reference</dt>
          <dd id="paper-reference-wrapper">
            <div class="value"><s:property value="predictor.paperReference" /></div>
            <div class="placeholder">
              <span class="text-muted">(No paper reference given.)</span>
            </div>
          </dd>
        </dl>

        <s:if test="editable">
          <s:form id="object-form" action="modelDetail" method="POST" theme="simple">
            <div class="form-group">
              <label for="description">Description:</label>
              <s:textarea id="description" name="description" value="%{predictor.description}"
                          cssClass="form-control" />
            </div>

            <div class="form-group">
              <label for="paper-reference">Paper reference:</label>
              <s:textarea id="paper-reference" name="paperReference" value="%{predictor.paperReference}"
                          cssClass="form-control" />
            </div>

            <s:hidden name="id" value="%{id}" />
          </s:form>
        </s:if>
      </div>
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

        <s:if test="predictor.childType == @edu.unc.ceccr.chembench.global.Constants@NFOLD">
          <nav class="text-center fold-navigation">
            <a href="#" class="btn btn-default all-folds active" role="button">All</a>

            <ul class="pagination">
              <li class="previous disabled">
                <a href="#" aria-label="Previous">
                  <span aria-hidden="true">&laquo;</span>
                </a>
              </li>
              <s:iterator value="foldNumbers">
                <li><a href="#"><s:property /></a></li>
              </s:iterator>
              <li class="next disabled">
                <a href="#" aria-label="Next">
                  <span aria-hidden="true">&raquo;</span>
                </a>
              </li>
            </ul>
          </nav>
        </s:if>

        <s:iterator value="evGroups" status="status">
          <div data-fold-number="<s:property value="#status.index" />">
            <div class="row">
              <div class="col-xs-7">
                <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CONTINUOUS">
                  <s:url var="imageUrl" action="imageServlet" escapeAmp="false">
                    <s:param name="project" value="%{predictor.name}" />
                    <s:param name="projectType" value="'modeling'" />
                    <s:param name="user" value="%{predictor.userName}" />
                    <s:param name="compoundId" value="'externalValidationChart'" />
                    <s:param name="currentFoldNumber" value="#status.index" />
                  </s:url>
                  <img src="<s:property value="imageUrl" />" class="img-thumbnail" alt="External validation chart"
                       width="500px" height="500px">
                </s:if>
                <s:elseif test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY">
                  <div class="panel panel-default">
                    <div class="panel-heading">
                      <h4 class="panel-title">Confusion Matrix</h4>
                    </div>
                    <table id="confusion-matrix" class="table table-bordered">
                      <thead>
                      <tr>
                        <th class="spacer"></th>
                        <th>Predicted 0</th>
                        <th>Predicted 1</th>
                        <th class="spacer"></th>
                      </tr>
                      </thead>
                      <tbody>
                      <tr>
                        <th>Observed 0</th>
                        <td><abbr class="initialism" title="True Negatives">TN</abbr>:
                          <s:property value="confusionMatrix.trueNegatives" /></td>
                        <td><abbr class="initialism" title="False Positives">FP</abbr>:
                          <s:property value="confusionMatrix.falsePositives" /></td>
                        <td class="statistic">Specificity:
                          <s:text name="format.double">
                            <s:param value="confusionMatrix.specificity" />
                          </s:text>
                        </td>
                      </tr>
                      <tr>
                        <th>Observed 1</th>
                        <td><abbr class="initialism" title="False Negatives">FN</abbr>:
                          <s:property value="confusionMatrix.falseNegatives" /></td>
                        <td><abbr class="initialism" title="True Positives">TP</abbr>:
                          <s:property value="confusionMatrix.truePositives" /></td>
                        <td class="statistic">Sensitivity:
                          <s:text name="format.double">
                            <s:param value="confusionMatrix.sensitivity" />
                          </s:text>
                        </td>
                      </tr>
                      <tr>
                        <td></td>
                        <td class="statistic"><abbr class="initialism" title="Negative Predictive Value">NPV</abbr>:
                          <s:text name="format.double">
                            <s:param value="confusionMatrix.npv" />
                          </s:text>
                        </td>
                        <td class="statistic"><abbr class="initialism" title="Positive Predictive Value">PPV</abbr>:
                          <s:text name="format.double">
                            <s:param value="confusionMatrix.ppv" />
                          </s:text>
                        </td>
                        <td></td>
                      </tr>
                      </tbody>
                    </table>
                  </div>
                </s:elseif>
              </div>

              <div class="col-xs-5">
                <div class="list-group">
                  <div class="list-group-item">
                    <h4 class="list-group-item-heading">Statistics</h4>
                    <dl class="dl-horizontal properties-list">
                      <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CONTINUOUS">
                      </s:if>
                      <s:elseif test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY">
                        <dt>Total number of predictions</dt>
                        <dd><s:property value="confusionMatrix.totalCorrect + confusionMatrix.totalIncorrect" /></dd>

                        <dt>Total number of correct predictions</dt>
                        <dd><s:property value="confusionMatrix.totalCorrect" /></dd>

                        <dt>Total number of incorrect predictions</dt>
                        <dd><s:property value="confusionMatrix.totalIncorrect" /></dd>

                        <dt>Accuracy</dt>
                        <dd><s:text name="format.double"><s:param value="confusionMatrix.accuracy" /></s:text></dd>

                        <dt><abbr class="initialism" title="Correct Classification Rate">CCR</abbr></dt>
                        <s:if test="predictor.childType == @edu.unc.ceccr.chembench.global.Constants@NFOLD">
                          <dd><s:property value="predictor.externalPredictionAccuracyAvg" /></dd>
                        </s:if>
                        <s:else>
                          <dd><s:property value="predictor.externalPredictionAccuracy" /></dd>
                        </s:else>
                      </s:elseif>
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
                </div>
              </div>
            </div>
          </div>
        </s:iterator>

        <table class="compound-list table table-hover table-bordered datatable" data-scroll="false">
          <thead>
          <tr>
            <th class="name" data-property="compoundName">Compound Name</th>
            <th data-transient="data-transient" class="unsortable">Structure</th>
            <th data-property="observedValue">Observed Value</th>
            <th data-property="predictedValue">Predicted Value</th>
            <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CONTINUOUS">
              <th data-property="residual">Residual</th>
            </s:if>
            <s:if test="!predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
              <th data-transient="data-transient">Predicting Models / Total Models</th>
            </s:if>
          </tr>
          </thead>
          <tbody>
          <s:iterator value="evGroups" status="status">
            <s:iterator value="displayedExternalValidationValues">
              <tr data-fold-number="<s:property value="#status.index" />">
                <td class="name"><s:property value="compoundName" /></td>
                <td class="structure">
                  <s:url var="imageUrl" action="imageServlet" escapeAmp="false">
                    <s:param name="user" value="%{modelingDataset.userName}" />
                    <s:param name="projectType" value="'dataset'" />
                    <s:param name="compoundId" value="%{compoundName}" />
                    <s:param name="datasetName" value="%{modelingDataset.name}" />
                  </s:url>
                  <img src="<s:property value="imageUrl" />" class="img-thumbnail compound-structure" width="125"
                       height="125" alt="Compound structure">
                </td>
                <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CONTINUOUS">
                  <td><s:property value="observedValue" /></td>
                  <td><s:property value="predictedValue" /></td>
                  <td><s:text name="format.double"><s:param value="residual" /></s:text></td>
                </s:if>
                <s:elseif test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY">
                  <td><s:text name="format.int"><s:param value="observedValue" /></s:text></td>
                  <td><s:text name="format.int"><s:param value="predictedValue" /></s:text></td>
                </s:elseif>
                <s:if
                    test="!predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
                  <td><s:property value="predictingModels" /> / <s:property value="totalModels" /></td>
                </s:if>
              </tr>
            </s:iterator>
          </s:iterator>
          </tbody>
        </table>
      </div>

      <s:if test="predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
        <div id="trees" class="tab-pane">
          <h3>Trees</h3>
          <s:set var="objectType">tree</s:set>
      </s:if>
      <s:else>
        <div id="models" class="tab-pane">
          <h3>Models</h3>
          <s:set var="objectType">model</s:set>
      </s:else>

        <s:if test="predictor.childType == @edu.unc.ceccr.chembench.global.Constants@NFOLD">
          <nav class="text-center fold-navigation">
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
        </s:if>

          <s:set var="staticTableData" value="models" />
          <s:set var="isYRandom">false</s:set>
          <%@ include file="modelDetail-modelTable.jsp" %>
        </div>

      <s:if test="predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
        <div id="y-randomized-trees" class="tab-pane">
          <h3><var>y</var>-Randomized Trees</h3>

          <p>In <var>y</var>-Randomization modeling, Chembench attempts to create a second predictor from a copy of your
            data where the compound activities have been shuffled. Ideally, no trees with a high R<sup>2</sup> will be
            produced. If the <var>y</var>-Randomized trees are similar to the real trees built on your data (see the <b>Trees</b>
            tab), the predictor should be considered invalid and the dataset or parameters must be revised. <var>y</var>-randomized
            trees are only created for validation purposes and are not used in predictions.
          </p>
      </s:if>
      <s:else>
        <div id="y-randomized-models" class="tab-pane">
          <h3><var>y</var>-Randomized Models</h3>

          <p>In <var>y</var>-Randomization modeling, Chembench attempts to create a second predictor from a copy of your
            data where the compound activities have been shuffled. Ideally, no models with a high R<sup>2</sup> will be
            produced. If the <var>y</var>-Randomized models are similar to the real models built on your data (see the <b>Models</b>
            tab), the predictor should be considered invalid and the dataset or parameters must be revised. <var>y</var>-randomized
            models are only created for validation purposes and are not used in predictions.
          </p>
      </s:else>

        <s:if test="predictor.childType == @edu.unc.ceccr.chembench.global.Constants@NFOLD">
          <nav class="text-center fold-navigation">
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
        </s:if>

          <s:set var="staticTableData" value="yRandomModels" />
          <s:set var="isYRandom">true</s:set>
          <%@ include file="modelDetail-modelTable.jsp" %>
      </div>

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
<script src="${pageContext.request.contextPath}/assets/js/autolink.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/editable.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/paging.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/modelDetail.js"></script>
</body>
</html>
