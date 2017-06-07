<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Datasets</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <div id="content">
    <ul class="nav nav-tabs">
      <li class="active"><a href="#new-datasets" data-toggle="tab"><h5>Create a New Dataset</h5></a></li>
      <li><a href="#existing-datasets" data-toggle="tab"><h5>Existing Datasets</h5></a></li>
    </ul>

      <div class="tab-content">
          <div id="new-datasets" class="tab-pane active">
    <section>
      <h2>Create a New Dataset</h2>

      <p>
        Here, you can create a dataset by uploading compound structures with or without associated activities. The
        activity data is required for building models.
      </p>

      <p>
        You can either create a <b>Modeling Dataset</b>, which has both structures and activities, or a <b>Prediction
        Dataset</b>, which only has structures.
      </p>

      <p>
        Each modeling dataset you create will appear as an option under the "Modeling" tab and under the "Prediction"
        tab. Prediction datasets will only appear under the "Prediction" tab. When you submit a dataset, chemical
        structure images will be generated for each compound. A pairwise compound similarity matrix will be created and
        displayed as a heatmap.
      </p>

      <p>
        For more information about dataset creation and defining external sets, see the <s:a action="datasets"
                                                                                             namespace="/help">Dataset help section</s:a>.
      </p>
      <hr>
      <s:form action="submitDataset" enctype="multipart/form-data" method="post" cssClass="form-horizontal"
              theme="simple">
        <div id="dataset-type-selection" class="panel panel-primary">
          <s:hidden id="datasetType" name="datasetType" />

          <div class="panel-heading">
            <h3 class="panel-title">Upload Dataset Files</h3>
          </div>
          <div class="panel-body">
            <p>Select the type of dataset to create.</p>

            <p>
              If you are creating a <b>Modeling</b> or <b>Prediction Dataset</b>, you do not have to provide
              descriptors; Chembench will generate them for you.<br> If you have your own descriptors that you would
              like to upload for your dataset, select <b>Modeling</b> or <b>Prediction Dataset with Descriptors</b>.
            </p>
            <hr>
            <ul class="nav nav-pills">
              <li class="active"><a href="#modeling-dataset" data-toggle="tab">Modeling Dataset</a></li>
              <li><a href="#prediction-dataset" data-toggle="tab">Prediction Dataset</a></li>
              <li><a href="#modeling-dataset-with-descriptors" data-toggle="tab">Modeling Dataset with Descriptors</a>
              </li>
              <li><a href="#prediction-dataset-with-descriptors" data-toggle="tab">Prediction Dataset with
                Descriptors</a>
              </li>
            </ul>

            <div class="tab-content">
              <div id="modeling-dataset" class="tab-pane active">
                <%--Modeling Dataset--%>
                <%@ include file="/jsp/dataset/datasetTypes/datasets-modeling.jsp" %>
              </div>
              <div id="prediction-dataset" class="tab-pane">
                  <%--Prediction Dataset--%>
                <%@ include file="/jsp/dataset/datasetTypes/datasets-prediction.jsp" %>
              </div>
              <div id="modeling-dataset-with-descriptors" class="tab-pane">
                  <%--Modeling Dataset with Descriptors--%>
                <%@ include file="/jsp/dataset/datasetTypes/dataset-modeling-with-desc.jsp" %>
              </div>
              <div id="prediction-dataset-with-descriptors" class="tab-pane">
                  <%--Prediction Dataset with Descriptors--%>
                <%@ include file="/jsp/dataset/datasetTypes/datasets-prediction-with-desc.jsp" %>
              </div>
            </div>
          </div>
        </div>

        <div id="external-set-settings" class="panel panel-primary">
          <s:hidden id="splitType" name="splitType" />
          <div class="panel-heading">
            <h3 class="panel-title">Define External Set</h3>
          </div>
          <div class="panel-body">
            <p>Select the splitting method to use.</p>

            <p>
              A subset of the compounds in the dataset will be reserved to test the models you build. If you have
              already defined a test set, select <b>Choose Compounds</b> to use those compounds as your external set on
              Chembench.
            </p>
            <hr>
            <ul class="nav nav-pills">
              <li class="active"><a href="#nfold-split" data-toggle="tab">n-Fold Split</a></li>
              <li><a href="#random-split" data-toggle="tab">Random Split</a></li>
              <li><a href="#choose-compounds" data-toggle="tab">Choose Compounds</a></li>
            </ul>

            <div class="tab-content">
              <div id="nfold-split" class="tab-pane active">
                <input type="hidden" name="split-type" value=<s:property
                    value="@edu.unc.ceccr.chembench.global.Constants@NFOLD" />>

                <p>
                  An <b>n-fold split</b> will generate <b>n</b> different external test sets. When you use this dataset
                  for modeling, <b>n</b> predictors will be created: one for each fold. Each external set will contain
                  1/<b>n</b> of the total dataset, and the external sets will not overlap.
                </p>

                <div class="form-group">
                  <label class="control-label col-xs-3">Number of splits (<i>n</i>&nbsp;): </label>

                  <div class="col-xs-2">
                    <s:textfield name="numExternalFolds" id="numExternalFolds" cssClass="form-control" theme="simple" />
                  </div>
                </div>
                <div class="form-group">
                  <div class="col-xs-offset-3 col-xs-9">
                    <s:checkbox name="useActivityBinningNFold" id="useActivityBinningNFold" theme="simple" /> <label
                      for="useActivityBinningNFold">Use activity binning</label>
                  </div>
                </div>
              </div>
              <div id="random-split" class="tab-pane">
                <input type="hidden" name="split-type" value=<s:property
                    value="@edu.unc.ceccr.chembench.global.Constants@RANDOM" />>

                <p>
                  A <b>random split</b> will divide your modeling dataset into training and test portions at random. You
                  can change the size of the external set below. Either a percentage of total compounds or a fixed
                  number of compounds can be entered.
                </p>

                <div class="form-group">
                  <label class="control-label col-xs-3">External set size:</label>

                  <div class="col-xs-2">
                    <s:textfield name="numExternalCompounds" id="numExternalCompounds" cssClass="form-control"
                                 theme="simple" />
                  </div>
                  <div class="col-xs-3 external-split-type">
                    <s:select name="externalCompoundsCountOrPercent"
                              list="#{'Percent':'Percent','Compounds':'Compounds'}" cssClass="form-control"
                              theme="simple" />
                  </div>
                </div>
                <div class="form-group">
                  <div class="col-xs-offset-3 col-xs-9">
                    <s:checkbox name="useActivityBinningNFold" id="useActivityBinningNFold" theme="simple" /> <label
                      for="useActivityBinningNFold">Use activity binning</label>
                  </div>
                </div>
              </div>
              <div id="choose-compounds" class="tab-pane">
                <input type="hidden" name="split-type" value=<s:property
                    value="@edu.unc.ceccr.chembench.global.Constants@USERDEFINED" />>

                <p>
                  If you want to specifically <b>choose the compounds</b> for the external set, enter the names of those
                  compounds in the box below. The compound names may be separated by commas, spaces, tabs, and/or
                  newlines.
                </p>

                <div class="form-group">
                  <div class="col-xs-12">
                    <s:textarea name="externalCompoundList" id="externalCompoundList" rows="3" cssClass="form-control"
                                theme="simple" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="panel panel-primary">
          <div class="panel-heading">
            <h3 class="panel-title">Add Dataset Metadata</h3>
          </div>
          <div class="panel-body">
            <div class="form-group">
              <label class="control-label col-xs-3">Dataset name:</label>

              <div class="col-xs-6">
                <s:textfield name="datasetName" id="datasetName" cssClass="form-control" theme="simple" required="required" />
              </div>
            </div>
            <div class="text-muted">
              <div class="form-group">
                <div class="control-label col-xs-3">
                  <label>Paper reference:</label> <span class="help-block">Optional.</span>
                </div>
                <div class="col-xs-6 paper-reference-input-wrapper">
                  <s:textfield name="paperReference" id="paperReference" cssClass="form-control" theme="simple" />
                </div>
              </div>
              <div class="form-group">
                <div class="control-label col-xs-3">
                  <label>Dataset description:</label> <span class="help-block">Optional.</span>
                </div>
                <div class="col-xs-6">
                  <s:textarea name="dataSetDescription" id="dataSetDescription" rows="3" cssClass="form-control"
                              theme="simple" />
                </div>
              </div>
              <div class="form-group">
                <div class="col-xs-offset-3 col-xs-9">
                  <button type="submit" class="btn btn-primary">Create Dataset</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </s:form>
    </section>
  </div>
          <div id="existing-datasets" class="tab-pane">
              <section>
                  <h2>Existing Datasets</h2>
                      <%@ include file="/jsp/mybench/mybench-datasets.jsp" %>
              </section>
          </div>
      </div>
  </div>


  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="${pageContext.request.contextPath}/assets/js/dataset.js"></script>
</body>
</html>
