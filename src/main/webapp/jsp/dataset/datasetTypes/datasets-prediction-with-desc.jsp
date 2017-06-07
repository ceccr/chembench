<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<input type="hidden" name="dataset-type" value=<s:property
    value="@edu.unc.ceccr.chembench.global.Constants@PREDICTIONWITHDESCRIPTORS" />>

<p>
  A <b>Prediction Dataset with Descriptors</b> can only be used for prediction. Choose this option if
  you have your own descriptors that you want to upload. (Otherwise, select <b>Prediction Dataset</b>
  instead.)
</p>

<p>
  You will need to supply an <s:a action="file-formats" namespace="/help" anchor="X">X file</s:a>
  containing your descriptor data. Optionally, you may provide a <s:a action="file-formats"
                                                                      namespace="/help"
                                                                      anchor="SDF">SDF file</s:a>
  containing the structures of the compounds in your dataset. If you do, Chembench will generate
  compound structure images for you.
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
    <div class="uploaded-descriptor-type">
      <div class="control-label col-xs-3">
        <input type="radio" name="predictorNameD" id="newDescriptorNameD" checked="checked"><label
          for="newDescriptorNameD">New type</label>
      </div>
      <div class="col-xs-9 uploaded-descriptor-type-entry">
        <s:textfield name="descriptorNewNameD" id="descriptorNewNameD" theme="simple" />
      </div>
    </div>
    <s:if test="userUploadedDescriptorTypes.size() > 0">
      <div class="uploaded-descriptor-type text-muted">
        <div class="control-label col-xs-3">
          <input type="radio" name="predictorNameD" id="usedDescriptorNameD"><label
            for="usedDescriptorNameD">Existing type</label>
        </div>
        <div class="col-xs-9 uploaded-descriptor-type-entry">
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
    <label for="hasBeenScaled-PWD"><s:checkbox name="hasBeenScaled" id="hasBeenScaled-PWD"
                                               theme="simple" />My
      descriptors have been scaled</label>

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
      <label for="standardizePredDesc"><s:checkbox name="standardizePredDesc" id="standardizePredDesc"
                                                   theme="simple" />Standardize molecule
        structures</label>

      <p class="help-block">
        If you choose not to standardize, ensure that your structure file contains explicit
        hydrogens.<br> Otherwise, Dragon descriptors will not be available.
      </p>
    </div>
  </div>
  <div class="form-group">
    <div class="col-xs-offset-3 col-xs-9">
      <label for="generateImagesPWD"><s:checkbox name="generateImagesPWD" id="generateImagesPWD"
                                                 theme="simple" />Generate Mahalanobis heatmap</label>

      <p class="help-block">Unchecking this box will accelerate dataset generation but will eliminate
        heatmap based on Mahalanobis distance measure.
      </p>
    </div>
  </div>
</div>
