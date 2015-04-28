<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Prediction</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <section>
      <h2>Existing Predictions</h2>

      <%@ include file="/jsp/jobs/predictions.jsp" %>
    </section>

    <hr>
    <section>
      <h2>Create a New Prediction</h2>

      <p>&nbsp;<!-- TODO description here --></p>

      <div class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Select Models</h3>
        </div>
        <div class="panel-body">
          <p>Select the model(s) you want to predict with. To select a model, simply click on its row in the table.
            To deselect it, click its row again.
          </p>

          <div id="prediction-model-selection">
            <%@ include file="/jsp/jobs/models.jsp" %>
          </div>

          <p id="minimum-model-warning" class="bg-danger text-danger">
            <strong>Please select at least one model to predict with.</strong>
          </p>

          <p id="model-list-message">
            Currently you have chosen <strong><span id="selected-model-count"></span></strong> model(s):
          </p>
          <ul id="model-list"></ul>
        </div>
      </div>

      <div id="make-prediction" class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Make a Prediction</h3>
        </div>

        <div class="panel-body">
          <p>Now you can make a prediction using the models you have selected above. You can either predict a single
            compound at a time, or predict an entire dataset at once. Use the tabs to change the selected prediction
            mode.
          </p>

          <ul class="nav nav-tabs">
            <li class="active"><a href="#single-compound" data-toggle="tab">Predict a Single Compound</a></li>
            <li><a href="#dataset" data-toggle="tab">Predict a Dataset</a></li>
          </ul>

          <div class="tab-content">
            <div id="single-compound" class="tab-pane active">
              <h3>Predict a Single Compound</h3>

              <div class="row">
                <div id="jsme-container" class="col-xs-5">
                  <div id="jsme"></div>

                  <div class="button-group">
                    <button id="jsme-clear" class="btn">Clear</button>
                    <button id="jsme-smiles-predict" class="btn btn-primary">Get SMILES and Predict</button>
                  </div>
                </div>

                <div class="col-xs-7">
                  <p>Enter a molecule in SMILES format, e.g. <kbd>C1=CC=C(C=C1)CC(C(=O)O)N</kbd> (phenylalanine).
                    Or, use the applet on the left to draw a molecule, then click "Get SMILES and Predict".
                  </p>

                  <s:form id="predict-compound" action="makeSmilesPrediction" method="get" cssClass="form-horizontal"
                          theme="simple">
                    <div class="form-group">
                      <label for="smiles" class="col-xs-6 control-label">SMILES:</label>

                      <div class="col-xs-6">
                        <div class="input-group">
                          <input id="smiles" name="smiles" class="form-control">

                          <div class="input-group-btn">
                            <a id="copy-smiles" class="btn" title="Copy to clipboard">
                              <span class="glyphicon glyphicon-copy"></span></a>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div class="form-group">
                      <label for="smiles-cutoff" class="col-xs-6 control-label">Applicability Domain cutoff:</label>

                      <div class="col-xs-6">
                        <select name="cutoff" id="smiles-cutoff" class="form-control">
                          <option value="N/A" selected="selected">Do not use</option>
                          <option value="3">3&sigma;</option>
                          <option value="2">2&sigma;</option>
                          <option value="1">1&sigma;</option>
                          <option value="0">0&sigma;</option>
                        </select>
                      </div>
                    </div>

                    <div class="form-group">
                      <div class="col-xs-offset-6 col-xs-6">
                        <button type="submit" class="btn btn-primary">Predict</button>
                      </div>
                    </div>
                  </s:form>

                  <hr>
                  <div id="prediction-results">
                    <p class="help-block">Your prediction results will appear here.</p>
                  </div>
                </div>
                <hr>
              </div>
            </div>

            <div id="dataset" class="tab-pane">
              <h3>Predict a Dataset</h3>

              <p>Select a dataset to predict. A prediction job will be created for each prediction dataset that you
                have selected.
              </p>

              <div id="prediction-dataset-selection">
                <%@ include file="/jsp/jobs/datasets.jsp" %>
              </div>

              <p id="minimum-dataset-warning" class="bg-danger text-danger">
                <strong>Please select at least one dataset to predict.</strong>
              </p>

              <p id="dataset-list-message">
                Currently you have chosen <strong><span id="selected-dataset-count"></span></strong> dataset(s):
              </p>
              <ul id="dataset-list"></ul>

              <s:form id="predict-dataset" action="makeDatasetPrediction" method="post" cssClass="form-horizontal"
                      theme="simple">
                <input name="selectedPredictorIds" id="selectedPredictorIds" type="hidden">
                <input name="selectedDatasetId" id="selectedDatasetId" type="hidden">

                <div class="form-group">
                  <label for="jobName" class="col-xs-3 control-label">Job name:</label>

                  <div class="col-xs-3">
                    <input name="jobName" id="jobName" class="form-control">
                  </div>
                </div>
                <div class="form-group">
                  <label for="dataset-cutoff" class="col-xs-3 control-label">Applicability Domain cutoff:</label>

                  <div class="col-xs-3">
                    <select name="cutOff" id="dataset-cutoff" class="form-control">
                      <option value="99999" selected="selected">Do not use</option>
                      <option value="3">3&sigma;</option>
                      <option value="2">2&sigma;</option>
                      <option value="1">1&sigma;</option>
                      <option value="0">0&sigma;</option>
                    </select>
                  </div>
                </div>

                <div class="form-group">
                  <div class="col-xs-offset-3 col-xs-3">
                    <button type="submit" class="btn btn-primary">Predict Dataset(s)</button>
                  </div>
                </div>
              </s:form>
            </div>
          </div>
        </div>
      </div>
    </section>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script>
  Chembench.MYBENCH_URL = "<s:url action="jobs" />";
</script>
<script src="/assets/js/jsme/jsme.nocache.js"></script>
<script src="/assets/js/jquery.zclip.min.js"></script>
<script src="/assets/js/prediction.js"></script>
</body>
</html>
