<%@include file ="/jsp/help/helpheader.jsp" %>

<table width="600" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr><td>
<div class="ccbHomeStandard">

Prediction

<p align="justify" class="ccbHomeStandard">
Selecting Predictors:

<p>Start by selecting one or more of the predictors. If you have built many predictors on the same modeling dataset, 
you may want to choose all of them, so that you can compare their predictions. In most cases, however, you will 
only need to use one of the predictors. Clicking the "Select Predictors" button will bring you to dataset selection.</p>
</p>

<p align="justify" class="ccbHomeStandard">
<b>Dataset Selection:</b>

<p>Choose a dataset from the dropdown list, and specify a similarity cutoff. Compounds in your prediction dataset 
 that are outside the cutoff range will not be predicted by a kNN model. Setting the cutoff higher (e.g. to 5) 
 will result in more predictions, but the predictions will be of lower confidence as they are likely to be 
 outside the models' applicability domain. The cutoff value is measured in standard deviations, so it is nonlinear.</p>
 </p>

<p align="justify" class="ccbHomeStandard">
<b>Single molecule prediction:</b>

<p>A prediction can be made on a single molecule instead of an entire dataset. You can enter in a SMILES string 
directly and hit "Predict". You may also draw a molecule using the Java applet, and click "Get SMILES" to retrieve 
its SMILES string for prediction. Single molecule predictions do not require the running of a job; the predictions 
will be calculated immediately, while you are on the Predictions page.</p>
</p>

<p align="justify" class="ccbHomeStandard">
<b>The Prediction Job:</b>

<p>In a prediction, the descriptor type of each predictor is examined. These descriptors are then generated for the 
dataset to be predicted. The descriptors are normalized according to the ranges used in the predictor's training 
set. Then, the predictors are each applied to the scaled descriptor sets to create predictions. The results can 
be viewed by clicking on the name of the prediction in the Predictions section of the jobs page. </p>
</p>

<%@include file ="/jsp/main/footer.jsp" %>

</body>
</html>