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
</body>
</html>

