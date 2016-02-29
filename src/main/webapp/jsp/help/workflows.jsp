<<<<<<< HEAD
<html>
<body>
<table width="900" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr><td>
<div class="ccbHomeStandard">

<p align="justify" class="ccbHomeStandard">
<b>Overall Workflow</b>

<p><img src="/theme/img/overall-workflow.png"></p>

<p>This flowchart, as presented in <a href="http://onlinelibrary.wiley.com/doi/10.1002/minf.201000061/full">
Tropsha, A. Best Practices for QSAR Model Development, Validation, and Exploitation Mol. Inf., 2010, 29, 476-488
</a>, forms the basis of the Chembench workflow. Chembench implements 
the best practices detailed in that review.</p>

<p>Chembench breaks that entire process down into three major steps: Dataset creation, Modeling, and Prediction. 
Each of the steps may be used independently. For example, a medicinal chemist or toxicologist could use the predictors 
we have made available in the Prediction section to help determine activity of a specific compound.</p>

<p class="ccbHomeStandard">
<b>Dataset Creation</b></p>
<p><img src="/theme/img/dataset-workflow.png"></p>
<p>The dataset workflow handles preprocessing of the compounds, including structure standardization using the JChem
standardizer. An external set is specified, and descriptors and visualizations are generated. When the dataset
creation is finished, the dataset, the selected external set, and visualizations can be viewed and downloaded. 
See the <a href="/help-dataset">Dataset help section</a> for more details.</p>

<p class="ccbHomeStandard">
<b>Modeling</b></p>
<p><img src="/theme/img/modeling-workflow.png"></p>
<p>The Modeling workflow generates a predictor composed of an ensemble of models. After descriptors have
been selected, the dataset's modeling set is split into several training and test sets, and one or more models 
is created for each train-test split. Once the predictor has been created, it is used to predict the activity of the
dataset's external set, so that the predictor's accuracy can be evaluated. In addition, a second predictor is created 
for validation purposes by y-Randomization modeling. These techniques are thoroughly described in the publication 
linked above. See the <a href="/help-modeling">Modeling help section</a> for more details on modeling in Chembench.</p>

<p class="ccbHomeStandard">
<b>Prediction</b></p>
<p><img src="/theme/img/prediction-workflow.png"></p>
<p>To make a prediction, a user selects one or more predictors and a dataset. See the <a href="/help-prediction">Prediction 
help section</a> for more details.</p>

</div></td></tr></table>
=======
<%@include file="/jsp/help/helpheader.jsp" %>

