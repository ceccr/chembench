<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="edu.unc.ceccr.chembench.global.Constants" %>

<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Dataset Creation</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Dataset Creation</h2>

    <p>Here, you can create a dataset by uploading compound structures with or without associated activities. The
      activity data is required for building models.</p>

    <p>
      You can either create a <b>Modeling Dataset</b>, which has both structures and activities, or a <b>Prediction
      Dataset</b>, which only has structures.
    </p>

    <p>Each modeling dataset you create will appear as an option under the "Modeling" tab and under the
      "Prediction" tab. Prediction datasets will only appear under the "Prediction" tab. When you submit a dataset,
      chemical structure images will be generated for each compound. A pairwise compound similarity matrix will be
      created and displayed as a heatmap.</p>

    <p>
      For more information about dataset creation and defining external sets, see the <a href="/help-dataset">Dataset
      help section</a>.
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
            descriptors; Chembench will generate them for you.<br> If you have your own descriptors that you
            would like to upload for your dataset, select <b>Modeling</b> or <b>Prediction Dataset with
            Descriptors</b>.
          </p>
          <hr>
          <ul class="nav nav-pills">
            <li class="active"><a href="#modeling-dataset" data-toggle="tab">Modeling Dataset</a></li>
            <li><a href="#prediction-dataset" data-toggle="tab">Prediction Dataset</a></li>
            <li><a href="#modeling-dataset-with-descriptors" data-toggle="tab">Modeling Dataset with
              Descriptors</a></li>
            <li><a href="#prediction-dataset-with-descriptors" data-toggle="tab">Prediction Dataset with
              Descriptors</a></li>
          </ul>

          <div class="tab-content">
            <div id="modeling-dataset" class="tab-pane active">
              <input type="hidden" name="dataset-type" value="<%= Constants.MODELING %>">

              <p>
                A <b>Modeling Dataset</b> can be used for both modeling and prediction. You will need to supply an <a
                  href="/help-fileformats#SDF">SDF file</a> containing the structures of the compounds in your
                dataset, and an <a href="/help-fileformats#ACT">ACT file</a> containing the activity values of those
                compounds.
              </p>

              <div class="form-group">
                <label class="control-label col-xs-3">Activity file (.act):</label>

                <div class="col-xs-9">
                  <s:file name="actFileModeling" id="actFileModeling" theme="simple" />
                </div>
              </div>
              <div class="form-group">
                <label class="control-label col-xs-3">Activity type:</label>

                <div class="inline-radio-group col-xs-9">
                  <s:radio name="dataTypeModeling" value="dataTypeModeling"
                           list="#{'CONTINUOUS':'Continuous (regression)','CATEGORY':'Category (classification)'}"
                           theme="simple" />
                </div>
              </div>
              <hr>
              <div class="form-group">
                <label class="control-label col-xs-3">Structure file (.sdf):</label>

                <div class="col-xs-9">
                  <s:file name="sdfFileModeling" id="sdfFileModeling" theme="simple" />
                </div>
              </div>
              <div class="form-group">
                <div class="col-xs-offset-3 col-xs-9">
                  <label for="standardizeModeling"><s:checkbox name="standardizeModeling"
                                                               id="standardizeModeling" theme="simple" />Standardize
                    molecule structures</label>

                  <p class="help-block">
                    If you choose not to standardize, ensure that your structure file contains explicit hydrogens.<br>
                    Otherwise, Dragon descriptors will not be available.
                  </p>
                </div>
              </div>
              <div class="form-group">
                <div class="col-xs-offset-3 col-xs-9">
                  <label for="generateImagesM"><s:checkbox name="generateImagesM" id="generateImagesM"
                                                           theme="simple" />Generate Mahalanobis heatmap</label>

                  <p class="help-block">Unchecking this box will accelerate dataset generation but will eliminate
                    heatmap based on Mahalanobis distance measure.</p>
                </div>
              </div>
            </div>
            <div id="prediction-dataset" class="tab-pane">
              <input type="hidden" name="dataset-type" value="<%= Constants.PREDICTION %>">

              <p>
                A <b>Prediction Dataset</b> can only be used for prediction. You will need to supply an <a
                  href="/help-fileformats#SDF">SDF file</a> containing the structures of the compounds in your
                dataset.
              </p>

              <div class="form-group">
                <label class="control-label col-xs-3">Structure file (.sdf):</label>

                <div class="col-xs-9">
                  <s:file name="sdfFilePrediction" id="sdfFilePrediction" theme="simple" />
                </div>
              </div>
              <div class="form-group">
                <div class="col-xs-offset-3 col-xs-9">
                  <label for="standardizePrediction"><s:checkbox name="standardizePrediction"
                                                                 id="standardizePrediction" theme="simple" />Standardize
                    molecule structures</label>

                  <p class="help-block">
                    If you choose not to standardize, ensure that your structure file contains explicit hydrogens.<br>
                    Otherwise, Dragon descriptors will not be available.
                  </p>
                </div>
              </div>
              <div class="form-group">
                <div class="col-xs-offset-3 col-xs-9">
                  <label for="generateImagesP"><s:checkbox name="generateImagesP" id="generateImagesP"
                                                           theme="simple" />Generate Mahalanobis heatmap</label>

                  <p class="help-block">Unchecking this box will accelerate dataset generation but will eliminate
                    heatmap based on Mahalanobis distance measure.</p>
                </div>
              </div>
            </div>
            <div id="modeling-dataset-with-descriptors" class="tab-pane">
              <input type="hidden" name="dataset-type" value="<%= Constants.MODELINGWITHDESCRIPTORS %>">

              <p>
                A <b>Modeling Dataset with Descriptors</b> can be used for both modeling and prediction. Choose this
                option if you have your own descriptors that you want to upload. (Otherwise, select <b>Modeling
                Dataset</b> instead.)
              </p>

              <p>
                You will need to supply an <a href="/help-fileformats#X">X file</a> containing your descriptor data,
                and an <a href="/help-fileformats#ACT">ACT file</a> containing your compounds' activity values.
                Optionally, you may provide a <a href="/help-fileformats#SDF">SDF file</a> containing the structures
                of the compounds in your dataset. If you do, Chembench will generate descriptors for you in addition
                to the ones you upload, as well as images of your compound structures.
              </p>

              <div class="form-group">
                <label class="control-label col-xs-3">Activity file (.act):</label>

                <div class="col-xs-9">
                  <s:file name="actFileModDesc" id="actFileModDesc" theme="simple" />
                </div>
              </div>
              <div class="form-group">
                <label class="control-label col-xs-3">Activity type:</label>

                <div class="inline-radio-group col-xs-9">
                  <s:radio name="dataTypeModDesc" value="dataTypeModDesc"
                           list="#{'CONTINUOUS':'Continuous (regression)','CATEGORY':'Category (classification)'}"
                           theme="simple" />
                </div>
              </div>
              <hr>
              <div class="form-group">
                <label class="control-label col-xs-3">Descriptor data (.x):</label>

                <div class="col-xs-9">
                  <s:file name="xFileModDesc" id="xFileModDesc" theme="simple" />
                </div>
              </div>
              <div class="form-group">
                <div class="control-label col-xs-3">
                  <label>Descriptor type:</label> <span class="help-block">e.g. "Dragon", "Hybrid", etc.</span>
                </div>
                <div class="col-xs-9">
                  <div class="descriptor-type">
                    <div class="control-label col-xs-3">
                      <input type="radio" name="predictorName" id="newDescriptorName" checked="checked"><label
                        for="newDescriptorName">New type</label>
                    </div>
                    <div class="col-xs-9 descriptor-type-entry">
                      <s:textfield name="descriptorNewName" id="descriptorNewName" theme="simple" />
                    </div>
                  </div>
                  <s:if test="userUploadedDescriptorTypes.size() > 0">
                    <div class="descriptor-type text-muted">
                      <div class="control-label col-xs-3">
                        <input type="radio" name="predictorName" id="usedDescriptorName"><label
                          for="usedDescriptorName">Existing type</label>
                      </div>
                      <div class="col-xs-9 descriptor-type-entry">
                        <s:select name="selectedDescriptorUsedName" id="descriptorUsedName"
                                  list="userUploadedDescriptorTypes" label="Select type" disabled="true"
                                  theme="simple" />
                      </div>
                    </div>
                  </s:if>
                </div>
              </div>
              <div class="form-group">
                <div class="col-xs-offset-3 col-xs-9">
                  <label for="hasBeenScaled"><s:checkbox name="hasBeenScaled" id="hasBeenScaled"
                                                         theme="simple" />My descriptors have been scaled</label>

                  <p class="help-block">If you leave this unchecked, Chembench will scale your descriptors for
                    you.</p>
                </div>
              </div>
              <hr>
              <div class="form-group optional-sdf text-muted">
                <div class="control-label col-xs-3">
                  <label>Structure file (.sdf):</label><span class="help-block">Optional.</span>
                </div>
                <div class="col-xs-9">
                  <s:file name="sdfFileModDesc" id="sdfFileModDesc" theme="simple" cssClass="optional-sdf-select" />
                </div>
              </div>
              <div class="optional-sdf-options">
                <div class="form-group">
                  <div class="col-xs-offset-3 col-xs-9">
                    <label for="standardizeModDesc"><s:checkbox name="standardizeModDesc"
                                                                id="standardizeModDesc" theme="simple" />Standardize
                      molecule structures</label>

                    <p class="help-block">
                      If you choose not to standardize, ensure that your structure file contains explicit hydrogens.<br>
                      Otherwise, Dragon descriptors will not be available.
                    </p>
                  </div>
                </div>
                <div class="form-group">
                  <div class="col-xs-offset-3 col-xs-9">
                    <label for="generateImagesMWD"><s:checkbox name="generateImagesMWD" id="generateImagesMWD"
                                                               theme="simple" />Generate Mahalanobis heatmap</label>

                    <p class="help-block">Unchecking this box will accelerate dataset generation but will
                      eliminate heatmap based on Mahalanobis distance measure.</p>
                  </div>
                </div>
              </div>
            </div>
            <div id="prediction-dataset-with-descriptors" class="tab-pane">
              <input type="hidden" name="dataset-type" value="<%= Constants.PREDICTIONWITHDESCRIPTORS %>">

              <p>
                A <b>Prediction Dataset with Descriptors</b> can only be used for prediction. Choose this option if
                you have your own descriptors that you want to upload. (Otherwise, select <b>Prediction Dataset</b>
                instead.)
              </p>

              <p>
                You will need to supply an <a href="/help-fileformats#X">X file</a> containing your descriptor data.
                Optionally, you may provide a <a href="/help-fileformats#SDF">SDF file</a> containing the structures
                of the compounds in your dataset. If you do, Chembench will generate compound structure images for
                you.
              </p>

              <div class="form-group">
                <label class="control-label col-xs-3">Descriptor data (.x):</label>

                <div class="col-xs-9">
                  <s:file name="xFilePredDesc" id="xFilePredDesc" theme="simple" />
                </div>
              </div>
              <div class="form-group">
                <div class="control-label col-xs-3">
                  <label>Descriptor type:</label> <span class="help-block">e.g. "Dragon", "Hybrid", etc.</span>
                </div>
                <div class="col-xs-9">
                  <div class="descriptor-type">
                    <div class="control-label col-xs-3">
                      <input type="radio" name="predictorNameD" id="newDescriptorNameD" checked="checked"><label
                        for="newDescriptorNameD">New type</label>
                    </div>
                    <div class="col-xs-9 descriptor-type-entry">
                      <s:textfield name="descriptorNewNameD" id="descriptorNewNameD" theme="simple" />
                    </div>
                  </div>
                  <s:if test="userUploadedDescriptorTypes.size() > 0">
                    <div class="descriptor-type text-muted">
                      <div class="control-label col-xs-3">
                        <input type="radio" name="predictorNameD" id="usedDescriptorNameD"><label
                          for="usedDescriptorNameD">Existing type</label>
                      </div>
                      <div class="col-xs-9 descriptor-type-entry">
                        <s:select name="selectedDescriptorUsedNameD" id="descriptorUsedNameD"
                                  list="userUploadedDescriptorTypes" label="Select type" disabled="true"
                                  theme="simple" />
                      </div>
                    </div>
                  </s:if>
                </div>
              </div>
              <div class="form-group">
                <div class="col-xs-offset-3 col-xs-9">
                  <label for="hasBeenScaled"><s:checkbox name="hasBeenScaled" id="hasBeenScaled"
                                                         theme="simple" />My descriptors have been scaled</label>

                  <p class="help-block">
                    If you leave this unchecked, Chembench will scale your descriptors for you.<br> <span
                      class="text-danger">Please note that your uploaded descriptors must have the same scaling
                        as your modeling dataset.</span>
                  </p>
                </div>
              </div>
              <hr>
              <div class="form-group optional-sdf text-muted">
                <div class="control-label col-xs-3">
                  <label>Structure file (.sdf):</label><span class="help-block">Optional.</span>
                </div>
                <div class="col-xs-9">
                  <s:file name="sdfFilePredDesc" id="sdfFilePredDesc" theme="simple" cssClass="optional-sdf-select" />
                </div>
              </div>
              <div class="optional-sdf-options">
                <div class="form-group">
                  <div class="col-xs-offset-3 col-xs-9">
                    <label for="standardizePredDesc"><s:checkbox name="standardizePredDesc"
                                                                 id="standardizePredDesc" theme="simple" />Standardize
                      molecule structures</label>

                    <p class="help-block">
                      If you choose not to standardize, ensure that your structure file contains explicit hydrogens.<br>
                      Otherwise, Dragon descriptors will not be available.
                    </p>
                  </div>
                </div>
                <div class="form-group">
                  <div class="col-xs-offset-3 col-xs-9">
                    <label for="generateImagesPWD"><s:checkbox name="generateImagesPWD" id="generateImagesPWD"
                                                               theme="simple" />Generate Mahalanobis heatmap</label>

                    <p class="help-block">Unchecking this box will accelerate dataset generation but will
                      eliminate heatmap based on Mahalanobis distance measure.</p>
                  </div>
                </div>
              </div>
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
              <input type="hidden" name="split-type" value="<%= Constants.NFOLD %>">

              <p>
                An <b>n-fold split</b> will generate <b>n</b> different external test sets. When you use this dataset
                for modeling, <b>n</b> predictors will be created: one for each fold. Each external set will contain
                1/<b>n</b> of the total dataset, and the external sets will not overlap.
              </p>

              <div class="form-group">
                <label class="control-label col-xs-3">Number of splits (<i>n</i>&nbsp;):
                </label>

                <div class="col-xs-2">
                  <s:textfield name="numExternalFolds" id="numExternalFolds" cssClass="form-control" theme="simple" />
                </div>
              </div>
              <div class="form-group">
                <div class="col-xs-offset-3 col-xs-9">
                  <s:checkbox name="useActivityBinningNFold" id="useActivityBinningNFold" theme="simple" />
                  <label for="useActivityBinningNFold">Use activity binning</label>
                </div>
              </div>
            </div>
            <div id="random-split" class="tab-pane">
              <input type="hidden" name="split-type" value="<%= Constants.RANDOM %>">

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
                  <s:checkbox name="useActivityBinningNFold" id="useActivityBinningNFold" theme="simple" />
                  <label for="useActivityBinningNFold">Use activity binning</label>
                </div>
              </div>
            </div>
            <div id="choose-compounds" class="tab-pane">
              <input type="hidden" name="split-type" value="<%= Constants.USERDEFINED %>">

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

            <div class="col-xs-4">
              <s:textfield name="datasetName" id="datasetName" cssClass="form-control" theme="simple" />
            </div>
          </div>
          <div class="text-muted">
            <div class="form-group">
              <div class="control-label col-xs-3">
                <label>Paper reference:</label> <span class="help-block">Optional.</span>
              </div>
              <div class="col-xs-4 paper-reference-input-wrapper">
                <s:textfield name="paperReference" id="paperReference" cssClass="form-control" theme="simple" />
              </div>
            </div>
            <div class="form-group">
              <div class="control-label col-xs-3">
                <label>Dataset description:</label> <span class="help-block">Optional.</span>
              </div>
              <div class="col-xs-4">
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

  <%@include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="assets/js/dataset.js"></script>
</body>
</html>
