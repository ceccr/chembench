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
          <dd class="descriptor-type"><s:property value="predictor.descriptorGeneration" /></dd>

          <dt>Correlation cutoff</dt>
          <dd><s:property value="predictor.correlationCutoff" /></dd>

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
        <li><a href="#descriptor-importance" data-toggle="tab">Descriptor Importance</a></li>
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
                       width="500" height="500">
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
                        <dt><var>r</var> <sup>2</sup></dt>
                        <s:if test="predictor.childType == @edu.unc.ceccr.chembench.global.Constants@NFOLD">
                          <dd><s:property value="predictor.externalPredictionAccuracyAvg" /></dd>
                        </s:if>
                        <s:else>
                          <dd><s:property value="predictor.externalPredictionAccuracy" /></dd>
                        </s:else>
                        <dt><abbr class="initialism" title="Root Mean Squared Error">RMSE</abbr></dt>
                        <dd><s:text name="format.double"><s:param value="continuousStatistics.rmse" /></s:text></dd>
                        <dt><abbr class="initialism" title="Mean Absolute Error">MAE</abbr></dt>
                        <dd><s:text name="format.double"><s:param value="continuousStatistics.mae" /></s:text></dd>
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

        <table class="compound-list table table-hover table-bordered datatable">
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
                  <td>
                    <s:property value="predictedValue" />
                    <s:if test="predictedValueStandardDeviation != 0.0f">
                      &plusmn; <s:property value="predictedValueStandardDeviation" />
                    </s:if>
                  </td>
                  <td><s:text name="format.double"><s:param value="residual" /></s:text></td>
                </s:if>
                <s:elseif test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY">
                  <td><s:text name="format.int"><s:param value="observedValue" /></s:text></td>
                  <td>
                    <s:text name="format.int"><s:param value="predictedValue" /></s:text>
                    <s:if test="predictedValueStandardDeviation != 0.0f">
                      &plusmn; <s:property value="predictedValueStandardDeviation" />
                    </s:if>
                  </td>
                </s:elseif>
                <s:if test="!predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
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
            data where the compound activities have been shuffled. Ideally, no trees with a high r<sup>2</sup> will be
            produced. If the <var>y</var>-Randomized trees are similar to the real trees built on your data (see the <b>Trees</b>
            tab), the predictor should be considered invalid and the dataset or parameters must be revised. <var>y</var>-randomized
            trees are only created for validation purposes and are not used in predictions.
          </p>
      </s:if>
      <s:else>
        <div id="y-randomized-models" class="tab-pane">
          <h3><var>y</var>-Randomized Models</h3>

          <p>In <var>y</var>-Randomization modeling, Chembench attempts to create a second predictor from a copy of your
            data where the compound activities have been shuffled. Ideally, no models with a high r<sup>2</sup> or
            q<sup>2</sup> will be produced. If the <var>y</var>-Randomized models are similar to the real models built
            on your data (see the <b>Models</b> tab), the predictor should be considered invalid and the dataset or
            parameters must be revised. <var>y</var>-randomized models are only created for validation purposes and are
            not used in predictions.
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

      <s:if test="predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
        <div id="descriptor-importance" class="tab-pane">
          <h3>Descriptor Importance</h3>

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

          <s:iterator value="randomForestDescriptorImportances" status="status">
            <div data-fold-number="<s:property value="#status.index" />">
              <table class="table table-bordered table-hover datatable">
                <thead>
                <tr>
                  <th>Descriptor</th>
                  <th data-sort-direction="desc"><s:property value="importanceMeasure" /></th>
                </tr>
                </thead>
                <tbody>
                <s:iterator>
                  <tr>
                    <td><s:property value="key" /></td>
                    <td><s:property value="value" /></td>
                  </tr>
                </s:iterator>
                </tbody>
              </table>
            </div>
          </s:iterator>
        </div>
      </s:if>

      <div id="modeling-parameters" class="tab-pane">
        <h3>Modeling Parameters</h3>

        <p>These are the parameters you used to generate this model.</p>

        <s:if test="predictor.modelMethod.startsWith(@edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST)">
          <dl class="dl-horizontal properties-list">
            <dt>Number of trees</dt>
            <dd><s:property value="modelParameters.numTrees" /></dd>

            <s:if test="predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@RANDOMFOREST">
              <dt>Random seed used</dt>
              <dd><s:property value="modelParameters.seed" /></dd>
            </s:if>
          </dl>
        </s:if>
        <s:else>
          <!-- non-RF models use internal splitting, so display those parameters -->
          <h4>Internal Data Split Parameters</h4>
          <dl class="dl-horizontal properties-list">
            <dt>Number of internal data splits</dt>
            <dd><s:property value="predictor.numSplits" /></dd>

            <s:if test="predictor.trainTestSplitType == @edu.unc.ceccr.chembench.global.Constants@RANDOM">
              <dt>Internal data split type</dt>
              <dd>Random split</dd>

              <dt>Minimum test set size</dt>
              <dd><s:property value="predictor.randomSplitMinTestSize" />%</dd>

              <dt>Maximum test set size</dt>
              <dd><s:property value="predictor.randomSplitMaxTestSize" />%</dd>
            </s:if>
            <s:elseif
                test="predictor.trainTestSplitType == @edu.unc.ceccr.chembench.global.Constants@SPHEREEXCLUSION">
              <dt>Internal data split type</dt>
              <dd>Sphere exclusion</dd>

              <dt>Minimum test set size</dt>
              <dd><s:property value="predictor.sphereSplitMinTestSize" />%</dd>

              <dt>Force minimum activity compound into all training sets</dt>
              <dd><s:if test="predictor.splitIncludesMin == 'true'">Yes</s:if><s:else>No</s:else></dd>

              <dt>Force maximum activity compound into all training sets</dt>
              <dd><s:if test="predictor.splitIncludesMax == 'true'">Yes</s:if><s:else>No</s:else></dd>

              <dt>Next training set point selection method</dt>
              <dd>
                <s:if test="predictor.selectionNextTrainPt == 0">Random selection</s:if>
                <s:elseif
                    test="predictor.selectionNextTrainPt == 1">Expand outwards from already selected points</s:elseif>
                <s:elseif test="predictor.selectionNextTrainPt == 2">Even coverage of descriptor space</s:elseif>
                <s:elseif
                    test="predictor.selectionNextTrainPt == 3">Work inwards from boundaries of descriptor space</s:elseif>
              </dd>
            </s:elseif>
          </dl>
          <hr>
          <s:if test="predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@KNNSA ||
                      predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@KNNGA ">
            <h4>General KNN+ Parameters</h4>
            <dl class="dl-horizontal properties-list">
              <dt>Descriptors per model</dt>
              <dd>from <s:property value="modelParameters.knnMinNumDescriptors" /> to
                <s:property value="modelParameters.knnMaxNumDescriptors" /></dd>

              <dt>Minimum nearest neighbors</dt>
              <dd><s:property value="modelParameters.knnMinNearestNeighbors" /></dd>

              <dt>Maximum nearest neighbors</dt>
              <dd><s:property value="modelParameters.knnMaxNearestNeighbors" /></dd>

              <dt>Applicability Domain cutoff</dt>
              <dd><s:property value="modelParameters.knnApplicabilityDomain" /></dd>

              <dt>Minimum for training set</dt>
              <dd><s:property value="modelParameters.knnMinTraining" /></dd>

              <dt>Minimum for test set</dt>
              <dd><s:property value="modelParameters.knnMinTest" /></dd>
            </dl>
            <hr>
            <s:if test="predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@KNNSA">
              <h4>KNN-SA-specific Parameters</h4>
              <dl class="dl-horizontal properties-list">
                <dt>Number of runs</dt>
                <dd><s:property value="modelParameters.saNumRuns" /></dd>

                <dt>Mutation probability per descriptor</dt>
                <dd><s:property value="modelParameters.saMutationProbabilityPerDescriptor" /></dd>

                <dt>Number of best models to store</dt>
                <dd><s:property value="modelParameters.saNumBestModels" /></dd>

                <dt>Temperature decrease coefficient</dt>
                <dd><s:property value="modelParameters.saTempDecreaseCoefficient" /></dd>

                <dt>Log initial temperature</dt>
                <dd><s:property value="modelParameters.saLogInitialTemp" /></dd>

                <dt>Final temperature</dt>
                <dd><s:property value="modelParameters.saFinalTemp" /></dd>

                <dt>Temperature convergence range</dt>
                <dd><s:property value="modelParameters.saTempConvergence" /></dd>

                <dt>Use error based fit index</dt>
                <dd><s:if test="modelParameters.knnSaErrorBasedFit">Yes</s:if><s:else>No</s:else></dd>
              </dl>
            </s:if>
            <s:elseif test="predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@KNNGA">
              <h4>KNN-GA-specific Parameters</h4>
              <dl class="dl-horizontal properties-list">
                <dt>Population size</dt>
                <dd><s:property value="modelParameters.gaPopulationSize" /></dd>

                <dt>Maximum number of generations</dt>
                <dd><s:property value="modelParameters.gaMaxNumGenerations" /></dd>

                <dt>Stop if stable for this many generations</dt>
                <dd><s:property value="modelParameters.gaNumStableGenerations" /></dd>

                <dt>Group size for tournament selection</dt>
                <dd><s:property value="modelParameters.gaTournamentGroupSize" /></dd>

                <dt>Minimum fitness difference to proceed</dt>
                <dd><s:property value="modelParameters.gaMinFitnessDifference" /></dd>

                <dt>Use error based fit index</dt>
                <dd><s:if test="modelParameters.knnGaErrorBasedFit">Yes</s:if><s:else>No</s:else></dd>
              </dl>
            </s:elseif>
          </s:if>
          <s:elseif test="predictor.modelMethod == @edu.unc.ceccr.chembench.global.Constants@SVM">
            <h4>SVM Parameters</h4>
            <dl class="dl-horizontal properties-list">
              <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY">
                <dt>SVM type (category)</dt>
                <dd>
                  <s:if test="modelParameters.svmTypeCategory == 0">
                    C-SVC
                  </s:if>
                  <s:elseif test="modelParameters.svmTypeCategory == 1">
                    nu-SVC
                  </s:elseif>
                </dd>
              </s:if>
              <s:elseif test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CONTINUOUS">
                <dt>SVM type (continuous)</dt>
                <dd>
                  <s:if test="modelParameters.svmTypeContinuous == 3">
                    epsilon-SVR
                  </s:if>
                  <s:elseif test="modelParameters.svmTypeContinuous == 4">
                    nu-SVR
                  </s:elseif>
                </dd>
              </s:elseif>

              <s:if test="!(predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY &&
                          modelParameters.svmTypeCategory == 1)">
                <dt>Cost</dt>
                <dd>from 2<sup><s:property value="modelParameters.svmCostFrom" /></sup>&nbsp; to 2<sup><s:property
                      value="modelParameters.svmCostTo" /></sup>, step: 2<sup><s:property value="modelParameters.svmCostStep" /></sup></dd>
              </s:if>

              <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY &&
                          modelParameters.svmTypeCategory == 0">
                <dt>Parameter C of class <var>i</var>&nbsp; to weight &sdot; C for C-SVC</dt>
                <dd><s:property value="modelParameters.svmWeight" /></dd>
              </s:if>

              <s:if test="(predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CONTINUOUS &&
                          modelParameters.svmTypeContinuous == 4) ||
                          (predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CATEGORY &&
                          modelParameters.svmTypeCategory == 1)">
                <dt>Nu</dt>
                <dd>from <s:property value="modelParameters.svmNuFrom" /> to <s:property
                    value="modelParameters.svmNuTo" />, step: <s:property value="modelParameters.svmNuStep" /></dd>
              </s:if>

              <s:if test="predictor.activityType == @edu.unc.ceccr.chembench.global.Constants@CONTINUOUS &&
                          modelParameters.svmTypeContinuous == 3">
                <dt>Epsilon in loss function of epsilon-SVR</dt>
                <dd>from <s:property value="modelParameters.svmPEpsilonFrom" />
                    to <s:property value="modelParameters.svmPEpsilonTo" />,
                    step: <s:property value="modelParameters.svmPEpsilonStep" /></dd>
              </s:if>

              <br>
              <dt>Kernel type</dt>
              <dd>
                <s:if test="modelParameters.svmKernel == 0">linear</s:if>
                <s:elseif test="modelParameters.svmKernel == 1">polynomial</s:elseif>
                <s:elseif test="modelParameters.svmKernel == 2">radial basis function</s:elseif>
                <s:elseif test="modelParameters.svmKernel == 3">sigmoid</s:elseif>
              </dd>

              <s:if test="modelParameters.svmKernel == 1">
                <dt>Degree</dt>
                <dd>from <s:property value="modelParameters.svmDegreeFrom" /> to <s:property
                    value="modelParameters.svmDegreeTo" />, step: <s:property value="modelParameters.svmDegreeStep" /></dd>
              </s:if>

              <s:if test="modelParameters.svmKernel != 0">
                <dt>Gamma</dt>
                <dd>from 2<sup><s:property value="modelParameters.svmGammaFrom" /></sup>&nbsp; to 2<sup><s:property
                      value="modelParameters.svmGammaTo" /></sup>, step: 2<sup><s:property value="modelParameters.svmGammaStep" /></sup></dd>
              </s:if>

              <br>
              <dt>Tolerance of termination criterion</dt>
              <dd><s:property value="modelParameters.svmEEpsilon" /></dd>

              <dt>Use shrinking heuristics</dt>
              <dd>
                <s:if test="modelParameters.svmHeuristics == 1">Yes</s:if>
                <s:elseif test="modelParameters.svmHeuristics == 0">No</s:elseif>
              </dd>

              <dt>Use probability estimates</dt>
              <dd>
                <s:if test="modelParameters.svmProbability == 1">Yes</s:if>
                <s:elseif test="modelParameters.svmProbability == 0">No</s:elseif>
              </dd>

              <dt>Model acceptance cutoff</dt>
              <dd><s:property value="modelParameters.svmCutoff" /></dd>
            </dl>
          </s:elseif>
        </s:else>
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
