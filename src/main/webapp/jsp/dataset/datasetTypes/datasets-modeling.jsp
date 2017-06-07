<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

  <input type="hidden" name="dataset-type" value=<s:property
      value="@edu.unc.ceccr.chembench.global.Constants@MODELING" />>

  <p>
    A <b>Modeling Dataset</b> can be used for both modeling and prediction. You will need to supply an
    <s:a action="file-formats" namespace="/help" anchor="SDF">SDF file</s:a> containing the structures of
    the compounds in your dataset, and an <s:a action="file-formats" namespace="/help"
                                               anchor="ACT">ACT file</s:a> containing the activity values
    of those compounds.
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
    <label class="control-label col-xs-3">Descriptors:</label>

    <div class="col-xs-9">
      <label>
        <input name="selectedModelingDescriptors" type="checkbox" value="CDK">
        CDK (202 descriptors)
      </label>
      <label >
        <input name="selectedModelingDescriptors" type="checkbox" value="DRAGON7">
        Dragon 7 (3850 descriptors)
      </label>
      <label >
        <input name="selectedModelingDescriptors" type="checkbox" value="DRAGONH">
        Dragon X, with hydrogens (2489 descriptors)
      </label>
      <label>
        <input name="selectedModelingDescriptors" type="checkbox" value="DRAGONNOH">
        Dragon X, no hydrogens (900 descriptors)
      </label>
      <label >
        <input name="selectedModelingDescriptors" type="checkbox" value="MACCS">
        MACCS (166 descriptors)
      </label>
      <label>
        <input name="selectedModelingDescriptors" type="checkbox" value="MOE2D">
        MOE2D (184 descriptors)
      </label>
      <label >
        <input name="selectedModelingDescriptors" type="checkbox" value="ISIDA">
        ISIDA
      </label>
      <label >
        <input name="selectedModelingDescriptors" type="checkbox" value="ALL" checked>
        All Descriptors
      </label>
    </div>
  </div>
  <div class="form-group">
    <div class="col-xs-offset-3 col-xs-9">
      <label for="standardizeModeling"><s:checkbox name="standardizeModeling" id="standardizeModeling"
                                                   theme="simple" />Standardize molecule
        structures</label>

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
        heatmap based on Mahalanobis distance measure.
      </p>
    </div>
  </div>
