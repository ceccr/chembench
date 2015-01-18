<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Model Creation</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Model Creation</h2>

    <p>
      Here you can develop Quantitative Structure-Activity Relationship (QSAR) models using your uploaded modeling
      datasets.<br>You can also build models using publicly available modeling datasets.
    </p>

    <p>
      For more information about creating models and selecting the right parameters, see the <a href="/help-modeling">Modeling
      help page</a>.
    </p>

    <p>
      The full modeling workflow as described in our <a href="/help-workflows">Workflow help page</a> is detailed in
      the following publication:

        <span class="citation"><a href="http://onlinelibrary.wiley.com/doi/10.1002/minf.201000061/full">
          Tropsha, A. (2010). Best Practices for QSAR Model Development, Validation, and Exploitation.
          Molecular Informatics, 29(6-7), 476-488.
        </a></span>
    </p>

    <hr>
    <s:form action="createModelingJob" enctype="multipart/form-data" cssClass="form-horizontal" theme="simple">
      <div class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Select a Modeling Dataset</h3>
        </div>
        <div class="panel-body">
          <p>Select a modeling dataset to create a model from. (You can create more datasets using the
            <b><s:a action="dataset">Dataset Creation</s:a></b> page.)</p>

          <div class="row">
            <div class="col-xs-6">
              <div class="col-xs-12">
                <div class="form-group">
                  <s:select id="dataset-selection" cssClass="form-control" list="userDatasets" listKey="id"
                            listValue="name" theme="simple" value="(select a dataset)"/>
                </div>
                <div class="form-group">
                  <a id="view-dataset-detail" href="#" class="btn btn-primary disabled">View Selected Dataset</a>
                  <span class="text-muted">Opens in a new window.</span>
                </div>
              </div>
            </div>

            <div id="dataset-info-wrapper" class="col-xs-6">
              <div id="dataset-info">
              </div>

              <div id="dataset-info-help">
                <h4>Select a dataset to continue</h4>

                <div class="text-muted">
                  <p>
                    Once you select a dataset, basic information about the dataset will be displayed here.
                  </p>

                  <p>
                    You can also click <b>View Selected Dataset</b> to view more detailed information about the selected
                    dataset.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Define Model Descriptors</h3>
        </div>
        <div class="panel-body">
          <s:hidden id="defaultDescriptorGenerationType" value="%{descriptorGenerationType}"/>
          <h4>Descriptor Set</h4>

          <div id="descriptor-types">
            <div class="radio">
              <label>
                <input name="descriptorGenerationType" type="radio" value="CDK">
                CDK (202 descriptors)
              </label>
            </div>
            <div class="radio">
              <label>
                <input name="descriptorGenerationType" type="radio" value="MOLCONNZ">
                MolconnZ (375 descriptors)
              </label>
            </div>
            <div class="radio">
              <label>
                <input name="descriptorGenerationType" type="radio" value="DRAGONH">
                Dragon, with hydrogens (2489 descriptors)
              </label>
            </div>
            <div class="radio">
              <label>
                <input name="descriptorGenerationType" type="radio" value="DRAGONNOH">
                Dragon, no hydrogens (900 descriptors)
              </label>
            </div>
            <div class="radio">
              <label>
                <input name="descriptorGenerationType" type="radio" value="MACCS">
                MACCS (166 descriptors)
              </label>
            </div>
            <div class="radio">
              <label>
                <input name="descriptorGenerationType" type="radio" value="MOE2D">
                MOE2D (184 descriptors)
              </label>
            </div>
            <div class="radio">
              <label>
                <input name="descriptorGenerationType" type="radio" value="ISIDA">
                ISIDA
              </label>
            </div>
            <div class="radio">
              <label>
                <input name="descriptorGenerationType" type="radio" value="UPLOADED">
                Uploaded descriptors
              </label>
            </div>
          </div>

          <h4>Scaling Type</h4>

          <div id="scaling-types" class="form-group">
            <div class="inline-radio-group col-xs-12">
              <s:radio name="scalingType" id="scalingType" value="scalingType"
                       list="#{'RANGESCALING':'Range Scaling','AUTOSCALING':'Auto Scaling','NOSCALING':'None'}"/>
            </div>
          </div>

          <h4>Descriptor Filtering Options</h4>

          <div class="form-group">
            <label class="control-label col-xs-3">Maximum correlation:</label>

            <div class="col-xs-2">
              <s:textfield name="correlationCutoff" id="correlationCutoff" cssClass="form-control"/>
            </div>
            <div class="col-xs-7">
              <span class="help-inline">(between 0.0 and 1.0, inclusive)</span>
            </div>
          </div>
          <div class="row">
            <div class="col-xs-offset-3 col-xs-9">
              <span class="help-block">For each pair of descriptors, if the correlation coefficient is above the
                maximum, one of the two will be removed. Note that descriptors with zero variance across compounds
                will always be removed.</span>
            </div>
          </div>
        </div>
      </div>

      <div class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Select Model Type and Parameters</h3>
        </div>
        <div class="panel-body">
          <ul class="nav nav-pills">
            <li class="active"><a href="#random-forest" data-toggle="tab">Random Forest</a></li>
            <li><a href="#support-vector-machine" data-toggle="tab">Support Vector Machine</a></li>
            <li><a href="#ga-knn" data-toggle="tab">GA-kNN</a></li>
            <li><a href="#sa-knn" data-toggle="tab">SA-kNN</a></li>
          </ul>

          <div class="tab-content">
            <div id="random-forest" class="tab-pane active">
              <h4>Random Forest</h4>

              <div class="form-group">
                <label class="control-label col-xs-4">Number of Trees per Split:</label>

                <div class="col-xs-2">
                  <s:textfield id="numTrees" name="numTrees" cssClass="form-control"/>
                </div>
              </div>
              <div class="form-group">
                <label class="control-label col-xs-4">Maximum Number of Terminal Nodes:</label>

                <div class="col-xs-2">
                  <s:textfield id="maxNumTerminalNodes" name="maxNumTerminalNodes" cssClass="form-control"/>
                </div>
                <div class="col-xs-6">
                  <span class="help-inline">(0 = no limit)</span>
                </div>
              </div>
            </div>

            <div id="support-vector-machine" class="tab-pane">
              <h4>Support Vector Machine (SVM)</h4>

              <div id="svm-type-category" class="form-group">
                <label class="control-label col-xs-4">SVM type (category):</label>

                <div class="col-xs-8 inline-radio-group">
                  <s:radio name="svmTypeCategory" id="svmTypeCategory" list="#{'0':'C-SVC','1':'nu-SVC'}"/>
                </div>
              </div>

              <div id="svm-type-continuous" class="form-group">
                <label class="control-label col-xs-4">SVM type (continuous):</label>

                <div class="col-xs-8 inline-radio-group">
                  <s:radio name="svmTypeContinuous" id="svmTypeContinuous" list="#{'3':'epsilon-SVR','4':'nu-SVR'}"/>
                </div>
              </div>

              <div id="cost-settings" class="form-group range-input-group">
                <label class="control-label col-xs-4">Cost (C) for C-SVC, epsilon-CVR, and nu-SVR:</label>

                <div class="col-xs-8 form-inline">
                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">From:</div>
                      <div class="input-wrapper">
                        <span class="input-prefix">2^</span>
                        <s:textfield id="svmCostFrom" name="svmCostFrom" cssClass="form-control"/>
                      </div>
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">To:</div>
                      <div class="input-wrapper">
                        <span class="input-prefix">2^</span>
                        <s:textfield id="svmCostTo" name="svmCostTo" cssClass="form-control"/>
                      </div>
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">Step:</div>
                      <div class="input-wrapper">
                        <span class="input-prefix">2^</span>
                        <s:textfield id="svmCostStep" name="svmCostStep" cssClass="form-control"/>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div id="nu-settings" class="form-group range-input-group">
                <label class="control-label col-xs-4">Nu of nu-SVC and nu-SVR:</label>

                <div class="col-xs-8 form-inline">
                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">From:</div>
                      <s:textfield id="svmNuFrom" name="svmNuFrom" cssClass="form-control"/>
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">To:</div>
                      <s:textfield id="svmNuTo" name="svmNuTo" cssClass="form-control"/>
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">Step:</div>
                      <s:textfield id="svmNuStep" name="svmNuStep" cssClass="form-control"/>
                    </div>
                  </div>
                </div>
              </div>

              <div id="epsilon-settings" class="form-group range-input-group">
                <label class="control-label col-xs-4">Epsilon in loss function of epsilon-SVR:</label>

                <div class="col-xs-8 form-inline">
                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">From:</div>
                      <s:textfield id="svmPEpsilonFrom" name="svmPEpsilonFrom" cssClass="form-control"/>
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">To:</div>
                      <s:textfield id="svmPEpsilonTo" name="svmPEpsilonTo" cssClass="form-control"/>
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">Step:</div>
                      <s:textfield id="svmPEpsilonStep" name="svmPEpsilonStep" cssClass="form-control"/>
                    </div>
                  </div>
                </div>
              </div>

              <div id="csvm-weight-settings" class="form-group">
                <label class="control-label col-xs-4">Parameter C of class <var>i</var>&nbsp; to weight &sdot; C for
                  C-SVC:</label>

                <div class="col-xs-8">
                  <s:textfield id="svmWeight" name="svmWeight" cssClass="form-control" />
                </div>
              </div>

              <h4>Kernel Function Settings</h4>

              <div class="form-group">
                <label class="control-label col-xs-4">Kernel type:</label>

                <div class="col-xs-8 inline-radio-group">
                  <s:radio id="svmKernel" name="svmKernel"
                           list="#{'0':'linear','1':'polynomial','2':'radial basis function','3':'sigmoid'}"/>
                </div>
              </div>

              <div id="degree-settings" class="form-group range-input-group">
                <label class="control-label col-xs-4">Degree in kernel function:</label>

                <div class="col-xs-8 form-inline">
                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">From:</div>
                      <s:textfield id="svmDegreeFrom" name="svmDegreeFrom" cssClass="form-control"/>
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">To:</div>
                      <s:textfield id="svmDegreeTo" name="svmDegreeTo" cssClass="form-control"/>
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">Step:</div>
                      <s:textfield id="svmDegreeStep" name="svmDegreeStep" cssClass="form-control"/>
                    </div>
                  </div>
                </div>
              </div>

              <div id="gamma-settings" class="form-group range-input-group">
                <label class="control-label col-xs-4">Gamma in kernel function:</label>

                <div class="col-xs-8 form-inline">
                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">From:</div>
                      <div class="input-wrapper">
                        <span class="input-prefix">2^</span>
                        <s:textfield id="svmGammaFrom" name="svmGammaFrom" cssClass="form-control"/>
                      </div>
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">To:</div>
                      <div class="input-wrapper">
                        <span class="input-prefix">2^</span>
                        <s:textfield id="svmGammaTo" name="svmGammaTo" cssClass="form-control"/>
                      </div>
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="input-group">
                      <div class="input-group-addon">Step:</div>
                      <div class="input-wrapper">
                        <span class="input-prefix">2^</span>
                        <s:textfield id="svmGammaStep" name="svmGammaStep" cssClass="form-control"/>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <h4>Other Advanced Settings</h4>

              <div class="form-group">
                <label class="control-label col-xs-4">Tolerance of termination criterion:</label>
                <div class="col-xs-8">
                  <s:textfield id="svmEEpsilon" name="svmEEpsilon" cssClass="form-control" />
                </div>
              </div>

              <div class="form-group">
                <label class="control-label col-xs-4">Use shrinking heuristics:</label>
                <div class="col-xs-8 inline-radio-group">
                  <s:radio name="svmHeuristics" list="#{'1':'Yes','0':'No'}" />
                </div>
              </div>

              <div class="form-group">
                <label class="control-label col-xs-4">Use probability heuristics:</label>
                <div class="col-xs-8 inline-radio-group">
                  <s:radio name="svmProbability" list="#{'1':'Yes','0':'No'}" />
                </div>
              </div>

              <div class="form-group">
                <label class="control-label col-xs-4">CCR or <var>R&nbsp;<sup>2</sup></var> cutoff for model
                  acceptance:</label>
                <div class="col-xs-8">
                  <s:textfield id="svmCutoff" name="svmCutoff" cssClass="form-control" />
                </div>
              </div>
            </div>

            <div id="ga-knn" class="tab-pane">
              <h4>Genetic Algorithm <var>k</var>-Nearest Neighbors (GA-kNN) Classifier</h4>
            </div>

            <div id="sa-knn" class="tab-pane">
              <h4>Simulated Annealing <var>k</var>-Nearest Neighbors (SA-kNN) Classifier</h4>
            </div>
          </div>
        </div>
      </div>

      <div id="internal-split-type-selection" class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Choose Internal Data Split Method</h3>
        </div>
        <div class="panel-body">
          Prosciutto brisket pastrami, bacon fatback tenderloin shankle leberkas shoulder chicken pork belly. Strip
          steak ham bacon hamburger, picanha pork belly andouille flank drumstick. Turducken andouille bacon, short ribs
          meatball sirloin fatback hamburger rump. Picanha bresaola meatloaf jowl, t-bone tri-tip turkey alcatra
          frankfurter. Landjaeger pork chop prosciutto ground round kevin jerky.
        </div>
      </div>

      <div class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Add Model Metadata</h3>
        </div>
        <div class="panel-body">
          Shoulder hamburger cupim brisket tenderloin ball tip, tongue turkey porchetta venison boudin tail shankle
          bresaola. Boudin swine leberkas tenderloin rump brisket ham hock picanha meatloaf chicken kevin short loin.
          Turkey pancetta prosciutto, ball tip meatloaf cupim sirloin beef ribs pork. Short loin ground round biltong,
          leberkas kevin meatloaf fatback. Shankle shank meatloaf tenderloin ribeye ball tip pork loin corned beef chuck
          pork belly brisket hamburger kevin turkey.
        </div>
      </div>
    </s:form>
  </section>

  <%@include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="assets/js/modeling.js"></script>
</body>
</html>
