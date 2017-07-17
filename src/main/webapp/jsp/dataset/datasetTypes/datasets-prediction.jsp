<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<input type="hidden" name="dataset-type" value=<s:property
    value="@edu.unc.ceccr.chembench.global.Constants@PREDICTION" />>

<p>
  A <b>Prediction Dataset</b> can only be used for prediction. You will need to supply an <s:a
    action="file-formats" namespace="/help" anchor="SDF"> SDF file</s:a> containing the structures of the
  compounds in your dataset.
</p>

<div class="form-group">
  <label class="control-label col-xs-3">Structure file (.sdf):</label>

  <div class="col-xs-9">
    <s:file name="sdfFilePrediction" id="sdfFilePrediction" theme="simple" />
  </div>
</div>
<div class="form-group">
  <label class="control-label col-xs-3">Descriptors:</label>

  <div class="col-xs-9">
    <label>
      <input name= "selectedPredictionDescriptors" type="checkbox" value="CDK">
      CDK (202 descriptors) &#09;
    </label>
    <label >
      <input name= "selectedPredictionDescriptors" type="checkbox" value="DRAGON7">
      Dragon 7 (3850 descriptors);
    </label>
    <label >
      <input name= "selectedPredictionDescriptors" type="checkbox" value="DRAGONH">
      Dragon X, with hydrogens (2489 descriptors);
    </label>
    <label>
      <input name= "selectedPredictionDescriptors" type="checkbox" value="DRAGONNOH">
      Dragon X, no hydrogens (900 descriptors);
    </label>
    <label >
      <input name="selectedPredictionDescriptors" type="checkbox" value="MACCS">
      MACCS (166 descriptors);
    </label>
    <label>
      <input name="selectedPredictionDescriptors" type="checkbox" value="MOE2D">
      MOE2D (184 descriptors);
    </label>
    <label >
      <input name="selectedPredictionDescriptors" type="checkbox" value="ISIDA">
      ISIDA;
    </label>
    <label >
      <input name="selectedPredictionDescriptors" type="checkbox" value="ALL" checked>
      All Descriptors;
    </label>
  </div>
</div>
<hr>
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
      heatmap based on Mahalanobis distance measure.
    </p>
  </div>
</div>