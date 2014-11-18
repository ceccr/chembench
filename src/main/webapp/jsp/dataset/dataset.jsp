<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html>
<head>
<%@ include file="/jsp/main/head.jsp"%>
<title>Chembench | Dataset Creation</title>
</head>
<body>
  <div id="main" class="container">
    <%@ include file="/jsp/main/header.jsp"%>

    <section id="content">
      <h2>Dataset Creation</h2>
      <p>Here, you may create a dataset by uploading compound structures with or without associated activities. The
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
        <div class="panel panel-primary">
          <div class="panel-heading">
            <h3 class="panel-title">1. Upload Dataset Files</h3>
          </div>
          <div class="panel-body">
            <p>Select the type of dataset to create.</p>
            <p>
              If you are creating a <b>Modeling</b> or <b>Prediction Dataset</b>, you do not have to provide
              descriptors; Chembench will generate them for you.<br> If you have your own descriptors that you
              would like to upload for your dataset, select <b>Modeling</b> or <b>Prediction Dataset with
                Descriptors</b>.<br> You will have to provide an <a href="/help-fileformats#X">X file</a> containing
              the descriptor values.
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
                <p>
                  A <b>Modeling Dataset</b> can be used for both modeling and prediction. You will need to supply an <a
                    href="/help-fileformats#SDF">SDF file</a> containing the structures of the compounds in your
                  dataset, and an <a href="/help-fileformats#ACT">ACT file</a> containing the activity values of those
                  compounds.
                </p>
                <div class="form-group">
                  <label class="control-label col-xs-3">Structure file (.sdf):</label>
                  <div class="col-xs-9">
                    <s:file name="sdfFileModeling" id="sdfFileModeling" theme="simple" />
                  </div>
                </div>
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
                  <div class="col-xs-offset-3 col-xs-9">
                    <label for="standardizeModeling"><s:checkbox name="standardizeModeling"
                        id="standardizeModeling" theme="simple" />Standardize molecule structures</label>
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
                <hr>
                <div class="form-group">
                  <div class="col-xs-offset-3 col-xs-9">
                    <label for="standardizeModeling"><s:checkbox name="standardizeModeling"
                        id="standardizeModeling" theme="simple" />Standardize molecule structures</label>
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
              <div id="modeling-dataset-with-descriptors" class="tab-pane">
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
                <div class="form-group text-muted">
                  <div class="control-label col-xs-3">
                    <label>Structure file (.sdf):</label><span class="help-block">Optional.</span>
                  </div>
                  <div class="col-xs-9">
                    <s:file name="sdfFileModeling" id="sdfFileModeling" theme="simple" cssClass="optional-sdf" />
                  </div>
                </div>
                <div class="form-group optional-sdf-standardization">
                  <div class="col-xs-offset-3 col-xs-9">
                    <label for="standardizeModDesc"><s:checkbox name="standardizeModDesc"
                        id="standardizeModDesc" theme="simple" />Standardize molecule structures</label>
                    <p class="help-block">
                      If you choose not to standardize, ensure that your structure file contains explicit hydrogens.<br>
                      Otherwise, Dragon descriptors will not be available.
                    </p>
                  </div>
                </div>
                <hr>
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
                    <div class="control-label col-xs-3">
                      <input type="radio" name="predictorName" id="newDescriptorName" checked="checked"><label
                        for="newDescriptorName">New type</label>
                    </div>
                    <div class="col-xs-9 bare-input-wrapper">
                      <s:textfield name="descriptorNewName" id="descriptorNewName" label="Enter a new type"
                        theme="simple" />
                    </div>
                    <s:if test="userUploadedDescriptorTypes.size() > 0">
                      <div class="control-label col-xs-3">
                        <input type="radio" name="predictorName" id="usedDescriptorName"> <label
                          for="usedDescriptorName">Existing type</label>
                      </div>
                      <div class="col-xs-9 bare-input-wrapper">
                        <s:select name="selectedDescriptorUsedName" id="descriptorUsedName"
                          list="userUploadedDescriptorTypes" label="Select type" theme="simple" />
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
              </div>
              <div id="prediction-dataset-with-descriptors" class="tab-pane">
                <p>Bacon ipsum dolor amet hamburger capicola brisket, pig pork fatback jerky doner t-bone prosciutto
                  ground round pork belly. Turkey tongue spare ribs filet mignon shank, salami capicola porchetta. Pork
                  belly fatback t-bone salami brisket chuck short ribs tenderloin porchetta tri-tip pig tail venison
                  prosciutto meatloaf. Shank beef leberkas ham. Turkey tail turducken jerky boudin capicola picanha
                  ribeye bacon pig. Beef ribs porchetta rump bacon spare ribs. Boudin shank alcatra, landjaeger cupim
                  doner sausage turkey short ribs t-bone capicola.</p>
              </div>
            </div>
          </div>
        </div>

        <div class="panel panel-primary">
          <div class="panel-heading">
            <h3 class="panel-title">2. Define External Set</h3>
          </div>
          <div class="panel-body">
            <ul class="nav nav-pills">
              <li class="active"><a href="#nfold-split" data-toggle="tab">n-Fold Split</a></li>
              <li><a href="#random-split" data-toggle="tab">Random Split</a></li>
              <li><a href="#choose-compounds" data-toggle="tab">Choose Compounds</a></li>
            </ul>

            <div class="tab-content">
              <div id="nfold-split" class="tab-pane active">
                <p>Chuck salami pork chop tongue. Venison hamburger strip steak t-bone. Tri-tip sausage turkey
                  shankle, leberkas pancetta porchetta boudin pork pastrami sirloin short ribs tail pork chop. T-bone
                  boudin short loin, beef ribs pork chop picanha bacon chuck. Tenderloin t-bone biltong, drumstick
                  sirloin pork chop flank sausage jerky bacon. Kielbasa turkey ribeye sirloin. Cow tenderloin tongue
                  chuck prosciutto, corned beef hamburger.</p>
              </div>
              <div id="random-split" class="tab-pane">
                <p>Brisket venison chicken, short ribs capicola t-bone andouille biltong corned beef. Corned beef
                  shankle filet mignon porchetta, chicken sausage beef. Brisket pork beef ribs ham swine leberkas tongue
                  sausage meatloaf strip steak drumstick. Leberkas ribeye swine, tri-tip tenderloin fatback rump t-bone
                  ham. Leberkas frankfurter shankle short loin turkey pork loin tri-tip. Pork loin corned beef turkey
                  doner pork chop.</p>
              </div>
              <div id="choose-compounds" class="tab-pane">
                <p>Short ribs andouille bresaola, sausage jerky bacon pork loin filet mignon. T-bone porchetta
                  ribeye corned beef pork loin short loin andouille ham. Picanha tail turkey shankle. Sausage rump pig
                  beef ribs, short loin ball tip flank landjaeger. Sausage bacon jerky, capicola ground round rump
                  meatloaf doner andouille. Sirloin prosciutto beef ribs picanha strip steak.</p>
              </div>
            </div>
          </div>
        </div>

        <div class="panel panel-primary">
          <div class="panel-heading">
            <h3 class="panel-title">3. Add Dataset Metadata</h3>
          </div>
          <div class="panel-body">
            <p>Tri-tip prosciutto meatball doner, drumstick alcatra fatback salami capicola jowl beef chicken
              tenderloin ground round. Drumstick beef pork loin boudin tri-tip flank. Leberkas beef ribs pork chop
              t-bone, kevin rump strip steak. Short loin picanha tongue t-bone pancetta venison turkey shankle rump.
              Cupim sirloin alcatra porchetta, capicola turkey ground round chuck salami ball tip hamburger shank.
              Salami cupim venison, pork belly cow spare ribs ribeye capicola ham biltong sausage frankfurter.</p>
          </div>
        </div>
      </s:form>
    </section>

    <%@include file="/jsp/main/footer.jsp"%>
  </div>

  <%@ include file="/jsp/main/tail.jsp"%>
  <script src="assets/js/dataset.js"></script>
</body>
</html>
