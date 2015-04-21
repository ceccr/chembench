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

          <p>Currently you have chosen <strong><span id="selected-model-count">0</span></strong> model(s).
            <strong><span class="text-danger"
                          id="minimum-model-warning">You must choose at least one model.</span></strong>
          </p>
          <ul id="model-list"></ul>

          <ul class="nav nav-tabs">
            <li class="active"><a href="#single-compound" data-toggle="tab">Predict a Single Compound</a></li>
            <li><a href="#dataset" data-toggle="tab">Predict a Dataset</a></li>
          </ul>

          <div class="tab-content">
            <div id="single-compound" class="tab-pane active">
              <h3>Predict a Single Compound</h3>

              <div id="jsme" class="col-xs-6">
                <div id="jsme-container"></div>

                <div class="button-group">
                  <button class="btn">Copy SMILES</button>
                  <button class="btn btn-primary">Get SMILES and Predict</button>
                </div>
              </div>

              <div class="col-xs-6">
                <p>Enter a molecule in SMILES format, e.g. <kbd>C1=CC=C(C=C1)CC(C(=O)O)N</kbd> (phenylalanine).
                  Or, use the applet on the left to draw a molecule, then click "Get SMILES and Predict".
                </p>

                <form class="form form-horizontal">
                  <div class="form-group">
                    <label class="col-xs-7 control-label">SMILES:</label>

                    <div class="col-xs-5">
                      <input class="form-control">
                    </div>
                  </div>

                  <div class="form-group">
                    <label class="col-xs-7 control-label">Applicability Domain cutoff:</label>

                    <div class="col-xs-5">
                      <select class="form-control"></select>
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="col-xs-offset-7 col-xs-5">
                      <button class="btn btn-primary">Predict</button>
                    </div>
                  </div>
                </form>
              </div>
            </div>

            <div id="dataset" class="tab-pane">
              <h3>Predict a Dataset</h3>

              <p>Select a dataset to predict.</p>

              <div id="prediction-dataset-selection">
                <%@ include file="/jsp/jobs/datasets.jsp" %>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="/assets/js/jsme/jsme.nocache.js"></script>
<script src="/assets/js/prediction.js"></script>
</body>
</html>