<div class="outer">
  <div class="ccbHomeStandard">

    <p align="justify" class="ccbHomeStandard">
      <b>Version 1.3.0 (July 1, 2011)</b>

    <p>
      Updates:<br /> <br /> <u>Datasets</u><br /> - CDK descriptors are now generated.<br /> - Uploaded descriptors
      may be scaled by user or by Chembench.<br /> <u>Modeling</u><br /> - External validation charts are now shown
      for each fold in continuous n-fold predictors.<br /> - Uploaded descriptors are now automatically selected where
      available.<br /> - External validation results now available as CSV.<br /> - R<sub>2</sub> or CCR are now
      displayed in the My Bench predictor summary table.<br /> <br /> <u>Prediction</u><br /> - Prediction results
      now available as CSV.<br /> - Ames genotoxicity predictors now available.<br /> <u>General</u><br /> - Only
      your own jobs will be shown on the My Bench page.<br /> - Improved login and forgot password interface.<br /> -
      Improved support and user interface for modeling and prediction of n-fold datasets.<br /> <br />
    </p>

    <p align="justify" class="ccbHomeStandard">
      <b>Version 1.2.0 (Jan. 16, 2011)</b>

    <p>
      Updates:<br /> <br /> <u>Datasets</u><br /> - n-Fold external validation datasets may now be created. Using
      this option creates <i>n</i> different external sets for the dataset. When modeling is run on the dataset,
      <i>n</i>
      predictors will be produced at once, each with a different external set.<br /> <br /> <u>Modeling</u><br /> -
      Major improvement on speed, space-efficiency, and predictivity of SVM models.<br /> - SVM models now provide
      detailed output to aid users in finding the best parameter ranges.<br /> - SVM modeling can now be run on LSF,
      allowing greater parallelism when needed.<br /> <br /> <u>Prediction</u><br /> - Predictions have been made
      more space-efficient, and will automatically resume if the server is patched while the prediction is
      running.<br />
      <u>General</u><br /> - Thanks to extensive testing by users and lab members, several edge-case bugs have been
      found and fixed.<br /> - Interface has been tuned in several spots to improve intuitiveness; for example, job
      names containing spaces may now be submitted.<br /> <br />
    </p>

    <p align="justify" class="ccbHomeStandard">
      <b>Version 1.1.0 (Oct. 2, 2010)</b>
    </p>

    <p>
      Updates:<br /> <br /> <u>Datasets</u><br /> - Datasets may now be uploaded that include user-created
      descriptors. The descriptors must be in the <a href="help-fileformats#X">X file format</a> we specify. If there is
      demand, we may add support for other formats later. <br /> - Datasets that include descriptors may omit the SDF;
      hence it is possible to use Chembench for modeling while keeping compound structures private. <br /> <br /> <u>Modeling</u><br />
      - kNN modeling jobs submitted to LSF now use the 'idle' queue instead of the 'month' or 'week' queues. <br />
      This allows for larger-scale modeling jobs to be supported and avoids queue slot issues.<br /> - Modeling can now
      be performed on uploaded descriptors. <br /> - Fixed a bug that occurred when modeling datasets that have no
      external compounds defined.<br /> <br /> <u>Prediction</u><br /> - Prediction may now be performed on uploaded
      descriptors.<br /> <br />
    </p>

    <p align="justify" class="ccbHomeStandard">
      <b>Version 1.0.0</b>
    </p>

    <p>
      Current supported features:<br /> <br /> <u>Dataset Preparation</u> <br /> - User uploads a set of compounds,
      with activity values (optional) <br /> - Standardizes compounds. Removes fragments, standardizes aromatization,
      and cleans. <br /> - Generates descriptors (MolconnZ, Dragon, Maccs, MOE, and ISIDA)<br /> - Splits external
      sets either automatically (activity binning available) or manually based on user input. <br /> - Generates an
      image for each compound. <br /> - Builds and displays a heatmap. <br /> <br /> <u>Model Building</u> <br /> -
      Descriptors can be scaled and can be narrowed based on a correlation cutoff <br /> - Does training / test
      splitting either randomly or by sphere exclusion. <br /> - Builds models with kNN (with genetic algorithm or
      simulated annealing variable selection) <br /> - Builds models with Random Forest <br /> - Displays summary
      information about created predictors, including plot and r^2 (continuous) or confusion matrix (category) <br /> -
      50+ public datasets available for comparing and testing modeling methods <br /> <br /> <u>Prediction</u> <br />
      - Can predict full datasets (submitted job) or single compounds (interactive) <br /> - Can predict using multiple
      predictors at once <br /> - Six published predictors available: Blood-Brain Barrier, Anticonvulsants, 5HT2B,
      Antimalarial, T.Pyriformis, and P-Glycoprotein. <br /> <br /> <u>Jobs / Job Tracking</u> <br /> - Runs hundreds
      of simultaneous kNN jobs through LSF. Other jobs run using local processing power; up to 6 jobs can be running at
      once. <br /> - Reports time estimate for modeling jobs and emails users on completion; all jobs have progress
      indicators <br /> - Tracks time taken by each job for performance analysis <br />

    </p>

    <%@include file="/jsp/help/helpcontents.jsp" %>

  </div>

  <div class="includes">
    <%@include file="/jsp/main/footer.jsp" %>
  </div>
</div>
>>>>>>> spring
</body>
</html>

